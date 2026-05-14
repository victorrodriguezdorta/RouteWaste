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
          <v-card-text>
                <div class="route-section__header">
              <div>
                <div class="route-section__title">
                  <span>{{ t('infrastructurePlan.show.daily.route.vehicleLabel') }}: {{ route.vehicleLabel }}</span>
                  <ButtonTooltip
                    text=""
                    :tooltip="viewTooltip"
                    icon="mdi-eye"
                    size="small"
                    variant="flat"
                    color="on-primary"
                    class="route-section__view-button"
                    :eventclick="() => openVehicle(route.vehicleId)"
                  />
                </div>
                <div class="route-section__meta">
                  <span>{{ t('infrastructurePlan.show.daily.route.facilityLabel') }}: {{ route.facilityLabel }}</span>
                  <ButtonTooltip
                    text=""
                    :tooltip="viewTooltip"
                    icon="mdi-eye"
                    size="small"
                    variant="flat"
                    color="on-primary"
                    class="route-section__view-button"
                    :eventclick="() => openFacility(route.facilityId)"
                  />
                </div>
              </div>
              <div class="route-section__summary">
                {{ t('infrastructurePlan.show.daily.route.totalDistanceLabel') }}: {{ formatMeters(route.totalDistanceMeters) }}
              </div>
            </div>

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
                    <div v-if="stop.type === StopType.FACILITY" class="route-stop__facility-indicator">
                      <v-icon icon="mdi-office-building" color="danger-dark" size="small" />
                      <span>{{ t('infrastructurePlan.show.daily.route.facilityStopMessage') }}</span>
                    </div>
                    <div v-else class="route-stop__container-row">
                      <span class="route-stop__container">
                        {{ t('infrastructurePlan.show.daily.route.containerLabel') }}: {{ stop.containerDisplay }}
                      </span>
                      <ButtonTooltip
                        v-if="stop.containerId"
                        text=""
                        :tooltip="viewTooltip"
                        icon="mdi-eye"
                        size="small"
                        variant="flat"
                        color="on-primary"
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
          </v-card-text>
        </v-card>
      </div>
  </div>
</template>

<script lang="ts" setup>
import { StopType } from '@/domain/enumerate/stop-type';
import type { InfrastructurePlanDailyPlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import { infrastructurePlanDetailFallbackDisplayNames } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-detail-mapper';
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
  collectedKilograms?: number;
  collectedLiters?: number;
  distanceFromPreviousMeters?: number;
  cumulativeDistanceMeters?: number;
  cumulativeKilograms?: number;
  cumulativeLiters?: number;
}

interface NormalizedRoute {
  key: string;
  vehicleId: string;
  vehicleLabel: string;
  facilityId: string;
  facilityLabel: string;
  totalDistanceMeters?: number;
  stops: NormalizedStop[];
}

const props = defineProps<{
  routes: VehicleRouteLike[];
}>();

const { t } = useI18n();

function unnamedEntityLabel(): string {
  const key = 'infrastructurePlan.show.daily.display.unnamedEntity';
  const translated = t(key);
  return translated === key ? '—' : translated;
}

function formatVehicleTypeLabel(value: unknown): string {
  if (value == null || value === '') {
    return '';
  }

  return String(value)
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (character) => character.toUpperCase());
}

const viewTooltip = computed(() => {
  const translated = t('algorithm.list.table.tooltips.view');
  return translated === 'algorithm.list.table.tooltips.view' ? 'View details' : translated;
});

const normalizedRoutes = computed<NormalizedRoute[]>(() =>
  props.routes.map((route) => {
    const vehicleName = route.dailyPlan.vehicle?.name?.getValue()?.trim();
    const vehicleTypeStr = formatVehicleTypeLabel(route.dailyPlan.vehicle?.vehicleType);
    const facilityName = route.dailyPlan.facilityName?.trim();
    return {
      key: route.key,
      vehicleId: route.vehicleId,
      vehicleLabel:
        vehicleName && vehicleName.length > 0
          ? vehicleName
          : vehicleTypeStr.length > 0
            ? vehicleTypeStr
            : unnamedEntityLabel(),
      facilityId: route.facilityId,
      facilityLabel:
        facilityName && facilityName.length > 0 ? facilityName : unnamedEntityLabel(),
      totalDistanceMeters: route.dailyPlan.totalDistanceMeters.getValue(),
      stops: normalizeStops(route.dailyPlan.stops),
    };
  }),
);

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
        collectedKilograms: stop.collectedKilograms.getKilograms(),
        collectedLiters: stop.collectedLiters.getLiters(),
        distanceFromPreviousMeters: stop.distanceFromPreviousMeters.getValue(),
        cumulativeDistanceMeters: stop.cumulativeDistanceMeters.getValue(),
      };
    })
    .sort((left, right) => left.sequence - right.sequence);

  // Calculate cumulative kilograms and liters
  let cumulativeKg = 0;
  let runningCumulativeLiters = 0;

  return sorted.map((stop) => {
    if (typeof stop.collectedKilograms === 'number') {
      cumulativeKg += stop.collectedKilograms;
    }
    if (typeof stop.collectedLiters === 'number') {
      runningCumulativeLiters += stop.collectedLiters;
    }

    const cumulativeKilograms = stop.type === StopType.FACILITY ? 0 : cumulativeKg;
    const cumulativeLiters = stop.type === StopType.FACILITY ? 0 : runningCumulativeLiters;

    return {
      ...stop,
      cumulativeKilograms,
      cumulativeLiters,
    };
  });
}

function formatMeters(value: number | undefined): string {
  if (typeof value !== 'number') {
    return '-';
  }

  return `${value.toLocaleString(undefined, { maximumFractionDigits: 2 })} m`;
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
}

.route-section__header {
  align-items: flex-start;
  display: flex;
  gap: 12px;
  justify-content: space-between;
  margin-bottom: 16px;
}

.route-section__title {
  color: rgb(var(--v-theme-primary));
  font-size: 1rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
}

.route-section__meta,
.route-section__summary {
  color: rgba(var(--v-theme-neutral-base), 0.7);
  font-size: 0.92rem;
  display: flex;
  align-items: center;
  gap: 6px;
}

.route-section__view-button {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
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
  box-shadow: 0 2px 8px rgba(var(--v-theme-neutral-base), 0.04);
  border-radius: 10px;
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

.route-stop__facility-indicator {
  align-items: center;
  color: rgb(var(--v-theme-danger-dark));
  display: inline-flex;
  gap: 6px;
  font-weight: 600;
}

.route-stop__container {
  color: rgba(var(--v-theme-neutral-base), 0.72);
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

.route-stop__metrics {
  color: rgba(var(--v-theme-neutral-base), 0.78);
  display: grid;
  gap: 6px;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
}

@media (max-width: 700px) {
  .route-section__header {
    flex-direction: column;
  }

  .route-stop__metrics {
    grid-template-columns: 1fr;
  }
}
</style>
