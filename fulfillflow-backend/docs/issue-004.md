# Issue #4: Cancel order and release reserved inventory

## Goal

Allow a pending order to be cancelled safely and return its reserved inventory to the available pool.

## Scope

- `POST /api/orders/{orderId}/cancellations`
- Only a `PENDING` order can perform the inventory release
- A successful cancellation changes order status to `CANCELLED`
- The matching quantity moves from `reserved_stock` back to `available_stock`
- Repeated cancellation is idempotent: it returns the existing cancelled order without releasing stock again

## Business rules

- The order status transition uses `WHERE id = :orderId AND status = 'PENDING'`. Only the request that updates one row owns the release operation.
- Stock release uses `WHERE reserved_stock >= :quantity` as a defensive condition. If it does not update a row, the transaction fails and rolls the order status back to `PENDING`.
- Status transition and stock release share one `@Transactional` MySQL transaction. A successful cancellation changes both order and inventory; a failure changes neither.

## Acceptance criteria

- [ ] Cancelling a quantity-3 order originally reserved from stock 5 restores available stock to 5 and reserved stock to 0.
- [ ] Repeating the same cancellation does not release stock twice.
- [ ] Cancelling a missing order returns HTTP 404.
- [ ] `mvn clean test` passes locally and in CI.
