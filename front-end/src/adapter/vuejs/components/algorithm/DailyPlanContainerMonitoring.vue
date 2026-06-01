<template>
  <div class="monitoring-root" :style="fillThemeStyle">
      <div v-if="selectedFacilityMonitoring.length === 0" class="text-body-2 text-medium-emphasis">
        {{ t('infrastructurePlan.show.daily.monitoring.noFacilities') }}
      </div>

      <div
        v-for="facilityMonitoring in selectedFacilityMonitoring"
        :key="facilityMonitoring.facility.id.getValue()"
        class="monitoring-facility mb-4"
      >
        <div class="monitoring-facility__header">
          <div class="monitoring-facility__heading">
            <div class="monitoring-facility__name">
              <v-icon icon="mdi-office-building" color="on-secondary" size="small" />
              <span>{{ displayEntityNameOrUnnamed(facilityMonitoring.facility.name.getValue()) }}</span>
            </div>
            <div class="monitoring-facility__type">
              {{ formatFacilityType(facilityMonitoring.facility.facilityType) }}
            </div>
          </div>
          <div class="monitoring-facility__count">
            {{ facilityMonitoring.containers.length }} {{ t('infrastructurePlan.show.daily.monitoring.containersUnit') }}
          </div>
        </div>

        <div class="monitoring-facility__body">
          <div v-if="facilityMonitoring.containers.length === 0" class="text-body-2 text-medium-emphasis">
            {{ t('infrastructurePlan.show.daily.monitoring.noContainersAssigned') }}
          </div>

          <div v-else class="monitoring-grid">
            <div
              v-for="containerMonitoring in facilityMonitoring.containers"
              :key="containerMonitoring.container.id.getValue()"
              class="monitoring-item"
              :class="`monitoring-item--fill-${containerFillTone(containerMonitoring)}`"
            >
              <div class="monitoring-item__header">
                <div>
                  <div class="monitoring-item__name">
                    <v-icon icon="mdi-trash-can" color="primary" size="small" />
                    <span>{{ displayEntityNameOrUnnamed(containerMonitoring.container.name.getValue()) }}</span>
                    <ButtonTooltip
                      text=""
                      :tooltip="viewTooltip"
                      icon="mdi-eye"
                      size="small"
                      variant="flat"
                      color="surface"
                      class="monitoring-item__view-button inline-view-button"
                      :eventclick="() => openContainer(containerMonitoring.container.id.getValue())"
                    />
                  </div>
                  <div class="text-body-2 text-medium-emphasis">
                    {{ formatContainerLocation(containerMonitoring.container) }}
                  </div>
                </div>
                <v-chip
                  size="small"
                  class="monitoring-chip"
                  :class="`monitoring-chip--fill-${containerFillTone(containerMonitoring)}`"
                  variant="flat"
                >
                  <strong v-if="fillPercent(containerMonitoring) !== null">{{ fillPercent(containerMonitoring) }}%</strong>
                  <span v-else>-</span>
                  &nbsp;{{ monitoringStatusLabel(containerMonitoring.state?.status) }}
                </v-chip>
              </div>

              <div class="monitoring-item__metrics">
                <div>
                  <strong>{{ t('infrastructurePlan.show.daily.monitoring.fillingLabel') }}:</strong>
                  {{ formatLiters(containerMonitoring.state?.dailyFillingLiters) }}
                </div>
                <div>
                  <strong>{{ t('infrastructurePlan.show.daily.monitoring.capacityLabel') }}:</strong>
                  {{ formatLiters(containerMonitoring.state?.containerCapacityLiters?.getLiters?.() ?? containerMonitoring.container.capacityLiters.getLiters()) }}
                </div>
                <div>
                  <strong>{{ t('infrastructurePlan.show.daily.monitoring.demandPerDayLabel') }}:</strong>
                  {{ formatLiters(containerMonitoring.state?.dailyDemandLitersPerDay?.getLitersPerDay?.() ?? containerMonitoring.container.dailyDemandLitersPerDay.getLitersPerDay()) }}
                </div>
              </div>
            </div>
          </div>

          <div
            v-if="facilityMonitoring.fillChartData.length > 0"
            class="monitoring-chart mt-3"
          >
            <div class="monitoring-chart__title">
              {{ t('infrastructurePlan.show.daily.monitoring.fillEvolutionChartTitle') }}
            </div>
            <div class="monitoring-chart__legend">
              <span
                v-for="legendItem in facilityMonitoring.fillChartLegend"
                :key="legendItem.key"
                class="monitoring-chart__legend-item"
              >
                {{ legendItem.label }}
              </span>
            </div>
            <div class="monitoring-chart__canvas">
              <SimpleLineChart
                :width="chartWidth"
                :height="chartHeight"
                :data="facilityMonitoring.fillChartData"
              />
            </div>
          </div>
        </div>
      </div>
  </div>
