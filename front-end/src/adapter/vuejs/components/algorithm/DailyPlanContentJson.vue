<template>
  <div class="mt-4">
    <v-row class="ma-0" dense>
      <v-col cols="12" md="2">
        <v-card variant="flat" class="selector-card h-100">
          <v-card-title class="d-flex align-center ga-2">
            <v-icon icon="mdi-factory" color="primary" />
            <span>{{ t('infrastructurePlan.show.daily.content.selectorTitle') }}</span>
          </v-card-title>
          <v-divider />
          <v-card-text>
            <p class="text-body-2 text-medium-emphasis mb-3">
              {{ t('infrastructurePlan.show.daily.content.selectorHint') }}
            </p>

            <div v-if="selectableFacilities.length === 0" class="text-body-2 text-medium-emphasis">
              {{ t('infrastructurePlan.show.daily.content.noFacilities') }}
            </div>

            <div
              v-for="facility in selectableFacilities"
              :key="facility.id.getValue()"
              class="entity-action-row mb-2"
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
                color="white"
                class="entity-button flex-grow-1"
                :class="{ 'entity-button-selected': isFacilitySelected(facility.id.getValue()) }"
                :eventclick="() => toggleFacilitySelection(facility.id.getValue())"
              />
              <ButtonTooltip
                text=""
                :tooltip="viewTooltip"
                icon="mdi-eye"
                size="small"
                variant="flat"
                color="white"
                class="entity-view-button"
                :eventclick="() => openFacility(facility.id.getValue())"
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="8">
        <v-card variant="flat" class="map-card">
          <v-card-title class="map-card__header">
            <div class="d-flex align-center ga-2">
              <v-icon icon="mdi-map" color="primary" />
              <span>{{ t('infrastructurePlan.show.daily.content.mapTitle') }}</span>
            </div>

            <div class="navigator-controller">
              <v-btn
                icon="mdi-chevron-left"
                variant="text"
                color="white"
                :disabled="!canGoPrevious"
                @click="emit('go-previous')"
              />

              <div class="navigator-controller__title">
                <v-icon icon="mdi-calendar-range" color="white" />
                <span>{{ serviceDate ?? '-' }}</span>
              </div>

              <div class="day-indicator">
                {{ t('infrastructurePlan.show.daily.navigator.dayLabel', { current: currentDay, total: totalDays }) }}
              </div>

              <v-btn
                icon="mdi-chevron-right"
                variant="text"
                color="white"
                :disabled="!canGoNext"
                @click="emit('go-next')"
              />
            </div>
          </v-card-title>
          <v-divider />
          <v-card-text>
            <div ref="mapContainer" class="daily-map" />
          </v-card-text>
        </v-card>

        <DailyPlanRouteTimeline :routes="selectedVehicleRoutes" />
      </v-col>

      <v-col cols="12" md="2">
        <v-card variant="flat" class="selector-card h-100">
          <v-card-title class="d-flex align-center ga-2">
            <v-icon icon="mdi-truck" color="primary" />
            <span>{{ t('infrastructurePlan.show.daily.content.vehiclesTitle') }}</span>
          </v-card-title>
          <v-divider />
          <v-card-text>
            <p class="text-body-2 text-medium-emphasis mb-3">
              {{ t('infrastructurePlan.show.daily.content.vehiclesHint') }}
            </p>

            <div v-if="selectableVehicleRoutes.length === 0" class="text-body-2 text-medium-emphasis">
              {{ t('infrastructurePlan.show.daily.content.noVehicles') }}
            </div>

            <div
              v-for="route in selectableVehicleRoutes"
              :key="route.key"
              class="entity-action-row mb-2"
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
                :text="vehicleButtonLabel(route.vehicleId)"
                :tooltip="route.vehicleId"
                icon=""
                size="small"
                variant="flat"
                color="white"
                class="entity-button flex-grow-1"
                :class="{ 'entity-button-selected': isVehicleRouteSelected(route.key) }"
                :eventclick="() => toggleVehicleRouteSelection(route.key)"
              />
              <ButtonTooltip
                text=""
                :tooltip="viewTooltip"
                icon="mdi-eye"
                size="small"
                variant="flat"
                color="white"
                class="entity-view-button"
                :eventclick="() => openVehicle(route.vehicleId)"
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-card variant="flat" class="mt-4 monitoring-card">
      <v-card-title class="d-flex align-center ga-2">
        <v-icon icon="mdi-trash-can-outline" color="primary" />
        <span>Container monitoring for day {{ planDay }}</span>
      </v-card-title>

      <v-divider />

      <v-card-text>
        <div v-if="selectedFacilityMonitoring.length === 0" class="text-body-2 text-medium-emphasis">
          Select a facility to inspect its containers for the selected day.
        </div>

        <div
          v-for="facilityMonitoring in selectedFacilityMonitoring"
          :key="facilityMonitoring.facility.id.getValue()"
          class="monitoring-facility mb-4"
        >
          <div class="monitoring-facility__header">
            <div>
              <div class="text-subtitle-1 font-weight-bold">
                {{ formatFacilityType(facilityMonitoring.facility.facilityType) }}
              </div>
              <div class="text-body-2 text-medium-emphasis">
                {{ facilityMonitoring.facility.id.getValue() }}
              </div>
            </div>
            <div class="text-body-2 text-medium-emphasis">
              {{ facilityMonitoring.containers.length }} containers
            </div>
          </div>

          <div v-if="facilityMonitoring.containers.length === 0" class="text-body-2 text-medium-emphasis mt-2">
            No containers assigned to this facility.
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
                    Container {{ truncateIdentifier(containerMonitoring.container.id.getValue(), 12) }}
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
                  {{ monitoringStatusLabel(containerMonitoring.state?.status) }}
                </v-chip>
              </div>

              <div class="monitoring-item__metrics">
                <div>
                  <strong>Filling:</strong>
                  {{ formatLiters(containerMonitoring.state?.dailyFillingLiters) }}
                </div>
                <div>
                  <strong>Capacity:</strong>
                  {{ formatLiters(containerMonitoring.state?.containerCapacityLiters?.getLiters?.() ?? containerMonitoring.container.capacityLiters.getLiters()) }}
                </div>
                <div>
                  <strong>Demand/day:</strong>
                  {{ formatLiters(containerMonitoring.state?.dailyDemandLitersPerDay?.getLitersPerDay?.() ?? containerMonitoring.container.dailyDemandLitersPerDay.getLitersPerDay()) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </v-card-text>
    </v-card>

    <v-card variant="flat" class="mt-4">
      <v-card-title class="d-flex align-center ga-2">
        <v-icon icon="mdi-code-json" color="primary" />
        <span>{{ t('infrastructurePlan.show.daily.content.title') }}</span>
      </v-card-title>

      <v-divider />

      <v-card-text>
        <pre class="json-block">{{ jsonContent }}</pre>
      </v-card-text>
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
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import router from '../../router/router';
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

const LEAFLET_CSS_ID = 'leaflet-stylesheet';
const LEAFLET_SCRIPT_ID = 'leaflet-script';
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

const selectedFacilityMonitoring = computed(() => {
  const selectedDay = normalizePlanDay(props.planDay);
  const monitoringByContainerId = new Map(
    props.containerStateMonitoring
      .filter((state) => normalizePlanDay(state.planDay) === selectedDay)
      .map((state) => [normalizeIdentifier(state.containerId.getValue()), state]),
  );

  return selectedFacilities.value.map((facility) => ({
    facility,
    containers: (facility.assignedContainers ?? []).map((container) => ({
      container,
      state: monitoringByContainerId.get(normalizeIdentifier(container.id.getValue())),
    })),
  }));
});

const jsonContent = computed(() => {
  const dayInfo = {
    planDay: props.planDay,
    serviceDate: props.serviceDate ?? null,
    totals: {
      dailyPlans: props.dailyPlans.length,
      facilities: props.facilities.length,
      stops: props.dailyPlans.reduce((acc, plan) => acc + (Array.isArray(plan.stops) ? plan.stops.length : 0), 0),
    },
    facilities: props.facilities,
    dailyPlans: props.dailyPlans,
  };

  return JSON.stringify(dayInfo, null, 2);
});

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

watch(
  () => props.facilities,
  () => {
    renderMarkers();
  },
  { deep: true },
);

onMounted(() => {
  void initializeMap();
});

onUnmounted(() => {
  mapInstance.value?.remove();
  mapInstance.value = null;
  markersLayer.value = null;
});

function facilityButtonLabel(facility: InfrastructurePlanFacilityDetail): string {
  return formatFacilityType(facility.facilityType);
}

function facilityTooltip(facility: InfrastructurePlanFacilityDetail): string {
  const facilityType = formatFacilityType(facility.facilityType);
  const facilityId = facility.id.getValue();
  return `${facilityType} (${facilityId})`;
}

function vehicleButtonLabel(vehicleId: string): string {
  return truncateIdentifier(vehicleId, 8);
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
    return 'No data';
  }

  return String(status)
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (character) => character.toUpperCase());
}

