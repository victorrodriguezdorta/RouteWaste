<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  data: Array<Record<string, number>>;
  width: number;
  height: number;
  xKey?: string;
  colors?: string[];
}>(), {
  xKey: 'product',
  colors: () => ['#4e79a7', '#f28e2b', '#59a14f', '#e15759', '#76b7b2'],
});

const margin = { top: 12, right: 20, bottom: 30, left: 44 };

const innerWidth = computed(() => Math.max(props.width - margin.left - margin.right, 1));
const innerHeight = computed(() => Math.max(props.height - margin.top - margin.bottom, 1));

const seriesKeys = computed(() => {
  const keys = new Set<string>();
  props.data.forEach((row) => {
    Object.keys(row).forEach((key) => {
      if (key !== props.xKey) {
        keys.add(key);
      }
    });
  });
  return Array.from(keys);
});

const xValues = computed(() => props.data.map((row) => Number(row[props.xKey] ?? 0)));

const xDomain = computed(() =>
  Array.from(new Set(xValues.value.filter(Number.isFinite))).sort((left, right) => left - right),
);

const yMax = computed(() => {
  const values = props.data.flatMap((row) =>
    seriesKeys.value.map((key) => Number(row[key] ?? 0)),
  ).filter(Number.isFinite);
  return Math.max(...values, 1);
});

const linePaths = computed(() =>
  seriesKeys.value.map((key, index) => ({
    key,
    color: props.colors[index % props.colors.length],
    d: props.data
      .map((row, rowIndex) => {
        const x = scaleX(Number(row[props.xKey] ?? 0));
        const y = scaleY(Number(row[key] ?? 0));
        return `${rowIndex === 0 ? 'M' : 'L'} ${x} ${y}`;
      })
      .join(' '),
  })),
);

const xTicks = computed(() => xDomain.value);
const yTicks = computed(() => buildTicks(0, yMax.value, 4));

function scaleX(value: number): number {
  const index = xDomain.value.indexOf(value);
  if (xDomain.value.length <= 1 || index < 0) {
    return margin.left + innerWidth.value / 2;
  }
  return margin.left + (index / (xDomain.value.length - 1)) * innerWidth.value;
}

function scaleY(value: number): number {
  return margin.top + innerHeight.value - (value / yMax.value) * innerHeight.value;
}

function buildTicks(min: number, max: number, count: number): number[] {
  if (count <= 1 || max <= min) {
    return [min, max];
  }
  const step = (max - min) / count;
  return Array.from({ length: count + 1 }, (_, index) => min + step * index);
}

function formatTick(value: number): string {
  return Number.isInteger(value)
    ? String(value)
    : value.toLocaleString(undefined, { maximumFractionDigits: 1 });
}
</script>

<template>
  <svg
    :width="width"
    :height="height"
    class="simple-line-chart"
    role="img"
  >
    <line
      :x1="margin.left"
      :y1="height - margin.bottom"
      :x2="width - margin.right"
      :y2="height - margin.bottom"
      class="simple-line-chart__axis"
    />
    <line
      :x1="margin.left"
      :y1="margin.top"
      :x2="margin.left"
      :y2="height - margin.bottom"
      class="simple-line-chart__axis"
    />

    <g v-for="tick in yTicks" :key="`y-${tick}`">
      <line
        :x1="margin.left"
        :x2="width - margin.right"
        :y1="scaleY(tick)"
        :y2="scaleY(tick)"
        class="simple-line-chart__grid"
      />
      <text
        :x="margin.left - 8"
        :y="scaleY(tick) + 4"
        text-anchor="end"
        class="simple-line-chart__tick"
      >
        {{ formatTick(tick) }}
      </text>
    </g>

    <g v-for="tick in xTicks" :key="`x-${tick}`">
      <text
        :x="scaleX(tick)"
        :y="height - 8"
        text-anchor="middle"
        class="simple-line-chart__tick"
      >
        {{ formatTick(tick) }}
      </text>
    </g>

    <path
      v-for="path in linePaths"
      :key="path.key"
      :d="path.d"
      :stroke="path.color"
      class="simple-line-chart__line"
    />
  </svg>
</template>

<style scoped>
.simple-line-chart {
  display: block;
  max-width: 100%;
}

.simple-line-chart__axis {
  stroke: rgba(0, 0, 0, 0.38);
  stroke-width: 1;
}

.simple-line-chart__grid {
  stroke: rgba(0, 0, 0, 0.08);
  stroke-width: 1;
}

.simple-line-chart__line {
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.5;
}

.simple-line-chart__tick {
  fill: rgba(0, 0, 0, 0.58);
  font-size: 10px;
}
</style>
