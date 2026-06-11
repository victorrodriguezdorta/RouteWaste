<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, useId, watch } from 'vue';
import type { RouteProgressChartDatum } from '@/adapter/vuejs/charts/route-progress/route-progress-chart-datum';
import type { RouteProgressChartSeries } from '@/adapter/vuejs/charts/route-progress/route-progress-chart-series';
import type { RouteProgressLegendPosition } from '@/adapter/vuejs/charts/route-progress/route-progress-legend-position';

const SIDE_LEGEND_WIDTH = 200;

interface SeriesTooltipState {
  key: string;
  label: string;
  color: string;
  x: number;
  y: number;
}

const props = withDefaults(defineProps<{
  data: RouteProgressChartDatum[];
  series: RouteProgressChartSeries[];
  width: number;
  height: number;
  xAxisLabel?: string;
  leftYAxisLabel?: string;
  rightYAxisLabel?: string;
  legendPosition?: RouteProgressLegendPosition;
  fluid?: boolean;
  showSeriesTooltip?: boolean;
}>(), {
  xAxisLabel: '',
  leftYAxisLabel: '',
  rightYAxisLabel: '',
  legendPosition: 'top',
  fluid: false,
  showSeriesTooltip: true,
});

const chartInstanceId = useId().replace(/:/g, '');
const hostRef = ref<HTMLElement | null>(null);
const measuredWidth = ref(props.width);
const hoveredSeriesKey = ref<string | null>(null);
const seriesTooltip = ref<SeriesTooltipState | null>(null);
let resizeObserver: ResizeObserver | null = null;

const hasSideLegend = computed(() => props.legendPosition === 'side-left');

const effectiveWidth = computed(() =>
  props.fluid ? measuredWidth.value : props.width,
);

const chartSvgWidth = computed(() =>
  hasSideLegend.value
    ? Math.max(effectiveWidth.value - SIDE_LEGEND_WIDTH - 12, 320)
    : effectiveWidth.value,
);

function observeHostWidth(): void {
  resizeObserver?.disconnect();
  resizeObserver = null;

  if (!props.fluid || !hostRef.value) {
    return;
  }

  resizeObserver = new ResizeObserver((entries) => {
    const entry = entries[0];
    if (!entry) {
      return;
    }
    measuredWidth.value = Math.max(Math.floor(entry.contentRect.width), props.width);
  });
  resizeObserver.observe(hostRef.value);
  measuredWidth.value = Math.max(hostRef.value.clientWidth, props.width);
}

onMounted(() => {
  observeHostWidth();
});

onBeforeUnmount(() => {
  resizeObserver?.disconnect();
});

watch(() => props.fluid, () => {
  observeHostWidth();
});

const margin = computed(() => {
  const hasRightAxis = props.series.some((item) => item.yAxis === 'right');
  return {
    top: hasSideLegend.value ? 14 : 34,
    right: hasRightAxis ? 52 : 18,
    bottom: props.xAxisLabel ? 40 : 28,
    left: 54,
  };
});

const innerWidth = computed(() =>
  Math.max(chartSvgWidth.value - margin.value.left - margin.value.right, 1),
);
const innerHeight = computed(() =>
  Math.max(props.height - margin.value.top - margin.value.bottom, 1),
);

const stopValues = computed(() =>
  props.data
    .map((row) => row.stop)
    .filter((value): value is number => typeof value === 'number' && Number.isFinite(value)),
);

function stopAxisLabel(stop: number): string {
  const row = props.data.find((entry) => entry.stop === stop);
  if (row?.stopLabel) {
    return String(row.stopLabel);
  }
  return formatTick(stop);
}

const leftSeries = computed(() =>
  props.series.filter((item) => item.yAxis !== 'right'),
);
const rightSeries = computed(() =>
  props.series.filter((item) => item.yAxis === 'right'),
);

const sharedYMax = computed(() => maxSeriesValue(props.series));
const leftYMax = computed(() =>
  rightSeries.value.length > 0 ? maxSeriesValue(leftSeries.value) : sharedYMax.value,
);
const rightYMax = computed(() => maxSeriesValue(rightSeries.value));

const leftYTicks = computed(() => buildTicks(0, leftYMax.value, 4));
const rightYTicks = computed(() => buildTicks(0, rightYMax.value, 4));

const legendLayout = computed(() => {
  const itemWidth = 112;
  const totalWidth = props.series.length * itemWidth;
  const startX = margin.value.left + Math.max((innerWidth.value - totalWidth) / 2, 0);
  return props.series.map((item, index) => ({
    ...item,
    x: startX + index * itemWidth,
  }));
});

