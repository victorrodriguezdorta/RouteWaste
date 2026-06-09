<template>
  <CrudLayout :title="t('algorithm.execute.title')" hide-header>
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

    <div class="pa-4 execute-algorithm">
      <div v-if="loading" class="algorithm-loader-overlay">
        <div class="algorithm-loader-card">
          <v-progress-circular indeterminate color="primary" size="56" width="5" />
          <p class="text-body-1 font-weight-medium mt-4 mb-0">
            {{ t('algorithm.execute.executing') }}
          </p>
        </div>
      </div>

      <AlgorithmStepIndicator
        :title="t('algorithm.execute.title')"
        icon="mdi-play"
        :current-step="currentStep"
        :total-steps="3"
      />

      <AlgorithmStepFooter
        :step-title="currentStepTitle"
        :show-back="currentStep > 1"
        :back-disabled="loading"
        :is-last-step="currentStep === 3"
        :can-proceed="canProceedCurrentStep"
        :loading="loading"
        @back="goToPreviousStep"
        @next="goToNextStep"
        @execute="handleExecuteAlgorithm"
      >
        <template v-if="currentStep === 2" #left-actions>
          <v-btn
            variant="outlined"
            color="primary"
            :disabled="step2ContainerItems.length === 0"
            @click="toggleVisibleContainersSelection"
          >
            {{
              areAllVisibleContainersSelected
                ? t('common.buttons.deselectAll')
                : t('common.buttons.selectAll')
            }}
          </v-btn>
        </template>
      </AlgorithmStepFooter>

      <div class="execute-algorithm__content">
        <Step1Content v-if="currentStep === 1" />
        <Step2Content v-else-if="currentStep === 2" />
        <Step3Content v-else :loading="loading" />
      </div>
    </div>
  </CrudLayout>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import AlgorithmStepFooter from '../../components/algorithm/AlgorithmStepFooter.vue';
import AlgorithmStepIndicator from '../../components/algorithm/AlgorithmStepIndicator.vue';
import CrudLayout from '../../components/common/CrudLayout.vue';
import { useAlgorithmExecution } from '@/adapter/vuejs/composables/use-algorithm-execution';
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
  currentStepTitle,
  initializeData,
  executeAlgorithm,
  isStep1Valid,
  isStep2Valid,
  isStep3Valid,
  step2ContainerItems,
  areAllVisibleContainersSelected,
  toggleVisibleContainersSelection,
} = useAlgorithmExecution();

const canProceedCurrentStep = computed(() => {
  if (currentStep.value === 1) {
    return isStep1Valid.value;
  }
  if (currentStep.value === 2) {
    return isStep2Valid.value;
  }
  return isStep3Valid.value;
});

const goToPreviousStep = () => {
  if (currentStep.value > 1) {
    currentStep.value -= 1;
  }
};

const goToNextStep = () => {
  if (currentStep.value < 3 && canProceedCurrentStep.value) {
    currentStep.value += 1;
  }
};

onMounted(async () => {
  const pendingJson = algorithmStore.dequeuePendingExecutionRequestJson();
  algorithmStore.resetForm();
  await initializeData();
  if (pendingJson) {
    const applied = algorithmStore.applyExecutionRequestFromJson(pendingJson);
    if (applied) {
      algorithmStore.setNotification(
        t('infrastructurePlan.show.replayLoadedTitle'),
        t('infrastructurePlan.show.replayLoadedMessage'),
        'mdi-playlist-check',
        'success',
      );
    } else {
      algorithmStore.setNotification(
        t('infrastructurePlan.show.replayFailedTitle'),
        t('infrastructurePlan.show.replayFailedMessage'),
        'mdi-alert',
        'warning',
      );
    }
  }
});

const handleExecuteAlgorithm = async () => {
  const result = await executeAlgorithm();

  if (result?.status === 'accepted' && result.infrastructurePlanId) {
    algorithmStore.resetForm();
    void router.push({ name: 'Algorithm' });
    return;
  }

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
  background: rgba(var(--v-theme-surface), 0.72);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(2px);
}

.algorithm-loader-card {
  background: rgb(var(--v-theme-surface));
  border-radius: 16px;
  box-shadow: 0 16px 40px rgba(var(--v-theme-neutral-base), 0.12);
  padding: 24px 32px;
  text-align: center;
  min-width: 240px;
}

.execute-algorithm__content {
  margin-top: 0;
}
</style>
