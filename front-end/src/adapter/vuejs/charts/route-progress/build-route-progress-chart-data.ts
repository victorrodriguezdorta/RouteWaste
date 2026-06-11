import type { RouteProgressStop } from '@/adapter/vuejs/analytics/infrastructure-plan/route-progress-stop';
import type { RouteCollectionChartDatum } from '@/adapter/vuejs/charts/route-progress/route-collection-chart-datum';
import type { RouteDistanceChartDatum } from '@/adapter/vuejs/charts/route-progress/route-distance-chart-datum';

export function buildRouteCollectionChartData(
  stops: RouteProgressStop[],
): RouteCollectionChartDatum[] {
  return stops.map((stop) => ({
    stop: stop.sequence,
    kilograms: stop.cumulativeKilograms ?? 0,
    liters: stop.cumulativeLiters ?? 0,
  }));
}

export function buildRouteDistanceChartData(
  stops: RouteProgressStop[],
): RouteDistanceChartDatum[] {
  return stops.map((stop) => ({
    stop: stop.sequence,
    meters: stop.cumulativeDistanceMeters ?? 0,
  }));
}
