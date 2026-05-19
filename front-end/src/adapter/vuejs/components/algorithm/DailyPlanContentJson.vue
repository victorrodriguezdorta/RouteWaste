<template>
  <div class="route-visualizer">
    <section ref="mapStageRef" class="map-stage" aria-label="Mapa de rutas">
      <div class="map-stage__map-wrap">
        <div ref="mapContainer" class="map-stage__leaflet" />
      </div>

      <aside class="map-stage__drawer">
        <div class="navigator-controller navigator-controller--drawer">
          <v-btn
            icon="mdi-chevron-left"
            variant="text"
            color="on-primary"
            size="large"
            :disabled="!canGoPrevious"
            @click="emit('go-previous')"
          />

          <div class="navigator-controller__body">
            <div class="navigator-controller__title">
              <v-icon icon="mdi-calendar-range" color="on-primary" size="22" />
              <span class="navigator-controller__date">{{ serviceDate ?? '—' }}</span>
            </div>
            <div class="navigator-controller__day">
              {{ t('infrastructurePlan.show.daily.navigator.dayLabel', { current: currentDay, total: totalDays }) }}
            </div>
          </div>

          <v-btn
            icon="mdi-chevron-right"
            variant="text"
            color="on-primary"
            size="large"
            :disabled="!canGoNext"
            @click="emit('go-next')"
          />
        </div>

        <div class="map-stage__drawer-cards">
        <v-card elevation="3" variant="flat" class="drawer-card">
          <div class="drawer-card__head">
            <v-icon icon="mdi-factory" color="primary" size="24" class="drawer-card__head-icon" />
            <span class="drawer-card__title text-truncate">{{ t('infrastructurePlan.show.daily.content.selectorTitle') }}</span>
          </div>
          <p class="drawer-card__hint text-medium-emphasis">
            {{ t('infrastructurePlan.show.daily.content.selectorHint') }}
          </p>

          <div v-if="selectableFacilities.length === 0" class="text-body-2 text-medium-emphasis px-3 pb-3">
            {{ t('infrastructurePlan.show.daily.content.noFacilities') }}
          </div>

          <div v-else class="entity-list entity-list--facilities">
            <div
              v-for="facility in selectableFacilities"
              :key="facility.id.getValue()"
              class="entity-action-row"
            >
              <v-checkbox
                :model-value="isFacilitySelected(facility.id.getValue())"
                color="primary"
                density="compact"
                hide-details
                class="entity-checkbox"
                @update:model-value="toggleFacilitySelection(facility.id.getValue())"
              />
              <ButtonTooltip
                :text="facilityButtonLabel(facility)"
                :tooltip="facilityTooltip(facility)"
                icon=""
                size="small"
                variant="flat"
                color="on-primary"
                class="entity-button flex-grow-1 text-truncate"
                :class="{ 'entity-button-selected': isFacilitySelected(facility.id.getValue()) }"
                :eventclick="() => toggleFacilitySelection(facility.id.getValue())"
              />
              <ButtonTooltip
                text=""
                :tooltip="viewTooltip"
                icon="mdi-eye"
                size="small"
                variant="flat"
                color="on-primary"
                class="entity-view-button"
                :eventclick="() => openFacility(facility.id.getValue())"
              />
            </div>
          </div>
        </v-card>

        <v-card elevation="3" variant="flat" class="drawer-card">
          <div class="drawer-card__head">
            <v-icon icon="mdi-truck" color="primary" size="24" class="drawer-card__head-icon" />
            <span class="drawer-card__title text-truncate">{{ t('infrastructurePlan.show.daily.content.vehiclesTitle') }}</span>
          </div>
          <p class="drawer-card__hint text-medium-emphasis">
            {{ t('infrastructurePlan.show.daily.content.vehiclesHint') }}
          </p>

          <div v-if="selectableVehicleRoutes.length === 0" class="text-body-2 text-medium-emphasis px-3 pb-3">
            {{ t('infrastructurePlan.show.daily.content.noVehicles') }}
          </div>

          <div v-else class="entity-list entity-list--vehicles">
            <div
              v-for="route in selectableVehicleRoutes"
              :key="route.key"
              class="entity-action-row"
            >
              <v-checkbox
                :model-value="isVehicleRouteSelected(route.key)"
                color="primary"
                density="compact"
                hide-details
                class="entity-checkbox"
                @update:model-value="toggleVehicleRouteSelection(route.key)"
              />
              <ButtonTooltip
                :text="vehicleButtonLabel(route)"
                :tooltip="vehicleRouteTooltip(route)"
                icon=""
                size="small"
                variant="flat"
                color="on-primary"
                class="entity-button flex-grow-1 text-truncate"
                :class="{ 'entity-button-selected': isVehicleRouteSelected(route.key) }"
                :eventclick="() => toggleVehicleRouteSelection(route.key)"
              />
              <ButtonTooltip
                text=""
                :tooltip="viewTooltip"
                icon="mdi-eye"
                size="small"
                variant="flat"
                color="on-primary"
                class="entity-view-button"
                :eventclick="() => openVehicle(route.vehicleId)"
              />
            </div>
          </div>
        </v-card>
        </div>
      </aside>
    </section>

    <section class="route-visualizer__details">
      <v-card elevation="2" variant="flat" class="details-shell">
        <div class="details-shell__intro">
          <div>
            <h3 class="text-h6 font-weight-bold">{{ detailsSectionHeadline }}</h3>
            <p class="text-caption text-medium-emphasis mb-0">
              {{ t('infrastructurePlan.show.daily.navigator.dayLabel', { current: currentDay, total: totalDays }) }}
              · {{ serviceDate ?? '—' }}
            </p>
          </div>
        </div>

        <v-tabs
          v-model="dailyDetailsTab"
          bg-color="transparent"
          color="primary"
          class="details-shell__tabs"
          density="comfortable"
          align-tabs="start"
        >
          <v-tab value="routes">{{ t('infrastructurePlan.show.daily.route.title') }}</v-tab>
          <v-tab value="monitoring">{{ t('infrastructurePlan.show.daily.monitoring.title', { day: planDay }) }}</v-tab>
        </v-tabs>

        <v-divider class="mb-0" />

        <v-window v-model="dailyDetailsTab" class="details-shell__window">
          <v-window-item value="routes" class="pa-4">
            <DailyPlanRouteTimeline :routes="selectedVehicleRoutes" />
          </v-window-item>

          <v-window-item value="monitoring" class="pa-4">
            <DailyPlanContainerMonitoring
              :plan-day="planDay"
              :selected-facilities="selectedFacilities"
              :container-state-monitoring="containerStateMonitoring"
            />
          </v-window-item>
        </v-window>
      </v-card>
    </section>

    <v-card elevation="1" variant="flat" class="route-visualizer__json-toggle">
      <v-btn
        block
        variant="text"
        color="primary"
        class="json-toggle-btn text-none"
        :append-icon="jsonExpanded ? 'mdi-chevron-up' : 'mdi-chevron-down'"
        @click="jsonExpanded = !jsonExpanded"
      >
        <v-icon start icon="mdi-code-json" />
        {{ technicalJsonTitle }}
      </v-btn>

      <v-expand-transition>
        <div v-show="jsonExpanded" class="px-4 pb-4">
          <pre class="json-block">{{ jsonContent }}</pre>
        </div>
      </v-expand-transition>
    </v-card>
  </div>
