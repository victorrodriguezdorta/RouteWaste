import type { InfrastructurePlanMoneyJsonResponse } from './infrastructure-plan-money-json-response';

/**
 * Metrics block returned by the newer infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanMetricsJsonResponse {
  totalCollectedKilograms?: number;
  totalCollectedLiters?: number;
  totalDistanceMeters?: number;
  averagePickupTimeMinutes?: number;
  estimatedTotalCost?: InfrastructurePlanMoneyJsonResponse;
  maxBudget?: InfrastructurePlanMoneyJsonResponse;
}
