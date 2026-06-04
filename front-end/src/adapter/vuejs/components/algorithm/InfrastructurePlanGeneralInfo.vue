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
              <template v-if="vehiclesPieData.length > 0">
                <div ref="vehiclesChartHostRef" class="chart-panel__canvas chart-panel__canvas--pie">
                  <PieChart
                    v-if="chartsReady"
                    :id="1"
                    :width_props="chartWidth"
                    :height_props="chartHeight"
                    :data="vehiclesPieData"
                  />
                </div>
                <ul class="chart-panel__legend">
                  <li
                    v-for="item in vehicleLegendItems"
                    :key="item.typeKey"
                    class="chart-panel__legend-item"
                  >
                    <span
                      class="chart-panel__swatch"
                      :style="{ backgroundColor: item.hexColor }"
                      aria-hidden="true"
                    />
                    <span class="chart-panel__legend-label">{{ item.label }}</span>
                    <span class="chart-panel__legend-count">{{ item.count }}</span>
                  </li>
                </ul>
              </template>
              <span v-else class="chart-panel__empty">
                {{ t('infrastructurePlan.show.generalInfo.charts.noVehicles') }}
              </span>
            </div>
          </v-col>

          <v-col cols="4" class="chart-col">
            <div class="chart-panel">
              <div class="chart-panel__title">
                {{ t('infrastructurePlan.show.generalInfo.charts.containersByType') }}
              </div>
              <template v-if="containersPieData.length > 0">
                <div ref="containersChartHostRef" class="chart-panel__canvas chart-panel__canvas--pie">
                  <PieChart
                    v-if="chartsReady"
                    :id="2"
                    :width_props="chartWidth"
                    :height_props="chartHeight"
                    :data="containersPieData"
                  />
                </div>
                <ul class="chart-panel__legend">
                  <li
                    v-for="item in containerLegendItems"
                    :key="item.typeKey"
                    class="chart-panel__legend-item"
                  >
                    <span
                      class="chart-panel__swatch"
                      :style="{ backgroundColor: item.hexColor }"
                      aria-hidden="true"
                    />
                    <span class="chart-panel__legend-label">{{ item.label }}</span>
                    <span class="chart-panel__legend-count">{{ item.count }}</span>
                  </li>
                </ul>
              </template>
              <span v-else class="chart-panel__empty">
                {{ t('infrastructurePlan.show.generalInfo.charts.noContainers') }}
              </span>
            </div>
          </v-col>

          <v-col cols="4" class="chart-col chart-col--bar">
            <div class="chart-panel chart-panel--facilities">
              <div class="chart-panel__title">
                {{ t('infrastructurePlan.show.generalInfo.charts.facilityAssignments') }}
              </div>
              <div
                v-if="facilityBarData.length > 0"
                class="chart-panel__bar-wrap"
              >
                <div class="chart-panel__series-legend">
                  <span class="chart-panel__series">
                    <span
                      class="chart-panel__swatch"
                      :style="{ backgroundColor: facilityVehiclesColor }"
                      aria-hidden="true"
                    />
                    {{ facilityVehiclesSeriesLabel }}
                  </span>
                  <span class="chart-panel__series">
                    <span
                      class="chart-panel__swatch"
                      :style="{ backgroundColor: facilityContainersColor }"
                      aria-hidden="true"
                    />
                    {{ facilityContainersSeriesLabel }}
                  </span>
                </div>
                <div
                  ref="facilityBarChartHostRef"
                  class="chart-panel__canvas chart-panel__canvas--bar"
                >
                  <StackedBarPlot
                    v-if="chartsReady && facilityBarWidth > 0"
                    :key="`facility-bar-${chartRenderKey}-${facilityBarWidth}-${facilityBarHeight}`"
                    :id="3"
                    :width_props="facilityBarWidth"
                    :height_props="facilityBarHeight"
                    :data="facilityBarData"
                  />
                </div>
                <ul v-if="facilityBarSummary.length > 0" class="chart-panel__facility-list">
                  <li
                    v-for="(row, index) in facilityBarSummary"
                    :key="`${row.group}-${index}`"
                    class="chart-panel__facility-item"
                  >
                    <span class="chart-panel__facility-name" :title="row.group">
                      {{ row.group }}
                    </span>
                    <span class="chart-panel__facility-metrics">
                      <span
                        class="chart-panel__facility-metric chart-panel__facility-metric--vehicles"
                        :title="facilityVehiclesSeriesLabel"
                      >
                        {{ row.vehicles }}
                      </span>
                      <span
                        class="chart-panel__facility-metric chart-panel__facility-metric--containers"
                        :title="facilityContainersSeriesLabel"
                      >
                        {{ row.containers }}
                      </span>
                    </span>
                  </li>
                </ul>
              </div>
              <span v-else class="chart-panel__empty">
                {{ t('infrastructurePlan.show.generalInfo.charts.noFacilities') }}
              </span>
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
import type { TypeCountDatum } from '@/adapter/vuejs/analytics/infrastructure-plan/type-count-datum';
import { vuetifyColorToHex } from '@/adapter/vuejs/utils/vuetify-semantic-color';
import type { InfrastructurePlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import { infrastructurePlanValidityStateLabel } from '@/domain/enumerate/infrastructure-plan-validity-state';
import { vehicleTypeColor, vehicleTypeLabel } from '@/domain/enumerate/vehicle-type';
import { wasteTypeColor, wasteTypeLabel } from '@/domain/enumerate/waste-type';
import { PieChart, StackedBarPlot } from '@ull-tfg/ull-tfg-vue';
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useTheme } from 'vuetify';

