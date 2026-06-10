<template>
  <v-card
    class="location-picker"
    color="surface"
    variant="outlined"
  >
    <div class="location-picker__header">
      <div class="location-picker__copy">
        <v-chip
          color="info"
          size="small"
          variant="tonal"
          class="mb-2"
        >
          {{ t('common.map.selector.badge') }}
        </v-chip>
        <h3 class="location-picker__title text-primary">{{ t('common.map.selector.title') }}</h3>
        <p class="location-picker__subtitle text-primary">{{ t('common.map.selector.description') }}</p>
      </div>
      <v-btn
        color="primary"
        variant="tonal"
        size="small"
        prepend-icon="mdi-crosshairs-gps"
        @click="centerOnCurrentPoint"
      >
        {{ t('common.map.selector.centerButton') }}
      </v-btn>
    </div>

    <div
      ref="mapContainer"
      class="location-picker__map"
    />

    <div class="location-picker__footer">
      <v-card
        class="location-picker__meta"
        color="info"
        variant="tonal"
      >
        <span class="location-picker__meta-label">{{ t('common.map.latitude') }}</span>
        <strong>{{ formattedLatitude }}</strong>
      </v-card>
      <v-card
        class="location-picker__meta"
        color="info"
        variant="tonal"
      >
        <span class="location-picker__meta-label">{{ t('common.map.longitude') }}</span>
        <strong>{{ formattedLongitude }}</strong>
      </v-card>
    </div>
  </v-card>
</template>

<script lang="ts" setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { loadLeaflet } from '../../utils/leaflet';
import { createMapPinIcon } from '../../utils/map-pin-icon';

interface Props {
  latitude: number;
  longitude: number;
}

interface LeafletMap {
  setView(coords: [number, number], zoom: number): LeafletMap;
  on(event: string, handler: (event: any) => void): LeafletMap;
  off(event: string, handler?: (event: any) => void): LeafletMap;
  remove(): void;
  invalidateSize(): void;
  getZoom(): number;
}

interface LeafletMarker {
  setLatLng(coords: [number, number]): LeafletMarker;
  addTo(map: LeafletMap): LeafletMarker;
}

interface LeafletNamespace {
  map(element: HTMLElement): LeafletMap;
  tileLayer(url: string, options: Record<string, unknown>): { addTo(map: LeafletMap): void };
  marker(coords: [number, number], options?: Record<string, unknown>): LeafletMarker;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  (e: 'update:location', value: { latitude: number; longitude: number }): void;
}>();

const { t } = useI18n();

const mapContainer = ref<HTMLDivElement | null>(null);
const mapInstance = ref<LeafletMap | null>(null);
const markerInstance = ref<LeafletMarker | null>(null);

const DEFAULT_ZOOM = 15;
const FALLBACK_LATITUDE = 28.4636;
const FALLBACK_LONGITUDE = -16.2518;

const formattedLatitude = computed(() => props.latitude.toFixed(6));
const formattedLongitude = computed(() => props.longitude.toFixed(6));

const hasSelectedLocation = computed(() =>
  Number.isFinite(props.latitude)
  && Number.isFinite(props.longitude)
  && !(props.latitude === 0 && props.longitude === 0),
);

const currentCoordinates = computed<[number, number]>(() => {
  if (hasSelectedLocation.value) {
    return [props.latitude, props.longitude];
  }
  return [FALLBACK_LATITUDE, FALLBACK_LONGITUDE];
});

let clickHandler: ((event: any) => void) | null = null;

const syncMarker = (coords: [number, number], updateView = true) => {
  if (!markerInstance.value || !mapInstance.value) {
    return;
  }

  markerInstance.value.setLatLng(coords);
  if (updateView) {
    mapInstance.value.setView(coords, mapInstance.value.getZoom());
  }
};

const emitLocation = (latitude: number, longitude: number) => {
  emit('update:location', {
    latitude: Number(latitude.toFixed(6)),
    longitude: Number(longitude.toFixed(6)),
  });
};

const centerOnCurrentPoint = () => {
  if (!mapInstance.value) {
    return;
  }
  mapInstance.value.setView(currentCoordinates.value, DEFAULT_ZOOM);
};

const initializeMap = async () => {
  if (!mapContainer.value || mapInstance.value) {
    return;
  }

  const L = (await loadLeaflet()) as unknown as LeafletNamespace;
  const initialCoordinates = currentCoordinates.value;

  const map = L.map(mapContainer.value).setView(initialCoordinates, DEFAULT_ZOOM);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(map);

  const marker = L.marker(initialCoordinates, {
    draggable: false,
    icon: createMapPinIcon('success'),
  }).addTo(map);

  clickHandler = (event: any) => {
    const latitude = event.latlng.lat;
    const longitude = event.latlng.lng;
    const coords: [number, number] = [latitude, longitude];
    syncMarker(coords, false);
    emitLocation(latitude, longitude);
  };

  map.on('click', clickHandler);

  mapInstance.value = map;
  markerInstance.value = marker;

  setTimeout(() => map.invalidateSize(), 0);
};

const cleanupMap = () => {
  if (mapInstance.value && clickHandler) {
    mapInstance.value.off('click', clickHandler);
  }
  if (mapInstance.value) {
    mapInstance.value.remove();
  }
  mapInstance.value = null;
  markerInstance.value = null;
  clickHandler = null;
};

watch(
  () => [props.latitude, props.longitude] as const,
  ([latitude, longitude]) => {
    if (!mapInstance.value || !markerInstance.value) {
      return;
    }
    syncMarker([latitude, longitude]);
  },
);

onMounted(() => {
  void initializeMap();
});

onUnmounted(() => {
  cleanupMap();
});
</script>

<style scoped>
.location-picker {
  border-radius: 16px;
  padding: 16px;
}

.location-picker__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 14px;
}

.location-picker__copy {
  max-width: 680px;
}

.location-picker__title {
  margin: 0;
  font-size: 1.05rem;
  line-height: 1.25;
}

.location-picker__subtitle {
  margin: 6px 0 0;
  line-height: 1.45;
}

.location-picker__map {
  width: 100%;
  height: 340px;
  border-radius: 14px;
  overflow: hidden;
}

.location-picker__footer {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.location-picker__meta {
  border-radius: 12px;
  padding: 12px 14px;
}

.location-picker__meta-label {
  display: block;
  margin-bottom: 4px;
  font-size: 0.78rem;
}

:global(.leaflet-container) {
  font-family: inherit;
}

@media (max-width: 640px) {
  .location-picker {
    padding: 14px;
  }

  .location-picker__header {
    flex-direction: column;
  }

  .location-picker__map {
    height: 280px;
  }

  .location-picker__footer {
    grid-template-columns: 1fr;
  }
}
</style>
