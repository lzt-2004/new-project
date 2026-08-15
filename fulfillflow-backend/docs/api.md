# FulfillFlow API

## Response envelope

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

Validation errors use HTTP `400` and code `40001`. Missing resources use HTTP `404` and code `40401`. Insufficient inventory uses HTTP `409` and code `40901`. Order state conflicts use HTTP `409` and code `40902`.

## Create SKU

`POST /api/skus`

```json
{
  "skuCode": "COFFEE-BEAN-1KG",
  "name": "Coffee bean 1kg",
  "unitPrice": 88.00,
  "initialStock": 5
}
```

Returns HTTP `201` with SKU and inventory data.

## Query inventory

`GET /api/skus/{skuId}/inventory`

Returns the current `availableStock` and `reservedStock`.

## Replenish inventory

`POST /api/skus/{skuId}/inventory/replenishments`

```json
{
  "quantity": 10
}
```

Quantity must be positive. Replenishment changes only `availableStock`.

## Create order

`POST /api/orders`

```json
{
  "skuId": 1,
  "quantity": 3
}
```

Returns HTTP `201` with an order number, price snapshot, total amount, and initial `PENDING` status. It atomically moves the requested quantity from `availableStock` to `reservedStock`. When available stock is insufficient, it returns HTTP `409` and code `40901`.

## Confirm payment

`POST /api/orders/{orderId}/payments`

No request body is required. This project simulates payment confirmation: a `PENDING` order changes to `PAID`. The stock was already reserved when the order was created, so payment does not change `availableStock`, `reservedStock`, or the inventory `version`. Repeating the request returns the existing `PAID` order. A cancelled order cannot be paid and returns HTTP `409` with code `40902`.

## Cancel order

`POST /api/orders/{orderId}/cancellations`

No request body is required. A `PENDING` order changes to `CANCELLED` and atomically releases its quantity from `reservedStock` back to `availableStock`. Repeating the request returns the existing `CANCELLED` order and does not release inventory again. A paid order cannot be cancelled and returns HTTP `409` with code `40902`.
