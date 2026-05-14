<template>
  <div class="monitoring-root">
      <div v-if="selectedFacilityMonitoring.length === 0" class="text-body-2 text-medium-emphasis">
        {{ t('infrastructurePlan.show.daily.monitoring.noFacilities') }}
      </div>

      <div
        v-for="facilityMonitoring in selectedFacilityMonitoring"
        :key="facilityMonitoring.facility.id.getValue()"
        class="monitoring-facility mb-4"
      >
        <div class="monitoring-facility__header">
          <div>
            <div class="text-subtitle-1 font-weight-bold">
              {{ displayEntityNameOrUnnamed(facilityMonitoring.facility.name.getValue()) }}
            </div>
            <div class="text-body-2 text-medium-emphasis">
              {{ formatFacilityType(facilityMonitoring.facility.facilityType) }}
            </div>
          </div>
          <div class="text-body-2 text-medium-emphasis">
            {{ facilityMonitoring.containers.length }} {{ t('infrastructurePlan.show.daily.monitoring.containersUnit') }}
          </div>
        </div>

        <div v-if="facilityMonitoring.containers.length === 0" class="text-body-2 text-medium-emphasis mt-2">
          {{ t('infrastructurePlan.show.daily.monitoring.noContainersAssigned') }}
        </div>

        <div v-else class="monitoring-grid">
          <div
            v-for="containerMonitoring in facilityMonitoring.containers"
            :key="containerMonitoring.container.id.getValue()"
            class="monitoring-item"
            :class="{ 'monitoring-item-overflowed': containerMonitoring.state?.status === 'OVERFLOWED' }"
          >
            <div class="monitoring-item__header">
              <div>
                <div class="text-body-1 font-weight-medium">
                  {{ displayEntityNameOrUnnamed(containerMonitoring.container.name.getValue()) }}
                  <ButtonTooltip
                    text=""
                    :tooltip="viewTooltip"
                    icon="mdi-eye"
                    size="small"
                    variant="flat"
                    color="white"
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
                :color="!containerMonitoring.state ? 'secondary' : containerMonitoring.state.status === 'OVERFLOWED' ? 'error' : 'success'"
                variant="flat"
              >
                <strong v-if="computeFillPercent(containerMonitoring) !== null">{{ computeFillPercent(containerMonitoring) }}%</strong>
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
      </div>
  </div>
</template>

<script lang="ts" setup>
import { FacilityType } from '@/domain/enumerate/facility-type';
import type {
    InfrastructurePlanContainerDailyStateDetail,
    InfrastructurePlanContainerDetail,
    InfrastructurePlanFacilityDetail,
} from '@/domain/read-model/infrastructure-plan-detail';
import { infrastructurePlanDetailFallbackDisplayNames } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-detail-mapper';
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import router from '../../router/router';

interface FacilityMonitoringEntry {
  facility: InfrastructurePlanFacilityDetail;
  containers: Array<{
    container: InfrastructurePlanContainerDetail;
    state?: InfrastructurePlanContainerDailyStateDetail;
  }>;
}

const props = defineProps<{
  planDay: number;
  selectedFacilities: InfrastructurePlanFacilityDetail[];
  containerStateMonitoring: InfrastructurePlanContainerDailyStateDetail[];
}>();

const { t } = useI18n();
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

function normalizePlanDay(value: number | string | null | undefined): number {
  if (typeof value === 'number' && Number.isFinite(value)) {
    return value;
  }

  if (typeof value === 'string') {
    const parsedValue = Number(value);
    if (Number.isFinite(parsedValue)) {
      return parsedValue;
    }
  }

  return -1;
}

const selectedFacilityMonitoring = computed<FacilityMonitoringEntry[]>(() => {
  const selectedDay = normalizePlanDay(props.planDay);
  const monitoringByContainerId = new Map(
    props.containerStateMonitoring
      .filter((state) => normalizePlanDay(state.planDay) === selectedDay)
      .map((state) => [normalizeIdentifier(state.containerId.getValue()), state]),
  );

  return props.selectedFacilities.map((facility) => ({
    facility,
    containers: (facility.assignedContainers ?? []).map((container) => ({
      container,
      state: monitoringByContainerId.get(normalizeIdentifier(container.id.getValue())),
    })),
  }));
});

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
  if (!status) {
    return t('infrastructurePlan.show.daily.monitoring.statuses.NO_DATA');
  }

  if (status === 'OVERFLOWED') {
    return t('infrastructurePlan.show.daily.monitoring.statuses.OVERFLOWED');
  }

  return t('infrastructurePlan.show.daily.monitoring.statuses.OK');
}

function computeFillPercent(entry: { container: InfrastructurePlanContainerDetail; state?: InfrastructurePlanContainerDailyStateDetail }): number | null {
  const filling = entry.state?.dailyFillingLiters;
  const capacityFromState = entry.state?.containerCapacityLiters?.getLiters?.();
  const capacityFromContainer = entry.container.capacityLiters?.getLiters?.();
  const capacity = typeof capacityFromState === 'number' ? capacityFromState : capacityFromContainer;

  if (typeof filling !== 'number' || !Number.isFinite(filling)) {
    return null;
  }
  if (typeof capacity !== 'number' || !Number.isFinite(capacity) || capacity === 0) {
    return null;
  }

  const percent = (filling / capacity) * 100;
  if (!Number.isFinite(percent)) return null;
  return Math.round(percent);
}

function openContainer(containerId: string | null): void {
  if (!containerId) return;
  void router.push({ name: 'ShowContainer', params: { id: containerId } });
}

function formatFacilityType(value?: FacilityType | string): string {
  if (!value) {
    return '-';
  }

  return String(value)
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (character) => character.toUpperCase());
}
</script>

<style scoped>
.monitoring-root {
  width: 100%;
}

.monitoring-facility {
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 14px;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.06);
  padding: 16px;
}

.monitoring-facility__header {
  align-items: center;
  display: flex;
  gap: 12px;
  justify-content: space-between;
  margin-bottom: 12px;
}

.monitoring-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.monitoring-item {
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  min-height: 160px;
  padding: 16px;
}

.monitoring-item-overflowed {
  background: rgba(136,13,30,0.08);
  border-color: rgba(88,13,30,0.55);
}

.monitoring-item__header {
  align-items: flex-start;
  display: flex;
  gap: 12px;
  justify-content: space-between;
  margin-bottom: 10px;
}

.monitoring-item__metrics {
  display: grid;
  gap: 6px;
}

.monitoring-item__view-button {
  background: #ffffff !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.inline-view-button {
  margin-left: 8px;
  vertical-align: middle;
}

@media (max-width: 900px) {
  .monitoring-grid {
    grid-template-columns: 1fr;
  }
}
</style>