const renderedSeries = computed(() =>
  props.series.map((item, index) => {
    const yAxis = item.yAxis === 'right' ? 'right' : 'left';
    const yMax = yAxis === 'right' ? rightYMax.value : leftYMax.value;
    const points = props.data.map((row) => ({
      stop: row.stop,
      x: scaleX(row.stop),
      y: scaleY(Number(row[item.key] ?? 0), yMax),
      value: Number(row[item.key] ?? 0),
    }));

    return {
      ...item,
      yAxis,
      gradientId: `${chartInstanceId}-gradient-${index}`,
      linePath: buildLinePath(points),
      areaPath: buildAreaPath(points),
      points,
    };
  }),
);

function maxSeriesValue(series: RouteProgressChartSeries[]): number {
  const values = props.data.flatMap((row) =>
    series.map((item) => Number(row[item.key] ?? 0)),
  ).filter(Number.isFinite);
  return Math.max(...values, 1);
}

function scaleX(stop: number): number {
  const stops = stopValues.value;
  const index = stops.indexOf(stop);
  if (stops.length <= 1 || index < 0) {
    return margin.value.left + innerWidth.value / 2;
  }
  return margin.value.left + (index / (stops.length - 1)) * innerWidth.value;
}

function scaleY(value: number, yMax: number): number {
  return margin.value.top + innerHeight.value - (value / yMax) * innerHeight.value;
}

function buildLinePath(points: Array<{ x: number; y: number }>): string {
  if (points.length === 0) {
    return '';
  }
  return points
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
    .join(' ');
}

function buildAreaPath(points: Array<{ x: number; y: number }>): string {
  if (points.length === 0) {
    return '';
  }

  const baseline = margin.value.top + innerHeight.value;
  const line = points
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
    .join(' ');
  const last = points[points.length - 1];
  const first = points[0];
  return `${line} L ${last.x} ${baseline} L ${first.x} ${baseline} Z`;
}

function buildTicks(min: number, max: number, count: number): number[] {
  if (count <= 1 || max <= min) {
    return [min, max];
  }
  const step = (max - min) / count;
  return Array.from({ length: count + 1 }, (_, index) => min + step * index);
}

function formatTick(value: number): string {
  if (!Number.isFinite(value)) {
    return '';
  }
  if (Number.isInteger(value)) {
    return String(value);
  }
  return value.toLocaleString(undefined, { maximumFractionDigits: 1 });
}

function updateSeriesTooltip(
  item: Pick<RouteProgressChartSeries, 'key' | 'label' | 'color'>,
  event: MouseEvent,
): void {
  if (!props.showSeriesTooltip) {
    return;
  }

  const host = hostRef.value;
  if (!host) {
    return;
  }

  const rect = host.getBoundingClientRect();
  hoveredSeriesKey.value = item.key;
  seriesTooltip.value = {
    key: item.key,
    label: item.label,
    color: item.color,
    x: event.clientX - rect.left + 14,
    y: event.clientY - rect.top - 10,
  };
}

function clearSeriesTooltip(): void {
  hoveredSeriesKey.value = null;
  seriesTooltip.value = null;
}
</script>

