<template>
  <v-card variant="outlined" class="mb-4">
    <v-card-title class="d-flex align-center justify-space-between ga-2 cursor-pointer" @click="isExpanded = !isExpanded">
      <div class="d-flex align-center ga-2">
        <v-icon icon="mdi-information-outline" color="primary" />
        <span>{{ t('infrastructurePlan.show.generalInfo.title') }}</span>
      </div>
      <v-icon :icon="isExpanded ? 'mdi-chevron-up' : 'mdi-chevron-down'" color="primary" />
    </v-card-title>

    <v-divider />

    <v-expand-transition>
      <div v-if="isExpanded" class="pa-4">
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              :model-value="plan?.id?.getValue() ?? '-'"
              :label="t('infrastructurePlan.show.generalInfo.fields.id')"
              prepend-icon="mdi-identifier"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              :model-value="formatDateTime(plan?.executedAt)"
              :label="t('infrastructurePlan.show.generalInfo.fields.executedAt')"
              prepend-icon="mdi-calendar-clock"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatNumber(generalInfo.totalCollectedKilograms, 'kg')"
              :label="t('infrastructurePlan.show.generalInfo.fields.totalCollectedKilograms')"
              prepend-icon="mdi-weight-kilogram"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatNumber(generalInfo.totalCollectedLiters, 'L')"
              :label="t('infrastructurePlan.show.generalInfo.fields.totalCollectedLiters')"
              prepend-icon="mdi-water"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatNumber(generalInfo.totalDistanceMeters, 'm')"
              :label="t('infrastructurePlan.show.generalInfo.fields.totalDistanceMeters')"
              prepend-icon="mdi-map-marker-distance"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatNumber(generalInfo.averagePickupTimeMinutes, 'min')"
              :label="t('infrastructurePlan.show.generalInfo.fields.averagePickupTimeMinutes')"
              prepend-icon="mdi-timer-outline"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatMoney(generalInfo.estimatedTotalCostAmount, generalInfo.estimatedTotalCostCurrency)"
              :label="t('infrastructurePlan.show.generalInfo.fields.estimatedTotalCost')"
              prepend-icon="mdi-cash"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatMoney(generalInfo.maxBudgetAmount, generalInfo.maxBudgetCurrency)"
              :label="t('infrastructurePlan.show.generalInfo.fields.maxBudget')"
              prepend-icon="mdi-cash-multiple"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="String(clusterCount)"
              :label="t('infrastructurePlan.show.generalInfo.fields.facilityCount')"
              prepend-icon="mdi-factory"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              :model-value="String(dailyPlanCount)"
              :label="t('infrastructurePlan.show.generalInfo.fields.dailyPlanCount')"
              prepend-icon="mdi-calendar-multiselect"
              variant="outlined"
              readonly
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              :model-value="String(dayCount)"
              :label="t('infrastructurePlan.show.generalInfo.fields.numberOfDays')"
              prepend-icon="mdi-calendar-range"
              variant="outlined"
              readonly
            />
          </v-col>
        </v-row>
      </div>
    </v-expand-transition>
  </v-card>
</template>

<script lang="ts" setup>
import type { InfrastructurePlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';

const props = defineProps<{
  plan?: InfrastructurePlanDetail;
}>();

const { t } = useI18n();
const isExpanded = ref(false);

const generalInfo = computed(() => {
  const plan = props.plan;

  return {
    totalCollectedKilograms: plan?.metrics.totalCollectedKilograms.getValue(),
    totalCollectedLiters: plan?.metrics.totalCollectedLiters.getValue(),
    totalDistanceMeters: plan?.metrics.totalDistanceMeters.getValue(),
    averagePickupTimeMinutes: plan?.metrics.averagePickupTimeMinutes ?? undefined,
    estimatedTotalCostAmount: plan?.metrics.estimatedTotalCost.getAmount(),
    estimatedTotalCostCurrency: plan?.metrics.estimatedTotalCost.getCurrency().getCode(),
    maxBudgetAmount: plan?.metrics.maxBudget.getAmount(),
    maxBudgetCurrency: plan?.metrics.maxBudget.getCurrency().getCode(),
  };
});

const clusterCount = computed(() => props.plan?.facilities.length ?? 0);

const allDailyPlans = computed(() => props.plan?.getDailyPlans() ?? []);

const dailyPlanCount = computed(() => allDailyPlans.value.length);

const dayCount = computed(() => {
  if (typeof props.plan?.numberOfDays === 'number' && props.plan.numberOfDays > 0) {
    return props.plan.numberOfDays;
  }

  const uniqueDates = new Set(
    allDailyPlans.value
      .map((plan) => getServiceDate(plan.serviceDate))
      .filter((value: string | undefined): value is string => typeof value === 'string' && value.length > 0),
  );

  return uniqueDates.size;
});

function formatDateTime(value: string | undefined): string {
  if (!value) return '-';
  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) return value;
  return parsed.toLocaleString();
}

function formatNumber(value: number | undefined, unit: string): string {
  if (typeof value !== 'number') return '-';
  return `${value.toLocaleString(undefined, { maximumFractionDigits: 2 })} ${unit}`;
}

function formatMoney(amount: number | undefined, currency: string | undefined): string {
  if (typeof amount !== 'number') return '-';
  const code = currency && currency.length > 0 ? currency : 'EUR';
  try {
    return new Intl.NumberFormat(undefined, {
      style: 'currency',
      currency: code,
      maximumFractionDigits: 2,
    }).format(amount);
  } catch {
    return `${amount.toLocaleString(undefined, { maximumFractionDigits: 2 })} ${code}`;
  }
}

function getServiceDate(value: unknown): string | undefined {
  if (typeof value === 'string') {
    return value;
  }
  if (value && typeof value === 'object') {
    const dateValue = (value as Record<string, unknown>).value;
    if (typeof dateValue === 'string') {
      return dateValue;
    }
    const date = (value as Record<string, unknown>).date;
    if (typeof date === 'string') {
      return date;
    }
  }
  return undefined;
}
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
  user-select: none;
}
</style>
