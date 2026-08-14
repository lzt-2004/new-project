# Issue #3: Create order and reserve inventory

## Goal

Implement the first transactional order flow: reserve stock atomically and save an order snapshot in one MySQL transaction.

## Scope

- Flyway V3 creates `sales_order`
- `POST /api/orders` accepts `skuId` and a positive `quantity`
- Conditional inventory update moves stock from `available_stock` to `reserved_stock`
- Order saves the current SKU price as `unit_price` and `total_amount`
- Insufficient inventory returns HTTP 409 with business code `40901`
- A persistence failure rolls back the preceding inventory reservation

## Business rules

- Order status starts as `PENDING`.
- The update condition is `available_stock >= quantity`; an affected-row count of zero means insufficient inventory.
- The service does not read stock then decrement it in Java, so concurrent orders cannot reserve more inventory than is available.
- `@Transactional` covers conditional reservation and order persistence. Both operations are MySQL operations in this issue, so either both commit or both roll back.

## Acceptance criteria

- [ ] Creating an order for stock 5 with quantity 3 leaves available stock 2 and reserved stock 3.
- [ ] An order preserves the submitted quantity and the SKU price snapshot.
- [ ] Requesting more than available stock returns HTTP 409 and leaves inventory unchanged.
- [ ] A duplicate order number causes order persistence to fail and rolls back the inventory update.
- [ ] `mvn clean test` passes locally and in CI.
