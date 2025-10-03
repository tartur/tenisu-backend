variable "aws_region" {
  type = string
  default = "eu-west-3"
}
variable "environment" {
  type = string
  default = "dev"
}
variable "project" {
  type = string
  default = "tenisu-backend"
}
variable "domain" {
  type = string
  default = "tenisu.turki4.net"
} # used for route53/cognito domain
variable "tf_state_bucket" {
  type = string
}