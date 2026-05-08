<template>
  <v-card variant="flat" class="route-timeline-card mt-4">
    <v-card-title class="d-flex align-center ga-2">
      <v-icon icon="mdi-route" color="primary" />
      <span>{{ t('infrastructurePlan.show.daily.route.title') }}</span>
    </v-card-title>

    <v-divider />

    <v-card-text>
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
                  {{ t('infrastructurePlan.show.daily.route.vehicleLabel') }}: {{ route.vehicleId }}
                </div>
                <div class="route-section__meta">
                  {{ t('infrastructurePlan.show.daily.route.facilityLabel') }}: {{ route.facilityId }}
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
                :key="`${route.key}-${stop.sequence}-${stop.containerId}`"
                class="route-stop"
              >
                <div class="route-stop__marker">
                  <span class="route-stop__dot" />
                </div>

                <div class="route-stop__content">
                  <div class="route-stop__header">
                    <span class="route-stop__title">
                      {{ t('infrastructurePlan.show.daily.route.stopLabel', { sequence: stop.sequence }) }}
                    </span>
                    <div class="route-stop__container-row">
                      <span class="route-stop__container">
                        {{ t('infrastructurePlan.show.daily.route.containerLabel') }}: {{ stop.containerId }}
                      </span>
                      <ButtonTooltip
                        text=""
                        :tooltip="viewTooltip"
                        icon="mdi-eye"
                        size="small"
                        variant="flat"
                        color="white"
                        class="route-stop__view-button"
                        :eventclick="() => openContainer(stop.containerId)"
                      />
                    </div>
                  </div>

                  <div class="route-stop__metrics">
                    <span>{{ t('infrastructurePlan.show.daily.route.collectedKilogramsLabel') }}: {{ formatNumber(stop.collectedKilograms) }}</span>
                    <span>{{ t('infrastructurePlan.show.daily.route.collectedLitersLabel') }}: {{ formatNumber(stop.collectedLiters) }}</span>
                    <span>{{ t('infrastructurePlan.show.daily.route.distanceFromPreviousLabel') }}: {{ formatMeters(stop.distanceFromPreviousMeters) }}</span>
                    <span>{{ t('infrastructurePlan.show.daily.route.cumulativeDistanceLabel') }}: {{ formatMeters(stop.cumulativeDistanceMeters) }}</span>
                  </div>
                </div>
              </article>
            </div>
          </v-card-text>
        </v-card>
      </div>
    </v-card-text>
  </v-card>
</template>

<script lang="ts" setup>
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import router from '../../router/router';

interface StopLike {
  sequence?: unknown;
  containerId?: string;
  collectedKilograms?: unknown;
  collectedLiters?: unknown;
  distanceFromPreviousMeters?: unknown;
  cumulativeDistanceMeters?: unknown;
}

interface DailyPlanLike {
  totalDistanceMeters?: unknown;
  stops?: unknown[];
}

interface VehicleRouteLike {
  key: string;
  vehicleId: string;
  facilityId: string;
  dailyPlan: DailyPlanLike;
}

interface NormalizedStop {
  sequence: number;
  containerId: string;
  collectedKilograms?: number;
  collectedLiters?: number;
  distanceFromPreviousMeters?: number;
  cumulativeDistanceMeters?: number;
}

interface NormalizedRoute {
  key: string;
  vehicleId: string;
  facilityId: string;
  totalDistanceMeters?: number;
  stops: NormalizedStop[];
}

const props = defineProps<{
  routes: VehicleRouteLike[];
}>();

const { t } = useI18n();
const viewTooltip = computed(() => {
  const translated = t('algorithm.list.table.tooltips.view');
  return translated === 'algorithm.list.table.tooltips.view' ? 'View details' : translated;
});

const normalizedRoutes = computed<NormalizedRoute[]>(() =>
  props.routes.map((route) => ({
    key: route.key,
    vehicleId: route.vehicleId,
    facilityId: route.facilityId,
    totalDistanceMeters: extractNumericValue(route.dailyPlan.totalDistanceMeters),
    stops: normalizeStops(route.dailyPlan.stops),
  })),
);

function normalizeStops(stops: unknown[] | undefined): NormalizedStop[] {
  if (!Array.isArray(stops)) {
    return [];
  }

  return [...stops]
    .map((stop) => {
      const parsedStop = stop as StopLike;
      return {
        sequence: extractSequence(parsedStop.sequence),
        containerId: typeof parsedStop.containerId === 'string' && parsedStop.containerId.length > 0 ? parsedStop.containerId : '-',
        collectedKilograms: extractNumericValue(parsedStop.collectedKilograms),
        collectedLiters: extractNumericValue(parsedStop.collectedLiters),
        distanceFromPreviousMeters: extractNumericValue(parsedStop.distanceFromPreviousMeters),
        cumulativeDistanceMeters: extractNumericValue(parsedStop.cumulativeDistanceMeters),
      };
    })
    .sort((left, right) => left.sequence - right.sequence);
}

function extractSequence(value: unknown): number {
  if (typeof value === 'number' && Number.isFinite(value)) {
    return value;
  }

  if (value && typeof value === 'object') {
    const nestedValue = (value as Record<string, unknown>).value;
    if (typeof nestedValue === 'number' && Number.isFinite(nestedValue)) {
      return nestedValue;
    }
  }

  return 0;
}

function extractNumericValue(value: unknown): number | undefined {
  if (typeof value === 'number' && Number.isFinite(value)) {
    return value;
  }

  if (value && typeof value === 'object') {
    const nestedValue = (value as Record<string, unknown>).value;
    if (typeof nestedValue === 'number' && Number.isFinite(nestedValue)) {
      return nestedValue;
    }
  }

  return undefined;
}

function formatNumber(value: number | undefined): string {
  if (typeof value !== 'number') {
    return '-';
  }

  return value.toLocaleString(undefined, { maximumFractionDigits: 2 });
}

function formatMeters(value: number | undefined): string {
  if (typeof value !== 'number') {
    return '-';
  }

  return `${value.toLocaleString(undefined, { maximumFractionDigits: 2 })} m`;
}

function openContainer(containerId: string): void {
  if (!containerId || containerId === '-') {
    return;
  }

  void router.push({ name: 'ShowContainer', params: { id: containerId } });
}
</script>

<style scoped>
.route-timeline-card {
  border-radius: 12px;
}

.route-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.route-section {
  border: 1px solid rgba(0, 0, 0, 0.24);
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
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
}

.route-section__meta,
.route-section__summary {
  color: rgba(0, 0, 0, 0.7);
  font-size: 0.92rem;
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
  background: rgba(49, 87, 44, 0.3);
  content: '';
  left: 50%;
  position: absolute;
  top: 14px;
  transform: translateX(-50%);
  width: 2px;
  height: calc(100% + 38px);
}

.route-stop__content {
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
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

.route-stop__container {
  color: rgba(0, 0, 0, 0.72);
  word-break: break-word;
}

.route-stop__container-row {
  align-items: center;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.route-stop__view-button {
  background: #ffffff !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.route-stop__metrics {
  color: rgba(0, 0, 0, 0.78);
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