const props = defineProps<{
  plan?: InfrastructurePlanDetail;
}>();

const { t } = useI18n();
const theme = useTheme();
const isExpanded = ref(false);
const chartsReady = ref(false);
const vehiclesChartHostRef = ref<HTMLElement | null>(null);
const containersChartHostRef = ref<HTMLElement | null>(null);
const facilityBarChartHostRef = ref<HTMLElement | null>(null);

const chartWidth = 340;
const chartHeight = 300;
/** Matches StackedBarPlot margins (top 30 + bottom 70) in ull-tfg-vue. */
const facilityBarChartChrome = 118;
const facilityBarBandHeight = 58;
const facilityBarWidthPerGroup = 72;

const facilityBarHostWidth = ref(0);
let facilityBarResizeObserver: ResizeObserver | undefined;
let facilityBarResizeFrame = 0;

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

interface PieSliceMeta {
  typeKey: string;
  label: string;
  count: number;
}

function resolveEnumColor(typeKey: string, colorForType: (typeKey: string) => string): string {
  try {
    return colorForType(typeKey);
  } catch {
    return 'grey';
  }
}

function themeColorStrings(): Record<string, string | undefined> {
  const result: Record<string, string | undefined> = {};
  for (const [key, value] of Object.entries(theme.current.value.colors)) {
    if (typeof value === 'string') {
      result[key] = value;
    }
  }
  return result;
}

function resolveHexColor(typeKey: string, colorForType: (typeKey: string) => string): string {
  return vuetifyColorToHex(resolveEnumColor(typeKey, colorForType), themeColorStrings());
}

function buildPieSliceMeta(
  entries: TypeCountDatum[],
  translate: (typeKey: string) => string,
): PieSliceMeta[] {
  return entries.map((entry) => ({
    typeKey: entry.typeKey,
    label: translate(entry.typeKey),
    count: entry.value,
  }));
}

/**
 * ull-tfg-vue PieChart sorts data ascending by `value` before drawing arcs.
 * Slice colors must use the same order as `path` elements in the SVG.
 */
function slicesSortedForPieColor(slices: PieSliceMeta[]): PieSliceMeta[] {
  return [...slices].sort((left, right) => left.count - right.count);
}

const vehiclePieSlices = computed(() =>
  buildPieSliceMeta(overviewAnalytics.value?.vehiclesByType ?? [], translateVehicleType),
);

const containerPieSlices = computed(() =>
  buildPieSliceMeta(overviewAnalytics.value?.containersByType ?? [], translateWasteType),
);

const vehiclesPieData = computed<PieChartDatum[]>(() =>
  vehiclePieSlices.value.map((slice) => ({
    name: slice.label,
    value: slice.count,
  })),
);

const containersPieData = computed<PieChartDatum[]>(() =>
  containerPieSlices.value.map((slice) => ({
    name: slice.label,
    value: slice.count,
  })),
);

const vehicleLegendItems = computed(() =>
  [...vehiclePieSlices.value]
    .sort((left, right) => right.count - left.count)
    .map((slice) => ({
      typeKey: slice.typeKey,
      label: slice.label,
      count: slice.count,
      hexColor: resolveHexColor(slice.typeKey, vehicleTypeColor),
    })),
);

const containerLegendItems = computed(() =>
  [...containerPieSlices.value]
    .sort((left, right) => right.count - left.count)
    .map((slice) => ({
      typeKey: slice.typeKey,
      label: slice.label,
      count: slice.count,
      hexColor: resolveHexColor(slice.typeKey, wasteTypeColor),
    })),
);

const facilityBarData = computed(
  () => overviewAnalytics.value?.facilityAssignmentBar ?? [],
);

const facilityBarSummary = computed(() =>
  facilityBarData.value.map((row) => ({
    group: row.group,
    vehicles: row.vehicles,
    containers: row.containers,
  })),
);

const facilityBarWidth = computed(() => {
  const groupCount = facilityBarData.value.length;
  const measured = facilityBarHostWidth.value;
  const scrollableMin = groupCount * facilityBarWidthPerGroup + 112;
  return Math.max(measured || chartWidth, scrollableMin);
});

