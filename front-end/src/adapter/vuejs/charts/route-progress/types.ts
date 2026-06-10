export type RouteProgressYAxisSide = 'left' | 'right';
export type RouteProgressLegendPosition = 'top' | 'side-left';

export interface RouteProgressChartSeries {
  key: string;
  label: string;
  color: string;
  yAxis?: RouteProgressYAxisSide;
}

export interface RouteProgressChartDatum {
  stop: number;
  stopLabel?: string;
  [metricKey: string]: number | string | undefined;
}

export interface RouteCollectionChartDatum {
  stop: number;
  kilograms: number;
  liters: number;
  [metricKey: string]: number;
}

export interface RouteDistanceChartDatum {
  stop: number;
  meters: number;
  [metricKey: string]: number;
}
