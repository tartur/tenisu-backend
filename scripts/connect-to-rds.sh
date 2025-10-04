#!/usr/bin/env bash
set -euo pipefail
REGION="eu-west-3"
ENV="dev"
PROJECT="tenisu-backend"
DB_IDENTIFIER="${PROJECT}-${ENV}-db"
LOCAL_PORT="15432"

# Find bastion by profile arn
BASTION_ID=$(aws ec2 describe-instances \
  --region eu-west-3 \
  --filters "Name=tag:Role,Values=ssm-bastion" "Name=instance-state-name,Values=running" \
  --query "Reservations[0].Instances[0].InstanceId" --output text)



if [[ -z "$BASTION_ID" || "$BASTION_ID" == "None" ]]; then
  echo "Could not find a running bastion instance with tag Role=ssm-bastion in $REGION" >&2
  exit 1
fi

# Resolve RDS endpoint dynamically
RDS_ENDPOINT=$(aws rds describe-db-instances --region "$REGION" \
  --db-instance-identifier "$DB_IDENTIFIER" \
  --query "DBInstances[0].Endpoint.Address" --output text)

if [[ -z "$RDS_ENDPOINT" || "$RDS_ENDPOINT" == "None" ]]; then
  echo "Could not resolve RDS endpoint for $DB_IDENTIFIER" >&2
  exit 1
fi

echo "Starting SSM port-forward: localhost:${LOCAL_PORT} -> ${RDS_ENDPOINT}:5432 via ${BASTION_ID}"
aws ssm start-session --region "$REGION" \
  --target "$BASTION_ID" \
  --document-name AWS-StartPortForwardingSessionToRemoteHost \
  --parameters "{\"host\":[\"${RDS_ENDPOINT}\"],\"portNumber\":[\"5432\"],\"localPortNumber\":[\"${LOCAL_PORT}\"]}"