function truncateIdentifier(value: string, visibleCharacters: number): string {
  if (value.length <= visibleCharacters) {
    return value;
  }
  return `${value.slice(0, visibleCharacters)}...`;
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

async function ensureLeafletAssets(): Promise<LeafletNamespace> {
  const leafletWindow = window as Window & { L?: LeafletNamespace; __leafletLoader?: Promise<LeafletNamespace> };

  if (leafletWindow.L) {
    return leafletWindow.L;
  }

  if (!document.getElementById(LEAFLET_CSS_ID)) {
    const link = document.createElement('link');
    link.id = LEAFLET_CSS_ID;
    link.rel = 'stylesheet';
    link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
    document.head.appendChild(link);
  }

  if (!leafletWindow.__leafletLoader) {
    leafletWindow.__leafletLoader = new Promise((resolve, reject) => {
      const existingScript = document.getElementById(LEAFLET_SCRIPT_ID) as HTMLScriptElement | null;
      if (existingScript) {
        existingScript.addEventListener('load', () => {
          if (leafletWindow.L) {
            resolve(leafletWindow.L);
            return;
          }
          reject(new Error('Leaflet failed to initialize'));
        });
        existingScript.addEventListener('error', () => reject(new Error('Leaflet failed to load')));
        return;
      }

      const script = document.createElement('script');
      script.id = LEAFLET_SCRIPT_ID;
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      script.async = true;
      script.onload = () => {
        if (leafletWindow.L) {
          resolve(leafletWindow.L);
          return;
        }
        reject(new Error('Leaflet failed to initialize'));
      };
      script.onerror = () => reject(new Error('Leaflet failed to load'));
      document.body.appendChild(script);
    });
  }

  return leafletWindow.__leafletLoader;
}

async function initializeMap(): Promise<void> {
  if (!mapContainer.value || mapInstance.value) {
    return;
  }

  const L = await ensureLeafletAssets();
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

  const boundsPoints: Array<[number, number]> = [];
  const selectedFacilityMap = new Map(selectedFacilities.value.map((facility) => [facility.id.getValue(), facility]));
  const routeContainerLocationMap = new Map<string, [number, number]>();

  selectedFacilities.value.forEach((facility) => {
    if (hasCoordinates(facility.location)) {
      const point: [number, number] = [facility.location.latitude, facility.location.longitude];
      boundsPoints.push(point);
      L.marker(point, { icon: facilityIcon })
        .addTo(layer)
        .bindPopup(`<strong>Facility</strong><br>${facility.id.getValue()}`);
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
      boundsPoints.push(point);
      L.marker(point)
        .addTo(layer)
        .bindPopup(`<strong>Container</strong><br>${containerId ?? '-'}`);
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
              color: '#b00020',
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
            color: '#1e88e5',
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
          color: '#00a83a',
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
.map-card,
.selector-card {
  border-radius: 12px;
}

.selector-card {
  background: rgb(var(--v-theme-surface-border-light));
}

.daily-map {
  width: 100%;
  height: 420px;
  border-radius: 10px;
  overflow: hidden;
}

.map-card__header {
  align-items: center;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: space-between;
}

.navigator-controller {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgb(var(--v-theme-primary));
  border-radius: 999px;
  color: #ffffff;
  padding: 8px 12px;
}

.navigator-controller__title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #ffffff;
  font-weight: 600;
  white-space: nowrap;
}

.day-indicator {
  color: #ffffff;
  font-weight: 600;
  min-width: 120px;
  text-align: center;
}

.entity-action-row {
  display: flex;
  align-items: center;
  gap: 2px;
}

.entity-checkbox {
  flex: 0 0 auto;
  margin: 0;
}

.entity-button,
.entity-view-button {
  background: #ffffff !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.entity-button {
  justify-content: flex-start;
  text-transform: none;
  padding-inline-start: 0 !important;
}

.entity-button-selected {
  box-shadow: inset 0 0 0 2px rgb(var(--v-theme-primary));
}

.entity-button :deep(.v-btn__content) {
  margin-left: -0.35rem;
}

.json-block {
  background: rgb(var(--v-theme-surface-variant));
  border-radius: 8px;
  font-family: Consolas, 'Courier New', monospace;
  font-size: 0.85rem;
  line-height: 1.45;
  margin: 0;
  max-height: 420px;
  overflow: auto;
  padding: 12px;
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
  width: 24px;
  height: 24px;
  display: block;
  border-radius: 999px;
  background: #d32f2f;
  border: 3px solid #ffffff;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.25);
}

.monitoring-card {
  border-radius: 12px;
}

.monitoring-facility {
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  border-radius: 12px;
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
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.monitoring-item {
  background: rgba(var(--v-theme-surface-variant), 0.55);
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  border-radius: 10px;
  padding: 12px;
}

.monitoring-item-overflowed {
  background: rgba(var(--v-theme-error), 0.08);
  border-color: rgba(var(--v-theme-error), 0.55);
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

@media (max-width: 960px) {
  .daily-map {
    height: 320px;
  }

  .map-card__header {
    align-items: stretch;
    flex-direction: column;
  }

  .navigator-controller {
    flex-wrap: wrap;
    justify-content: center;
    width: 100%;
  }
}
</style>
