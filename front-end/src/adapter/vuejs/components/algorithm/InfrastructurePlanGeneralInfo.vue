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
              :model-value="plan?.id ?? '-'"
              :label="t('infrastructurePlan.show.generalInfo.fields.id')"
              prepend-icon="mdi-identifier"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              :model-value="formatDateTime(plan?.executedAt)"
              :label="t('infrastructurePlan.show.generalInfo.fields.executedAt')"
              prepend-icon="mdi-calendar-clock"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="generalInfo.status"
              :label="t('infrastructurePlan.show.generalInfo.fields.status')"
              prepend-icon="mdi-state-machine"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatNumber(generalInfo.totalCollectedKilograms, 'kg')"
              :label="t('infrastructurePlan.show.generalInfo.fields.totalCollectedKilograms')"
              prepend-icon="mdi-weight-kilogram"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatNumber(generalInfo.totalCollectedLiters, 'L')"
              :label="t('infrastructurePlan.show.generalInfo.fields.totalCollectedLiters')"
              prepend-icon="mdi-water"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatNumber(generalInfo.totalDistanceMeters, 'm')"
              :label="t('infrastructurePlan.show.generalInfo.fields.totalDistanceMeters')"
              prepend-icon="mdi-map-marker-distance"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="formatMoney(generalInfo.maxBudgetAmount, generalInfo.maxBudgetCurrency)"
              :label="t('infrastructurePlan.show.generalInfo.fields.maxBudget')"
              prepend-icon="mdi-cash-multiple"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              :model-value="String(clusterCount)"
              :label="t('infrastructurePlan.show.generalInfo.fields.facilityCount')"
              prepend-icon="mdi-factory"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              :model-value="String(dailyPlanCount)"
              :label="t('infrastructurePlan.show.generalInfo.fields.dailyPlanCount')"
              prepend-icon="mdi-calendar-multiselect"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              :model-value="String(dayCount)"
              :label="t('infrastructurePlan.show.generalInfo.fields.numberOfDays')"
              prepend-icon="mdi-calendar-range"
              variant="outlined"
              readonly
              disabled
            />
          </v-col>
        </v-row>
      </div>
    </v-expand-transition>
  </v-card>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';

interface InfrastructurePlanLike {
  id?: string;
  executedAt?: string;
  status?: string;
  totalCollectedKilograms?: number;
  totalCollectedLiters?: number;
  totalDistanceMeters?: number;
  maxBudget?: { amount?: number; currency?: string };
  metrics?: {
    totalCollectedKilograms?: number;
    totalCollectedLiters?: number;
    totalDistanceMeters?: number;
    maxBudget?: { amount?: number; currency?: string };
  };
  dailyPlans?: unknown[];
  facilities?: Array<{ status?: string; dailyPlans?: unknown[] }>;
  clusters?: unknown[];
}

const props = defineProps<{
  plan?: InfrastructurePlanLike;
}>();

const { t } = useI18n();
const isExpanded = ref(false);

const generalInfo = computed(() => {
  const plan = (props.plan ?? {}) as Record<string, any>;
  const metrics = (plan.metrics ?? {}) as Record<string, any>;

  const facilities = Array.isArray(plan.facilities)
    ? plan.facilities
    : Array.isArray(plan.clusters)
      ? plan.clusters
      : [];

  const status =
    (typeof plan.status === 'string' && plan.status.length > 0
      ? plan.status
      : typeof facilities[0]?.status === 'string'
        ? facilities[0].status
        : '-');

  const maxBudgetSource = (plan.maxBudget ?? metrics.maxBudget ?? {}) as Record<string, any>;

  return {
    status,
    totalCollectedKilograms: asNumber(plan.totalCollectedKilograms) ?? asNumber(metrics.totalCollectedKilograms),
    totalCollectedLiters: asNumber(plan.totalCollectedLiters) ?? asNumber(metrics.totalCollectedLiters),
    totalDistanceMeters: asNumber(plan.totalDistanceMeters) ?? asNumber(metrics.totalDistanceMeters),
    maxBudgetAmount: asNumber(maxBudgetSource.amount),
    maxBudgetCurrency: typeof maxBudgetSource.currency === 'string' ? maxBudgetSource.currency : undefined,
  };
});

const clusterCount = computed(() => {
  const plan = (props.plan ?? {}) as Record<string, any>;
  if (Array.isArray(plan.facilities)) {
    return plan.facilities.length;
  }
  if (Array.isArray(plan.clusters)) {
    return plan.clusters.length;
  }
  return 0;
});

const allDailyPlans = computed(() => {
  const plan = (props.plan ?? {}) as Record<string, any>;
  if (Array.isArray(plan.dailyPlans)) {
    return plan.dailyPlans;
  }
  if (Array.isArray(plan.facilities)) {
    return plan.facilities.flatMap((facility: any) => (Array.isArray(facility.dailyPlans) ? facility.dailyPlans : []));
  }
  return [] as any[];
});

const dailyPlanCount = computed(() => allDailyPlans.value.length);

const dayCount = computed(() => {
  const uniqueDates = new Set(
    allDailyPlans.value
      .map((plan: any) => getServiceDate(plan?.serviceDate))
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

function asNumber(value: unknown): number | undefined {
  return typeof value === 'number' ? value : undefined;
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