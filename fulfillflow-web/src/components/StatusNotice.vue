<script setup lang="ts">
defineProps<{
  tone: "success" | "error" | "info";
  message: string;
}>();
</script>

<template>
  <p class="notice" :class="`notice--${tone}`">{{ message }}</p>
</template>