<template>
  <div
    ref="hostRef"
    class="route-progress-chart-host"
    :class="{
      'route-progress-chart-host--side-legend': hasSideLegend,
      'route-progress-chart-host--fluid': fluid,
    }"
    :style="fluid ? undefined : { width: `${width}px`, maxWidth: '100%' }"
  >
    <aside
      v-if="hasSideLegend"
      class="route-progress-chart__side-legend"
      :style="{ maxHeight: `${height}px` }"
      aria-label="Chart legend"
    >
      <div
        v-for="item in series"
        :key="item.key"
        class="route-progress-chart__side-legend-item"
      >
        <span
          class="route-progress-chart__side-legend-dot"
          :style="{ backgroundColor: item.color }"
        />
        <span class="route-progress-chart__side-legend-label">{{ item.label }}</span>
      </div>
    </aside>

    <svg
      :width="chartSvgWidth"
      :height="height"
      :viewBox="`0 0 ${chartSvgWidth} ${height}`"
      class="route-progress-chart"
      role="img"
    >
      <defs>
        <linearGradient
          v-for="item in renderedSeries"
          :id="item.gradientId"
          :key="item.gradientId"
          x1="0"
          y1="0"
          x2="0"
          y2="1"
        >
          <stop offset="0%" :stop-color="item.color" stop-opacity="0.28" />
          <stop offset="100%" :stop-color="item.color" stop-opacity="0.02" />
        </linearGradient>
      </defs>

      <g v-if="!hasSideLegend" class="route-progress-chart__legend">
        <g
          v-for="item in legendLayout"
          :key="item.key"
          :transform="`translate(${item.x}, 10)`"
        >
          <circle cx="5" cy="5" r="4.5" :fill="item.color" />
          <text x="14" y="9" class="route-progress-chart__legend-label">
            {{ item.label }}
          </text>
        </g>
      </g>

      <g v-for="tick in leftYTicks" :key="`left-${tick}`">
        <line
          :x1="margin.left"
          :x2="chartSvgWidth - margin.right"
          :y1="scaleY(tick, leftYMax)"
          :y2="scaleY(tick, leftYMax)"
          class="route-progress-chart__grid"
        />
        <text
          :x="margin.left - 10"
          :y="scaleY(tick, leftYMax) + 4"
          text-anchor="end"
          class="route-progress-chart__tick route-progress-chart__tick--y"
        >
          {{ formatTick(tick) }}
        </text>
      </g>

      <template v-if="rightSeries.length > 0">
        <g v-for="tick in rightYTicks" :key="`right-${tick}`">
          <text
            :x="chartSvgWidth - margin.right + 10"
            :y="scaleY(tick, rightYMax) + 4"
            text-anchor="start"
            class="route-progress-chart__tick route-progress-chart__tick--y route-progress-chart__tick--right"
          >
            {{ formatTick(tick) }}
          </text>
        </g>
      </template>

      <line
        :x1="margin.left"
        :y1="height - margin.bottom"
        :x2="chartSvgWidth - margin.right"
        :y2="height - margin.bottom"
        class="route-progress-chart__axis"
      />
      <line
        :x1="margin.left"
        :y1="margin.top"
        :x2="margin.left"
        :y2="height - margin.bottom"
        class="route-progress-chart__axis"
      />
      <line
        v-if="rightSeries.length > 0"
        :x1="chartSvgWidth - margin.right"
        :y1="margin.top"
        :x2="chartSvgWidth - margin.right"
        :y2="height - margin.bottom"
        class="route-progress-chart__axis route-progress-chart__axis--right"
      />

      <g v-for="stop in stopValues" :key="`stop-${stop}`">
        <text
          :x="scaleX(stop)"
          :y="height - (xAxisLabel ? 18 : 8)"
          text-anchor="middle"
          class="route-progress-chart__tick route-progress-chart__tick--x"
        >
          {{ stopAxisLabel(stop) }}
        </text>
      </g>

      <text
        v-if="xAxisLabel"
        :x="margin.left + innerWidth / 2"
        :y="height - 4"
        text-anchor="middle"
        class="route-progress-chart__axis-label"
      >
        {{ xAxisLabel }}
      </text>

      <text
        v-if="leftYAxisLabel"
        :transform="`translate(14 ${margin.top + innerHeight / 2}) rotate(-90)`"
        text-anchor="middle"
        class="route-progress-chart__axis-label"
      >
        {{ leftYAxisLabel }}
      </text>

      <text
        v-if="rightYAxisLabel && rightSeries.length > 0"
        :transform="`translate(${chartSvgWidth - 10} ${margin.top + innerHeight / 2}) rotate(90)`"
        text-anchor="middle"
        class="route-progress-chart__axis-label"
      >
        {{ rightYAxisLabel }}
      </text>

      <path
        v-for="item in renderedSeries"
        :key="`${item.key}-area`"
        :d="item.areaPath"
        :fill="`url(#${item.gradientId})`"
        class="route-progress-chart__area"
      />

      <path
        v-for="item in renderedSeries"
        :key="`${item.key}-line`"
        :d="item.linePath"
        :stroke="item.color"
        class="route-progress-chart__line"
        :class="{ 'route-progress-chart__line--active': hoveredSeriesKey === item.key }"
      />

      <path
        v-for="item in renderedSeries"
        :key="`${item.key}-hit`"
        :d="item.linePath"
        class="route-progress-chart__line-hit"
        @mouseenter="updateSeriesTooltip(item, $event)"
        @mousemove="updateSeriesTooltip(item, $event)"
        @mouseleave="clearSeriesTooltip"
      />

      <g v-for="item in renderedSeries" :key="`${item.key}-points`">
        <circle
          v-for="point in item.points"
          :key="`${item.key}-${point.stop}`"
          :cx="point.x"
          :cy="point.y"
          r="4"
          :fill="item.color"
          class="route-progress-chart__point"
        />
        <circle
          v-for="point in item.points"
          :key="`${item.key}-${point.stop}-ring`"
          :cx="point.x"
          :cy="point.y"
          r="4"
          fill="none"
          :stroke="item.color"
          class="route-progress-chart__point-ring"
        />
      </g>
    </svg>

    <div
      v-if="seriesTooltip"
      class="route-progress-chart__tooltip"
      :style="{ left: `${seriesTooltip.x}px`, top: `${seriesTooltip.y}px` }"
    >
      <span
        class="route-progress-chart__tooltip-dot"
        :style="{ backgroundColor: seriesTooltip.color }"
      />
      <span class="route-progress-chart__tooltip-label">{{ seriesTooltip.label }}</span>
    </div>
  </div>
