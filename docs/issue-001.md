# Issue #1: Initialize backend and quality baseline

## Goal
Create a runnable Spring Boot backend skeleton for FulfillFlow and establish the migration, test, and CI baseline before implementing inventory or order behavior.

## Scope
- Java 17 / Spring Boot 3 / Maven structure
- Externalized MySQL and Redis configuration
- Flyway V1 migration
- Actuator health endpoint, Swagger UI, and sample ping API
- MockMvc API test
- GitHub Actions CI
- README and contribution templates

## Out of scope
- SKU, inventory, and order business APIs
- RabbitMQ, Docker, Nginx, frontend, authentication

## Acceptance criteria
- [ ] Application starts against an empty MySQL schema and Flyway applies V1.
- [ ] `mvn test` passes.
- [ ] `/actuator/health`, `/api/system/ping`, and Swagger UI are accessible.
- [ ] CI runs tests and package build on push/PR.
- [ ] No local secret is committed.

## Test evidence
- [ ] MockMvc validates the standard success response from `/api/system/ping`.
- [ ] Startup logs show Flyway migration applied.
