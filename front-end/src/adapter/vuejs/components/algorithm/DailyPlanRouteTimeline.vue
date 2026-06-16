<template>
  <div class="route-timeline">
    <div v-if="routes.length === 0" class="text-body-2 text-medium-emphasis">
      {{ t('infrastructurePlan.show.daily.route.noRoutes') }}
    </div>

    <div v-else class="route-list">
      <v-card
        v-for="route in normalizedRoutes"
        :key="route.key"
        variant="flat"
        class="route-section"
      >
        <div class="route-section__title">
          <span>{{ t('infrastructurePlan.show.daily.route.vehicleLabel') }}: {{ route.vehicleLabel }}</span>
          <ButtonTooltip
            text=""
            :tooltip="viewTooltip"
            icon="mdi-eye"
            size="small"
            variant="flat"
            color="primary"
            class="route-section__view-button"
            :eventclick="() => openVehicle(route.vehicleId)"
          />
        </div>
        <v-card-text>
          <div class="route-section__meta">
            <span class="route-section__entity">
              <v-icon icon="mdi-office-building" color="primary" size="small" />
              <span>{{ t('infrastructurePlan.show.daily.route.facilityLabel') }}: {{ route.facilityLabel }}</span>
            </span>
            <ButtonTooltip
              text=""
              :tooltip="viewTooltip"
              icon="mdi-eye"
              size="small"
              variant="flat"
              color="surface"
              class="route-section__view-button route-section__view-button--surface"
              :eventclick="() => openFacility(route.facilityId)"
            />
          </div>

          <v-row dense class="route-stats mb-3">
            <v-col
              v-for="stat in route.summaryStats"
              :key="stat.key"
              cols="6"
              sm="3"
            >
              <div class="info-stat">
                <span class="info-stat__label">{{ stat.label }}</span>
                <span class="info-stat__value">{{ stat.value }}</span>
              </div>
            </v-col>
          </v-row>

          <div v-if="route.stops.length === 0" class="text-body-2 text-medium-emphasis">
            {{ t('infrastructurePlan.show.daily.route.noStops') }}
          </div>

          <div v-else class="route-stops">
            <article
              v-for="stop in route.stops"
              :key="`${route.key}-${stop.sequence}-${stop.containerId}-${stop.type}`"
              class="route-stop"
              :class="{ 'route-stop--facility': stop.type === StopType.FACILITY }"
            >
              <div class="route-stop__marker">
                <span class="route-stop__dot" />
              </div>

              <div class="route-stop__content">
                <div class="route-stop__header">
                  <span class="route-stop__title">
                    {{ t('infrastructurePlan.show.daily.route.stopLabel', { sequence: stop.sequence }) }}
                  </span>
                  <span v-if="stop.collectedAt" class="route-stop__time">
                    <v-icon icon="mdi-clock-outline" size="x-small" />
                    <span>{{ stop.collectedAt }}</span>
                  </span>
                  <div v-if="stop.type === StopType.FACILITY" class="route-stop__facility-indicator">
                    <v-icon icon="mdi-office-building" color="danger-dark" size="small" />
                    <span>{{ t('infrastructurePlan.show.daily.route.facilityStopMessage') }}</span>
                  </div>
                  <div v-else class="route-stop__container-row">
                    <span class="route-stop__container">
                      <v-icon icon="mdi-trash-can" color="primary" size="small" />
                      <span>{{ t('infrastructurePlan.show.daily.route.containerLabel') }}: {{ stop.containerDisplay }}</span>
                    </span>
                    <ButtonTooltip
                      v-if="stop.containerId"
                      text=""
                      :tooltip="viewTooltip"
                      icon="mdi-eye"
                      size="small"
                      variant="flat"
                      color="surface"
                      class="route-stop__view-button"
                      :eventclick="() => openContainer(stop.containerId)"
                    />
                  </div>
                </div>

                <div class="route-stop__metrics">
                  <span>{{ t('infrastructurePlan.show.daily.route.collectedKilogramsLabel') }}: {{ formatWithCumulative(stop.collectedKilograms, stop.cumulativeKilograms, 'kg') }}</span>
                  <span>{{ t('infrastructurePlan.show.daily.route.collectedLitersLabel') }}: {{ formatWithCumulative(stop.collectedLiters, stop.cumulativeLiters, 'L') }}</span>
                  <span>{{ t('infrastructurePlan.show.daily.route.distanceFromPreviousLabel') }}: {{ formatMeters(stop.distanceFromPreviousMeters) }}</span>
                  <span>{{ t('infrastructurePlan.show.daily.route.cumulativeDistanceLabel') }}: {{ formatMeters(stop.cumulativeDistanceMeters) }}</span>
                </div>
              </div>
            </article>
          </div>

          <div v-if="route.hasChartData" class="route-charts mt-3">
            <v-row dense class="route-charts__row">
              <v-col cols="12" lg="6">
                <div class="chart-panel">
                  <div class="chart-panel__title">
                    {{ t('infrastructurePlan.show.daily.route.charts.collectionTitle') }}
                  </div>
                  <div class="chart-panel__canvas">
                    <RouteProgressLineChart
                      :data="route.collectionChart"
                      :series="collectionChartSeries"
                      :width="chartWidth"
                      :height="chartHeight"
                      :x-axis-label="stopAxisLabel"
                      :left-y-axis-label="collectionAxisLabel"
                    />
                  </div>
                </div>
              </v-col>
              <v-col cols="12" lg="6">
                <div class="chart-panel">
                  <div class="chart-panel__title">
                    {{ t('infrastructurePlan.show.daily.route.charts.distanceTitle') }}
                  </div>
                  <div class="chart-panel__canvas">
                    <RouteProgressLineChart
                      :data="route.distanceChart"
                      :series="distanceChartSeries"
                      :width="chartWidth"
                      :height="chartHeight"
                      :x-axis-label="stopAxisLabel"
                      :left-y-axis-label="distanceAxisLabel"
                    />
                  </div>
                </div>
              </v-col>
            </v-row>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { buildRouteSummaryMetrics } from '@/adapter/vuejs/analytics/infrastructure-plan/build-route-progress-charts';
