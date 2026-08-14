<script setup lang="ts">
import { ListOrdered } from "lucide-vue-next";
import type { Order } from "../types/api";

defineProps<{ orders: Order[] }>();
const emit = defineEmits<{ cancel: [orderId: number] }>();
</script>

<template>
  <section class="history-panel">
    <div class="panel-heading">
      <div>
        <p class="eyebrow">本次会话</p>
        <h2>已操作订单</h2>
      </div>
      <ListOrdered :size="20" aria-hidden="true" />
    </div>
    <div v-if="orders.length" class="order-list">
      <article v-for="order in orders" :key="order.orderId" class="order-row">
        <div>
          <strong>{{ order.orderNo }}</strong>
          <span>订单 ID {{ order.orderId }} · SKU {{ order.skuId }} · {{ order.quantity }} 件</span>
        </div>
        <div class="order-row__right">
          <b>¥ {{ Number(order.totalAmount).toFixed(2) }}</b>
          <span class="status-badge" :class="`status-badge--${order.status.toLowerCase()}`">{{ order.status }}</span>
          <button v-if="order.status === 'PENDING'" class="text-button" type="button" @click="emit('cancel', order.orderId)">取消</button>
        </div>
      </article>
    </div>
    <div v-else class="empty-state">本次会话创建或取消的订单会显示在这里。</div>
  </section>
</template>
