import type { ContainerDailyStateJsonResponse } from './container-daily-state-json-response';
import type { InfrastructurePlanClusterJsonResponse } from './infrastructure-plan-cluster-json-response';
import type { InfrastructurePlanDailyPlanJsonResponse } from './infrastructure-plan-daily-plan-json-response';
import type { InfrastructurePlanFacilityJsonResponse } from './infrastructure-plan-facility-json-response';
import type { InfrastructurePlanGreedyWeightsJsonResponse } from './infrastructure-plan-greedy-weights-json-response';
import type { InfrastructurePlanMetricsJsonResponse } from './infrastructure-plan-metrics-json-response';
import type { InfrastructurePlanMoneyJsonResponse } from './infrastructure-plan-money-json-response';

/**
 * Infrastructure plan detail payload returned by the backend.
 */
export interface InfrastructurePlanDetailJsonResponse {
  id?: string;
  executedAt: string;
  period?: number;
  numberOfDays?: number;
  /** Time when the collection workday starts, formatted as "HH:mm" */
  collectionStartTime?: string;
  /** Average time in minutes a vehicle takes to move between two points */
  averageTransferTimeMinutes?: number;
  /** Greedy scoring weights used by the algorithm */
  greedyWeights?: InfrastructurePlanGreedyWeightsJsonResponse;
  totalCollectedKilograms?: number;
  totalCollectedLiters?: number;
  maxBudget?: InfrastructurePlanMoneyJsonResponse;
  totalDistanceMeters?: number;
  averagePickupTimeMinutes?: number;
  estimatedTotalCost?: InfrastructurePlanMoneyJsonResponse;
  metrics?: InfrastructurePlanMetricsJsonResponse;
  facilities?: InfrastructurePlanFacilityJsonResponse[];
  clusters?: InfrastructurePlanClusterJsonResponse[];
  status?: string;
  dailyPlans?: InfrastructurePlanDailyPlanJsonResponse[];
  containerStateMonitoring?: ContainerDailyStateJsonResponse[];
  /** Backend: VALID | OBSOLETE */
  validityState?: string;
  /** Backend: RUNNING | COMPLETED | FAILED */
  executionState?: string;
  /** Present when executionState is FAILED */
  failureReason?: string | null;
  /** JSON snapshot of the client execution request */
  executionRequestJson?: string;
}
