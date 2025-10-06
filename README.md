# tenisu-backend
Tennis players statistics api

## How to test

This section shows exactly how to use the provided IntelliJ HTTP client file to exercise the API with the correct scopes.
The `http/http-requests.env.json` and `http/http-requests.private.env.json` files are used to configure the environment variables.

### Prerequisites
#### Local environment
- You don't need to configure anything for local environment.
- Run your application with `local` active profile
    ```bash
      ./gradlew bootRun --args='--spring.profiles.active=<profile_name>'
    ```
- follow the next sections to test endpoints [from here](#get-a-read-only-token-default)

#### Remote environment (dev)
- Configure `http/http-client.private.env.json` with your values:
  ```json
  {
    "dev": {
       "client_basic_auth": "<base64(clientId:clientSecret)>"
    }
  }
  ```
- go to [here](#get-a-read-only-token-default)

### 1) Get a read-only token (default)
- Open `http/http-requests.http` in IntelliJ.
- Run the first request block “Get token via OAUTH2…”. It posts:
    - `grant_type=client_credentials&scope=tenisu-api/read`
- This saves the token into a global variable: `{{auth.access_token}}`.
- With this token you can call:
    - `GET {{base_url}}/health`
    - `GET {{base_url}}/players`
    - `GET {{base_url}}/players/17`
    - `GET {{base_url}}/statistics`
    - `POST {{base_url}}/players`


**Important**: This read-only token will NOT allow creating a player.

### 2) Create a player
- Get a read/write token by changing the request to post:
    - `grant_type=client_credentials&scope=tenisu-api/read%20tenisu-api/write`
- After obtaining the read+write token, run the “Create valid player” block:
    - `POST {{base_url}}/players`
    - Headers include `Authorization: Bearer {{auth.access_token}}`
    - Body sample already provided in the file:
      ```json
      {
        "firstname": "titi",
        "lastname": "toto",
        "sex": "M",
        "countryCode": "FRA",
        "picture": "http://url.com/format",
        "data": {
          "rank": 10,
          "points": 100,
          "weight": 75500,
          "height": 170,
          "age": 30,
          "last": [1, 1, 0, 0, 1]
        }
      }
      ```

If you use the read-only token by mistake, you will get `403 Forbidden` on this endpoint.

### 3) Field validation notes (aligned with the server)
- `data.last` must have up to 5 elements, each element only `0` or `1`.
- Other constraints (positivity, minimums) apply as usual; invalid input returns `400` with details.



## Infrastructure - runbook

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

### How to connect to RDS

The connection is done through a bastion host.

* Please install AWS SSM plugin for your machine https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html
* run `./scripts/connect-to-rds.sh`
* you can connect your favorite database client to `localhost:15432`

## Architecture
![App Logo](/documentation/tenisu-backend-aws-architecture.svg)
