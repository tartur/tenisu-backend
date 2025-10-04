output "ecr_repo_url" {
  value = aws_ecr_repository.app.repository_url
}

output "nlb_dns" {
  value = aws_lb.nlb.dns_name
}

output "cognito_user_pool_id" {
  value = aws_cognito_user_pool.users.id
}

output "cognito_user_pool_client_id" {
  value = aws_cognito_user_pool_client.app_client.id
}