</template>

<script lang="ts" setup>
import { FacilityType } from '@/domain/enumerate/facility-type';
import { StopType } from '@/domain/enumerate/stop-type';
import type {
  InfrastructurePlanContainerDailyStateDetail,
  InfrastructurePlanContainerDetail,
  InfrastructurePlanDailyPlanDetail,
  InfrastructurePlanFacilityDetail,
} from '@/domain/read-model/infrastructure-plan-detail';
import { infrastructurePlanDetailFallbackDisplayNames } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-detail-mapper';
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import router from '../../router/router';
import {
  type ContainerFillMarkerTone,
  buildContainerFillThemePalette,
  computeContainerFillPercent,
  containerFillColorForTone,
  hexToRgba,
  normalizePlanDay,
  resolveContainerFillMarkerTone,
} from '../../utils/container-fill-level';
import { professionalLightColors } from '@/theme/professional-light-colors';
import { loadLeaflet } from '../../utils/leaflet';
import DailyPlanContainerMonitoring from './DailyPlanContainerMonitoring.vue';
import DailyPlanRouteTimeline from './DailyPlanRouteTimeline.vue';

interface VehicleRouteOption {
  key: string;
  vehicleId: string;
  facilityId: string;
  dailyPlan: InfrastructurePlanDailyPlanDetail;
}

interface LeafletMap {
  setView(coords: [number, number], zoom: number): LeafletMap;
  fitBounds(bounds: unknown, options?: Record<string, unknown>): LeafletMap;
  remove(): void;
  invalidateSize(): void;
}

interface LeafletLayerGroup {
  addTo(map: LeafletMap): LeafletLayerGroup;
  clearLayers(): void;
}

interface LeafletMarker {
  addTo(target: LeafletLayerGroup): LeafletMarker;
  bindPopup(content: string): LeafletMarker;
}

