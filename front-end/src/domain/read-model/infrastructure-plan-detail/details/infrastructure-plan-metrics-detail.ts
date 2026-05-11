import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { TotalCost } from '@/domain/valueobject/cost/total-cost';
import { Distance } from '@/domain/valueobject/location/distance';

/**
 * Read-only metrics block of an infrastructure plan detail.
 */
export class InfrastructurePlanMetricsDetail {
  constructor(
    public readonly totalCollectedKilograms: CollectedWeightKilograms,
    public readonly totalCollectedLiters: CollectedVolumeLiters,
    public readonly totalDistanceMeters: Distance,
    public readonly averagePickupTimeMinutes: number | null,
    public readonly estimatedTotalCost: TotalCost,
    public readonly maxBudget: MaximumBudget,
  ) {}
}
