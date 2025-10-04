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

###################################
# SSM Definition
###################################
# SG for SSM VPC interface endpoints: allow inbound 443 from instance SG
resource "aws_security_group" "vpce_ssm" {
  name   = "${local.name_prefix}-vpce-ssm-sg"
  vpc_id = module.vpc.vpc_id

  ingress {
    from_port       = 443
    to_port         = 443
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


# Interface Endpoints for SSM in your VPC
resource "aws_vpc_endpoint" "ssm" {
  vpc_id              = module.vpc.vpc_id
  service_name        = "com.amazonaws.${var.aws_region}.ssm"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = module.vpc.private_subnets
  security_group_ids  = [aws_security_group.vpce_ssm.id] # SG permitting egress
  private_dns_enabled = true
}

resource "aws_vpc_endpoint" "ssmmessages" {
  vpc_id              = module.vpc.vpc_id
  service_name        = "com.amazonaws.${var.aws_region}.ssmmessages"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = module.vpc.private_subnets
  security_group_ids  = [aws_security_group.vpce_ssm.id]
  private_dns_enabled = true
}

resource "aws_vpc_endpoint" "ec2messages" {
  vpc_id              = module.vpc.vpc_id
  service_name        = "com.amazonaws.${var.aws_region}.ec2messages"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = module.vpc.private_subnets
  security_group_ids  = [aws_security_group.vpce_ssm.id]
  private_dns_enabled = true
}