interface LeafletNamespace {
  map(element: HTMLElement): LeafletMap;
  tileLayer(url: string, options: Record<string, unknown>): { addTo(map: LeafletMap): void };
  marker(coords: [number, number], options?: Record<string, unknown>): LeafletMarker;
  polyline(coords: Array<[number, number]>, options?: Record<string, unknown>): { addTo(target: LeafletLayerGroup): void };
  layerGroup(): LeafletLayerGroup;
  latLngBounds(coords: Array<[number, number]>): unknown;
  divIcon(options: Record<string, unknown>): unknown;
}

const props = defineProps<{
  planDay: number;
  serviceDate?: string;
  dailyPlans: InfrastructurePlanDailyPlanDetail[];
  facilities: InfrastructurePlanFacilityDetail[];
  containerStateMonitoring: InfrastructurePlanContainerDailyStateDetail[];
  currentDay: number;
  totalDays: number;
  canGoPrevious: boolean;
  canGoNext: boolean;
}>();

const emit = defineEmits<{
  (event: 'go-previous'): void;
  (event: 'go-next'): void;
}>();

const { t } = useI18n();

function leafletRouteLineColors(): { returnLeg: string; departureLeg: string; progressionLeg: string } {
  return {
    returnLeg: professionalLightColors['route-return-leg'],
    departureLeg: professionalLightColors['route-departure-leg'],
    progressionLeg: professionalLightColors['route-progression-leg'],
  };
}

const containerFillPalette = computed(() => buildContainerFillThemePalette());

const dailyDetailsTab = ref<'routes' | 'monitoring'>('routes');
const jsonExpanded = ref(false);
const mapStageRef = ref<HTMLElement | null>(null);
let mapStageResizeObserver: ResizeObserver | null = null;

const technicalJsonTitle = computed(() => {
  const key = 'infrastructurePlan.show.daily.content.technicalJsonTitle';
  const translated = t(key);
  return translated === key ? 'Datos técnicos (JSON)' : translated;
});

const detailsSectionHeadline = computed(() => {
  const key = 'infrastructurePlan.show.daily.content.visualizerDetailsTitle';
  const translated = t(key);
  return translated === key ? 'Detalle operativo del día' : translated;
});

const viewTooltip = computed(() => {
  const translated = t('algorithm.list.table.tooltips.view');
  return translated === 'algorithm.list.table.tooltips.view' ? 'View details' : translated;
});
const mapContainer = ref<HTMLDivElement | null>(null);
const mapInstance = ref<LeafletMap | null>(null);
const markersLayer = ref<LeafletLayerGroup | null>(null);
const leaflet = ref<LeafletNamespace | null>(null);

const selectedFacilityIds = ref<string[]>([]);
const selectedVehicleRouteKeys = ref<string[]>([]);

const DEFAULT_CENTER: [number, number] = [28.4636, -16.2518];
const DEFAULT_ZOOM = 12;

const selectableFacilities = computed(() =>
  props.facilities.filter((facility) => facility.id.getValue().length > 0),
);

const selectedFacilities = computed(() => {
  const ids = new Set(selectedFacilityIds.value);
  return selectableFacilities.value.filter((facility) => ids.has(facility.id.getValue()));
});

const selectableVehicleRoutes = computed<VehicleRouteOption[]>(() => {
  const selectedIds = new Set(selectedFacilityIds.value);
  const deduped = new Map<string, VehicleRouteOption>();

  props.dailyPlans.forEach((dailyPlan) => {
    const vehicleId = dailyPlan.vehicleId.getValue();
    const facilityId = dailyPlan.facilityId.getValue();
    if (vehicleId.length === 0) return;
    if (facilityId.length === 0) return;
    if (!selectedIds.has(facilityId)) return;

    const key = `${facilityId}::${vehicleId}`;
    if (!deduped.has(key)) {
      deduped.set(key, { key, vehicleId, facilityId, dailyPlan });
    }
  });

  return Array.from(deduped.values());
});

const selectedVehicleRoutes = computed<VehicleRouteOption[]>(() => {
  const selectedKeys = new Set(selectedVehicleRouteKeys.value);
  return selectableVehicleRoutes.value.filter((route) => selectedKeys.has(route.key));
});

const jsonContent = computed(() => JSON.stringify(buildPublicDaySummary(), null, 2));

watch(
  () => selectableFacilities.value.map((facility) => facility.id.getValue()),
  (ids) => {
    selectedFacilityIds.value = ids;
  },
  { immediate: true },
);

watch(
  () => selectedFacilityIds.value.slice(),
  () => {
    renderMarkers();
  },
);

watch(
  () => selectableVehicleRoutes.value.map((route) => route.key),
  (keys) => {
    selectedVehicleRouteKeys.value = keys;
  },
  { immediate: true },
);

watch(
  () => selectedVehicleRouteKeys.value.slice(),
  () => {
    renderMarkers();
  },
);

watch(jsonExpanded, (open) => {
  if (open) {
    void nextTick(() => {
      mapInstance.value?.invalidateSize();
    });
  }
});

