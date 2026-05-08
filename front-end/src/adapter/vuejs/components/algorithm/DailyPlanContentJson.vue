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
              :key="facility.id"
              class="selector-row"
            >
              <v-btn
                color="accent"
                :variant="isFacilitySelected(facility.id) ? 'flat' : 'tonal'"
                class="selector-btn"
                @click="toggleFacilitySelection(facility.id)"
              >
                {{ facilityLabel(facility) }}
              </v-btn>
              <v-btn
                icon="mdi-eye"
                color="primary"
                variant="text"
                :title="t('infrastructurePlan.show.daily.content.viewFacility')"
                @click="goToFacility(facility.id)"
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="8">
        <v-card variant="flat" class="map-card">
          <v-card-title class="d-flex align-center ga-2">
            <v-icon icon="mdi-map" color="primary" />
            <span>{{ t('infrastructurePlan.show.daily.content.mapTitle') }}</span>
          </v-card-title>
          <v-divider />
          <v-card-text>
            <div ref="mapContainer" class="daily-map" />
          </v-card-text>
        </v-card>
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
              class="selector-row"
            >
              <v-btn
                color="accent"
                :variant="isVehicleSelected(route.key) ? 'flat' : 'tonal'"
                class="selector-btn"
                @click="toggleVehicleSelection(route.key)"
              >
                {{ vehicleRouteLabel(route) }}
              </v-btn>
              <v-btn
                icon="mdi-eye"
                color="primary"
                variant="text"
                :title="t('infrastructurePlan.show.daily.content.viewVehicle')"
                @click="goToVehicle(route.vehicleId)"
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

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
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

interface DailyPlanLike {
  id?: string;
  infrastructurePlanId?: string;
  facilityId?: string;
  serviceDate?: unknown;
  planDay?: number;
  vehicleId?: string;
  totalCollectedKilograms?: unknown;
  totalCollectedLiters?: unknown;
  totalDistanceMeters?: unknown;
  stops?: unknown[];
}

interface VehicleRouteOption {
  key: string;
  vehicleId: string;
  facilityId: string;
  dailyPlan: DailyPlanLike;
}

interface FacilityLike {
  id?: string;
  facilityType?: string;
  status?: string;
  location?: unknown;
  capacities?: unknown;
  assignedContainers?: unknown[];
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
  dailyPlans: DailyPlanLike[];
  facilities: FacilityLike[];
}>();

const { t } = useI18n();
const router = useRouter();
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
  props.facilities.filter((facility): facility is FacilityLike & { id: string } =>
    typeof facility.id === 'string' && facility.id.length > 0,
  ),
);

const selectedFacilities = computed(() => {
  const ids = new Set(selectedFacilityIds.value);
  return selectableFacilities.value.filter((facility) => ids.has(facility.id!));
});

const selectableVehicleRoutes = computed<VehicleRouteOption[]>(() => {
  const selectedIds = new Set(selectedFacilityIds.value);
  const deduped = new Map<string, VehicleRouteOption>();

  props.dailyPlans.forEach((dailyPlan) => {
    const vehicleId = dailyPlan.vehicleId;
    const facilityId = dailyPlan.facilityId;
    if (typeof vehicleId !== 'string' || vehicleId.length === 0) return;
    if (typeof facilityId !== 'string' || facilityId.length === 0) return;
    if (!selectedIds.has(facilityId)) return;

    const key = `${facilityId}::${vehicleId}`;
    if (!deduped.has(key)) {
      deduped.set(key, { key, vehicleId, facilityId, dailyPlan });
    }
  });

  return Array.from(deduped.values());
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
  () => selectableFacilities.value.map((facility) => facility.id),
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

function facilityLabel(facility: FacilityLike): string {
  const type = facility.facilityType ? ` (${facility.facilityType})` : '';
  return `${facility.id ?? '-'}${type}`;
}

function vehicleRouteLabel(route: VehicleRouteOption): string {
  return `${route.vehicleId} (${route.facilityId})`;
}

function isFacilitySelected(id: string | undefined): boolean {
  return typeof id === 'string' && selectedFacilityIds.value.includes(id);
}

function toggleFacilitySelection(id: string | undefined): void {
  if (typeof id !== 'string' || id.length === 0) return;
  const set = new Set(selectedFacilityIds.value);
  if (set.has(id)) {
    set.delete(id);
  } else {
    set.add(id);
  }
  selectedFacilityIds.value = Array.from(set);
}

function isVehicleSelected(key: string): boolean {
  return selectedVehicleRouteKeys.value.includes(key);
}

function toggleVehicleSelection(key: string): void {
  const set = new Set(selectedVehicleRouteKeys.value);
  if (set.has(key)) {
    set.delete(key);
  } else {
    set.add(key);
  }
  selectedVehicleRouteKeys.value = Array.from(set);
}

function goToFacility(id: string | undefined): void {
  if (!id) return;
  void router.push({ name: 'ShowFacility', params: { id } });
}

function goToVehicle(id: string | undefined): void {
  if (!id) return;
  void router.push({ name: 'ShowVehicle', params: { id } });
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

function extractContainerId(container: unknown): string | undefined {
  if (!container || typeof container !== 'object') {
    return undefined;
  }
  const id = (container as Record<string, unknown>).id;
  return typeof id === 'string' ? id : undefined;
}

function extractContainerLocation(container: unknown): unknown {
  if (!container || typeof container !== 'object') {
    return undefined;
  }
  return (container as Record<string, unknown>).location;
}

function extractStopContainerId(stop: unknown): string | undefined {
  if (!stop || typeof stop !== 'object') {
    return undefined;
  }
  const containerId = (stop as Record<string, unknown>).containerId;
  return typeof containerId === 'string' ? containerId : undefined;
}

function extractStopSequence(stop: unknown): number {
  if (!stop || typeof stop !== 'object') {
    return Number.MAX_SAFE_INTEGER;
  }

  const sequence = (stop as Record<string, unknown>).sequence;
  if (typeof sequence === 'number') {
    return sequence;
  }
  if (sequence && typeof sequence === 'object') {
    const sequenceValue = (sequence as Record<string, unknown>).value;
    if (typeof sequenceValue === 'number') {
      return sequenceValue;
    }
  }
  return Number.MAX_SAFE_INTEGER;
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
  const selectedFacilityMap = new Map(selectedFacilities.value.map((facility) => [facility.id ?? '', facility]));
  const routeContainerLocationMap = new Map<string, [number, number]>();

  selectedFacilities.value.forEach((facility) => {
    if (hasCoordinates(facility.location)) {
      const point: [number, number] = [facility.location.latitude, facility.location.longitude];
      boundsPoints.push(point);
      L.marker(point, { icon: facilityIcon })
        .addTo(layer)
        .bindPopup(`<strong>Facility</strong><br>${facility.id ?? '-'}`);
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

      orderedStops.forEach((stop) => {
        const containerId = extractStopContainerId(stop);
        if (!containerId) return;
        const containerPoint = routeContainerLocationMap.get(containerId);
        if (!containerPoint) return;
        routePoints.push(containerPoint);
      });

      if (routePoints.length >= 2) {
        L.polyline(routePoints, {
          color: '#00a83a',
          weight: 5,
          opacity: 0.95,
        }).addTo(layer);
      }

      boundsPoints.push(...routePoints);
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

.daily-map {
  width: 100%;
  height: 420px;
  border-radius: 10px;
  overflow: hidden;
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

@media (max-width: 960px) {
  .daily-map {
    height: 320px;
  }
}
</style>