</template>

<script lang="ts" setup>
import { FacilityType, facilityTypeLabel } from '@/domain/enumerate/facility-type';
import { containerStatusLabel } from '@/domain/enumerate/container-status';
import type {
    InfrastructurePlanContainerDailyStateDetail,
    InfrastructurePlanContainerDetail,
    InfrastructurePlanFacilityDetail,
} from '@/domain/read-model/infrastructure-plan-detail';
import { infrastructurePlanDetailFallbackDisplayNames } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-detail-mapper';
import SimpleLineChart from '@/adapter/vuejs/components/common/SimpleLineChart.vue';
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import router from '../../router/router';
import {
  type ContainerFillMarkerTone,
  buildContainerFillCssVars,
  computeContainerFillPercent,
  containerFillToneForEntry,
  normalizePlanDay,
} from '../../utils/container-fill-level';

interface FacilityMonitoringEntry {
  facility: InfrastructurePlanFacilityDetail;
  containers: Array<{
    container: InfrastructurePlanContainerDetail;
    state?: InfrastructurePlanContainerDailyStateDetail;
  }>;
  chartId: number;
  fillChartData: ContainerFillLinePlotDatum[];
  fillChartLegend: ContainerFillChartLegendItem[];
}

type ContainerFillLinePlotDatum = Record<string, number> & {
  product: number;
};

interface ContainerFillChartLegendItem {
  key: string;
  label: string;
}

const props = defineProps<{
  planDay: number;
  selectedFacilities: InfrastructurePlanFacilityDetail[];
  containerStateMonitoring: InfrastructurePlanContainerDailyStateDetail[];
}>();

const { t } = useI18n();

const themeColorsRevision = ref(0);
const chartWidth = 720;
const chartHeight = 260;

const fillThemeStyle = computed(() => {
  themeColorsRevision.value;
  return buildContainerFillCssVars();
});

onMounted(() => {
  if (import.meta.hot) {
    import.meta.hot.accept('@/theme/professional-light-colors', () => {
      themeColorsRevision.value += 1;
    });
  }
});

const viewTooltip = computed(() => {
  const translated = t('algorithm.list.table.tooltips.view');
  return translated === 'algorithm.list.table.tooltips.view' ? 'View details' : translated;
});

function unnamedEntityLabel(): string {
  const key = 'infrastructurePlan.show.daily.display.unnamedEntity';
  const translated = t(key);
  return translated === key ? '—' : translated;
}

function displayEntityNameOrUnnamed(raw: string): string {
  const trimmed = raw.trim();
  if (
    trimmed.length === 0
    || trimmed === infrastructurePlanDetailFallbackDisplayNames.facility
    || trimmed === infrastructurePlanDetailFallbackDisplayNames.container
  ) {
    return unnamedEntityLabel();
  }
  return trimmed;
}

function normalizeIdentifier(value: string | null | undefined): string {
  return String(value ?? '').trim().toLowerCase();
}

