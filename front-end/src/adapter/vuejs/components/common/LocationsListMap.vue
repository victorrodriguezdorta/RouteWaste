<template>
  <v-card
    v-if="validLocations.length > 0"
    class="locations-list-map"
    :class="{ 'locations-list-map--no-header': !showHeader }"
    color="surface"
    variant="outlined"
  >
    <div
      v-if="showHeader"
      class="locations-list-map__header"
    >
      <div class="locations-list-map__actions">
        <v-chip
          v-if="resolvedEyebrow"
          color="info"
          size="large"
          variant="tonal"
          class="locations-list-map__chip"
        >
          {{ resolvedEyebrow }}
        </v-chip>
      </div>

      <div class="locations-list-map__heading">
        <h3 class="locations-list-map__title text-primary">{{ resolvedTitle }}</h3>
        <p v-if="subtitle" class="locations-list-map__subtitle text-primary">{{ subtitle }}</p>
      </div>
    </div>

    <div
      ref="mapContainer"
      class="locations-list-map__map"
    />
  </v-card>

  <v-alert
    v-else
    type="info"
    variant="tonal"
    class="locations-list-map locations-list-map--empty"
  >
    {{ t('common.map.listOverview.empty') }}
  </v-alert>
</template>

<script lang="ts">
export interface MapLocationPin {
  /** When set together with `detailRouteName` on the map, opens this entity on marker click. */
  id?: string;
  latitude: number;
  longitude: number;
  label: string;
  /**
   * Colored map-pin marker: gray (`muted`), theme primary (`primary`) or green (`success`).
   * Defaults to `success` (list views).
   */
  markerTone?: 'muted' | 'primary' | 'success';
}
</script>

<script lang="ts" setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import type { RouteRecordNameGeneric } from 'vue-router';
import router from '../../router/router';
import { loadLeaflet } from '../../utils/leaflet';
import { createMapPinIcon, type MapPinTone } from '../../utils/map-pin-icon';

interface Props {
  locations: MapLocationPin[];
  title?: string;
  subtitle?: string;
  eyebrow?: string;
  /** When true, shows chip (eyebrow), title and optional subtitle. Default false (map only). */
  showHeader?: boolean;
  /** Route `name` (e.g. `ShowContainer`) used with each pin's `id` when the marker is clicked. */
  detailRouteName?: RouteRecordNameGeneric;
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  subtitle: '',
  eyebrow: '',
  showHeader: false,
});

const { t } = useI18n();

const mapContainer = ref<HTMLDivElement | null>(null);
const mapInstance = ref<LeafletMap | null>(null);
const markersLayer = ref<LeafletLayerGroup | null>(null);
const leafletRef = ref<LeafletNamespace | null>(null);

interface LeafletMap {
  setView(coords: [number, number], zoom: number): LeafletMap;
  fitBounds(bounds: LeafletLatLngBounds, options?: Record<string, unknown>): LeafletMap;
  invalidateSize(): void;
  remove(): void;
}

interface LeafletLatLngBounds {
  getCenter(): { lat: number; lng: number };
  pad(padding: number): LeafletLatLngBounds;
}

interface LeafletLayerGroup {
  clearLayers(): void;
  addTo(map: LeafletMap): LeafletLayerGroup;
}

interface LeafletMarker {
  addTo(layer: LeafletLayerGroup): LeafletMarker;
  bindTooltip(content: string, options?: Record<string, unknown>): LeafletMarker;
  on(event: string, handler: () => void): LeafletMarker;
}

interface LeafletDivIcon {
  // opaque Leaflet Icon instance
}

interface LeafletNamespace {
  map(element: HTMLElement): LeafletMap;
  tileLayer(url: string, options: Record<string, unknown>): { addTo(map: LeafletMap): void };
  marker(coords: [number, number], options?: { icon?: LeafletDivIcon }): LeafletMarker;
  divIcon(options: {
    className?: string;
    html?: string;
    iconSize?: [number, number];
    iconAnchor?: [number, number];
  }): LeafletDivIcon;
  layerGroup(): LeafletLayerGroup;
  latLngBounds(points: [number, number][] | [number, number]): LeafletLatLngBounds;
}

const clampLatitude = (value: number) => Math.min(Math.max(value, -90), 90);
const clampLongitude = (value: number) => Math.min(Math.max(value, -180), 180);

const escapeHtml = (value: string) =>
  value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');

