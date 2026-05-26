import type { RouteChartIds } from './route-chart-ids';
import type { RouteLinePlotDatum } from './route-line-plot-datum';
import type { RouteProgressStop } from './route-progress-stop';
import type { RouteSummaryMetrics } from './route-summary-metrics';
import type { InfrastructurePlanDailyPlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import type { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';

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

/**
 * Total route distance in meters: cumulative distance at the last stop when stops exist,
 * otherwise the plan-level total from the backend.
 */
export function resolveRouteTotalDistanceMeters(
  dailyPlan: InfrastructurePlanDailyPlanDetail,
): number {
  const stops = dailyPlan.stops;
  if (stops.length > 0) {
    const sorted = [...stops].sort(
      (left, right) => left.sequence.getValue() - right.sequence.getValue(),
    );
    return sorted[sorted.length - 1].cumulativeDistanceMeters.getValue();
  }

  return dailyPlan.totalDistanceMeters.getValue();
}

/**
 * Estimated route cost: (total km) × (vehicle cost per km).
 */
export function calculateEstimatedRouteCost(
  totalDistanceMeters: number,
  costPerKilometer: TransportationVariableCost | null,
): number | null {
  if (costPerKilometer == null) {
    return null;
  }

  const kilometers = totalDistanceMeters / 1000;
  return kilometers * costPerKilometer.getAmount();
}

export function buildRouteSummaryMetrics(
  dailyPlan: InfrastructurePlanDailyPlanDetail,
): RouteSummaryMetrics {
  const totalDistanceMeters = resolveRouteTotalDistanceMeters(dailyPlan);
  const costPerKilometer = dailyPlan.vehicle?.costPerKilometer ?? null;
  const estimatedRouteCostAmount = calculateEstimatedRouteCost(
    totalDistanceMeters,
    costPerKilometer,
  );

  return {
    totalDistanceMeters,
    estimatedRouteCostAmount,
    estimatedRouteCostCurrency: costPerKilometer?.getCurrency().getCode() ?? 'EUR',
    capacityKilograms: dailyPlan.vehicle?.capacityKilograms?.getKilograms() ?? null,
    capacityLiters: dailyPlan.vehicle?.capacityLiters?.getLiters() ?? null,
  };
}
