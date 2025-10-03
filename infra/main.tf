locals {
  name_prefix = "${var.project}-${var.environment}"
  app_name    = "tenisu"
}


# ECR repo
resource "aws_ecr_repository" "app" {
  name = var.project
  image_scanning_configuration { scan_on_push = true }
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