</template>

<style scoped>
.route-progress-chart-host {
  display: block;
  max-width: 100%;
  position: relative;
}

.route-progress-chart-host--fluid {
  width: 100%;
}

.route-progress-chart-host--side-legend {
  align-items: stretch;
  display: flex;
  gap: 8px;
}

.route-progress-chart__side-legend {
  border-right: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  display: flex;
  flex: 0 0 188px;
  flex-direction: column;
  gap: 6px;
  max-width: 188px;
  min-width: 160px;
  overflow-y: auto;
  padding: 4px 10px 4px 2px;
  scrollbar-gutter: stable;
}

.route-progress-chart__side-legend-item {
  align-items: flex-start;
  display: flex;
  gap: 8px;
  line-height: 1.3;
}

.route-progress-chart__side-legend-dot {
  border-radius: 999px;
  flex: 0 0 10px;
  height: 10px;
  margin-top: 3px;
  width: 10px;
}

.route-progress-chart__side-legend-label {
  color: rgba(var(--v-theme-on-surface), 0.78);
  font-size: 0.72rem;
  font-weight: 600;
  word-break: break-word;
}

.route-progress-chart {
  display: block;
  flex: 1 1 auto;
  max-width: 100%;
  min-width: 0;
  width: 100%;
}

.route-progress-chart__legend-label {
  fill: rgba(var(--v-theme-on-surface), 0.78);
  font-size: 11px;
  font-weight: 600;
}

.route-progress-chart__axis {
  stroke: rgba(var(--v-theme-on-surface), 0.22);
  stroke-width: 1.25;
}

.route-progress-chart__axis--right {
  stroke: rgba(var(--v-theme-on-surface), 0.16);
}

.route-progress-chart__grid {
  stroke: rgba(var(--v-theme-on-surface), 0.08);
  stroke-width: 1;
}

.route-progress-chart__area {
  pointer-events: none;
}

.route-progress-chart-host:has(.route-progress-chart__line--active) .route-progress-chart__line:not(.route-progress-chart__line--active) {
  opacity: 0.28;
}

.route-progress-chart__line {
  fill: none;
  pointer-events: none;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.75;
  transition: stroke-width 0.15s ease, opacity 0.15s ease;
}

.route-progress-chart__line--active {
  stroke-width: 4;
}

.route-progress-chart__line-hit {
  cursor: pointer;
  fill: none;
  pointer-events: stroke;
  stroke: transparent;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 14;
}

.route-progress-chart__tooltip {
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.12);
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(var(--v-theme-neutral-base), 0.14);
  display: inline-flex;
  gap: 8px;
  left: 0;
  max-width: min(280px, calc(100% - 16px));
  padding: 6px 10px;
  pointer-events: none;
  position: absolute;
  top: 0;
  transform: translateY(-100%);
  z-index: 2;
}

.route-progress-chart__tooltip-dot {
  border-radius: 999px;
  flex: 0 0 10px;
  height: 10px;
  margin-top: 3px;
  width: 10px;
}

.route-progress-chart__tooltip-label {
  color: rgba(var(--v-theme-on-surface), 0.88);
  font-size: 0.74rem;
  font-weight: 600;
  line-height: 1.3;
  word-break: break-word;
}

.route-progress-chart__point {
  stroke: rgb(var(--v-theme-surface));
  stroke-width: 1.5;
}

.route-progress-chart__point-ring {
  opacity: 0.35;
  stroke-width: 1.25;
}

.route-progress-chart__tick {
  fill: rgba(var(--v-theme-on-surface), 0.58);
  font-size: 10px;
}

.route-progress-chart__tick--y {
  font-size: 9.5px;
}

.route-progress-chart__tick--right {
  fill: rgba(var(--v-theme-on-surface), 0.48);
}

.route-progress-chart__tick--x {
  font-size: 10px;
  font-weight: 600;
}

.route-progress-chart__axis-label {
  fill: rgba(var(--v-theme-on-surface), 0.62);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.02em;
  text-transform: uppercase;
}
</style>
