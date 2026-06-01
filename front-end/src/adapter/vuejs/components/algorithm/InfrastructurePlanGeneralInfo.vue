<template>
  <v-card variant="outlined" class="mb-4">
    <v-card-title
      class="d-flex align-center justify-space-between ga-2 cursor-pointer py-2"
      @click="isExpanded = !isExpanded"
    >
      <div class="d-flex align-center ga-2">
        <v-icon icon="mdi-information-outline" color="primary" size="small" />
        <span class="text-body-1">{{ t('infrastructurePlan.show.generalInfo.title') }}</span>
      </div>
      <v-icon :icon="isExpanded ? 'mdi-chevron-up' : 'mdi-chevron-down'" color="primary" size="small" />
    </v-card-title>

    <v-divider />

    <v-expand-transition>
      <div v-if="isExpanded" class="pa-3">
        <v-row dense class="mb-2">
          <v-col
            v-for="stat in compactStats"
            :key="stat.key"
            cols="6"
            sm="4"
            md="3"
            lg="2"
          >
            <div class="info-stat">
              <span class="info-stat__label">{{ stat.label }}</span>
              <span class="info-stat__value">{{ stat.value }}</span>
            </div>
          </v-col>
        </v-row>

        <v-divider class="my-2" />

        <div class="text-caption font-weight-medium text-medium-emphasis mb-2">
          {{ t('infrastructurePlan.show.generalInfo.charts.sectionTitle') }}
        </div>

        <v-alert
          v-if="plan && !overviewAnalytics?.hasChartData"
          type="info"
          variant="tonal"
          density="compact"
          class="mb-0"
        >
          {{ t('infrastructurePlan.show.generalInfo.charts.noData') }}
        </v-alert>

        <v-row v-else-if="chartsReady && overviewAnalytics" :key="chartRenderKey" dense class="charts-row">
          <v-col cols="4" class="chart-col">
            <div class="chart-panel">
              <div class="chart-panel__title">
                {{ t('infrastructurePlan.show.generalInfo.charts.vehiclesByType') }}
              </div>
              <div class="chart-panel__canvas">
                <PieChart
                  v-if="vehiclesPieData.length > 0"
                  :id="1"
                  :width_props="chartWidth"
                  :height_props="chartHeight"
                  :data="vehiclesPieData"
                />
                <span v-else class="chart-panel__empty">
                  {{ t('infrastructurePlan.show.generalInfo.charts.noVehicles') }}
                </span>
              </div>
            </div>
          </v-col>

          <v-col cols="4" class="chart-col">
            <div class="chart-panel">
              <div class="chart-panel__title">
                {{ t('infrastructurePlan.show.generalInfo.charts.containersByType') }}
              </div>
              <div class="chart-panel__canvas">
                <PieChart
                  v-if="containersPieData.length > 0"
                  :id="2"
                  :width_props="chartWidth"
                  :height_props="chartHeight"
                  :data="containersPieData"
                />
                <span v-else class="chart-panel__empty">
                  {{ t('infrastructurePlan.show.generalInfo.charts.noContainers') }}
                </span>
              </div>
            </div>
          </v-col>

          <v-col cols="4" class="chart-col">
            <div class="chart-panel">
              <div class="chart-panel__title">
                {{ t('infrastructurePlan.show.generalInfo.charts.facilityAssignments') }}
              </div>
              <div class="chart-panel__canvas chart-panel__canvas--scroll">
                <StackedBarPlot
                  v-if="facilityBarData.length > 0"
                  :id="3"
                  :width_props="chartWidth"
                  :height_props="chartHeight"
                  :data="facilityBarData"
                />
                <span v-else class="chart-panel__empty">
                  {{ t('infrastructurePlan.show.generalInfo.charts.noFacilities') }}
                </span>
              </div>
            </div>
          </v-col>
        </v-row>
      </div>
    </v-expand-transition>
  </v-card>
</template>

