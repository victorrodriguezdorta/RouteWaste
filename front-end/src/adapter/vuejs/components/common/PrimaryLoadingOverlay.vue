<template>
  <div class="primary-loading-overlay" role="status" aria-live="polite">
    <div class="primary-loading-card">
      <v-progress-circular
        indeterminate
        color="primary"
        size="56"
        width="5"
        class="primary-loading-spinner"
      />
      <p v-if="message" class="primary-loading-message">
        {{ message }}
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  message?: string;
}>();
</script>

<style scoped>
.primary-loading-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  background: rgba(var(--v-theme-surface), 0.72);
  backdrop-filter: blur(2px);
}

.primary-loading-card {
  width: 220px;
  height: 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 24px;
  background: #ffffff;
  box-shadow: 0 16px 40px rgba(var(--v-theme-neutral-base), 0.12);
}

.primary-loading-spinner {
  flex-shrink: 0;
}

.primary-loading-message {
  margin: 16px 0 0;
  padding: 0;
  font-size: 0.9375rem;
  font-weight: 500;
  line-height: 1.4;
  color: rgb(var(--v-theme-primary));
}
</style>
