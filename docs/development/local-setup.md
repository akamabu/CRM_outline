# Local setup

## Infrastructure

```bash
cd deployment/docker-compose
docker compose up -d
```

## Backend MVP API

```bash
cd backend
mvn spring-boot:run
```

Example lead webhook:

```bash
curl -X POST http://localhost:8080/api/v1/integrations/webhooks/leads \
  -H 'Content-Type: application/json' \
  -d '{"tenantId":"tenant-demo","fullName":"Nguyen Van A","email":"a@example.com","phone":"0900000001","source":"website"}'
```