const selectedFacilityMonitoring = computed<FacilityMonitoringEntry[]>(() => {
  const selectedDay = normalizePlanDay(props.planDay);
  const monitoringByContainerId = new Map(
    props.containerStateMonitoring
      .filter((state) => normalizePlanDay(state.planDay) === selectedDay)
      .map((state) => [normalizeIdentifier(state.containerId.getValue()), state]),
  );

  return props.selectedFacilities.map((facility, facilityIndex) => {
    const containers = (facility.assignedContainers ?? []).map((container) => ({
      container,
      state: monitoringByContainerId.get(normalizeIdentifier(container.id.getValue())),
    }));

    return {
      facility,
      containers,
      chartId: 20_000 + facilityIndex,
      fillChartData: buildFacilityFillChartData(facility),
      fillChartLegend: buildFacilityFillChartLegend(facility),
    };
  });
});

function buildFacilityFillChartLegend(
  facility: InfrastructurePlanFacilityDetail,
): ContainerFillChartLegendItem[] {
  return (facility.assignedContainers ?? []).map((container, index) => ({
    key: container.id.getValue(),
    label: chartSeriesLabelForContainer(container, index),
  }));
}

function buildFacilityFillChartData(
  facility: InfrastructurePlanFacilityDetail,
): ContainerFillLinePlotDatum[] {
  const containers = facility.assignedContainers ?? [];
  if (containers.length === 0) {
    return [];
  }

  const containerIds = new Set(containers.map((container) => normalizeIdentifier(container.id.getValue())));
  const statesByDayAndContainer = new Map<string, InfrastructurePlanContainerDailyStateDetail>();
  const availableDays = new Set<number>();

  props.containerStateMonitoring.forEach((state) => {
    const containerId = normalizeIdentifier(state.containerId.getValue());
    if (!containerIds.has(containerId)) {
      return;
    }

    const day = normalizePlanDay(state.planDay);
    if (day < 0) {
      return;
    }

    availableDays.add(day);
    statesByDayAndContainer.set(`${day}::${containerId}`, state);
  });

  return Array.from(availableDays)
    .sort((left, right) => left - right)
    .map((day) => {
      const row: ContainerFillLinePlotDatum = { product: day };

      containers.forEach((container, index) => {
        const containerId = normalizeIdentifier(container.id.getValue());
        const state = statesByDayAndContainer.get(`${day}::${containerId}`);
        const percent = computeContainerFillPercent({ container, state });
        row[chartSeriesLabelForContainer(container, index)] = percent ?? 0;
      });

      return row;
    });
}

function chartSeriesLabelForContainer(container: InfrastructurePlanContainerDetail, index: number): string {
  const name = displayEntityNameOrUnnamed(container.name.getValue());
  return `${name} ${index + 1}`;
}

function formatContainerLocation(container: InfrastructurePlanContainerDetail): string {
  const { postalAddress, gisReference, latitude, longitude } = container.location;

  if (postalAddress) {
    return postalAddress;
  }

  if (gisReference) {
    return gisReference;
  }

  return `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`;
}

function formatLiters(value: number | null | undefined): string {
  if (typeof value !== 'number' || !Number.isFinite(value)) {
    return '-';
  }

  return `${value.toFixed(1)} L`;
}

function monitoringStatusLabel(status: string | null | undefined): string {
  return containerStatusLabel(t, status);
}

function fillPercent(entry: { container: InfrastructurePlanContainerDetail; state?: InfrastructurePlanContainerDailyStateDetail }): number | null {
  return computeContainerFillPercent(entry);
}

function containerFillTone(entry: { container: InfrastructurePlanContainerDetail; state?: InfrastructurePlanContainerDailyStateDetail }): ContainerFillMarkerTone {
  return containerFillToneForEntry(entry);
}

function openContainer(containerId: string | null): void {
  if (!containerId) return;
  void router.push({ name: 'ShowContainer', params: { id: containerId } });
}

function formatFacilityType(value?: FacilityType | string): string {
  if (!value) {
    return '-';
  }

  return facilityTypeLabel(t, value);
}
</script>

<style scoped>
.monitoring-root {
  width: 100%;
}

.monitoring-facility {
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-neutral-base), 0.1);
  border-radius: 14px;
  box-shadow: 0 4px 16px rgba(var(--v-theme-shadow-slate), 0.06);
  overflow: hidden;
}

