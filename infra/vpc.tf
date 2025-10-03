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