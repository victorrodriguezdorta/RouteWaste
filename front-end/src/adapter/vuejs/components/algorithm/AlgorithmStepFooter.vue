<template>
  <div class="algorithm-step-footer">
    <div class="algorithm-step-footer__left">
      <v-btn
        v-if="showBack"
        variant="outlined"
        prepend-icon="mdi-chevron-left"
        :disabled="backDisabled"
        @click="$emit('back')"
      >
        {{ t('common.buttons.back') }}
      </v-btn>
      <slot name="left-actions" />
    </div>

    <h2 class="algorithm-step-footer__title">{{ stepTitle }}</h2>

    <div class="algorithm-step-footer__right">
      <slot name="right-actions">
        <v-btn
          v-if="isLastStep"
          variant="elevated"
          color="success"
          :loading="loading"
          :disabled="!canProceed || loading"
          @click="$emit('execute')"
        >
          {{ t('algorithm.list.runButton') }}
        </v-btn>
        <v-btn
          v-else
          variant="elevated"
          color="primary"
          append-icon="mdi-chevron-right"
          :disabled="!canProceed"
          @click="$emit('next')"
        >
          {{ t('common.buttons.next') }}
        </v-btn>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';

defineProps<{
  stepTitle: string;
  showBack?: boolean;
  backDisabled?: boolean;
  isLastStep?: boolean;
  canProceed?: boolean;
  loading?: boolean;
}>();

defineEmits<{
  back: [];
  next: [];
  execute: [];
}>();

const { t } = useI18n();
</script>

<style scoped>
.algorithm-step-footer {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
  margin-bottom: 8px;
  padding: 4px 0 12px;
  border-bottom: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}

.algorithm-step-footer__left {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-self: start;
}

.algorithm-step-footer__title {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  line-height: 1.3;
  text-align: center;
  color: rgb(var(--v-theme-on-surface));
  justify-self: center;
}

.algorithm-step-footer__right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  justify-self: end;
}

@media (max-width: 768px) {
  .algorithm-step-footer {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
  }

  .algorithm-step-footer__left,
  .algorithm-step-footer__right {
    justify-self: stretch;
    justify-content: center;
  }

  .algorithm-step-footer__title {
    order: -1;
  }
}
</style>
