<template>
  <CrudLayout :title="t('algorithm.execute.title')" icon="mdi-play">
    <v-snackbar
      v-model="algorithmNotification.flag"
      :color="algorithmNotification.color"
      :timeout="algorithmNotification.timeout"
      location="top"
    >
      <v-icon class="mr-2">{{ algorithmNotification.icon }}</v-icon>
      <strong>{{ algorithmNotification.title }}</strong>
      <p class="mb-0">{{ algorithmNotification.msg }}</p>
    </v-snackbar>

    <div class="pa-4">
      <div v-if="loading" class="algorithm-loader-overlay">
        <div class="algorithm-loader-card">
          <v-progress-circular indeterminate color="primary" size="56" width="5" />
          <p class="text-body-1 font-weight-medium mt-4 mb-0">
            {{ t('algorithm.execute.executing') }}
          </p>
        </div>
      </div>

      <v-stepper
        v-model="currentStep"
        :items="stepperItems"
        class="execute-algorithm-stepper elevation-0"
        flat
        hide-actions
      >
        <template v-slot:default>
          <v-stepper-window>
            
            <v-stepper-window-item :value="1">
              <Step1Content @next="currentStep = 2" />
            </v-stepper-window-item>

            <v-stepper-window-item :value="2">
              <Step2Content @back="currentStep = 1" @next="currentStep = 3" />
            </v-stepper-window-item>

            <v-stepper-window-item :value="3">
              <Step3Content :loading="loading" @back="currentStep = 2" @execute="handleExecuteAlgorithm" />
            </v-stepper-window-item>

          </v-stepper-window>
        </template>
      </v-stepper>
    </div>
  </CrudLayout>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia';
import { onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import CrudLayout from '../../components/common/CrudLayout.vue';
import { useAlgorithmExecution } from '../../composables/useAlgorithmExecution';
import router from '../../router/router';
import { useAlgorithmStore } from '../../stores/algorithm-store';
import Step1Content from './Step1Content.vue';
import Step2Content from './Step2Content.vue';
import Step3Content from './Step3Content.vue';

const { t } = useI18n();
const algorithmStore = useAlgorithmStore();
const { algorithmNotification, loading } = storeToRefs(algorithmStore);

const {
  currentStep,
  stepperItems,
  initializeData,
  executeAlgorithm,
} = useAlgorithmExecution();

onMounted(() => {
  initializeData();
});

const handleExecuteAlgorithm = async () => {
  const result = await executeAlgorithm();

  if (result?.status === 'success' && result.infrastructurePlanId) {
    void router.push({
      name: 'ShowInfrastructurePlan',
      params: { id: result.infrastructurePlanId },
    });
  }
};
</script>

<style scoped>
.algorithm-loader-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.72);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(2px);
}

.algorithm-loader-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.12);
  padding: 24px 32px;
  text-align: center;
  min-width: 240px;
}

/* Stepper header: sin sombra duplicada y líneas de unión más claras */
.execute-algorithm-stepper :deep(.v-stepper-header) {
  box-shadow: none;
  background: color-mix(in srgb, rgb(var(--v-theme-surface)) 92%, rgb(var(--v-theme-primary)) 8%);
  border-radius: 12px;
  padding-block: 8px 12px;
  padding-inline: 4px;
  margin-bottom: 4px;
}

.execute-algorithm-stepper :deep(.v-stepper-header .v-divider) {
  align-self: center;
  flex: 1 1 auto;
  height: 4px;
  min-height: 4px;
  max-height: 4px;
  margin-block: 0;
  margin-inline: 4px;
  border: none;
  opacity: 1;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, rgb(var(--v-theme-primary)) 18%, transparent),
    color-mix(in srgb, rgb(var(--v-theme-primary)) 55%, transparent),
    color-mix(in srgb, rgb(var(--v-theme-primary)) 18%, transparent)
  );
  box-shadow: 0 1px 2px color-mix(in srgb, rgb(var(--v-theme-primary)) 12%, transparent);
}

.execute-algorithm-stepper :deep(.v-stepper-window) {
  margin-top: 0.5rem;
}
</style>