.monitoring-facility__header {
  align-items: center;
  background: rgb(var(--v-theme-secondary));
  color: rgb(var(--v-theme-on-secondary));
  display: flex;
  gap: 12px;
  justify-content: space-between;
  padding: 12px 16px;
}

.monitoring-facility__body {
  padding: 16px;
}

.monitoring-facility__heading {
  display: grid;
  gap: 2px;
}

.monitoring-facility__name {
  align-items: center;
  display: inline-flex;
  font-size: 1rem;
  font-weight: 700;
  gap: 6px;
}

.monitoring-facility__type {
  color: rgba(var(--v-theme-on-secondary), 0.78);
  font-size: 0.875rem;
}

.monitoring-facility__count {
  color: rgba(var(--v-theme-on-secondary), 0.85);
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
}

.monitoring-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.monitoring-item {
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(var(--v-theme-neutral-base), 0.04);
  min-height: 160px;
  padding: 12px 14px;
}

.monitoring-item--fill-normal {
  border-color: var(--cbf-normal-border);
}

.monitoring-item--fill-medium {
  background: var(--cbf-medium-bg);
  border-color: var(--cbf-medium-border);
}

.monitoring-item--fill-overflow {
  background: var(--cbf-overflow-bg);
  border-color: var(--cbf-overflow-border);
}

.monitoring-chip {
  color: rgb(var(--v-theme-on-primary));
}

.monitoring-chip--fill-unknown {
  background: var(--cbf-unknown) !important;
  color: rgb(var(--v-theme-on-primary)) !important;
}

.monitoring-chip--fill-normal {
  background: var(--cbf-normal) !important;
  color: rgb(var(--v-theme-on-primary)) !important;
}

.monitoring-chip--fill-medium {
  background: var(--cbf-medium) !important;
  color: rgb(var(--v-theme-on-primary)) !important;
}

.monitoring-chip--fill-overflow {
  background: var(--cbf-overflow) !important;
  color: rgb(var(--v-theme-on-primary)) !important;
}

.monitoring-item__header {
  align-items: flex-start;
  display: flex;
  gap: 12px;
  justify-content: space-between;
  margin-bottom: 10px;
}

.monitoring-item__name {
  align-items: center;
  color: rgba(var(--v-theme-neutral-base), 0.82);
  display: inline-flex;
  flex-wrap: wrap;
  font-weight: 600;
  gap: 6px;
  word-break: break-word;
}

.monitoring-item__metrics {
  display: grid;
  gap: 6px;
}

.monitoring-item__view-button {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.monitoring-item__view-button:deep(.v-btn),
.monitoring-item__view-button :deep(.v-btn) {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.monitoring-item__view-button:deep(.v-icon),
.monitoring-item__view-button :deep(.v-icon) {
  color: rgb(var(--v-theme-primary)) !important;
}

.inline-view-button {
  margin-left: 8px;
  vertical-align: middle;
}

.monitoring-chart {
  background: #fff;
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 10px;
  padding: 10px 8px 6px;
}

.monitoring-chart__title {
  color: rgba(var(--v-theme-on-surface), 0.75);
  font-size: 0.78rem;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 6px;
  text-align: center;
}

.monitoring-chart__legend {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 12px;
  justify-content: center;
  margin-bottom: 6px;
}

.monitoring-chart__legend-item {
  align-items: center;
  color: rgba(var(--v-theme-on-surface), 0.68);
  display: inline-flex;
  font-size: 0.68rem;
  gap: 4px;
}

.monitoring-chart__legend-item::before {
  background: rgb(var(--v-theme-primary));
  border-radius: 999px;
  content: '';
  display: inline-block;
  height: 8px;
  width: 8px;
}

.monitoring-chart__canvas {
  background: #fff;
  display: flex;
  justify-content: center;
  overflow-x: auto;
}

.monitoring-chart :deep(h1) {
  display: none;
}

.monitoring-chart :deep(svg) {
  background: #fff;
}

.monitoring-chart :deep([id^='mainDiv']) {
  background: #fff;
}

@media (max-width: 900px) {
  .monitoring-grid {
    grid-template-columns: 1fr;
  }
}
</style>