<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { Ban, ClipboardPlus, PackageCheck, XCircle } from "lucide-vue-next";
import { cancelOrder, createOrder } from "../api/order";
import { readableError } from "../api/http";
import type { Inventory, Order } from "../types/api";
import StatusNotice from "./StatusNotice.vue";

const props = defineProps<{ inventory: Inventory | undefined }>();
const emit = defineEmits<{ orderCreated: [order: Order]; inventoryRefresh: [skuId: number] }>();

const skuId = ref<number | undefined>(props.inventory?.skuId);
const quantity = ref(1);
const cancelOrderId = ref<number | undefined>();
const creating = ref(false);
const cancelling = ref(false);
const notice = ref<{ tone: "success" | "error" | "info"; message: string }>();

const selectedSkuId = computed(() => skuId.value ?? props.inventory?.skuId);

watch(
  () => props.inventory?.skuId,
  (id) => {
    if (id) {
      skuId.value = id;
    }
  },
  { immediate: true }
);

function useSku(id: number) {
  skuId.value = id;
}

defineExpose({ useSku });

async function submitOrder() {
  const id = selectedSkuId.value;
  if (!id || quantity.value < 1) {
    notice.value = { tone: "error", message: "SKU ID 和下单数量必须为正数" };
    return;
  }
  creating.value = true;
  notice.value = undefined;
  try {
    const order = await createOrder(id, quantity.value);
    cancelOrderId.value = order.orderId;
    emit("orderCreated", order);
    emit("inventoryRefresh", id);
    notice.value = { tone: "success", message: `订单 ${order.orderNo} 已创建并预占库存` };
  } catch (error) {
    notice.value = { tone: "error", message: readableError(error) };
  } finally {
    creating.value = false;
  }
}

async function submitCancellation() {
  if (!cancelOrderId.value || cancelOrderId.value < 1) {
    notice.value = { tone: "error", message: "请输入有效订单 ID" };
    return;
  }
  cancelling.value = true;
  notice.value = undefined;
  try {
    const order = await cancelOrder(cancelOrderId.value);
    emit("orderCreated", order);
    emit("inventoryRefresh", order.skuId);
    notice.value = { tone: "success", message: `订单 ${order.orderNo} 已取消，库存已释放` };
  } catch (error) {
    notice.value = { tone: "error", message: readableError(error) };
  } finally {
    cancelling.value = false;
  }
}
</script>

<template>
  <section class="tool-panel">
    <div class="panel-heading">
      <div>
        <p class="eyebrow">订单操作</p>
        <h2>创建与取消订单</h2>
      </div>
      <PackageCheck :size="20" aria-hidden="true" />
    </div>

    <form class="form-grid form-grid--order" @submit.prevent="submitOrder">
      <label>
        <span>SKU ID</span>
        <input v-model.number="skuId" :placeholder="selectedSkuId ? String(selectedSkuId) : '例如：1'" min="1" type="number" />
      </label>
      <label>
        <span>下单数量</span>
        <input v-model.number="quantity" min="1" step="1" type="number" />
      </label>
      <div class="form-actions form-actions--full">
        <button class="button button--primary" :disabled="creating" type="submit">
          <ClipboardPlus :size="16" aria-hidden="true" />
          {{ creating ? "创建中" : "创建订单" }}
        </button>
      </div>
    </form>

    <div class="divider"></div>

    <form class="compact-row" @submit.prevent="submitCancellation">
      <label class="compact-input">
        <span>订单 ID</span>
        <input v-model.number="cancelOrderId" min="1" placeholder="创建后自动回填" type="number" />
      </label>
      <button class="button button--danger" :disabled="cancelling" type="submit">
        <XCircle :size="16" aria-hidden="true" />
        {{ cancelling ? "取消中" : "取消订单" }}
      </button>
    </form>
    <p class="help-line"><Ban :size="14" aria-hidden="true" />重复取消同一订单不会重复释放库存。</p>
    <StatusNotice v-if="notice" v-bind="notice" />
  </section>
</template>
