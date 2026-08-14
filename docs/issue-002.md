# Issue #2: SKU and inventory baseline

## Goal

Create the SKU and inventory model used by later order reservation work.

## Scope

- Flyway V2 migration for `sku` and `inventory`
- SKU creation with an initial inventory record
- Inventory query endpoint
- Positive-only inventory replenishment endpoint
- MockMvc tests covering successful replenishment and a zero-quantity validation error

## Business rules

- A SKU has one inventory record.
- Replenishment changes `available_stock` only; `reserved_stock` remains unchanged.
- Replenishment increments `version` and updates `updated_at`.
- The order reservation flow is deliberately deferred to the next issue.

## Acceptance criteria

- [ ] Flyway V2 creates the SKU and inventory tables without altering V1.
- [ ] A positive replenishment increases available inventory.
- [ ] Replenishment by zero returns HTTP 400.
- [ ] `mvn clean test` passes locally and in CI.