locals {
  name_prefix = "${var.project}-${var.environment}"
  app_name    = "tenisu"
}


# ECR repo
resource "aws_ecr_repository" "app" {
  name = var.project
  image_scanning_configuration { scan_on_push = true }
}

#########################################
# VPC using terraform-aws-modules/vpc
#########################################
module "vpc" {
  source             = "terraform-aws-modules/vpc/aws"
  version            = "6.4.0"
  name               = local.name_prefix
  cidr               = "10.0.0.0/16"
  azs                = ["eu-west-3a", "eu-west-3c"]
  public_subnets     = ["10.0.1.0/24", "10.0.3.0/24"]
  private_subnets    = ["10.0.101.0/24", "10.0.103.0/24"]
  enable_nat_gateway = true

  # Important: make sure NAT is not created in the AZ you are removing
  single_nat_gateway     = true
  one_nat_gateway_per_az = false
}

###################################
# NLB Definition
###################################

# NLB in public subnets (you can make it internal = true if you prefer private)
resource "aws_lb" "nlb" {
  name               = "${local.name_prefix}-nlb"
  load_balancer_type = "network"
  internal           = true # set to false if you want a public NLB
  subnets            = module.vpc.public_subnets
}

# Target group for ECS tasks via IP targets
resource "aws_lb_target_group" "app" {
  name        = "${local.name_prefix}-tg"
  port        = 8080
  protocol    = "TCP" # NLB supports TCP/UDP. Health checks can be HTTP (below).
  vpc_id      = module.vpc.vpc_id
  target_type = "ip"

  # Optional HTTP health check against your Spring endpoint
  health_check {
    protocol            = "HTTP"
    path                = "/health"
    healthy_threshold   = 2
    unhealthy_threshold = 2
    interval            = 15
    timeout             = 5
    matcher             = "200"
  }
}

# Listener on NLB
resource "aws_lb_listener" "tcp_8080" {
  load_balancer_arn = aws_lb.nlb.arn
  port              = 8080
  protocol          = "TCP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}

# Security group for ECS tasks
resource "aws_security_group" "ecs_app" {
  name        = "${local.name_prefix}-ecs-app-sg"
  description = "Allow app port from NLB subnets"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = module.vpc.public_subnets_cidr_blocks # since NLB is in public subnets (adjust if using private)
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "db" {
  name   = "${local.name_prefix}-db-sg"
  vpc_id = module.vpc.vpc_id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_app.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


# Secrets: generate DB password and store in Secrets Manager
resource "random_password" "db" { length = 24 }

# RDS Postgres
resource "aws_db_instance" "postgres" {
  identifier             = "${local.name_prefix}-db"
  engine                 = "postgres"
  engine_version         = "15"
  instance_class         = "db.t4g.micro"
  allocated_storage      = 20
  db_name                = local.app_name
  username               = "${local.app_name}_admin"
  password               = random_password.db.result
  skip_final_snapshot    = true
  publicly_accessible    = false
  vpc_security_group_ids = [module.vpc.default_security_group_id, aws_security_group.db.id]
  db_subnet_group_name   = aws_db_subnet_group.db_subnets.name
}

resource "aws_secretsmanager_secret" "db" {
  name = "${local.name_prefix}-db-creds"
}

resource "aws_secretsmanager_secret_version" "db_secret_version" {
  secret_id = aws_secretsmanager_secret.db.id
  secret_string = jsonencode({
    host     = aws_db_instance.postgres.address
    port     = aws_db_instance.postgres.port
    username = aws_db_instance.postgres.username
    password = random_password.db.result
    dbname   = aws_db_instance.postgres.db_name
  })
}

resource "aws_db_subnet_group" "db_subnets" {
  name       = "${local.name_prefix}-db-subnet"
  subnet_ids = module.vpc.private_subnets
}

# ECS Cluster
resource "aws_ecs_cluster" "cluster" {
  name = local.name_prefix
}

###################################
# ECS Task Definition
###################################
# Trust policy
data "aws_iam_policy_document" "ecs_task_trust" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name               = "${local.name_prefix}-exec-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_trust.json
}

resource "aws_iam_role_policy_attachment" "ecs_exec_managed" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy" "ecs_exec_secrets" {
  name = "${local.name_prefix}-exec-secrets"
  role = aws_iam_role.ecs_task_execution_role.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = ["secretsmanager:GetSecretValue"],
        Resource = aws_secretsmanager_secret.db.arn
      }
    ]
  })
}