const facilityBarHeight = computed(() => {
  const groupCount = Math.max(facilityBarData.value.length, 1);
  return facilityBarChartChrome + groupCount * facilityBarBandHeight;
});

const facilityVehiclesSeriesLabel = computed(() =>
  t('infrastructurePlan.show.generalInfo.charts.series.vehicles'),
);

const facilityContainersSeriesLabel = computed(() =>
  t('infrastructurePlan.show.generalInfo.charts.series.containers'),
);

const facilityVehiclesColor = vuetifyColorToHex('blue', themeColorStrings());
const facilityContainersColor = vuetifyColorToHex('red', themeColorStrings());

function applyPieSliceColors(
  host: HTMLElement | null,
  slices: PieSliceMeta[],
  colorForType: (typeKey: string) => string,
): void {
  if (!host) {
    return;
  }

  const paths = host.querySelectorAll('svg path');
  const ordered = slicesSortedForPieColor(slices);

  paths.forEach((path, index) => {
    const slice = ordered[index];
    if (!slice) {
      return;
    }
    path.setAttribute('fill', resolveHexColor(slice.typeKey, colorForType));
    path.setAttribute('stroke', '#ffffff');
    path.setAttribute('stroke-width', '2.5');
    path.setAttribute('opacity', '1');
  });
}

function applyFacilityBarStyles(host: HTMLElement | null): void {
  if (!host) {
    return;
  }

  const axisLabelFill = 'rgba(0, 0, 0, 0.62)';
  const axisTickStroke = 'rgba(0, 0, 0, 0.14)';

  const layerGroups = [...host.querySelectorAll('svg > g > g > g')].filter(
    (group) => group.querySelector('rect') !== null,
  );
  const seriesColors = [facilityVehiclesColor, facilityContainersColor];

  layerGroups.forEach((layer, index) => {
    const fill = seriesColors[index] ?? seriesColors[seriesColors.length - 1];
    layer.setAttribute('fill', fill);
    layer.querySelectorAll('rect').forEach((rect) => {
      rect.setAttribute('fill', fill);
      rect.setAttribute('rx', '3');
      rect.setAttribute('ry', '3');
    });
  });

  const axisGroups = host.querySelectorAll('svg > g > g');
  axisGroups.forEach((axisGroup, axisIndex) => {
    const isXAxis = axisIndex === 0;
    axisGroup.querySelectorAll('.tick text').forEach((node) => {
      const text = node as SVGTextElement;
      text.setAttribute('fill', axisLabelFill);
      text.style.fontSize = '11px';
      text.style.fontFamily = 'inherit';

      if (!isXAxis) {
        return;
      }

      const label = text.textContent?.trim() ?? '';
      if (label.length > 22) {
        text.textContent = `${label.slice(0, 20)}…`;
      }
      text.setAttribute('transform', 'rotate(-24)');
      text.setAttribute('text-anchor', 'end');
      text.setAttribute('dx', '-0.35em');
      text.setAttribute('dy', '0.15em');
    });

    axisGroup.querySelectorAll('.tick line').forEach((line) => {
      line.setAttribute('stroke', axisTickStroke);
    });
  });
}

async function scheduleChartStyleSync(): Promise<void> {
  if (!chartsReady.value) {
    return;
  }
  await nextTick();
  await nextTick();
  requestAnimationFrame(() => {
    applyPieSliceColors(vehiclesChartHostRef.value, vehiclePieSlices.value, vehicleTypeColor);
    applyPieSliceColors(containersChartHostRef.value, containerPieSlices.value, wasteTypeColor);
    applyFacilityBarStyles(facilityBarChartHostRef.value);
    requestAnimationFrame(() => {
      applyPieSliceColors(vehiclesChartHostRef.value, vehiclePieSlices.value, vehicleTypeColor);
      applyPieSliceColors(containersChartHostRef.value, containerPieSlices.value, wasteTypeColor);
      applyFacilityBarStyles(facilityBarChartHostRef.value);
    });
  });
}

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

watch(
  () => [
    chartsReady.value,
    vehiclesPieData.value.length,
    containersPieData.value.length,
    vehiclePieSlices.value,
    containerPieSlices.value,
    facilityBarData.value,
    facilityBarWidth.value,
    facilityBarHeight.value,
  ] as const,
  () => {
    void scheduleChartStyleSync();
  },
);

function syncFacilityBarHostWidth(): void {
  const host = facilityBarChartHostRef.value;
  if (!host) {
    return;
  }
  const width = Math.floor(host.clientWidth);
  if (width > 0) {
    facilityBarHostWidth.value = width;
  }
}

