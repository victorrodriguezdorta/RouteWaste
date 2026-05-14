/**
 * Summary payload returned by the backend when listing infrastructure plans.
 */
export interface InfrastructurePlanSummaryJsonResponse {
  id: string;
  executedAt: string;
  estimatedTotalCost: { amount: number; currency?: string };
  numberOfDays: number;
  averagePickupTimeMinutes: number;
  /** Backend: VALID | OBSOLETE */
  validityState?: string;
}