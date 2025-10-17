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

# Resource Server with custom API scopes
resource "aws_cognito_resource_server" "api" {
  user_pool_id = aws_cognito_user_pool.users.id
  identifier   = "tenisu-api"            # becomes the audience in tokens
  name         = "Tenisu API"

  scope {
    scope_name        = "read"
    scope_description = "Read access"
  }
  scope {
    scope_name        = "write"
    scope_description = "Write access"
  }
}

resource "aws_cognito_user_pool_client" "app_client" {
  name            = "${local.name_prefix}-client"
  user_pool_id    = aws_cognito_user_pool.users.id
  generate_secret = true

  explicit_auth_flows = ["ALLOW_USER_PASSWORD_AUTH", "ALLOW_REFRESH_TOKEN_AUTH"]

  # OAuth 2.0 settings
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_flows                  = ["client_credentials"]
  allowed_oauth_scopes = [
    "${aws_cognito_resource_server.api.identifier}/read",
    "${aws_cognito_resource_server.api.identifier}/write",
  ]


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

resource "aws_cognito_user_pool_client" "public_front_client" {
  name            = "${local.name_prefix}-front-client"
  user_pool_id    = aws_cognito_user_pool.users.id
  generate_secret = false

  # Flows autorisés pour un front web
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_flows = ["code"]  # PKCE flow

  # Scopes d'autorisation
  allowed_oauth_scopes = [
    "openid",
    "email",
    "profile",
    "${aws_cognito_resource_server.api.identifier}/read",
    "${aws_cognito_resource_server.api.identifier}/write",
  ]

  supported_identity_providers = ["COGNITO"]

  # URLs de redirection
  callback_urls = [
    "http://localhost:3000/auth/callback", # ton front CloudFront
  ]
  logout_urls = [
    "https://example.com/",
  ]

  access_token_validity  = 60
  id_token_validity      = 60
  refresh_token_validity = 30
  token_validity_units {
    access_token  = "minutes"
    id_token      = "minutes"
    refresh_token = "days"
  }
}

resource "aws_cognito_user_pool_domain" "domain" {
  domain       = local.name_prefix
  user_pool_id = aws_cognito_user_pool.users.id
}