<script lang="ts" setup>
import { buildPlanOverviewAnalytics } from '@/adapter/vuejs/analytics/infrastructure-plan/build-plan-overview-charts';
import type { PieChartDatum } from '@/adapter/vuejs/analytics/infrastructure-plan/pie-chart-datum';
import type { InfrastructurePlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import { infrastructurePlanValidityStateLabel } from '@/domain/enumerate/infrastructure-plan-validity-state';
import { vehicleTypeLabel } from '@/domain/enumerate/vehicle-type';
import { wasteTypeLabel } from '@/domain/enumerate/waste-type';
import { PieChart, StackedBarPlot } from '@ull-tfg/ull-tfg-vue';
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const props = defineProps<{
  plan?: InfrastructurePlanDetail;
}>();

const { t } = useI18n();
const isExpanded = ref(false);
const chartsReady = ref(false);

const chartWidth = 300;
const chartHeight = 260;

const overviewAnalytics = computed(() => buildPlanOverviewAnalytics(props.plan));

const chartRenderKey = computed(() => props.plan?.id?.getValue() ?? 'no-plan');

const analyticsTotals = computed(() => overviewAnalytics.value?.totals ?? {
  facilities: props.plan?.facilities.length ?? 0,
  containers: 0,
  vehicles: 0,
});

const validityLabel = computed(() => {
  if (!props.plan) return '';
  return infrastructurePlanValidityStateLabel(t, props.plan.validityState);
});

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

interface CompactStat {
  key: string;
  label: string;
  value: string;
}

const compactStats = computed<CompactStat[]>(() => {
  const info = generalInfo.value;
  const totals = analyticsTotals.value;

  return [
    {
      key: 'executedAt',
      label: t('infrastructurePlan.show.generalInfo.fields.executedAt'),
      value: formatDateTime(props.plan?.executedAt),
    },
    {
      key: 'validity',
      label: t('infrastructurePlan.show.generalInfo.fields.validityState'),
      value: validityLabel.value || '-',
    },
    {
      key: 'days',
      label: t('infrastructurePlan.show.generalInfo.fields.numberOfDays'),
      value: String(dayCount.value),
    },
    {
      key: 'dailyPlans',
      label: t('infrastructurePlan.show.generalInfo.fields.dailyPlanCount'),
      value: String(dailyPlanCount.value),
    },
    {
      key: 'facilities',
      label: t('infrastructurePlan.show.generalInfo.fields.facilityCount'),
      value: String(totals.facilities),
    },
    {
      key: 'containers',
      label: t('infrastructurePlan.show.generalInfo.fields.containerCount'),
      value: String(totals.containers),
    },
    {
      key: 'vehicles',
      label: t('infrastructurePlan.show.generalInfo.fields.vehicleCount'),
      value: String(totals.vehicles),
    },
    {
      key: 'kg',
      label: t('infrastructurePlan.show.generalInfo.fields.totalCollectedKilograms'),
      value: formatNumber(info.totalCollectedKilograms, 'kg'),
    },
    {
      key: 'liters',
      label: t('infrastructurePlan.show.generalInfo.fields.totalCollectedLiters'),
      value: formatNumber(info.totalCollectedLiters, 'L'),
    },
    {
      key: 'distance',
      label: t('infrastructurePlan.show.generalInfo.fields.totalDistanceMeters'),
      value: formatNumber(info.totalDistanceMeters, 'm'),
    },
    {
      key: 'pickup',
      label: t('infrastructurePlan.show.generalInfo.fields.averagePickupTimeMinutes'),
      value: formatNumber(info.averagePickupTimeMinutes, 'min'),
    },
    {
      key: 'cost',
      label: t('infrastructurePlan.show.generalInfo.fields.estimatedTotalCost'),
      value: formatMoney(info.estimatedTotalCostAmount, info.estimatedTotalCostCurrency),
    },
    {
      key: 'budget',
      label: t('infrastructurePlan.show.generalInfo.fields.maxBudget'),
      value: formatMoney(info.maxBudgetAmount, info.maxBudgetCurrency),
    },
  ];
});

function translateVehicleType(typeKey: string): string {
  return vehicleTypeLabel(t, typeKey);
}

function translateWasteType(typeKey: string): string {
  return wasteTypeLabel(t, typeKey);
}

const vehiclesPieData = computed<PieChartDatum[]>(() =>
  (overviewAnalytics.value?.vehiclesByType ?? []).map((entry) => ({
    name: translateVehicleType(entry.typeKey),
    value: entry.value,
  })),
);

const containersPieData = computed<PieChartDatum[]>(() =>
  (overviewAnalytics.value?.containersByType ?? []).map((entry) => ({
    name: translateWasteType(entry.typeKey),
    value: entry.value,
  })),
);

const facilityBarData = computed(() => {
  const vehiclesLabel = t('infrastructurePlan.show.generalInfo.charts.series.vehicles');
  const containersLabel = t('infrastructurePlan.show.generalInfo.charts.series.containers');

  return (overviewAnalytics.value?.facilityAssignmentBar ?? []).map((row) => ({
    group: row.group,
    [vehiclesLabel]: row.vehicles,
    [containersLabel]: row.containers,
  }));
});

watch(
  () => [props.plan?.id?.getValue(), isExpanded.value, overviewAnalytics.value?.hasChartData] as const,
  ([, expanded, hasData]) => {
    chartsReady.value = false;
    if (!expanded || hasData === false) {
      return;
    }
    requestAnimationFrame(() => {
      chartsReady.value = true;
    });
  },
  { immediate: true },
);

function formatDateTime(value: string | undefined): string {
  if (!value) return '-';
  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) return value;
  return parsed.toLocaleString(undefined, {
    dateStyle: 'short',
    timeStyle: 'short',
  });
}

