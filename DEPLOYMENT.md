# Container deployment

Run `docker compose up --build -d` to build the backend and start MySQL. The backend waits until MySQL passes its health check. MySQL data is retained in the named `mysql-data` Docker volume across container restarts.

For AWS, set `MYSQL_PASSWORD` and `MYSQL_ROOT_PASSWORD` from AWS Secrets Manager. The fallback values in `compose.yml` are for local development only. A named Compose volume is host-local; use a durable AWS volume and backups if this MySQL container is used beyond local or single-host development. Set `CORE_API_URL` to the deployed core-service URL when this service forwards requests to Professy.
