<template>
  <div class="mt-4">
    <v-row class="ma-0" dense>
      <v-col cols="12" md="3">
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

            <v-checkbox
              v-for="facility in selectableFacilities"
              :key="facility.id"
              v-model="selectedFacilityIds"
              :value="facility.id"
              :label="facilityLabel(facility)"
              color="primary"
              density="compact"
              hide-details
              class="mb-1"
            />
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="9">
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
const mapContainer = ref<HTMLDivElement | null>(null);
const mapInstance = ref<LeafletMap | null>(null);
const markersLayer = ref<LeafletLayerGroup | null>(null);
const leaflet = ref<LeafletNamespace | null>(null);

const selectedFacilityIds = ref<string[]>([]);

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

  selectedFacilities.value.forEach((facility) => {
    if (hasCoordinates(facility.location)) {
      const point: [number, number] = [facility.location.latitude, facility.location.longitude];
      boundsPoints.push(point);
      L.marker(point, { icon: facilityIcon })
        .addTo(layer)
        .bindPopup(`<strong>Facility</strong><br>${facility.id ?? '-'}`);
    }

    (facility.assignedContainers ?? []).forEach((container) => {
      const containerLocation = extractContainerLocation(container);
      if (!hasCoordinates(containerLocation)) {
        return;
      }

      const point: [number, number] = [containerLocation.latitude, containerLocation.longitude];
      boundsPoints.push(point);
      L.marker(point)
        .addTo(layer)
        .bindPopup(`<strong>Container</strong><br>${extractContainerId(container) ?? '-'}`);
    });
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
