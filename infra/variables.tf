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
  default = "dev-api.tenisu.turki4.net"
}
variable "root_domain" {
  type = string
  default = "turki4.net"
}
variable "tf_state_bucket" {
  type = string
}