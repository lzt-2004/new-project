import axios, { type AxiosError, type AxiosResponse } from "axios";
import { ref } from "vue";
import type { ApiResponse, RequestLogItem } from "../types/api";

export const requestLogs = ref<RequestLogItem[]>([]);

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "/api",
  timeout: 10_000
});

function addLog(entry: Omit<RequestLogItem, "id" | "at">) {
  requestLogs.value.unshift({
    id: Date.now() + Math.random(),
    at: new Date().toLocaleTimeString("zh-CN", { hour12: false }),
    ...entry
  });
  requestLogs.value = requestLogs.value.slice(0, 20);
}

client.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<unknown>>) => {
    addLog({
      method: response.config.method?.toUpperCase() ?? "GET",
      url: response.config.url ?? "",
      status: response.status,
      outcome: "success",
      message: response.data.message
    });
    return response;
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    const response = error.response;
    const payload = response?.data;
    addLog({
      method: error.config?.method?.toUpperCase() ?? "GET",
      url: error.config?.url ?? "",
      status: response?.status ?? "网络错误",
      outcome: "error",
      message: payload?.message ?? error.message ?? "请求失败"
    });
    return Promise.reject(error);
  }
);

export async function unwrap<T>(request: Promise<AxiosResponse<ApiResponse<T>>>) {
  const response = await request;
  if (response.data.code !== 0) {
    throw new Error(response.data.message);
  }
  return response.data.data;
}

export function readableError(error: unknown) {
  if (axios.isAxiosError<ApiResponse<unknown>>(error)) {
    return error.response?.data?.message ?? error.message;
  }
  return error instanceof Error ? error.message : "未知错误";
}

export function clearRequestLogs() {
  requestLogs.value = [];
}

export default client;
