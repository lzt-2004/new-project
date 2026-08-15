import client, { unwrap } from "./http";
import type { ApiResponse, Order } from "../types/api";

export function createOrder(skuId: number, quantity: number) {
  return unwrap(client.post<ApiResponse<Order>>("/orders", { skuId, quantity }));
}

export function cancelOrder(orderId: number) {
  return unwrap(client.post<ApiResponse<Order>>(`/orders/${orderId}/cancellations`));
}

export function payOrder(orderId: number) {
  return unwrap(client.post<ApiResponse<Order>>(`/orders/${orderId}/payments`));
}
