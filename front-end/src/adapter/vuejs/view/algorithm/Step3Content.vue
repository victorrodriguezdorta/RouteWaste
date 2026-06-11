<template>
  <v-card flat>
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

      <!-- Collection Start Time and Average Transfer Time -->
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 32px;">
        <!-- Collection Start Time -->
        <div>
          <label class="text-body2 font-weight-bold d-block mb-2">
            {{ t('algorithm.execute.step3.collectionStartTime') }}
            <span class="text-error">*</span>
          </label>
          <v-text-field
            v-model="collectionStartTime"
            type="time"
            variant="outlined"
            density="compact"
            hide-details="auto"
            :error="!isCollectionStartTimeValid"
          />
          <small v-if="!isCollectionStartTimeValid" class="text-error">
            {{ t('algorithm.execute.step3.collectionStartTimeError') }}
          </small>
        </div>

        <!-- Average Transfer Time -->
        <div>
          <label class="text-body2 font-weight-bold d-block mb-2">
            {{ t('algorithm.execute.step3.averageTransferTime') }}
            <span class="text-error">*</span>
          </label>
          <v-text-field
            v-model.number="averageTransferTime"
            type="number"
            :placeholder="t('algorithm.execute.step3.averageTransferTimePlaceholder')"
            min="0"
            max="1440"
            variant="outlined"
            density="compact"
            hide-details="auto"
            :error="averageTransferTime < 0"
          />
          <small v-if="averageTransferTime < 0" class="text-error">
            {{ t('algorithm.execute.step3.averageTransferTimeError') }}
          </small>
        </div>
      </div>

      <!-- Greedy Scoring Weights -->
      <div style="margin-bottom: 32px;">
        <label class="text-body2 font-weight-bold d-block mb-2">
          {{ t('algorithm.execute.step3.greedyWeights') }}
          <span class="text-error">*</span>
        </label>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
          <!-- Distance Weight -->
          <div>
            <span class="text-caption d-block mb-1">{{ t('algorithm.execute.step3.distanceWeight') }}</span>
            <v-text-field
              v-model.number="distanceWeight"
              type="number"
              min="0"
              max="1"
              step="0.05"
              variant="outlined"
              density="compact"
              hide-details="auto"
              :error="!areGreedyWeightsValid"
            />
          </div>
          <!-- Fill Weight -->
          <div>
            <span class="text-caption d-block mb-1">{{ t('algorithm.execute.step3.fillWeight') }}</span>
            <v-text-field
              v-model.number="fillWeight"
              type="number"
              min="0"
              max="1"
              step="0.05"
              variant="outlined"
              density="compact"
              hide-details="auto"
              :error="!areGreedyWeightsValid"
            />
          </div>
        </div>
        <small v-if="!areGreedyWeightsValid" class="text-error">
          {{ t('algorithm.execute.step3.greedyWeightsError') }}
        </small>
        <small v-else class="text-medium-emphasis">
          {{ t('algorithm.execute.step3.greedyWeightsHint') }}
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
              <p>
                <strong>{{ t('algorithm.execute.step3.collectionStartTime') }}:</strong> {{ collectionStartTime }}
              </p>
              <p>
                <strong>{{ t('algorithm.execute.step3.averageTransferTime') }}:</strong> {{ averageTransferTime }}
              </p>
              <p>
                <strong>{{ t('algorithm.execute.step3.greedyWeights') }}:</strong>
                {{ t('algorithm.execute.step3.distanceWeight') }} {{ distanceWeight }} /
                {{ t('algorithm.execute.step3.fillWeight') }} {{ fillWeight }}
              </p>
            </div>
          </v-card-text>
        </v-card>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useAlgorithmExecution } from '@/adapter/vuejs/composables/use-algorithm-execution';

const { t } = useI18n();

defineProps<{
  loading?: boolean;
}>();

const {
  // Computed - Step 1
  totalSelectedVehicles,
  
  // Computed - Step 3
  numberOfDaysRef,
  averagePickupTimeMinutesRef,
  maxBudgetAmountRef,
  collectionStartTimeRef,
  averageTransferTimeMinutesRef,
  distanceWeightRef,
  fillWeightRef,
  isCollectionStartTimeValid,
  areGreedyWeightsValid,

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

const collectionStartTime = computed({
  get: () => collectionStartTimeRef.value,
  set: (value: string) => {
    collectionStartTimeRef.value = value;
  }
});

const averageTransferTime = computed({
  get: () => averageTransferTimeMinutesRef.value,
  set: (value: number) => {
    averageTransferTimeMinutesRef.value = value;
  }
});

const distanceWeight = computed({
  get: () => distanceWeightRef.value,
  set: (value: number) => {
    distanceWeightRef.value = value;
  }
});

const fillWeight = computed({
  get: () => fillWeightRef.value,
  set: (value: number) => {
    fillWeightRef.value = value;
  }
});

const facilitiesWithVehicles = computed(() => algorithmStore.facilitiesWithVehicles);
const containersSelected = computed(() => algorithmStore.selectedContainerIds);
const totalVehicles = computed(() => totalSelectedVehicles);
</script>