function setupFacilityBarResizeObserver(): void {
  facilityBarResizeObserver?.disconnect();
  const host = facilityBarChartHostRef.value;
  if (!host) {
    return;
  }
  syncFacilityBarHostWidth();
  facilityBarResizeObserver = new ResizeObserver(() => {
    cancelAnimationFrame(facilityBarResizeFrame);
    facilityBarResizeFrame = requestAnimationFrame(syncFacilityBarHostWidth);
  });
  facilityBarResizeObserver.observe(host);
}

watch(
  [chartsReady, isExpanded],
  () => {
    if (chartsReady.value && isExpanded.value) {
      void nextTick(() => setupFacilityBarResizeObserver());
      return;
    }
    facilityBarResizeObserver?.disconnect();
  },
);

onBeforeUnmount(() => {
  facilityBarResizeObserver?.disconnect();
  cancelAnimationFrame(facilityBarResizeFrame);
});

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
  min-width: 260px;
  flex: 1 1 260px;
}

.chart-col--bar {
  min-width: 300px;
  flex: 1.15 1 300px;
}

.chart-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 8px;
  padding: 8px 6px 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
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
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: rgb(var(--v-theme-surface));
  overflow: hidden;
}

.chart-panel__canvas--pie {
  flex: 1;
}

.chart-panel--facilities .chart-panel__title {
  font-size: 0.78rem;
}

.chart-panel__canvas--bar {
  flex: 0 0 auto;
  width: 100%;
  min-height: 200px;
  overflow-x: auto;
  overflow-y: hidden;
  justify-content: center;
  padding: 6px 8px 4px;
  border-radius: 6px;
  background: rgba(var(--v-theme-on-surface), 0.03);
}

.chart-panel__canvas--bar :deep(svg) {
  min-width: 100%;
}

.chart-panel__empty {
  font-size: 0.75rem;
  color: rgba(var(--v-theme-on-surface), 0.55);
  text-align: center;
  padding: 24px 8px;
}

.chart-panel__bar-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.chart-panel__facility-list {
  list-style: none;
  margin: 0;
  padding: 0 4px 2px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 140px;
  overflow-y: auto;
}

.chart-panel__facility-item {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: 10px;
  font-size: 0.75rem;
  line-height: 1.3;
  padding: 6px 8px;
  border-radius: 4px;
  background: rgba(var(--v-theme-on-surface), 0.04);
}

.chart-panel__facility-name {
  word-break: break-word;
  color: rgba(var(--v-theme-on-surface), 0.87);
  font-weight: 500;
}

.chart-panel__facility-metrics {
  display: inline-flex;
  gap: 6px;
  flex-shrink: 0;
}

.chart-panel__facility-metric {
  min-width: 1.75rem;
  padding: 2px 7px;
  border-radius: 4px;
  font-size: 0.72rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  text-align: center;
  color: #fff;
}

.chart-panel__facility-metric--vehicles {
  background-color: v-bind(facilityVehiclesColor);
}

.chart-panel__facility-metric--containers {
  background-color: v-bind(facilityContainersColor);
}

.chart-panel__series-legend {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px 18px;
  font-size: 0.78rem;
  color: rgba(var(--v-theme-on-surface), 0.75);
}

.chart-panel--facilities .chart-panel__swatch {
  width: 12px;
  height: 12px;
}

.chart-panel__series {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.chart-panel__legend {
  list-style: none;
  margin: 4px 0 0;
  padding: 0 4px;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chart-panel__legend-item {
  display: grid;
  grid-template-columns: 10px 1fr auto;
  align-items: start;
  column-gap: 6px;
  font-size: 0.7rem;
  line-height: 1.3;
}

.chart-panel__swatch {
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-top: 2px;
  flex-shrink: 0;
}

.chart-panel__legend-label {
  word-break: break-word;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.chart-panel__legend-count {
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.chart-panel :deep(h1) {
  display: none;
}

.chart-panel :deep(svg) {
  background: transparent;
  display: block;
}

.chart-panel :deep([id^='mainDiv']) {
  background: transparent;
  line-height: 0;
}

.chart-panel__canvas--bar :deep(.domain) {
  stroke: rgba(var(--v-theme-on-surface), 0.22);
}

.chart-panel__canvas--bar :deep(.tick text) {
  fill: rgba(var(--v-theme-on-surface), 0.72);
  font-size: 11px;
}

.chart-panel__canvas--bar :deep(rect) {
  transition: opacity 0.15s ease;
}

.chart-panel__canvas--bar :deep(rect:hover) {
  opacity: 0.88;
}

.chart-panel__canvas--bar :deep(.tooltip) {
  font-size: 0.75rem;
  line-height: 1.35;
  color: rgba(var(--v-theme-on-surface), 0.9);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  pointer-events: none;
}

/* PieChart labels overlap slices; legend below is the source of truth. */
.chart-panel__canvas--pie :deep(svg text) {
  display: none;
}
</style>
