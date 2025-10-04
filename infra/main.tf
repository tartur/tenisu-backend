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
  password_policy {
    minimum_length    = 8
    require_uppercase = false
    require_lowercase = false
    require_numbers   = false
    require_symbols   = false
  }
}

resource "aws_cognito_user_pool_client" "app_client" {
  name            = "${local.name_prefix}-client"
  user_pool_id    = aws_cognito_user_pool.users.id
  generate_secret = false

  explicit_auth_flows = ["ALLOW_USER_PASSWORD_AUTH", "ALLOW_REFRESH_TOKEN_AUTH"]

  # OAuth 2.0 settings
  allowed_oauth_flows_user_pool_client = false
  #allowed_oauth_flows                  = ["code"]
  #allowed_oauth_scopes = ["phone", "email", "openid", "profile", "aws.cognito.signin.user.admin"]
  #callback_urls = ["https://dev-api.tenisu.turki4.net/callback"]  # REQUIRED
  #logout_urls   = ["https://tenisu.turki4.net/logout"]            # REQUIRED

  # Optional but good to set explicitly
  access_token_validity  = 60   # minutes
  id_token_validity      = 60   # minutes
  refresh_token_validity = 30   # days (default 30, max 3650)
  token_validity_units {
    access_token  = "minutes"
    id_token      = "minutes"
    refresh_token = "days"
  }

  # Optional for Hosted UI usage; harmless to include
  supported_identity_providers = ["COGNITO"]
}

resource "aws_cognito_user_pool_domain" "domain" {
  domain       = local.name_prefix
  user_pool_id = aws_cognito_user_pool.users.id
}