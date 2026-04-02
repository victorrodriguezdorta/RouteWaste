<template>
  <v-card
    class="location-map"
    color="surface"
    variant="outlined"
  >
    <div class="location-map__header">
      <div class="location-map__actions">
        <v-chip
          v-if="eyebrow"
          color="info"
          size="large"
          variant="tonal"
          class="location-map__chip"
        >
          {{ eyebrow }}
        </v-chip>

        <v-btn
          color="primary"
          variant="elevated"
          size="large"
          append-icon="mdi-open-in-new"
          class="location-map__link"
          :href="openStreetMapLink"
          target="_blank"
          rel="noopener noreferrer"
        >
          {{ t('common.map.openMap') }}
        </v-btn>
      </div>

      <div class="location-map__heading">
        <h3 class="location-map__title text-primary">{{ title }}</h3>
        <p v-if="subtitle" class="location-map__subtitle text-primary">{{ subtitle }}</p>
      </div>
    </div>

    <div class="location-map__frame-shell">
      <iframe
        class="location-map__frame"
        :src="embedUrl"
        :title="title"
        loading="lazy"
        referrerpolicy="no-referrer-when-downgrade"
      />
    </div>

    <div class="location-map__meta">
      <v-card
        class="location-map__meta-item"
        color="info"
        variant="tonal"
      >
        <span class="location-map__meta-label">{{ t('common.map.latitude') }}</span>
        <strong>{{ formattedLatitude }}</strong>
      </v-card>
      <v-card
        class="location-map__meta-item"
        color="info"
        variant="tonal"
      >
        <span class="location-map__meta-label">{{ t('common.map.longitude') }}</span>
        <strong>{{ formattedLongitude }}</strong>
      </v-card>
    </div>
  </v-card>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

interface Props {
  latitude: number;
  longitude: number;
  title?: string;
  subtitle?: string;
  eyebrow?: string;
  zoom?: number;
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  subtitle: '',
  eyebrow: '',
  zoom: 16,
});

const { t } = useI18n();

const clampLatitude = (value: number) => Math.min(Math.max(value, -90), 90);
const clampLongitude = (value: number) => Math.min(Math.max(value, -180), 180);

const safeLatitude = computed(() => clampLatitude(props.latitude));
const safeLongitude = computed(() => clampLongitude(props.longitude));
const title = computed(() => props.title || t('common.map.title'));
const eyebrow = computed(() => props.eyebrow || t('common.map.eyebrow'));
const subtitle = computed(() => props.subtitle);

const formattedLatitude = computed(() => safeLatitude.value.toFixed(6));
const formattedLongitude = computed(() => safeLongitude.value.toFixed(6));

const bbox = computed(() => {
  const latPadding = 0.008;
  const lngPadding = 0.012;

  const minLon = clampLongitude(safeLongitude.value - lngPadding);
  const minLat = clampLatitude(safeLatitude.value - latPadding);
  const maxLon = clampLongitude(safeLongitude.value + lngPadding);
  const maxLat = clampLatitude(safeLatitude.value + latPadding);

  return `${minLon}%2C${minLat}%2C${maxLon}%2C${maxLat}`;
});

const marker = computed(() => `${safeLatitude.value}%2C${safeLongitude.value}`);

const embedUrl = computed(() =>
  `https://www.openstreetmap.org/export/embed.html?bbox=${bbox.value}&layer=mapnik&marker=${marker.value}`,
);

const openStreetMapLink = computed(() =>
  `https://www.openstreetmap.org/?mlat=${safeLatitude.value}&mlon=${safeLongitude.value}#map=${props.zoom}/${safeLatitude.value}/${safeLongitude.value}`,
);
</script>

<style scoped>
.location-map {
  border-radius: 18px;
  padding: 18px;
}

.location-map__header {
  display: grid;
  gap: 14px;
  margin-bottom: 16px;
}

.location-map__actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.location-map__heading {
  display: grid;
  gap: 4px;
}

.location-map__chip {
  font-weight: 600;
}

.location-map__title {
  margin: 0;
  font-size: 1.15rem;
  line-height: 1.2;
}

.location-map__subtitle {
  margin: 0;
  line-height: 1.45;
}

.location-map__link {
  white-space: nowrap;
  min-height: 42px;
  padding-inline: 16px;
}

.location-map__frame-shell {
  overflow: hidden;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.location-map__frame {
  display: block;
  width: 100%;
  height: 320px;
  border: 0;
}

.location-map__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.location-map__meta-item {
  border-radius: 12px;
  padding: 12px 14px;
}

.location-map__meta-label {
  display: block;
  margin-bottom: 4px;
  font-size: 0.78rem;
}

@media (max-width: 640px) {
  .location-map {
    padding: 14px;
  }

  .location-map__header {
    gap: 12px;
  }

  .location-map__frame {
    height: 260px;
  }

  .location-map__meta {
    grid-template-columns: 1fr;
  }
}
</style>
