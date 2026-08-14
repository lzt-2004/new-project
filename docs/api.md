# API notes

## GET /api/system/ping

A smoke-test endpoint used by the initial MockMvc test.

## POST /api/skus

Creates a SKU and an inventory record.

```json
{
  "skuCode": "COFFEE-BEAN-1KG",
  "name": "Coffee bean 1kg",
  "unitPrice": 88.00,
  "initialStock": 5
}
```

## GET /api/skus/{skuId}/inventory

Returns SKU information with available and reserved inventory.

## POST /api/skus/{skuId}/inventory/replenishments

Increases available inventory only. `quantity` must be greater than zero.

```json
{
  "quantity": 10
}
```