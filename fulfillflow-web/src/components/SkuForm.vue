<script setup lang="ts">
import { reactive, ref } from "vue";
import { PackagePlus, RotateCcw } from "lucide-vue-next";
import { createSku } from "../api/inventory";
import { readableError } from "../api/http";
import type { Inventory } from "../types/api";
import StatusNotice from "./StatusNotice.vue";

const emit = defineEmits<{ created: [inventory: Inventory] }>();

const form = reactive({
  skuCode: "DEMO-TEA-100",
  name: "演示茶包 100g",
  unitPrice: 12.5,
  initialStock: 5
});
const busy = ref(false);
const notice = ref<{ tone: "success" | "error"; message: string }>();

async function submit() {
  busy.value = true;
  notice.value = undefined;
  try {
    const inventory = await createSku({ ...form });
    emit("created", inventory);
    notice.value = { tone: "success", message: `SKU 创建成功，编号为 ${inventory.skuId}` };
  } catch (error) {
    notice.value = { tone: "error", message: readableError(error) };
  } finally {
    busy.value = false;
  }
}

function reset() {
  Object.assign(form, { skuCode: "DEMO-TEA-100", name: "演示茶包 100g", unitPrice: 12.5, initialStock: 5 });
  notice.value = undefined;
}
</script>

<template>
  <section class="tool-panel">
    <div class="panel-heading">
      <div>
        <p class="eyebrow">SKU 建档</p>
        <h2>创建 SKU 与初始库存</h2>
      </div>
      <PackagePlus :size="20" aria-hidden="true" />
    </div>

    <form class="form-grid" @submit.prevent="submit">
      <label>
        <span>SKU 编码</span>
        <input v-model.trim="form.skuCode" required maxlength="64" placeholder="例如：TEA-100G" />
      </label>
      <label>
        <span>商品名称</span>
        <input v-model.trim="form.name" required maxlength="200" placeholder="例如：茶包 100g" />
      </label>
      <label>
        <span>单价</span>
        <input v-model.number="form.unitPrice" required min="0.01" step="0.01" type="number" />
      </label>
      <label>
        <span>初始库存</span>
        <input v-model.number="form.initialStock" required min="0" step="1" type="number" />
      </label>
      <div class="form-actions form-actions--full">
        <button class="button button--primary" :disabled="busy" type="submit">
          <PackagePlus :size="16" aria-hidden="true" />
          {{ busy ? "创建中" : "创建 SKU" }}
        </button>
        <button class="icon-button" title="重置表单" type="button" @click="reset">
          <RotateCcw :size="17" aria-hidden="true" />
        </button>
      </div>
    </form>
    <StatusNotice v-if="notice" v-bind="notice" />
  </section>
</template>
