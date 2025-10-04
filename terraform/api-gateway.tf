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
# API Gateway Custom Domain
###################################

resource "aws_apigatewayv2_domain_name" "custom" {
  domain_name = var.domain
  domain_name_configuration {
    certificate_arn = aws_acm_certificate.api_domain.arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }

  depends_on = [aws_acm_certificate_validation.api_domain]
}

# Create log group
resource "aws_cloudwatch_log_group" "apigw_access" {
  name              = "/apigw/${local.name_prefix}/access"
  retention_in_days = 1
}

# Base path mapping: https://<domain> -> HTTP API `<env>` stage
resource "aws_apigatewayv2_api_mapping" "stage" {
  api_id      = aws_apigatewayv2_api.rest_api.id
  domain_name = aws_apigatewayv2_domain_name.custom.id
  stage       = aws_apigatewayv2_stage.stage.id
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

  # Header mappings to backend (overwrite/append semantics)
  request_parameters = {
    # Inject stage as X-TENISU-ENV
    "overwrite:header.X-TENISU-ENV"    = "$context.stage"
  }
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
resource "aws_apigatewayv2_stage" "stage" {
  api_id      = aws_apigatewayv2_api.rest_api.id
  name        = var.environment
  auto_deploy = true


  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.apigw_access.arn
    # Common JSON format including requestId and user principal if present
    format = jsonencode({
      requestId       = "$context.requestId",
      ip              = "$context.identity.sourceIp",
      requestTime     = "$context.requestTime",
      httpMethod      = "$context.httpMethod",
      routeKey        = "$context.routeKey",
      status          = "$context.status",
      protocol        = "$context.protocol",
      integrationStatus = "$context.integrationStatus",
      integrationError  = "$context.integrationErrorMessage",
      responseLength  = "$context.responseLength",
      user            = "$context.authorizer.principalId"
    })
  }
}