# task definition
resource "aws_ecs_task_definition" "app" {
  family                   = local.name_prefix
  cpu                      = "256"
  memory                   = "512"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name      = "${local.name_prefix}"
      image     = "${aws_ecr_repository.app.repository_url}:${var.environment}"
      cpu       = 256
      memory    = 512
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      environment = [
        { name = "ENV", value = var.environment }
      ]
      secrets = [
        { name = "DB_HOST", valueFrom = "${aws_secretsmanager_secret.db.arn}:host::" },
        { name = "DB_PORT", valueFrom = "${aws_secretsmanager_secret.db.arn}:port::" },
        { name = "DB_NAME", valueFrom = "${aws_secretsmanager_secret.db.arn}:dbname::" },
        { name = "DB_USERNAME", valueFrom = "${aws_secretsmanager_secret.db.arn}:username::" },
        { name = "DB_PASSWORD", valueFrom = "${aws_secretsmanager_secret.db.arn}:password::" }
      ]
    }
  ])
}

###################################
# ECS Service
###################################
resource "aws_ecs_service" "service" {
  name            = "${local.name_prefix}-service"
  cluster         = aws_ecs_cluster.cluster.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = 0
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = module.vpc.private_subnets
    assign_public_ip = false
    security_groups  = [aws_security_group.ecs_app.id]
  }

  # Register tasks into the NLB target group
  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = local.name_prefix
    container_port   = 8080
  }
}


###################################
# Cognito User Pool
###################################
resource "aws_cognito_user_pool" "users" {
  name = "${local.name_prefix}-pool"
}

resource "aws_cognito_user_pool_client" "app_client" {
  name            = "${local.name_prefix}-client"
  user_pool_id    = aws_cognito_user_pool.users.id
  generate_secret = false

  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]
}

resource "aws_cognito_user_pool_domain" "domain" {
  domain       = local.name_prefix
  user_pool_id = aws_cognito_user_pool.users.id
}


###################################
# API Gateway VPC Link
###################################
# (HTTP API v2)
resource "aws_security_group" "vpc_link" {
  name   = "${local.name_prefix}-vpc-link-sg"
  vpc_id = module.vpc.vpc_id

  # Allow egress to NLB subnets (NLB has no SG). You can keep this wide-open for simplicity.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_apigatewayv2_vpc_link" "ecs_vpc_link" {
  name               = "${local.name_prefix}-vpc-link"
  subnet_ids         = module.vpc.public_subnets
  security_group_ids = [aws_security_group.vpc_link.id]
}

###################################
# API Gateway REST API
###################################
resource "aws_apigatewayv2_api" "rest_api" {
  name          = "${local.name_prefix}-api"
  protocol_type = "HTTP"
}

###################################
# API Gateway Integration with ECS
###################################
resource "aws_apigatewayv2_integration" "ecs_integration" {
  api_id                 = aws_apigatewayv2_api.rest_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = aws_lb_listener.tcp_8080.arn
  connection_type        = "VPC_LINK"
  connection_id          = aws_apigatewayv2_vpc_link.ecs_vpc_link.id
  payload_format_version = "1.0"
}

###################################
# API Gateway Authorizer
###################################
resource "aws_apigatewayv2_authorizer" "cognito_auth" {
  api_id           = aws_apigatewayv2_api.rest_api.id
  authorizer_type  = "JWT"
  identity_sources = ["$request.header.Authorization"]

  jwt_configuration {
    issuer   = "https://cognito-idp.${var.aws_region}.amazonaws.com/${aws_cognito_user_pool.users.id}"
    audience = [aws_cognito_user_pool_client.app_client.id]
  }

  name = "${local.name_prefix}-cognito-auth"
}

###################################
# Attach Authorizer to Route
###################################
resource "aws_apigatewayv2_route" "default_route" {
  api_id    = aws_apigatewayv2_api.rest_api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ecs_integration.id}"

  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.cognito_auth.id
}


###################################
# API Gateway Deployment & Stage
###################################
resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.rest_api.id
  name        = var.environment
  auto_deploy = true
}



# NOTE: ECS Task, service, iam roles etc. are intentionally omitted to keep this starter compact.
# See the README section below on how to add a simple Fargate task definition pointing to the ECR image,
# and how to wire Task Role permissions to read from Secrets Manager (DB creds).
