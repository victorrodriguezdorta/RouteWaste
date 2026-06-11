import type { RouteProgressChartDatum } from '@/adapter/vuejs/charts/route-progress/route-progress-chart-datum';
import type { RouteProgressChartSeries } from '@/adapter/vuejs/charts/route-progress/route-progress-chart-series';

export interface ContainerFillChartResult {
  data: RouteProgressChartDatum[];
  series: RouteProgressChartSeries[];
}
