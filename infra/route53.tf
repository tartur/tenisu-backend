# Lookup existing hosted zone for your domain
data "aws_route53_zone" "root" {
  name         = var.root_domain
  private_zone = false
}

# Request a public certificate for the exact host
resource "aws_acm_certificate" "api_domain" {
  domain_name       = var.domain
  validation_method = "DNS"
}

# Create DNS records for validation
resource "aws_route53_record" "api_domain_validation" {
  for_each = {
    for dvo in aws_acm_certificate.api_domain.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }
  zone_id = data.aws_route53_zone.root.zone_id
  name    = each.value.name
  type    = each.value.type
  ttl     = 60
  records = [each.value.record]
}

# Wait for ACM to validate
resource "aws_acm_certificate_validation" "api_domain" {
  certificate_arn         = aws_acm_certificate.api_domain.arn
  validation_record_fqdns = [for r in aws_route53_record.api_domain_validation : r.fqdn]
}

# Route53 alias to the API custom domain
resource "aws_route53_record" "apigw_alias" {
  zone_id = data.aws_route53_zone.root.zone_id
  name    = var.domain
  type    = "A"
  alias {
    name                   = aws_apigatewayv2_domain_name.custom.domain_name_configuration[0].target_domain_name
    zone_id                = aws_apigatewayv2_domain_name.custom.domain_name_configuration[0].hosted_zone_id
    evaluate_target_health = false
  }
}
