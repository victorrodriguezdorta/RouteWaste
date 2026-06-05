<script setup lang="ts">
import { LinePlot } from '@ull-tfg/ull-tfg-vue';
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue';

export interface UllLinePlotDatum {
  product: number;
  value: number;
  stop?: number;
}

const props = withDefaults(defineProps<{
  data: UllLinePlotDatum[];
  width: number;
  height: number;
  mountIndex?: number;
}>(), {
  mountIndex: 0,
});

const hostRef = ref<HTMLElement | null>(null);
const chartReady = ref(false);
let mountGeneration = 0;

/** LinePlot only reads the first two numeric keys (`product`, `value`). */
const linePlotData = computed(() =>
  props.data.map(({ product, value }) => ({ product, value })),
);

/**
 * LinePlot from ull-tfg-vue always targets `#mainDiv` (no unique id prop).
 * Before mounting a new instance, rename foreign `#mainDiv` nodes so d3 draws in this host.
 */
function demoteForeignMainDivs(): void {
  const host = hostRef.value;
  if (!host) {
    return;
  }

  document.querySelectorAll<HTMLElement>('#mainDiv').forEach((element, index) => {
    if (!host.contains(element)) {
      element.id = `mainDiv-foreign-${props.mountIndex}-${index}`;
    }
  });
}

function clearHostChart(): void {
  const mainDiv = hostRef.value?.querySelector('#mainDiv');
  mainDiv?.replaceChildren();
}

/**
 * LinePlot formats `product` as clock time. Replace x-axis labels with stop numbers.
 */
function relabelXAxisToStopNumbers(): void {
  const svg = hostRef.value?.querySelector('#mainDiv svg');
  if (!svg) {
    return;
  }

  const stopLabels = [...props.data]
    .sort((left, right) => left.product - right.product)
    .map((datum) => datum.stop ?? datum.product);

  const xAxisGroup = Array.from(svg.querySelectorAll('g')).find((group) => {
    const transform = group.getAttribute('transform') ?? '';
    return /^translate\(0,\s*\d+/.test(transform) && group.querySelector('.tick') != null;
  });
  if (!xAxisGroup) {
    return;
  }

  const tickTexts = Array.from(xAxisGroup.querySelectorAll<SVGTextElement>('.tick text'));
  tickTexts.forEach((text, index) => {
    const stop = stopLabels[index];
    text.textContent = stop == null ? '' : String(stop);
  });
}

async function scheduleAxisLabelSync(): Promise<void> {
  await nextTick();
  requestAnimationFrame(() => {
    relabelXAxisToStopNumbers();
    requestAnimationFrame(() => relabelXAxisToStopNumbers());
  });
}

async function scheduleMount(): Promise<void> {
  if (props.data.length === 0) {
    chartReady.value = false;
    clearHostChart();
    return;
  }

  const generation = ++mountGeneration;
  chartReady.value = false;
  clearHostChart();
  await nextTick();

  await new Promise<void>((resolve) => {
    window.setTimeout(resolve, props.mountIndex * 50);
  });
  if (generation !== mountGeneration) {
    return;
  }

  demoteForeignMainDivs();
  chartReady.value = true;
  await scheduleAxisLabelSync();
}

watch(
  () => [props.data, props.width, props.height, props.mountIndex] as const,
  () => {
    void scheduleMount();
  },
  { immediate: true, deep: true },
);

watch(chartReady, (ready) => {
  if (ready) {
    void scheduleAxisLabelSync();
  }
});

onBeforeUnmount(() => {
  mountGeneration += 1;
  chartReady.value = false;
  clearHostChart();
});
</script>

<template>
  <div ref="hostRef" class="ull-line-plot-host">
    <LinePlot
      v-if="chartReady"
      :width_props="width"
      :height_props="height"
      :data="linePlotData"
    />
  </div>
</template>

<style scoped>
.ull-line-plot-host {
  align-items: center;
  display: flex;
  justify-content: center;
  min-height: 120px;
  width: 100%;
}

.ull-line-plot-host :deep(h1) {
  display: none;
}

.ull-line-plot-host :deep(#mainDiv) {
  display: flex;
  justify-content: center;
  margin-inline: auto;
  width: fit-content;
}

.ull-line-plot-host :deep(svg) {
  display: block;
  margin-inline: auto;
  max-width: 100%;
}
</style>
