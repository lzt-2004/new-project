<script setup lang="ts">
import { computed, ref } from "vue";
import { Activity, Boxes, CircleCheck, CircleX, Gauge, ListOrdered, PackageCheck, PanelLeftClose, PanelLeftOpen } from "lucide-vue-next";
import { cancelOrder, payOrder } from "./api/order";
import { getInventory } from "./api/inventory";
import { readableError } from "./api/http";
import InventoryPanel from "./components/InventoryPanel.vue";
import OrderHistory from "./components/OrderHistory.vue";
import OrderPanel from "./components/OrderPanel.vue";
import RequestLog from "./components/RequestLog.vue";
import SkuForm from "./components/SkuForm.vue";
import type { Inventory, Order } from "./types/api";

const activeView = ref<"inventory" | "orders" | "logs">("inventory");
const sidebarOpen = ref(true);
const inventory = ref<Inventory>();
const orders = ref<Order[]>([]);
const connectionState = ref<"checking" | "online" | "offline">("checking");
const pageNotice = ref<string>();

const navItems = [
  { id: "inventory", label: "库存中心", icon: Boxes },
  { id: "orders", label: "订单操作", icon: PackageCheck },
  { id: "logs", label: "接口日志", icon: ListOrdered }
] as const;

const connectionLabel = computed(() => ({
  checking: "检测后端中",
  online: "后端已连接",
  offline: "后端未连接"
}[connectionState.value]));

async function checkConnection() {
  connectionState.value = "checking";
  try {
    const response = await fetch("/actuator/health", { signal: AbortSignal.timeout(3_000) });
    connectionState.value = response.ok ? "online" : "offline";
  } catch {
    connectionState.value = "offline";
  }
}

async function refreshInventory(skuId: number) {
  try {
    inventory.value = await getInventory(skuId);
  } catch (error) {
    pageNotice.value = readableError(error);
  }
}

function handleSkuCreated(result: Inventory) {
  inventory.value = result;
  activeView.value = "inventory";
}

function upsertOrder(order: Order) {
  const index = orders.value.findIndex((item) => item.orderId === order.orderId);
  if (index === -1) {
    orders.value.unshift(order);
  } else {
    orders.value.splice(index, 1, order);
  }
}

async function cancelFromHistory(orderId: number) {
  pageNotice.value = undefined;
  try {
    const order = await cancelOrder(orderId);
    upsertOrder(order);
    await refreshInventory(order.skuId);
    activeView.value = "orders";
  } catch (error) {
    pageNotice.value = readableError(error);
  }
}

async function payFromHistory(orderId: number) {
  pageNotice.value = undefined;
  try {
    const order = await payOrder(orderId);
    upsertOrder(order);
    await refreshInventory(order.skuId);
    activeView.value = "orders";
  } catch (error) {
    pageNotice.value = readableError(error);
  }
}

void checkConnection();
</script>

<template>
  <main class="app-shell" :class="{ 'app-shell--collapsed': !sidebarOpen }">
    <aside class="sidebar">
      <div class="brand-lockup">
        <div class="brand-mark"><Gauge :size="21" aria-hidden="true" /></div>
        <div v-if="sidebarOpen">
          <strong>FulfillFlow</strong>
          <span>履约管理台</span>
        </div>
      </div>

      <nav class="sidebar-nav" aria-label="主导航">
        <button
          v-for="item in navItems"
          :key="item.id"
          class="nav-item"
          :class="{ 'nav-item--active': activeView === item.id }"
          :title="item.label"
          type="button"
          @click="activeView = item.id"
        >
          <component :is="item.icon" :size="19" aria-hidden="true" />
          <span v-if="sidebarOpen">{{ item.label }}</span>
        </button>
      </nav>

      <div class="sidebar-footer" v-if="sidebarOpen">
        <span>API 代理</span>
        <code>/api → :8081</code>
      </div>
    </aside>

    <section class="content-shell">
      <header class="topbar">
        <button class="icon-button" :title="sidebarOpen ? '收起导航' : '展开导航'" type="button" @click="sidebarOpen = !sidebarOpen">
          <PanelLeftClose v-if="sidebarOpen" :size="19" aria-hidden="true" />
          <PanelLeftOpen v-else :size="19" aria-hidden="true" />
        </button>
        <div class="topbar-title">
          <p>FulfillFlow / 业务操作</p>
          <h1>{{ activeView === "inventory" ? "库存中心" : activeView === "orders" ? "订单操作" : "接口日志" }}</h1>
        </div>
        <button class="connection" :class="`connection--${connectionState}`" type="button" @click="checkConnection">
          <Activity :size="15" aria-hidden="true" />
          {{ connectionLabel }}
        </button>
      </header>

      <div v-if="pageNotice" class="page-notice">
        <CircleX :size="17" aria-hidden="true" />
        <span>{{ pageNotice }}</span>
        <button type="button" @click="pageNotice = undefined">关闭</button>
      </div>

      <section v-show="activeView === 'inventory'" class="workspace workspace--inventory">
        <SkuForm @created="handleSkuCreated" />
        <InventoryPanel :inventory="inventory" @updated="inventory = $event" />
        <section class="info-strip">
          <CircleCheck :size="19" aria-hidden="true" />
          <div>
            <strong>库存模型</strong>
            <span>创建订单时，可售库存转为预占库存；支付只确认订单，取消才会释放库存。</span>
          </div>
        </section>
      </section>

      <section v-show="activeView === 'orders'" class="workspace workspace--orders">
        <OrderPanel :inventory="inventory" @order-created="upsertOrder" @inventory-refresh="refreshInventory" />
        <OrderHistory :orders="orders" @pay="payFromHistory" @cancel="cancelFromHistory" />
      </section>

      <section v-show="activeView === 'logs'" class="workspace workspace--logs">
        <RequestLog />
      </section>
    </section>
  </main>
</template>
