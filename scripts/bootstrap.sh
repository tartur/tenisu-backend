#!/usr/bin/env bash
set -euo pipefail

# Usage: ./scripts/bootstrap.sh <aws-account-id> <github-org/repo>

ACCOUNT_ID="$1"
REGION="eu-west-3" # PARIS region
TF_STATE_BUCKET="tenisu-backend.terraform-state"
GITHUB_REPO="$2"

echo "Account: $ACCOUNT_ID, Region: $REGION, Bucket: $TF_STATE_BUCKET, Repo: $GITHUB_REPO"

# 1) create S3 bucket for terraform state (if not exists)
aws s3api head-bucket --bucket "$TF_STATE_BUCKET" 2>/dev/null || {
  echo "Creating s3 bucket $TF_STATE_BUCKET"
  aws s3api create-bucket --bucket "$TF_STATE_BUCKET" --region "$REGION" \
    --create-bucket-configuration LocationConstraint=$REGION
  aws s3api put-bucket-encryption --bucket "$TF_STATE_BUCKET" --server-side-encryption-configuration '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
}

# 2) Create OIDC provider (if not exists)
OIDC_URL="https://token.actions.githubusercontent.com"
EXISTS=$(aws iam list-open-id-connect-providers --query "OpenIDConnectProviderList[?contains(Arn, 'token.actions.githubusercontent.com')] | length(@)" --output text || echo 0)
if [ "$EXISTS" -eq "0" ]; then
  echo "Creating OIDC provider"
  aws iam create-open-id-connect-provider --url "$OIDC_URL" --client-id-list sts.amazonaws.com
else
  echo "OIDC provider already exists"
fi

# 3) Create IAM role with trust to GitHub OIDC for this repo
ROLE_NAME="github-actions-terraform-${GITHUB_REPO//\//_}"
TRUST_POLICY_FILE="/tmp/${ROLE_NAME}-trust.json"
cat > "$TRUST_POLICY_FILE" <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::${ACCOUNT_ID}:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:${GITHUB_REPO}:ref:refs/heads/*"
        },
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        }
      }
    }
  ]
}
EOF

aws iam get-role --role-name "$ROLE_NAME" >/dev/null 2>&1 || {
  echo "Creating role $ROLE_NAME"
  aws iam create-role --role-name "$ROLE_NAME" --assume-role-policy-document file://$TRUST_POLICY_FILE
  # For dev convenience attach AdministratorAccess. Replace with least privilege in prod.
  aws iam attach-role-policy --role-name "$ROLE_NAME" --policy-arn arn:aws:iam::aws:policy/AdministratorAccess
}

ROLE_ARN=$(aws iam get-role --role-name "$ROLE_NAME" --query 'Role.Arn' --output text)

echo "Created/Found role: $ROLE_ARN"
echo "Set this as GitHub secret AWS_ROLE_ARN in your repo. Also set TF_STATE_BUCKET=$TF_STATE_BUCKET and AWS_ACCOUNT_ID=$ACCOUNT_ID"