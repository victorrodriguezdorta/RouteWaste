/**
 * Re-exports the published @ull-tfg/ull-tfg-vue bundle and adds `Footer` from the
 * course library source (not yet in the npm 1.0.0 artifact). Vite resolves
 * `@ull-tfg/ull-tfg-vue` to this file so app code can use `import { Footer } from '@ull-tfg/ull-tfg-vue'`.
 */
// @ts-expect-error Published bundle has no .d.ts next to ull-tfg-vue.es.js; typings come from package root.
export * from '../../../../node_modules/@ull-tfg/ull-tfg-vue/dist/ull-tfg-vue.es.js';
export { default as Footer } from '../../../../../../ull-tfg-vue/ull-tfg-vue/src/components/footer/Footer.vue';
// @ts-expect-error Course library SFC; typings via src/types/ull-tfg-vue-footer-augment.ts
export { default as PieChart } from '../../../../../../ull-tfg-vue/ull-tfg-vue/src/components/pie-chart/PieChart.vue';
// @ts-expect-error Course library SFC; typings via src/types/ull-tfg-vue-footer-augment.ts
export { default as StackedBarPlot } from '../../../../../../ull-tfg-vue/ull-tfg-vue/src/components/bar-plot/StackedBarPlot.vue';
// @ts-expect-error Course library SFC; typings via src/types/ull-tfg-vue-footer-augment.ts
export { default as LinePlot } from '../../../../../../ull-tfg-vue/ull-tfg-vue/src/components/line-plot/LinePlot.vue';
