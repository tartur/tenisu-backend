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
