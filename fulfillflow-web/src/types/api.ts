export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface Inventory {
  skuId: number;
  skuCode: string;
  name: string;
  unitPrice: number;
  availableStock: number;
  reservedStock: number;
  version: number;
  updatedAt: string;
}

export type OrderStatus = "PENDING" | "PAID" | "CANCELLED";

export interface Order {
  orderId: number;
  orderNo: string;
  skuId: number;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
}

export interface RequestLogItem {
  id: number;
  at: string;
  method: string;
  url: string;
  status: number | "网络错误";
  outcome: "success" | "error";
  message: string;
}
