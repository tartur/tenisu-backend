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