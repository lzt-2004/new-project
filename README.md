# FulfillFlow

> Inventory order fulfillment service. This repository is a one-week engineering MVP for learning delivery logic: versioned schema migration, tests, CI, containerized local runtime, and business design around inventory reservation and order status.

## Current scope (Issue #1)

- Java 17 + Spring Boot 3 + Maven
- MySQL configuration externalized through environment variables
- Redis client configuration reserved for later inventory cache work
- Flyway migration baseline; JPA uses `ddl-auto=validate`, never `update`
- Actuator health endpoint and Swagger UI
- MockMvc API test baseline
- GitHub Actions CI for tests and package build

## Local prerequisites

- Java 17
- Maven 3.9+ (or a Maven container)
- MySQL 8 running locally, with an empty `fulfillflow` database and an application account
- Redis is optional for Issue #1 because no Redis operation is executed yet

## Environment variables

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/fulfillflow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai'
$env:DB_USERNAME='fulfillflow'
$env:DB_PASSWORD='replace-with-local-secret'
```

Do not commit real passwords or tokens. Copy example values only into local shell variables or an ignored local file.

## Run and verify

```powershell
mvn test
mvn spring-boot:run
```

After startup:

- Health: `http://localhost:8081/actuator/health`
- Example API: `http://localhost:8081/api/system/ping`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

## Database migration rule

Never alter an already executed migration. Add a new migration for every schema or data correction, e.g. `V2__add_order_expire_at.sql`.

## Next planned issues

1. Define SKU and inventory schema through Flyway.
2. Add inventory query and adjustment APIs with tests.
3. Add transactional order creation and conditional inventory reservation.