const validLocations = computed(() =>
  props.locations.filter(
    (p) =>
      Number.isFinite(p.latitude)
      && Number.isFinite(p.longitude)
      && typeof p.label === 'string',
  ),
);

const resolvedTitle = computed(() => props.title || t('common.map.listOverview.title'));
const resolvedEyebrow = computed(() => props.eyebrow || t('common.map.listOverview.eyebrow'));

const canNavigateFromPin = (pin: MapLocationPin) =>
  Boolean(props.detailRouteName && pin.id && String(pin.id).length > 0);

const destroyMap = () => {
  if (mapInstance.value) {
    mapInstance.value.remove();
  }
  mapInstance.value = null;
  markersLayer.value = null;
};

const renderMarkers = () => {
  const L = leafletRef.value;
  if (!L || !mapInstance.value || !markersLayer.value) {
    return;
  }

  const layer = markersLayer.value;
  layer.clearLayers();

  const pts = [...validLocations.value].sort((a, b) => {
    const rank = (p: MapLocationPin) => (p.markerTone === 'primary' ? 1 : 0);
    return rank(a) - rank(b);
  });
  const latlngs: [number, number][] = pts.map((p) => [clampLatitude(p.latitude), clampLongitude(p.longitude)]);

  pts.forEach((p, i) => {
    const coord = latlngs[i];
    const tone: MapPinTone = p.markerTone ?? 'success';
    const marker = L.marker(coord, {
      icon: createMapPinIcon(tone, canNavigateFromPin(p)),
    })
      .addTo(layer)
      .bindTooltip(escapeHtml(p.label), {
        sticky: true,
        direction: 'top',
      });

    if (canNavigateFromPin(p)) {
      marker.on('click', () => {
        void router.push({
          name: props.detailRouteName,
          params: { id: String(p.id) },
        });
      });
    }
  });

  if (latlngs.length === 1) {
    mapInstance.value.setView(latlngs[0], 14);
  } else {
    const bounds = L.latLngBounds(latlngs).pad(0.08);
    mapInstance.value.fitBounds(bounds, { padding: [28, 28], maxZoom: 15 });
  }

  setTimeout(() => mapInstance.value?.invalidateSize(), 0);
};

const initializeMap = async () => {
  if (!mapContainer.value || mapInstance.value || validLocations.value.length === 0) {
    return;
  }

  const L = (await loadLeaflet()) as unknown as LeafletNamespace;
  leafletRef.value = L;

  const first = validLocations.value[0];
  const start: [number, number] = [clampLatitude(first.latitude), clampLongitude(first.longitude)];

  const map = L.map(mapContainer.value).setView(start, 14);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(map);

  const layer = L.layerGroup().addTo(map);
  mapInstance.value = map;
  markersLayer.value = layer;

  await nextTick();
  renderMarkers();
  setTimeout(() => map.invalidateSize(), 0);
};

const syncMap = async () => {
  if (validLocations.value.length === 0) {
    destroyMap();
    return;
  }

  await nextTick();

  if (!mapInstance.value) {
    await initializeMap();
    return;
  }

  renderMarkers();
};

onMounted(() => {
  void syncMap();
});

watch(
  () => props.locations,
  () => {
    void syncMap();
  },
  { deep: true },
);

onUnmounted(() => {
  destroyMap();
});
</script>

<style scoped>
.locations-list-map {
  border-radius: 18px;
  padding: 18px;
  margin-top: 24px;
}

.locations-list-map--no-header {
  padding-top: 14px;
  padding-bottom: 14px;
}

.locations-list-map--empty {
  border-radius: 18px;
  margin-top: 24px;
}

.locations-list-map__header {
  display: grid;
  gap: 14px;
  margin-bottom: 16px;
}

.locations-list-map__actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.locations-list-map__heading {
  display: grid;
  gap: 4px;
}

.locations-list-map__chip {
  font-weight: 600;
}

.locations-list-map__title {
  margin: 0;
  font-size: 1.15rem;
  line-height: 1.2;
}

.locations-list-map__subtitle {
  margin: 0;
  line-height: 1.45;
}

.locations-list-map__map {
  width: 100%;
  height: 320px;
  border-radius: 14px;
  border: 1px solid rgb(var(--v-theme-border-light));
  overflow: hidden;
}

@media (max-width: 640px) {
  .locations-list-map {
    padding: 14px;
  }

  .locations-list-map__map {
    height: 260px;
  }
}

.locations-list-map__map :deep(.leaflet-container) {
  font-family: inherit;
  height: 100%;
  width: 100%;
}

</style>
