# API notes

## GET /api/system/ping

A smoke-test endpoint used by the initial MockMvc test.

Success response:

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "service": "fulfillflow",
    "timestamp": "2026-08-13T00:00:00+08:00"
  }
}
```
