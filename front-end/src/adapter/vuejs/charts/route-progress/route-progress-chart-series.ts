import type { RouteProgressYAxisSide } from '@/adapter/vuejs/charts/route-progress/route-progress-y-axis-side';

export interface RouteProgressChartSeries {
  key: string;
  label: string;
  color: string;
  yAxis?: RouteProgressYAxisSide;
}
