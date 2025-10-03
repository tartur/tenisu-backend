# tenisu-backend
tennis statistics api

## Infrastructure - runbook (short)

Infrastructure is managed by Terraform.

1. Run `scripts/bootstrap.sh` once with your AWS CLI creds if from new AWS account:
   ```bash
   ./scripts/bootstrap.sh <AWS_ACCOUNT_ID> <GITHUB_ORG/REPO>
   ```
   
2. Add GitHub secrets: `AWS_ROLE_ARN`, `TF_STATE_BUCKET`, `AWS_ACCOUNT_ID`, `AWS_REGION` to your GitHub repo.

3. From Actions tab run `Terraform - plan & apply` (workflow_dispatch) selecting `dev` to create infra.
