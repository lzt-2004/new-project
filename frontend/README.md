# FulfillFlow Admin UI

Vue 3 management UI for the FulfillFlow inventory and order APIs. It is intentionally limited to the current backend API surface:

- Create SKU with initial stock
- Query inventory and replenish available stock
- Create an order and inspect its price snapshot
- Cancel a pending order
- Inspect request results in the browser session

## Prerequisites

- Node.js 20 or later
- FulfillFlow backend running on `http://localhost:8081`

## Development

```powershell
cd D:\project-zz\fulfillflow\frontend
npm.cmd install
npm.cmd run dev
```

Open `http://localhost:5173`. The Vite development proxy forwards `/api` and `/actuator` requests to port `8081`, so no backend CORS configuration is needed for local development.

## Build

```powershell
npm.cmd run build
```

The static build is written to `frontend/dist/`. It is ignored by Git and will later be served by Nginx in the deployment task.

## Manual integration path

1. Create a SKU with initial stock `5`.
2. Query the generated SKU ID and confirm available stock is `5`.
3. Create an order with quantity `3`; available stock becomes `2` and reserved stock becomes `3`.
4. Cancel that order; available stock returns to `5` and reserved stock returns to `0`.
5. Repeat the cancellation. The status remains `CANCELLED` and inventory does not change again.