watch(
  () => props.facilities,
  () => {
    renderMarkers();
  },
  { deep: true },
);

watch(
  () => [props.planDay, props.containerStateMonitoring] as const,
  () => {
    renderMarkers();
  },
  { deep: true },
);

watch(containerFillPalette, () => {
  renderMarkers();
});

onMounted(async () => {
  void initializeMap();
  await nextTick();
  mapStageResizeObserver = new ResizeObserver(() => {
    mapInstance.value?.invalidateSize();
  });
  if (mapStageRef.value) {
    mapStageResizeObserver.observe(mapStageRef.value);
  }

  if (import.meta.hot) {
    import.meta.hot.accept('@/theme/professional-light-colors', () => {
      renderMarkers();
    });
  }
});

onUnmounted(() => {
  mapStageResizeObserver?.disconnect();
  mapStageResizeObserver = null;
  mapInstance.value?.remove();
  mapInstance.value = null;
  markersLayer.value = null;
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

function facilityDisplayName(facility: InfrastructurePlanFacilityDetail): string {
  return displayEntityNameOrUnnamed(facility.name.getValue());
}

function facilityButtonLabel(facility: InfrastructurePlanFacilityDetail): string {
  return facilityDisplayName(facility);
}

function facilityTooltip(facility: InfrastructurePlanFacilityDetail): string {
  const name = facilityDisplayName(facility);
  const facilityType = formatFacilityType(facility.facilityType);
  return facilityType && facilityType !== '-' ? `${name} · ${facilityType}` : name;
}

function vehicleButtonLabel(route: VehicleRouteOption): string {
  const name = route.dailyPlan.vehicle?.name?.getValue()?.trim();
  return name && name.length > 0 ? name : unnamedEntityLabel();
}

function vehicleRouteTooltip(route: VehicleRouteOption): string {
  return vehicleButtonLabel(route);
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

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function publicLocationSnapshot(location: InfrastructurePlanContainerDetail['location']): Record<string, unknown> {
  return {
    latitude: location.latitude,
    longitude: location.longitude,
    postalAddress: location.postalAddress,
    gisReference: location.gisReference,
  };
}

function serializeVehicleSnapshot(vehicle: InfrastructurePlanDailyPlanDetail['vehicle']): Record<string, unknown> | null {
  if (!vehicle) {
    return null;
  }

  const cost = vehicle.costPerKilometer;
  return {
    name: vehicle.name?.getValue() ?? null,
    vehicleType: vehicle.vehicleType,
    capacityKilograms: vehicle.capacityKilograms?.getKilograms() ?? null,
    capacityLiters: vehicle.capacityLiters?.getLiters() ?? null,
    costPerKilometer:
      cost != null
        ? {
            amount: cost.getAmount(),
            currency: cost.getCurrency().getCode(),
          }
        : null,
  };
}

function serializeStopPublic(stop: InfrastructurePlanDailyPlanDetail['stops'][number]): Record<string, unknown> {
  return {
    sequence: stop.sequence.getValue(),
    type: stop.type,
    containerName: stop.containerName?.getValue() ?? null,
    collectedKilograms: stop.collectedKilograms.getKilograms(),
    collectedLiters: stop.collectedLiters.getLiters(),
    distanceFromPreviousMeters: stop.distanceFromPreviousMeters.getValue(),
    cumulativeDistanceMeters: stop.cumulativeDistanceMeters.getValue(),
    containerActualLiters: stop.containerActualLiters,
    alerts: stop.alerts.map((alert) => ({
      type: alert.type,
      message: alert.message,
      value: alert.value,
    })),
  };
}

function serializeDailyPlanPublic(plan: InfrastructurePlanDailyPlanDetail): Record<string, unknown> {
  return {
    facilityName: plan.facilityName,
    serviceDate: plan.serviceDate,
    planDay: plan.planDay,
    vehicle: serializeVehicleSnapshot(plan.vehicle),
    totalCollectedKilograms: plan.totalCollectedKilograms.getKilograms(),
    totalCollectedLiters: plan.totalCollectedLiters.getLiters(),
    totalDistanceMeters: plan.totalDistanceMeters.getValue(),
    stops: plan.stops.map(serializeStopPublic),
  };
}

function serializeFacilityPublic(facility: InfrastructurePlanFacilityDetail): Record<string, unknown> {
  return {
    name: facility.name.getValue(),
    facilityType: facility.facilityType,
    status: facility.status,
    location: publicLocationSnapshot(facility.location),
    storageCapacityKilograms: facility.storageCapacity.getKilograms(),
    processingCapacityKilogramsPerDay: facility.processingCapacity.getKilogramsPerDay(),
    assignedContainers: (facility.assignedContainers ?? []).map((container) => ({
      name: container.name.getValue(),
      wasteType: container.wasteType,
      serviceZone: container.serviceZone,
      capacityLiters: container.capacityLiters.getLiters(),
      dailyDemandLitersPerDay: container.dailyDemandLitersPerDay.getLitersPerDay(),
      location: publicLocationSnapshot(container.location),
    })),
    dailyPlans: facility.dailyPlans.map(serializeDailyPlanPublic),
  };
}

function buildPublicDaySummary(): Record<string, unknown> {
  return {
    planDay: props.planDay,
    serviceDate: props.serviceDate ?? null,
    totals: {
      dailyPlans: props.dailyPlans.length,
      facilities: props.facilities.length,
      stops: props.dailyPlans.reduce((acc, plan) => acc + (Array.isArray(plan.stops) ? plan.stops.length : 0), 0),
    },
    facilities: props.facilities.map(serializeFacilityPublic),
    dailyPlans: props.dailyPlans.map(serializeDailyPlanPublic),
    containerStateMonitoring: props.containerStateMonitoring
      .filter((state) => state.planDay === props.planDay)
      .map((state) => ({
        planDay: state.planDay,
        containerName: state.containerName?.getValue() ?? null,
        dailyFillingLiters: state.dailyFillingLiters,
        containerCapacityLiters: state.containerCapacityLiters.getLiters(),
        dailyDemandLitersPerDay: state.dailyDemandLitersPerDay?.getLitersPerDay() ?? null,
        status: state.status,
      })),
  };
}

function isFacilitySelected(facilityId: string): boolean {
  return selectedFacilityIds.value.includes(facilityId);
}

function isVehicleRouteSelected(routeKey: string): boolean {
  return selectedVehicleRouteKeys.value.includes(routeKey);
}

function toggleFacilitySelection(facilityId: string): void {
  if (isFacilitySelected(facilityId)) {
    selectedFacilityIds.value = selectedFacilityIds.value.filter((id) => id !== facilityId);
    return;
  }

  selectedFacilityIds.value = [...selectedFacilityIds.value, facilityId];
}

function toggleVehicleRouteSelection(routeKey: string): void {
  if (isVehicleRouteSelected(routeKey)) {
    selectedVehicleRouteKeys.value = selectedVehicleRouteKeys.value.filter((key) => key !== routeKey);
    return;
  }

  selectedVehicleRouteKeys.value = [...selectedVehicleRouteKeys.value, routeKey];
}

function openFacility(facilityId: string): void {
  void router.push({ name: 'ShowFacility', params: { id: facilityId } });
}

function openVehicle(vehicleId: string): void {
  void router.push({ name: 'ShowVehicle', params: { id: vehicleId } });
}

function hasCoordinates(location: unknown): location is { latitude: number; longitude: number } {
  if (!location || typeof location !== 'object') {
    return false;
  }

  const latitude = (location as Record<string, unknown>).latitude;
  const longitude = (location as Record<string, unknown>).longitude;

  return (
    typeof latitude === 'number'
    && Number.isFinite(latitude)
    && typeof longitude === 'number'
    && Number.isFinite(longitude)
  );
}

function extractContainerId(container: InfrastructurePlanContainerDetail): string | undefined {
  return container.id.getValue();
}

function extractContainerLocation(container: InfrastructurePlanContainerDetail): InfrastructurePlanContainerDetail['location'] {
  return container.location;
}

function extractStopContainerId(stop: InfrastructurePlanDailyPlanDetail['stops'][number]): string | undefined {
  return stop.containerId ? stop.containerId.getValue() : undefined;
}

function extractStopSequence(stop: InfrastructurePlanDailyPlanDetail['stops'][number]): number {
  return stop.sequence.getValue();
}

function normalizeIdentifier(value: string | null | undefined): string {
  return String(value ?? '').trim().toLowerCase();
}

function containerMonitoringByContainerId(): Map<string, InfrastructurePlanContainerDailyStateDetail> {
  const selectedDay = normalizePlanDay(props.planDay);
  return new Map(
    props.containerStateMonitoring
      .filter((state) => normalizePlanDay(state.planDay) === selectedDay)
      .map((state) => [normalizeIdentifier(state.containerId.getValue()), state]),
  );
}

function containerMarkerIcon(L: LeafletNamespace, tone: ContainerFillMarkerTone): unknown {
  const palette = containerFillPalette.value;
  const fill = containerFillColorForTone(palette, tone);
  const surface = professionalLightColors.surface;
  const shadow = hexToRgba(professionalLightColors['neutral-base'], 0.35);
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 36" width="28" height="42" aria-hidden="true" style="display:block;filter:drop-shadow(0 2px 3px ${shadow})"><path fill="${fill}" stroke="${surface}" stroke-width="1.1" stroke-linejoin="round" d="M12 1C6.48 1 2 5.48 2 11c0 7.75 10 23 10 23s10-15.25 10-23C22 5.48 17.52 1 12 1z"/><circle cx="12" cy="11" r="3.6" fill="${surface}"/></svg>`;
  return L.divIcon({
    className: 'container-fill-marker',
    html: '<div class="container-fill-marker__pin">' + svg + '</div>',
    iconSize: [28, 42],
    iconAnchor: [14, 40],
  });
}

async function initializeMap(): Promise<void> {
  if (!mapContainer.value || mapInstance.value) {
    return;
  }

  const L = (await loadLeaflet()) as unknown as LeafletNamespace;
  leaflet.value = L;

  const map = L.map(mapContainer.value).setView(DEFAULT_CENTER, DEFAULT_ZOOM);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(map);

  const layer = L.layerGroup().addTo(map);
  mapInstance.value = map;
  markersLayer.value = layer;

  setTimeout(() => map.invalidateSize(), 0);
  renderMarkers();
  await nextTick();
  requestAnimationFrame(() => renderMarkers());
}

function renderMarkers(): void {
  if (!leaflet.value || !mapInstance.value || !markersLayer.value) {
    return;
  }

  const L = leaflet.value;
  const map = mapInstance.value;
  const layer = markersLayer.value;
  const facilityIcon = L.divIcon({
    className: 'facility-red-icon',
    html: '<span></span>',
    iconSize: [24, 24],
    iconAnchor: [12, 12],
  });

  layer.clearLayers();

  const lineColors = leafletRouteLineColors();

  const boundsPoints: Array<[number, number]> = [];
  const selectedFacilityMap = new Map(selectedFacilities.value.map((facility) => [facility.id.getValue(), facility]));
  const routeContainerLocationMap = new Map<string, [number, number]>();
  const monitoringByContainerId = containerMonitoringByContainerId();

  selectedFacilities.value.forEach((facility) => {
    if (hasCoordinates(facility.location)) {
      const point: [number, number] = [facility.location.latitude, facility.location.longitude];
      boundsPoints.push(point);
      const facilityTitle = t('infrastructurePlan.show.daily.route.facilityLabel');
      const facilityNameHtml = escapeHtml(facilityDisplayName(facility));
      const facilityTitleHtml = escapeHtml(
        facilityTitle === 'infrastructurePlan.show.daily.route.facilityLabel' ? 'Facility' : facilityTitle,
      );
      L.marker(point, { icon: facilityIcon })
        .addTo(layer)
        .bindPopup(`<strong>${facilityTitleHtml}</strong><br>${facilityNameHtml}`);
    }

    (facility.assignedContainers ?? []).forEach((container) => {
      const containerId = extractContainerId(container);
      const containerLocation = extractContainerLocation(container);
      if (!hasCoordinates(containerLocation)) {
        return;
      }

      const point: [number, number] = [containerLocation.latitude, containerLocation.longitude];
      if (containerId) {
        routeContainerLocationMap.set(containerId, point);
      }
      const containerTitle = t('infrastructurePlan.show.daily.route.containerLabel');
      const containerTitleHtml = escapeHtml(
        containerTitle === 'infrastructurePlan.show.daily.route.containerLabel' ? 'Container' : containerTitle,
      );
      const containerNameHtml = escapeHtml(displayEntityNameOrUnnamed(container.name.getValue()));
      const state = containerId
        ? monitoringByContainerId.get(normalizeIdentifier(containerId))
        : undefined;
      const fillPercent = computeContainerFillPercent({ container, state });
      const fillTone = resolveContainerFillMarkerTone(fillPercent);
      const fillPercentHtml = fillPercent !== null
        ? `<br>${escapeHtml(String(fillPercent))}%`
        : '';
      boundsPoints.push(point);
      L.marker(point, { icon: containerMarkerIcon(L, fillTone) })
        .addTo(layer)
        .bindPopup(`<strong>${containerTitleHtml}</strong><br>${containerNameHtml}${fillPercentHtml}`);
    });
  });

  const selectedRouteSet = new Set(selectedVehicleRouteKeys.value);
  selectableVehicleRoutes.value
    .filter((route) => selectedRouteSet.has(route.key))
    .forEach((route) => {
      const facility = selectedFacilityMap.get(route.facilityId);
      const routePoints: Array<[number, number]> = [];

      if (facility && hasCoordinates(facility.location)) {
        routePoints.push([facility.location.latitude, facility.location.longitude]);
      }

      const orderedStops = Array.isArray(route.dailyPlan.stops)
        ? [...route.dailyPlan.stops].sort((a, b) => extractStopSequence(a) - extractStopSequence(b))
        : [];

      // Facility return lines: draw red polylines from last visited container to the facility
      const facilityPoint: [number, number] | null = facility && hasCoordinates(facility.location)
        ? [facility.location.latitude, facility.location.longitude]
        : null;

      let lastContainerPoint: [number, number] | null = null;
      // Track facility as departure point: initialize so first container after facility draws blue line
      let lastFacilityPoint: [number, number] | null = facilityPoint;
      const containerRoutePoints: Array<[number, number]> = [];

      orderedStops.forEach((stop) => {
        if (stop.type === StopType.FACILITY) {
          // Draw red line from last container to facility (if both exist)
          if (lastContainerPoint && facilityPoint) {
            L.polyline([lastContainerPoint, facilityPoint], {
              color: lineColors.returnLeg,
              weight: 4,
              opacity: 0.95,
            }).addTo(layer);
            boundsPoints.push(facilityPoint);
          }
          // After returning to facility, reset lastContainerPoint so subsequent containers start a new segment
          lastContainerPoint = null;
          // Mark facility as origin for the next departure (draw blue to next container)
          lastFacilityPoint = facilityPoint;
          return;
        }

        // Default: container stop
        const containerId = extractStopContainerId(stop);
        if (!containerId) return;
        const containerPoint = routeContainerLocationMap.get(containerId);
        if (!containerPoint) return;
        // If we have a facility departure pending, draw a blue line to this container
        if (lastFacilityPoint && containerPoint) {
          L.polyline([lastFacilityPoint, containerPoint], {
            color: lineColors.departureLeg,
            weight: 4,
            opacity: 0.95,
          }).addTo(layer);
          // facility already in bounds; ensure container included
        }
        containerRoutePoints.push(containerPoint);
        boundsPoints.push(containerPoint);
        // clear pending facility departure after drawing
        lastFacilityPoint = null;
        lastContainerPoint = containerPoint;
      });

      if (containerRoutePoints.length >= 2) {
        L.polyline(containerRoutePoints, {
          color: lineColors.progressionLeg,
          weight: 5,
          opacity: 0.95,
        }).addTo(layer);
      }
    });

  if (boundsPoints.length > 0) {
    map.fitBounds(L.latLngBounds(boundsPoints), { padding: [30, 30] });
  } else {
    map.setView(DEFAULT_CENTER, DEFAULT_ZOOM);
  }
}
</script>

<style scoped>
.route-visualizer {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.map-stage {
  background: linear-gradient(
    145deg,
    rgb(var(--v-theme-panel-gradient-start)) 0%,
    rgb(var(--v-theme-panel-gradient-end)) 100%
  );
  border-radius: 20px;
  box-shadow:
    0 16px 48px rgba(var(--v-theme-shadow-slate), 0.12),
    0 0 0 1px rgba(var(--v-theme-shadow-slate), 0.04);
  overflow: hidden;
  position: relative;
}

.map-stage__map-wrap {
  height: min(74vh, 900px);
  min-height: 420px;
  position: relative;
  width: 100%;
}

.map-stage__leaflet {
  inset: 0;
  position: absolute;
  z-index: 0;
}

.navigator-controller {
  align-items: center;
  background: rgb(var(--v-theme-primary));
  border-radius: 999px;
  box-shadow: 0 8px 24px rgba(var(--v-theme-neutral-base), 0.15);
  color: rgb(var(--v-theme-on-primary));
  display: inline-flex;
  flex-shrink: 0;
  gap: 6px;
}

.navigator-controller--drawer {
  border-radius: 18px;
  padding: 10px 12px;
  width: 100%;
  box-sizing: border-box;
}

.navigator-controller__body {
  align-items: center;
  display: flex;
  flex: 1 1 auto;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
  min-width: 0;
  padding-inline: 4px;
}

.navigator-controller__title {
  align-items: center;
  color: rgb(var(--v-theme-on-primary));
  display: inline-flex;
  font-size: 1.05rem;
  font-weight: 700;
  gap: 8px;
  line-height: 1.2;
  white-space: nowrap;
}

.navigator-controller__date {
  overflow: hidden;
  text-overflow: ellipsis;
}

.navigator-controller__day {
  color: rgb(var(--v-theme-on-primary));
  font-size: 0.9rem;
  font-weight: 600;
  line-height: 1.2;
  opacity: 0.95;
  text-align: center;
}

.map-stage__drawer {
  bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  left: 16px;
  max-height: calc(100% - 32px);
  pointer-events: none;
  position: absolute;
  top: 16px;
  width: min(320px, 32vw);
  z-index: 1000;
}

.map-stage__drawer > * {
  pointer-events: auto;
}

.map-stage__drawer-cards {
  display: flex;
  flex: 1 1 0;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
  overflow: hidden;
}

.drawer-card {
  backdrop-filter: blur(10px);
  background: rgba(var(--v-theme-surface), 0.97) !important;
  border: 1px solid rgba(var(--v-theme-shadow-slate), 0.12) !important;
  border-radius: 14px !important;
  box-shadow: 0 2px 12px rgba(var(--v-theme-shadow-slate), 0.06) !important;
  display: flex;
  flex: 1 1 0;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.drawer-card__head {
  align-items: center;
  display: flex;
  flex-shrink: 0;
  gap: 10px;
  padding: 14px 14px 0;
}

.drawer-card__head-icon {
  flex-shrink: 0;
}

.drawer-card__title {
  font-size: 1.125rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  line-height: 1.25;
}

.drawer-card__hint {
  flex-shrink: 0;
  font-size: 0.6875rem;
  line-height: 1.35;
  margin: 0;
  padding: 4px 14px 0;
}

.entity-action-row {
  align-items: center;
  display: flex;
  gap: 2px;
}

.entity-list {
  flex: 1 1 auto;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 4px 10px 12px;
  scrollbar-color: rgba(var(--v-theme-neutral-base), 0.25) transparent;
  scrollbar-width: thin;
}

.entity-checkbox {
  flex: 0 0 auto;
  margin: 0;
}

.entity-button,
.entity-view-button {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.entity-button {
  justify-content: flex-start;
  min-width: 0;
  padding-inline-start: 0 !important;
  text-transform: none;
}

.entity-button-selected {
  box-shadow: inset 0 0 0 2px rgb(var(--v-theme-primary));
}

.entity-button :deep(.v-btn__content) {
  margin-left: -0.35rem;
}

.route-visualizer__details {
  width: 100%;
}

.details-shell {
  background: rgb(var(--v-theme-surface)) !important;
  border: 1px solid rgba(var(--v-theme-neutral-base), 0.08);
  border-radius: 16px;
  overflow: hidden;
}

.details-shell__intro {
  padding: 20px 20px 4px;
}

.details-shell__tabs {
  padding-inline: 12px;
}

.details-shell__window {
  background: rgba(var(--v-theme-surface), 0.5);
}

.route-visualizer__json-toggle {
  border: 1px solid rgba(var(--v-theme-neutral-base), 0.08);
  border-radius: 14px;
  overflow: hidden;
}

.json-toggle-btn {
  justify-content: center;
  letter-spacing: 0.01em;
}

.json-block {
  background: rgb(var(--v-theme-surface-variant));
  border-radius: 10px;
  font-family: Consolas, 'Courier New', monospace;
  font-size: 0.8rem;
  line-height: 1.45;
  margin: 0;
  max-height: min(50vh, 480px);
  overflow: auto;
  padding: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

:global(.leaflet-container) {
  font-family: inherit;
}

:global(.facility-red-icon) {
  background: transparent;
  border: 0;
}

:global(.facility-red-icon span) {
  background: rgb(var(--v-theme-facility-marker-fill));
  border: 3px solid rgb(var(--v-theme-surface));
  border-radius: 999px;
  box-shadow: 0 0 0 1px rgba(var(--v-theme-neutral-base), 0.25);
  display: block;
  height: 24px;
  width: 24px;
}

:global(.leaflet-div-icon.container-fill-marker) {
  background: transparent !important;
  border: none !important;
}

:global(.container-fill-marker__pin) {
  align-items: flex-start;
  display: flex;
  justify-content: center;
  line-height: 0;
}

@media (max-width: 960px) {
  .map-stage {
    display: flex;
    flex-direction: column;
  }

  .map-stage__map-wrap {
    flex-shrink: 0;
    height: 50vh;
    max-height: 520px;
    min-height: 300px;
  }

  .map-stage__drawer {
    background: linear-gradient(
      180deg,
      rgb(var(--v-theme-drawer-backdrop-gradient-start)) 0%,
      rgb(var(--v-theme-surface)) 100%
    );
    border-top: 1px solid rgba(var(--v-theme-neutral-base), 0.06);
    bottom: auto;
    flex-direction: column;
    gap: 12px;
    left: auto;
    max-height: none;
    overflow: visible;
    padding: 12px;
    pointer-events: auto;
    position: relative;
    top: auto;
    width: 100%;
  }

  .navigator-controller--drawer {
    max-width: 100%;
  }

  .map-stage__drawer-cards {
    flex-direction: row;
    flex: 0 0 auto;
    gap: 12px;
    overflow-x: auto;
    overflow-y: hidden;
    padding-bottom: 4px;
    width: 100%;
  }

  .map-stage__drawer-cards .drawer-card {
    flex: 0 0 min(300px, 85vw);
    max-height: 260px;
  }

  .entity-list {
    max-height: 160px;
  }
}
</style>
