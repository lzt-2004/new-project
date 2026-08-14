import client, { unwrap } from "./http";
import type { Inventory } from "../types/api";

export interface CreateSkuInput {
  skuCode: string;
  name: string;
  unitPrice: number;
  initialStock: number;
}

export function createSku(input: CreateSkuInput) {
  return unwrap(client.post<import("../types/api").ApiResponse<Inventory>>("/skus", input));
}

export function getInventory(skuId: number) {
  return unwrap(client.get<import("../types/api").ApiResponse<Inventory>>(`/skus/${skuId}/inventory`));
}

export function replenishInventory(skuId: number, quantity: number) {
  return unwrap(client.post<import("../types/api").ApiResponse<Inventory>>(
    `/skus/${skuId}/inventory/replenishments`,
    { quantity }
  ));
}
