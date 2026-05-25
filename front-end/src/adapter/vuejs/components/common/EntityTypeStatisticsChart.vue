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

      <div v-else class="entity-stats__chart">
        <PieChart
          v-if="chartReady"
          :id="chartId"
          :width_props="chartWidth"
          :height_props="chartHeight"
          :data="pieData"
        />
      </div>
    </v-card-text>
  </v-card>
</template>

<script lang="ts" setup>
import type { PieChartDatum } from '@/adapter/vuejs/analytics/infrastructure-plan/pie-chart-datum';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';
import { PieChart } from '@ull-tfg/ull-tfg-vue';
import { computed, onMounted, ref, watch } from 'vue';

const props = withDefaults(
  defineProps<{
    statistics?: EntityTypeStatistics;
    chartId: number;
    chartTitle: string;
    emptyMessage: string;
    translateType: (typeKey: string) => string;
    chartWidth?: number;
    chartHeight?: number;
  }>(),
  {
    chartWidth: 320,
    chartHeight: 260,
  },
);

const chartReady = ref(false);

const pieData = computed<PieChartDatum[]>(() => {
  if (!props.statistics) {
    return [];
  }
  return props.statistics.byType
    .filter((entry) => entry.count > 0)
    .map((entry) => ({
      name: props.translateType(entry.type),
      value: entry.count,
    }));
});

const hasChartData = computed(() => pieData.value.length > 0);

watch(
  () => [props.statistics?.total, pieData.value.length] as const,
  () => {
    chartReady.value = false;
    if (!hasChartData.value) {
      return;
    }
    requestAnimationFrame(() => {
      chartReady.value = true;
    });
  },
  { immediate: true },
);

onMounted(() => {
  if (hasChartData.value) {
    requestAnimationFrame(() => {
      chartReady.value = true;
    });
  }
});
</script>

<style scoped>
.entity-stats__chart {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: #fff;
  overflow: hidden;
}

.entity-stats__chart :deep(h1) {
  display: none;
}

.entity-stats__chart :deep(svg) {
  background: #fff;
}

.entity-stats__chart :deep([id^='mainDiv']) {
  background: #fff;
}
</style>
