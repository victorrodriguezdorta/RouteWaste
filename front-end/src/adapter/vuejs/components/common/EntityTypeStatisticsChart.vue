<template>
  <v-card v-if="statistics" variant="outlined" class="entity-stats">
    <v-card-text class="pa-3">
      <div class="entity-stats__title text-caption font-weight-medium text-medium-emphasis mb-2">
        {{ chartTitle }}
      </div>

      <v-alert
        v-if="!hasChartData"
        type="info"
        variant="tonal"
        density="compact"
        class="mb-0"
      >
        {{ emptyMessage }}
      </v-alert>

      <template v-else>
        <div ref="chartHostRef" class="entity-stats__chart">
          <PieChart
            v-if="chartReady"
            :id="chartId"
            :width_props="chartWidth"
            :height_props="chartHeight"
            :data="pieData"
          />
        </div>

        <div
          v-if="colorForType && legendItems.length > 0"
          class="entity-stats__legend mt-3"
        >
          <v-chip
            v-for="item in legendItems"
            :key="item.typeKey"
            :color="item.semanticColor"
            size="small"
            variant="flat"
            class="entity-stats__legend-chip"
          >
            <span class="entity-stats__legend-label">{{ item.label }}</span>
            <span class="entity-stats__legend-count">{{ item.count }}</span>
          </v-chip>
        </div>
      </template>
    </v-card-text>
  </v-card>
</template>

<script lang="ts" setup>
import type { PieChartDatum } from '@/adapter/vuejs/analytics/infrastructure-plan/pie-chart-datum';
import { vuetifyColorToHex } from '@/adapter/vuejs/utils/vuetify-semantic-color';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';
import { PieChart } from '@ull-tfg/ull-tfg-vue';
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { useTheme } from 'vuetify';

interface TypeSlice {
  typeKey: string;
  label: string;
  count: number;
}

const props = withDefaults(
  defineProps<{
    statistics?: EntityTypeStatistics;
    chartId: number;
    chartTitle: string;
    emptyMessage: string;
    translateType: (typeKey: string) => string;
    /** Same resolver as table chips (`vehicleTypeColor`, `wasteTypeColor`, …). */
    colorForType?: (typeKey: string) => string;
    chartWidth?: number;
    chartHeight?: number;
  }>(),
  {
    chartWidth: 320,
    chartHeight: 280,
  },
);

const theme = useTheme();
const chartReady = ref(false);
const chartHostRef = ref<HTMLElement | null>(null);

const slices = computed<TypeSlice[]>(() => {
  if (!props.statistics) {
    return [];
  }
  return props.statistics.byType
    .filter((entry) => entry.count > 0)
    .map((entry) => ({
      typeKey: entry.type,
      label: props.translateType(entry.type),
      count: entry.count,
    }));
});

/**
 * ull-tfg-vue PieChart sorts data ascending by `value` before drawing arcs.
 * Slice colors must use the same order as `path` elements in the SVG.
 */
const slicesSortedForPie = computed(() =>
  [...slices.value].sort((left, right) => left.count - right.count),
);

const pieData = computed<PieChartDatum[]>(() =>
  slices.value.map((slice) => ({
    name: slice.label,
    value: slice.count,
  })),
);

const hasChartData = computed(() => pieData.value.length > 0);

const legendItems = computed(() =>
  [...slices.value]
    .sort((left, right) => right.count - left.count)
    .map((slice) => ({
      typeKey: slice.typeKey,
      label: slice.label,
      count: slice.count,
      semanticColor: resolveSemanticColor(slice.typeKey),
    })),
);

function resolveSemanticColor(typeKey: string): string {
  if (!props.colorForType) {
    return 'grey';
  }
  try {
    return props.colorForType(typeKey);
  } catch {
    return 'grey';
  }
}

function themeColorStrings(): Record<string, string | undefined> {
  const result: Record<string, string | undefined> = {};
  for (const [key, value] of Object.entries(theme.current.value.colors)) {
    if (typeof value === 'string') {
      result[key] = value;
    }
  }
  return result;
}

function resolveHexColor(typeKey: string): string {
  return vuetifyColorToHex(resolveSemanticColor(typeKey), themeColorStrings());
}

function applyPieSliceColors(): void {
  if (!props.colorForType || !chartHostRef.value) {
    return;
  }

  const paths = chartHostRef.value.querySelectorAll('svg path');
  const ordered = slicesSortedForPie.value;

  paths.forEach((path, index) => {
    const slice = ordered[index];
    if (!slice) {
      return;
    }
    path.setAttribute('fill', resolveHexColor(slice.typeKey));
    path.setAttribute('stroke', '#ffffff');
    path.setAttribute('stroke-width', '2.5');
    path.setAttribute('opacity', '1');
  });
}

function scheduleChartMount(): void {
  chartReady.value = false;
  if (!hasChartData.value) {
    return;
  }
  requestAnimationFrame(() => {
    chartReady.value = true;
  });
}

async function scheduleColorSync(): Promise<void> {
  if (!chartReady.value || !props.colorForType) {
    return;
  }
  await nextTick();
  await nextTick();
  requestAnimationFrame(() => {
    applyPieSliceColors();
    requestAnimationFrame(applyPieSliceColors);
  });
}

watch(
  () => [props.statistics?.total, pieData.value.length, props.colorForType] as const,
  () => {
    scheduleChartMount();
  },
  { immediate: true },
);

watch(chartReady, () => {
  void scheduleColorSync();
});

watch(slicesSortedForPie, () => {
  void scheduleColorSync();
});

onMounted(() => {
  if (hasChartData.value) {
    scheduleChartMount();
  }
});
</script>

<style scoped>
.entity-stats__chart {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  padding: 4px 0;
  background: #fff;
  overflow: visible;
}

.entity-stats__chart :deep(h1) {
  display: none;
}

.entity-stats__chart :deep(svg) {
  background: #fff;
  overflow: visible;
}

.entity-stats__chart :deep([id^='mainDiv']) {
  background: #fff;
  overflow: visible;
}

/* Labels drawn by PieChart overlap slices and clip; legend below is the source of truth. */
.entity-stats__chart :deep(svg text) {
  display: none;
}

.entity-stats__legend {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 8px;
}

.entity-stats__legend-chip {
  justify-content: space-between;
  height: auto;
  min-height: 28px;
  padding-inline: 10px;
  font-weight: 500;
}

.entity-stats__legend-chip :deep(.v-chip__content) {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.entity-stats__legend-label {
  flex: 1;
  text-align: left;
  line-height: 1.25;
  white-space: normal;
}

.entity-stats__legend-count {
  flex-shrink: 0;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
</style>
