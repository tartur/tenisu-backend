# In the pipeline you should run the following terraform init \
#  -backend-config="bucket=$TF_STATE_BUCKET" \
#  -backend-config="key=infra/${ENV}/terraform.tfstate" \
#  -backend-config="region=$AWS_REGION"
terraform {
  backend "s3" {
    encrypt        = true
    use_lockfile  = true
  }
}