import {
  RouteProgressLineChart,
  buildRouteCollectionChartData,
  buildRouteDistanceChartData,
} from '@/adapter/vuejs/charts';
import type {
  RouteCollectionChartDatum,
  RouteDistanceChartDatum,
  RouteProgressChartSeries,
} from '@/adapter/vuejs/charts';
import { infrastructurePlanDetailFallbackDisplayNames } from '@/adapter/http/mapper/infrastructure-plan-detail-mapper';
import { StopType } from '@/domain/enumerate/stop-type';
import type { InfrastructurePlanDailyPlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import router from '../../router/router';

interface VehicleRouteLike {
  key: string;
  vehicleId: string;
  facilityId: string;
  dailyPlan: InfrastructurePlanDailyPlanDetail;
}

interface NormalizedStop {
  sequence: number;
  type: StopType;
  containerId: string | null;
  containerDisplay: string;
  collectedAt: string | null;
  collectedKilograms?: number;
  collectedLiters?: number;
  distanceFromPreviousMeters?: number;
  cumulativeDistanceMeters?: number;
  cumulativeKilograms?: number;
  cumulativeLiters?: number;
}

interface RouteCompactStat {
  key: string;
  label: string;
  value: string;
}

interface NormalizedRoute {
  key: string;
  vehicleId: string;
  vehicleLabel: string;
  facilityId: string;
  facilityLabel: string;
  stops: NormalizedStop[];
  summaryStats: RouteCompactStat[];
  collectionChart: RouteCollectionChartDatum[];
  distanceChart: RouteDistanceChartDatum[];
  hasChartData: boolean;
}

const props = defineProps<{
  routes: VehicleRouteLike[];
}>();

const { t } = useI18n();

const chartWidth = 520;
const chartHeight = 260;

const COLLECTION_CHART_COLORS = {
  kilograms: '#4e79a7',
  liters: '#f28e2b',
} as const;

const DISTANCE_CHART_COLOR = '#59a14f';

const collectionChartSeries = computed<RouteProgressChartSeries[]>(() => [
  {
    key: 'kilograms',
    label: t('infrastructurePlan.show.daily.route.charts.kilogramsSeries'),
    color: COLLECTION_CHART_COLORS.kilograms,
  },
  {
    key: 'liters',
    label: t('infrastructurePlan.show.daily.route.charts.litersSeries'),
    color: COLLECTION_CHART_COLORS.liters,
  },
]);

const distanceChartSeries = computed<RouteProgressChartSeries[]>(() => [
  {
    key: 'meters',
    label: t('infrastructurePlan.show.daily.route.charts.distanceSeries'),
    color: DISTANCE_CHART_COLOR,
    yAxis: 'left',
  },
]);

const stopAxisLabel = computed(() =>
  t('infrastructurePlan.show.daily.route.charts.stopAxisLabel'),
);
const collectionAxisLabel = computed(() =>
  t('infrastructurePlan.show.daily.route.charts.collectionYaxisLabel'),
);
const distanceAxisLabel = computed(() =>
  t('infrastructurePlan.show.daily.route.charts.distanceSeries'),
);

function unnamedEntityLabel(): string {
  const key = 'infrastructurePlan.show.daily.display.unnamedEntity';
  const translated = t(key);
  return translated === key ? '—' : translated;
}

const viewTooltip = computed(() => {
  const translated = t('algorithm.list.table.tooltips.view');
  return translated === 'algorithm.list.table.tooltips.view' ? 'View details' : translated;
});

const normalizedRoutes = computed<NormalizedRoute[]>(() =>
  props.routes.map((route) => {
    const vehicleName = route.dailyPlan.vehicle?.name?.getValue()?.trim();
    const facilityName = route.dailyPlan.facilityName?.trim();
    const stops = normalizeStops(route.dailyPlan.stops);
    const summary = buildRouteSummaryMetrics(route.dailyPlan);

    return {
      key: route.key,
      vehicleId: route.vehicleId,
      vehicleLabel:
        vehicleName && vehicleName.length > 0 ? vehicleName : unnamedEntityLabel(),
      facilityId: route.facilityId,
      facilityLabel:
        facilityName && facilityName.length > 0 ? facilityName : unnamedEntityLabel(),
      stops,
      summaryStats: buildSummaryStats(summary),
      collectionChart: buildRouteCollectionChartData(stops),
      distanceChart: buildRouteDistanceChartData(stops),
      hasChartData: stops.length > 0,
    };
  }),
);

function buildSummaryStats(
  summary: ReturnType<typeof buildRouteSummaryMetrics>,
): RouteCompactStat[] {
  return [
    {
      key: 'distance',
      label: t('infrastructurePlan.show.daily.route.summary.totalDistance'),
      value: formatMeters(summary.totalDistanceMeters ?? undefined),
    },
    {
      key: 'cost',
      label: t('infrastructurePlan.show.daily.route.summary.estimatedCost'),
      value: formatMoney(summary.estimatedRouteCostAmount, summary.estimatedRouteCostCurrency),
    },
    {
      key: 'capacityKg',
      label: t('infrastructurePlan.show.daily.route.summary.capacityKg'),
      value: formatCapacity(summary.capacityKilograms, 'kg'),
    },
    {
      key: 'capacityL',
      label: t('infrastructurePlan.show.daily.route.summary.capacityLiters'),
      value: formatCapacity(summary.capacityLiters, 'L'),
    },
  ];
}

function normalizeStops(stops: InfrastructurePlanDailyPlanDetail['stops']): NormalizedStop[] {
  const sorted = [...stops]
    .map((stop) => {
      const containerId = stop.containerId ? stop.containerId.getValue() : null;
      const nameStr = stop.containerName?.getValue()?.trim();
      let containerDisplay: string;
      if (nameStr && nameStr.length > 0) {
        if (
          nameStr === infrastructurePlanDetailFallbackDisplayNames.facility
          || nameStr === infrastructurePlanDetailFallbackDisplayNames.container
        ) {
          containerDisplay = unnamedEntityLabel();
        } else {
          containerDisplay = nameStr;
        }
      } else {
        containerDisplay = unnamedEntityLabel();
      }
      return {
        sequence: stop.sequence.getValue(),
        type: stop.type,
        containerId,
        containerDisplay,
        collectedAt: formatStopTime(stop.collectedAt),
        collectedKilograms: stop.collectedKilograms.getKilograms(),
        collectedLiters: stop.collectedLiters.getLiters(),
        distanceFromPreviousMeters: stop.distanceFromPreviousMeters.getValue(),
        cumulativeDistanceMeters: stop.cumulativeDistanceMeters.getValue(),
      };
    })
    .sort((left, right) => left.sequence - right.sequence);

  let cumulativeKg = 0;
  let runningCumulativeLiters = 0;

  return sorted.map((stop) => {
    if (stop.type === StopType.FACILITY) {
      cumulativeKg = 0;
      runningCumulativeLiters = 0;
      return {
        ...stop,
        cumulativeKilograms: 0,
        cumulativeLiters: 0,
      };
    }

    if (typeof stop.collectedKilograms === 'number') {
      cumulativeKg += stop.collectedKilograms;
    }
    if (typeof stop.collectedLiters === 'number') {
      runningCumulativeLiters += stop.collectedLiters;
    }

    return {
      ...stop,
      cumulativeKilograms: cumulativeKg,
      cumulativeLiters: runningCumulativeLiters,
    };
  });
}

function formatMeters(value: number | undefined): string {
  if (typeof value !== 'number') {
    return '-';
  }

  return `${value.toLocaleString(undefined, { maximumFractionDigits: 1 })} m`;
}

/**
 * Normalizes a stop time-of-day string into a "HH:mm" label.
 *
 * @param value time-of-day string ("HH:mm" or "HH:mm:ss"), when available
 * @returns trimmed "HH:mm" label, or null when absent
 */
function formatStopTime(value: string | null): string | null {
  if (typeof value !== 'string' || value.trim().length === 0) {
    return null;
  }

  return value.trim().slice(0, 5);
}

function formatCapacity(value: number | null, unit: string): string {
  if (typeof value !== 'number') {
    return '-';
  }

  return `${value.toLocaleString(undefined, { maximumFractionDigits: 1 })} ${unit}`;
}

function formatMoney(amount: number | null, currency: string): string {
  if (typeof amount !== 'number') {
    return '-';
  }

  const code = currency.length > 0 ? currency : 'EUR';
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

function formatWithCumulative(value: number | undefined, cumulative: number | undefined, unit: string): string {
  if (typeof value !== 'number') {
    return '-';
  }

  const formatted = value.toLocaleString(undefined, { maximumFractionDigits: 2 });
  if (typeof cumulative !== 'number') {
    return `${formatted} ${unit}`;
  }

  const cumulativeFormatted = cumulative.toLocaleString(undefined, { maximumFractionDigits: 2 });
  return `${formatted} ${unit} (ac. ${cumulativeFormatted} ${unit})`;
}

function openContainer(containerId: string | null): void {
  if (!containerId) {
    return;
  }

  void router.push({ name: 'ShowContainer', params: { id: containerId } });
}

function openVehicle(vehicleId: string): void {
  if (!vehicleId) {
    return;
  }

  void router.push({ name: 'ShowVehicle', params: { id: vehicleId } });
}

function openFacility(facilityId: string): void {
  if (!facilityId) {
    return;
  }

  void router.push({ name: 'ShowFacility', params: { id: facilityId } });
}
</script>

<style scoped>
.route-timeline {
  width: 100%;
}

.route-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.route-section {
  border: 1px solid rgba(var(--v-theme-neutral-base), 0.1);
  border-radius: 14px;
  box-shadow: 0 4px 16px rgba(var(--v-theme-shadow-slate), 0.06);
  overflow: hidden;
}

.route-section__title {
  align-items: center;
  background: rgb(var(--v-theme-secondary));
  color: rgb(var(--v-theme-on-secondary));
  display: flex;
  font-size: 1rem;
  font-weight: 700;
  gap: 6px;
  justify-content: space-between;
  padding: 12px 16px;
}

.route-section__meta {
  align-items: center;
  color: rgba(var(--v-theme-neutral-base), 0.7);
  display: flex;
  font-size: 0.92rem;
  gap: 6px;
  margin-bottom: 8px;
}

.route-section__entity {
  align-items: center;
  display: inline-flex;
  gap: 6px;
}

.route-section__view-button {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.route-section__view-button--surface:deep(.v-btn),
.route-section__view-button--surface :deep(.v-btn) {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.route-section__view-button--surface:deep(.v-icon),
.route-section__view-button--surface :deep(.v-icon) {
  color: rgb(var(--v-theme-primary)) !important;
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

.route-stops {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.route-stop {
  display: grid;
  grid-template-columns: 18px 1fr;
  gap: 14px;
}

.route-stop--facility .route-stop__dot {
  background: rgb(var(--v-theme-danger-dark));
}

.route-stop--facility .route-stop__content {
  background: rgba(var(--v-theme-danger-dark), 0.08);
  border: 1px solid rgba(var(--v-theme-danger-dark), 0.18);
}

.route-stop--facility .route-stop__title {
  color: rgb(var(--v-theme-danger-dark));
}

.route-stop__marker {
  align-items: stretch;
  display: flex;
  flex-direction: column;
}

.route-stop__dot {
  background: rgb(var(--v-theme-primary));
  border-radius: 999px;
  display: block;
  height: 12px;
  margin-top: 4px;
  position: relative;
  width: 12px;
}

.route-stop:not(:last-child) .route-stop__dot::after {
  background: rgba(var(--v-theme-primary), 0.3);
  content: '';
  left: 50%;
  position: absolute;
  top: 14px;
  transform: translateX(-50%);
  width: 2px;
  height: calc(100% + 38px);
}

.route-stop__content {
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(var(--v-theme-neutral-base), 0.04);
  padding: 12px 14px;
}

.route-stop__header {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 16px;
  margin-bottom: 8px;
}

.route-stop__title {
  color: rgb(var(--v-theme-primary));
  font-weight: 700;
}

.route-stop__time {
  align-items: center;
  background: rgba(var(--v-theme-primary), 0.12);
  border: 1px solid rgba(var(--v-theme-primary), 0.24);
  border-radius: 999px;
  color: rgb(var(--v-theme-primary));
  display: inline-flex;
  font-size: 0.82rem;
  font-weight: 700;
  gap: 4px;
  padding: 2px 10px;
}

.route-stop--facility .route-stop__time {
  background: rgba(var(--v-theme-danger-dark), 0.12);
  border-color: rgba(var(--v-theme-danger-dark), 0.24);
  color: rgb(var(--v-theme-danger-dark));
}

.route-stop__facility-indicator {
  align-items: center;
  color: rgb(var(--v-theme-danger-dark));
  display: inline-flex;
  gap: 6px;
  font-weight: 600;
}

.route-stop__container {
  align-items: center;
  color: rgba(var(--v-theme-neutral-base), 0.72);
  display: inline-flex;
  gap: 6px;
  word-break: break-word;
}

.route-stop__container-row {
  align-items: center;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.route-stop__view-button {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.route-stop__view-button:deep(.v-btn),
.route-stop__view-button :deep(.v-btn) {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.route-stop__view-button:deep(.v-icon),
.route-stop__view-button :deep(.v-icon) {
  color: rgb(var(--v-theme-primary)) !important;
}

.route-stop__metrics {
  color: rgba(var(--v-theme-neutral-base), 0.78);
  display: grid;
  gap: 6px;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
}

.chart-panel {
  align-items: stretch;
  background: linear-gradient(
    180deg,
    rgba(var(--v-theme-surface), 1) 0%,
    rgba(var(--v-theme-on-surface), 0.02) 100%
  );
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 12px;
  box-shadow: 0 4px 14px rgba(var(--v-theme-neutral-base), 0.05);
  display: flex;
  flex-direction: column;
  padding: 12px 10px 8px;
  width: 100%;
}

.chart-panel__title {
  color: rgba(var(--v-theme-on-surface), 0.82);
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.01em;
  line-height: 1.25;
  margin-bottom: 6px;
  text-align: center;
}

.chart-panel__canvas {
  align-items: center;
  display: flex;
  justify-content: center;
  overflow-x: auto;
  width: 100%;
}

.chart-panel__canvas :deep(.route-progress-chart) {
  min-width: 320px;
}

@media (max-width: 700px) {
  .route-section__title {
    align-items: flex-start;
    flex-direction: column;
  }

  .route-stop__metrics {
    grid-template-columns: 1fr;
  }
}
</style>
