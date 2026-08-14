<script setup lang="ts">
import { ClipboardList, Trash2 } from "lucide-vue-next";
import { clearRequestLogs, requestLogs } from "../api/http";
</script>

<template>
  <section class="log-panel">
    <div class="panel-heading">
      <div>
        <p class="eyebrow">请求记录</p>
        <h2>接口日志</h2>
      </div>
      <button class="icon-button" title="清空请求记录" type="button" @click="clearRequestLogs">
        <Trash2 :size="17" aria-hidden="true" />
      </button>
    </div>

    <div v-if="requestLogs.length" class="log-list">
      <article v-for="item in requestLogs" :key="item.id" class="log-entry" :class="`log-entry--${item.outcome}`">
        <div class="log-entry__top">
          <span class="log-method">{{ item.method }}</span>
          <strong>{{ item.status }}</strong>
          <time>{{ item.at }}</time>
        </div>
        <code>{{ item.url }}</code>
        <p>{{ item.message }}</p>
      </article>
    </div>
    <div v-else class="empty-state empty-state--log">
      <ClipboardList :size="21" aria-hidden="true" />
      <span>接口调用记录会显示在这里。</span>
    </div>
  </section>
</template>
