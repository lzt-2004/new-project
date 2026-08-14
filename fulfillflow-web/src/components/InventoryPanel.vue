<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { Boxes, RefreshCw, Search, Truck } from "lucide-vue-next";
import { getInventory, replenishInventory } from "../api/inventory";
import { readableError } from "../api/http";
import type { Inventory } from "../types/api";
import StatusNotice from "./StatusNotice.vue";

const props = defineProps<{ inventory: Inventory | undefined }>();
const emit = defineEmits<{ updated: [inventory: Inventory] }>();

const skuId = ref<number | undefined>(props.inventory?.skuId);
const replenishQuantity = ref(10);
const busy = ref(false);
const replenishing = ref(false);
const notice = ref<{ tone: "success" | "error" | "info"; message: string }>();

const inventory = computed(() => props.inventory);

watch(
  () => props.inventory?.skuId,
  (id) => {
    if (id) {
      skuId.value = id;
    }
  },
  { immediate: true }
);

async function queryInventory() {
  if (!skuId.value || skuId.value < 1) {
    notice.value = { tone: "error", message: "请输入有效的 SKU ID" };
    return;
  }
  busy.value = true;
  notice.value = undefined;
  try {
    const result = await getInventory(skuId.value);
    emit("updated", result);
    notice.value = { tone: "success", message: "库存已刷新" };
  } catch (error) {
    notice.value = { tone: "error", message: readableError(error) };
  } finally {
    busy.value = false;
  }
}

async function replenish() {
  if (!skuId.value || replenishQuantity.value < 1) {
    notice.value = { tone: "error", message: "SKU ID 和补货数量必须为正数" };
    return;
  }
  replenishing.value = true;
  notice.value = undefined;
  try {
    const result = await replenishInventory(skuId.value, replenishQuantity.value);
    emit("updated", result);
    notice.value = { tone: "success", message: `已补货 ${replenishQuantity.value} 件` };
  } catch (error) {
    notice.value = { tone: "error", message: readableError(error) };
  } finally {
    replenishing.value = false;
  }
}

function useSku(id: number) {
  skuId.value = id;
  void queryInventory();
}

defineExpose({ useSku });
</script>

<template>
  <section class="tool-panel">
    <div class="panel-heading">
      <div>
        <p class="eyebrow">库存中心</p>
        <h2>查询与补货</h2>
      </div>
      <Boxes :size="20" aria-hidden="true" />
    </div>

    <div class="compact-row">
      <label class="compact-input">
        <span>SKU ID</span>
        <input v-model.number="skuId" min="1" placeholder="例如：1" type="number" @keyup.enter="queryInventory" />
      </label>
      <button class="button button--secondary" :disabled="busy" type="button" @click="queryInventory">
        <Search :size="16" aria-hidden="true" />
        {{ busy ? "查询中" : "查询库存" }}
      </button>
    </div>

    <div v-if="inventory" class="inventory-summary">
      <div class="sku-summary">
        <div>
          <strong>{{ inventory.name }}</strong>
          <span>{{ inventory.skuCode }} · ID {{ inventory.skuId }}</span>
        </div>
        <b>¥ {{ Number(inventory.unitPrice).toFixed(2) }}</b>
      </div>
      <div class="metric-grid">
        <div class="metric metric--available">
          <span>可售库存</span>
          <strong>{{ inventory.availableStock }}</strong>
        </div>
        <div class="metric metric--reserved">
          <span>预占库存</span>
          <strong>{{ inventory.reservedStock }}</strong>
        </div>
        <div class="metric">
          <span>版本号</span>
          <strong>{{ inventory.version }}</strong>
        </div>
      </div>
      <p class="muted">更新时间：{{ new Date(inventory.updatedAt).toLocaleString("zh-CN", { hour12: false }) }}</p>
    </div>
    <div v-else class="empty-state">输入 SKU ID 查询当前库存。</div>

    <form class="replenish-bar" @submit.prevent="replenish">
      <label class="compact-input">
        <span>补货数量</span>
        <input v-model.number="replenishQuantity" min="1" step="1" type="number" />
      </label>
      <button class="button button--primary" :disabled="replenishing" type="submit">
        <Truck :size="16" aria-hidden="true" />
        {{ replenishing ? "处理中" : "提交补货" }}
      </button>
      <button v-if="inventory" class="icon-button" title="刷新当前库存" type="button" @click="queryInventory">
        <RefreshCw :size="17" aria-hidden="true" />
      </button>
    </form>
    <StatusNotice v-if="notice" v-bind="notice" />
  </section>
</template>
