# FulfillFlow

> Inventory order fulfillment service. This repository is a one-week engineering MVP for learning delivery logic: versioned schema migration, tests, CI, containerized local runtime, and business design around inventory reservation and order status.

## Current scope (Issue #3)

- Java 17 + Spring Boot 3 + Maven
- MySQL configuration externalized through environment variables
- Redis client configuration reserved for later inventory cache work
- Flyway migration baseline; JPA uses `ddl-auto=validate`, never `update`
- Actuator health endpoint and Swagger UI
- MockMvc API test baseline
- GitHub Actions CI for tests and package build
- SKU and inventory schema managed by Flyway V2
- SKU creation, inventory query, and replenishment APIs
- Transactional order creation with conditional inventory reservation and order price snapshots

## Inventory rules

- `available_stock` (available stock): inventory available for new orders.
- `reserved_stock` (reserved stock): inventory held by existing orders. Replenishment does not change it.
- Replenishment accepts only a positive quantity and increases `available_stock` and `version`.
- Order creation atomically moves stock from `available_stock` to `reserved_stock` with a conditional update and saves the order in the same database transaction.

## Local prerequisites

- Java 17
- Maven 3.9+ (or a Maven container)
- MySQL 8 running locally, with an empty `fulfillflow` database and an application account
- Redis is optional for Issue #2 because no Redis operation is executed yet

## Environment variables

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/fulfillflow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai'
$env:DB_USERNAME='fulfillflow'
$env:DB_PASSWORD='replace-with-local-secret'
```

Do not commit real passwords or tokens. Copy example values only into local shell variables or an ignored local file.

## Test verification

```bash
mvn clean test
```

The test profile uses an H2 in-memory database and runs Flyway V1, V2, and V3 migrations. It verifies:

- `GET /api/system/ping` returns the standard success response.
- Creating a SKU with initial stock 5 then replenishing 10 results in available stock 15.
- Replenishment does not change reserved stock.
- Replenishment with quantity 0 returns HTTP 400.
- Creating an order reserves stock and records a price snapshot.
- Insufficient stock returns HTTP 409 without changing inventory.
- An order persistence failure rolls back its inventory reservation.

## Run the application

```bash
mvn spring-boot:run
```

After configuring a local MySQL database and successful startup:

- Health: `http://localhost:8081/actuator/health`
- Example API: `http://localhost:8081/api/system/ping`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

## Database migration rule

Never alter an already executed migration. Add a new migration for every schema or data correction, for example `V4__add_order_expire_at.sql`.

## Next planned issues

1. Add cancellation and release of reserved inventory.
2. Add Docker Compose and a local deployment guide.
3. Add a minimal management UI for API integration.