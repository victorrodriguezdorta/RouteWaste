import type { RouteProgressStop } from '@/adapter/vuejs/analytics/infrastructure-plan/route-progress-stop';
import type {
  RouteCollectionChartDatum,
  RouteDistanceChartDatum,
} from './types';

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
