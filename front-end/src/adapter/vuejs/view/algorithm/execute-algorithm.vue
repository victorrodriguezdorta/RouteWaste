<template>
  <CrudLayout :title="t('algorithm.execute.title')" icon="mdi-play">
    <div class="pa-4">
      <v-stepper 
        v-model="currentStep"
        :items="stepperItems"
        class="elevation-0"
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
              <Step3Content @back="currentStep = 2" @execute="executeAlgorithm" />
            </v-stepper-window-item>

          </v-stepper-window>
        </template>
      </v-stepper>
    </div>
  </CrudLayout>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import CrudLayout from '../../components/common/CrudLayout.vue';
import { useAlgorithmExecution } from '../../composables/useAlgorithmExecution';
import Step1Content from './Step1Content.vue';
import Step2Content from './Step2Content.vue';
import Step3Content from './Step3Content.vue';

const { t } = useI18n();

const {
  currentStep,
  stepperItems,
  initializeData,
  executeAlgorithm,
} = useAlgorithmExecution();

onMounted(() => {
  initializeData();
});
</script>