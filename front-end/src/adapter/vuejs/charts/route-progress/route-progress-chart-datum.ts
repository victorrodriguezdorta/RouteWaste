export interface RouteProgressChartDatum {
  stop: number;
  stopLabel?: string;
  [metricKey: string]: number | string | undefined;
}
