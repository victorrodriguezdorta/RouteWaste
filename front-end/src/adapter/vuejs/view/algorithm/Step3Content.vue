<template>
  <v-card flat class="mt-4">
    <v-card-actions>
      <v-btn variant="outlined" prepend-icon="mdi-chevron-left" :disabled="loading" @click="emit('back')">
        {{ t('common.buttons.back') }}
      </v-btn>
      <v-spacer />
      <v-btn
        variant="elevated"
        color="success"
        :loading="loading"
        @click="emit('execute')"
        :disabled="!isStep3Valid || loading"
      >
        {{ t('algorithm.list.runButton') }}
      </v-btn>
    </v-card-actions>

    <v-card-text>
      <!-- Input Fields -->
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 32px;">
        <!-- Number of Days -->
        <div>
          <label class="text-body2 font-weight-bold d-block mb-2">
            {{ t('algorithm.execute.step3.numberOfDays') }}
            <span class="text-error">*</span>
          </label>
          <v-text-field
            v-model.number="numberOfDays"
            type="number"
            :placeholder="t('algorithm.execute.step3.numberOfDaysPlaceholder')"
            min="1"
            max="365"
            variant="outlined"
            density="compact"
            hide-details="auto"
            :error="numberOfDays <= 0"
          />
          <small v-if="numberOfDays <= 0" class="text-error">
            {{ t('algorithm.execute.step3.numberOfDaysError') }}
          </small>
        </div>

        <!-- Average Pickup Time -->
        <div>
          <label class="text-body2 font-weight-bold d-block mb-2">
            {{ t('algorithm.execute.step3.averagePickupTime') }}
            <span class="text-error">*</span>
          </label>
          <v-text-field
            v-model.number="averagePickupTime"
            type="number"
            :placeholder="t('algorithm.execute.step3.averagePickupTimePlaceholder')"
            min="1"
            max="1440"
            variant="outlined"
            density="compact"
            hide-details="auto"
            :error="averagePickupTime <= 0"
          />
          <small v-if="averagePickupTime <= 0" class="text-error">
            {{ t('algorithm.execute.step3.averagePickupTimeError') }}
          </small>
        </div>
      </div>

      <!-- Max Budget -->
      <div style="margin-bottom: 32px;">
        <label class="text-body2 font-weight-bold d-block mb-2">
          {{ t('algorithm.execute.step3.maxBudget') }}
          <span class="text-error">*</span>
        </label>
        <v-text-field
          v-model.number="maxBudgetAmount"
          type="number"
          :placeholder="t('algorithm.execute.step3.maxBudgetPlaceholder')"
          min="0"
          variant="outlined"
          density="compact"
          hide-details="auto"
          :error="maxBudgetAmount < 0"
        />
        <small v-if="maxBudgetAmount < 0" class="text-error">
          {{ t('algorithm.execute.step3.maxBudgetError') }}
        </small>
      </div>

      <!-- Summary Section -->
      <div class="mt-6 pt-4 border-t">
        <p class="text-h6 font-weight-bold mb-4">{{ t('algorithm.execute.step3.finalSummary') }}</p>

        <!-- Step 1 Summary -->
        <v-card variant="outlined" class="mb-4">
          <v-card-title class="text-subtitle2">{{ t('algorithm.execute.step1.label') }}</v-card-title>
          <v-card-text>
            <div class="text-caption">
              <p v-if="facilitiesWithVehicles.length > 0">
                <strong>{{ facilitiesWithVehicles.length }}</strong> {{ t('algorithm.execute.step3.facilitiesSelected') }}
              </p>
              <p v-else class="text-error">{{ t('algorithm.execute.step3.noFacilitiesSelected') }}</p>
              <p v-if="totalVehicles.value > 0">
                <strong>{{ totalVehicles.value }}</strong> {{ t('algorithm.execute.step3.vehiclesSelected') }}
              </p>
            </div>
          </v-card-text>
        </v-card>

        <!-- Step 2 Summary -->
        <v-card variant="outlined" class="mb-4">
          <v-card-title class="text-subtitle2">{{ t('algorithm.execute.step2.label') }}</v-card-title>
          <v-card-text>
            <div class="text-caption">
              <p v-if="containersSelected.length > 0">
                <strong>{{ containersSelected.length }}</strong> {{ t('algorithm.execute.step3.containersSelected') }}
              </p>
              <p v-else class="text-error">{{ t('algorithm.execute.step3.noContainersSelected') }}</p>
            </div>
          </v-card-text>
        </v-card>

        <!-- Step 3 Summary -->
        <v-card variant="outlined" class="mb-4">
          <v-card-title class="text-subtitle2">{{ t('algorithm.execute.step3.label') }}</v-card-title>
          <v-card-text>
            <div class="text-caption">
              <p>
                <strong>{{ t('algorithm.execute.step3.numberOfDays') }}:</strong> {{ numberOfDays }}
              </p>
              <p>
                <strong>{{ t('algorithm.execute.step3.averagePickupTime') }}:</strong> {{ averagePickupTime }}
              </p>
              <p>
                <strong>{{ t('algorithm.execute.step3.maxBudget') }}:</strong> {{ maxBudgetAmount }}
              </p>
            </div>
          </v-card-text>
        </v-card>

        <!-- Final JSON Preview -->
        <div class="mt-4">
          <p class="text-caption font-weight-bold mb-2">{{ t('algorithm.execute.step3.jsonPreview') }}</p>
          <v-card variant="outlined" class="bg-surface-soft">
            <v-card-text class="pa-3">
              <pre class="text-caption" style="overflow-x: auto; font-size: 11px; line-height: 1.4;">{{ formattedCommandJson }}</pre>
            </v-card-text>
          </v-card>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useAlgorithmExecution } from '@/adapter/vuejs/composables/use-algorithm-execution';

const { t } = useI18n();

const emit = defineEmits<{
  back: [];
  execute: [];
}>();

defineProps<{
  loading?: boolean;
}>();

const {
  // Computed - Step 1
  totalSelectedVehicles,
  
  // Computed - Step 3
  isStep3Valid,
  formattedCommandJson,
  numberOfDaysRef,
  averagePickupTimeMinutesRef,
  maxBudgetAmountRef,

  // Store reference
  algorithmStore,
} = useAlgorithmExecution();

// Computed properties
const numberOfDays = computed({
  get: () => numberOfDaysRef.value,
  set: (value: number) => {
    numberOfDaysRef.value = value;
  }
});

const averagePickupTime = computed({
  get: () => averagePickupTimeMinutesRef.value,
  set: (value: number) => {
    averagePickupTimeMinutesRef.value = value;
  }
});

const maxBudgetAmount = computed({
  get: () => maxBudgetAmountRef.value,
  set: (value: number) => {
    maxBudgetAmountRef.value = value;
  }
});

const facilitiesWithVehicles = computed(() => algorithmStore.facilitiesWithVehicles);
const containersSelected = computed(() => algorithmStore.selectedContainerIds);
const totalVehicles = computed(() => totalSelectedVehicles);
</script>