function formatNumber(value: number | undefined, unit: string): string {
  if (typeof value !== 'number') return '-';
  return `${value.toLocaleString(undefined, { maximumFractionDigits: 1 })} ${unit}`;
}

function formatMoney(amount: number | undefined, currency: string | undefined): string {
  if (typeof amount !== 'number') return '-';
  const code = currency && currency.length > 0 ? currency : 'EUR';
  try {
    return new Intl.NumberFormat(undefined, {
      style: 'currency',
      currency: code,
      maximumFractionDigits: 0,
    }).format(amount);
  } catch {
    return `${amount.toLocaleString(undefined, { maximumFractionDigits: 0 })} ${code}`;
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

.info-stat {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 6px 8px;
  border-radius: 6px;
  background: rgba(var(--v-theme-on-surface), 0.04);
  min-height: 100%;
}

.info-stat__label {
  font-size: 0.65rem;
  line-height: 1.2;
  color: rgba(var(--v-theme-on-surface), 0.6);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.info-stat__value {
  font-size: 0.8rem;
  font-weight: 600;
  line-height: 1.25;
  word-break: break-word;
}

.charts-row {
  align-items: stretch;
  flex-wrap: nowrap;
  overflow-x: auto;
}

.chart-col {
  display: flex;
}

.chart-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  background: #fff;
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 8px;
  padding: 8px 6px 4px;
}

.chart-panel__title {
  font-size: 0.7rem;
  font-weight: 600;
  text-align: center;
  line-height: 1.2;
  margin-bottom: 4px;
  color: rgba(var(--v-theme-on-surface), 0.75);
  min-height: 2.4em;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-panel__canvas {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: #fff;
  overflow: hidden;
}

.chart-panel__canvas--scroll {
  overflow-x: auto;
  justify-content: flex-start;
}

.chart-panel__empty {
  font-size: 0.75rem;
  color: rgba(var(--v-theme-on-surface), 0.55);
  text-align: center;
  padding: 24px 8px;
}

.chart-panel :deep(h1) {
  display: none;
}

.chart-panel :deep(svg) {
  background: #fff;
}

.chart-panel :deep([id^='mainDiv']) {
  background: #fff;
}
</style>
