<template>
  <div class="algorithm-step-indicator" role="navigation" :aria-label="t('algorithm.execute.stepIndicatorAria')">
    <div class="algorithm-step-indicator__header">
      <v-icon v-if="icon" size="40" class="algorithm-step-indicator__icon" color="primary">
        {{ icon }}
      </v-icon>
      <h1 class="algorithm-step-indicator__title">{{ title }}</h1>
    </div>

    <div class="algorithm-step-indicator__steps">
      <template v-for="step in totalSteps" :key="step">
        <div
          class="algorithm-step-indicator__step"
          :class="{ 'algorithm-step-indicator__step--active': step === currentStep }"
          :aria-current="step === currentStep ? 'step' : undefined"
        >
          <span class="algorithm-step-indicator__number">{{ step }}</span>
        </div>
        <div
          v-if="step < totalSteps"
          class="algorithm-step-indicator__line"
          :class="{ 'algorithm-step-indicator__line--completed': step < currentStep }"
          aria-hidden="true"
        />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';

defineProps<{
  title: string;
  icon?: string;
  currentStep: number;
  totalSteps: number;
}>();

const { t } = useI18n();
</script>

<style scoped>
.algorithm-step-indicator {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 12px;
  padding: 0 0 8px;
}

.algorithm-step-indicator__header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.algorithm-step-indicator__title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1.2;
  color: rgb(var(--v-theme-primary));
}

.algorithm-step-indicator__steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
}

.algorithm-step-indicator__step {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
}

.algorithm-step-indicator__number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  font-size: 0.9375rem;
  font-weight: 600;
  color: rgba(var(--v-theme-on-surface), 0.55);
  transition: background-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

.algorithm-step-indicator__step--active .algorithm-step-indicator__number {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgb(var(--v-theme-primary));
  color: rgb(var(--v-theme-on-primary));
  box-shadow: 0 2px 8px color-mix(in srgb, rgb(var(--v-theme-primary)) 35%, transparent);
}

.algorithm-step-indicator__line {
  flex: 1 1 72px;
  max-width: 120px;
  height: 3px;
  margin-inline: 8px;
  border-radius: 999px;
  background: color-mix(in srgb, rgb(var(--v-theme-on-surface)) 18%, transparent);
}

.algorithm-step-indicator__line--completed {
  background: linear-gradient(
    90deg,
    color-mix(in srgb, rgb(var(--v-theme-primary)) 18%, transparent),
    color-mix(in srgb, rgb(var(--v-theme-primary)) 55%, transparent),
    color-mix(in srgb, rgb(var(--v-theme-primary)) 18%, transparent)
  );
}
</style>
