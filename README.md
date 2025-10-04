# tenisu-backend
tennis statistics api

## Infrastructure - runbook (short)

### Prerequisites (run once)
Infrastructure is managed by Terraform.

1. Run `scripts/bootstrap.sh` once with your AWS CLI creds if from new AWS account:
   ```bash
   ./scripts/bootstrap.sh <AWS_ACCOUNT_ID> <GITHUB_ORG/REPO>
   ```
   
2. Add GitHub secrets: `AWS_ROLE_ARN`, `TF_STATE_BUCKET`, `AWS_ACCOUNT_ID`, `AWS_REGION` to your GitHub repo.

### Apply Infrastructure changes (run manually)

From Actions tab run `Terraform - plan & apply` (workflow_dispatch) selecting the wanted environement
`dev` to create infra.

### Connect to RDS

The connection is done through a bastion host.

* Please install AWS SSM plugin for your machine https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html
* run `infra/connect-to-rds.sh`
* you can connect your favorite database client to `localhost:15432`



