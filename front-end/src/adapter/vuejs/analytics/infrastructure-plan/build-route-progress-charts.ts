import type { InfrastructurePlanDailyPlanDetail } from '@/domain/read-model/infrastructure-plan-detail';

export type RouteLinePlotDatum = Record<string, number> & {
  product: number;
};

export interface RouteProgressStop {
  sequence: number;
  collectedKilograms?: number;
  collectedLiters?: number;
  cumulativeKilograms?: number;
  cumulativeLiters?: number;
  cumulativeDistanceMeters?: number;
}

export interface RouteSummaryMetrics {
  totalDistanceMeters: number | null;
  estimatedRouteCostAmount: number | null;
  estimatedRouteCostCurrency: string;
  capacityKilograms: number | null;
  capacityLiters: number | null;
}

export interface RouteChartIds {
  collection: number;
  distance: number;
}

export function buildRouteChartIds(routeIndex: number): RouteChartIds {
  const baseId = 10_000 + routeIndex * 10;
  return {
    collection: baseId + 1,
    distance: baseId + 2,
  };
}

export function buildRouteCollectionLineSeries(
  stops: RouteProgressStop[],
  kilogramsLabel = 'kg',
  litersLabel = 'L',
): RouteLinePlotDatum[] {
  let cumulativeKilograms = 0;
  let cumulativeLiters = 0;

  return stops.map((stop) => {
    cumulativeKilograms += stop.collectedKilograms ?? 0;
    cumulativeLiters += stop.collectedLiters ?? 0;

    return {
      product: stop.sequence,
      [kilogramsLabel]: cumulativeKilograms,
      [litersLabel]: cumulativeLiters,
    };
  });
}

export function buildRouteDistanceLineSeries(
  stops: RouteProgressStop[],
  distanceLabel = 'm',
): RouteLinePlotDatum[] {
  return stops.map((stop) => ({
    product: stop.sequence,
    [distanceLabel]: stop.cumulativeDistanceMeters ?? 0,
  }));
}

export function buildRouteSummaryMetrics(
  dailyPlan: InfrastructurePlanDailyPlanDetail,
): RouteSummaryMetrics {
  const totalDistanceMeters = dailyPlan.totalDistanceMeters.getValue();
  const costPerKilometer = dailyPlan.vehicle?.costPerKilometer ?? null;
  const estimatedRouteCostAmount = costPerKilometer
    ? (totalDistanceMeters / 1000) * costPerKilometer.getAmount()
    : null;

  return {
    totalDistanceMeters,
    estimatedRouteCostAmount,
    estimatedRouteCostCurrency: costPerKilometer?.getCurrency().getCode() ?? 'EUR',
    capacityKilograms: dailyPlan.vehicle?.capacityKilograms?.getKilograms() ?? null,
    capacityLiters: dailyPlan.vehicle?.capacityLiters?.getLiters() ?? null,
  };
}
