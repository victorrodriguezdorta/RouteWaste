/**
 * Summary payload returned by the backend when listing infrastructure plans.
 */
export interface InfrastructurePlanSummaryJsonResponse {
  id: string;
  executedAt: string | null;
  estimatedTotalCost: { amount: number; currency?: string } | null;
  numberOfDays: number | null;
  averagePickupTimeMinutes: number | null;
  /** Backend: VALID | OBSOLETE */
  validityState?: string;
}