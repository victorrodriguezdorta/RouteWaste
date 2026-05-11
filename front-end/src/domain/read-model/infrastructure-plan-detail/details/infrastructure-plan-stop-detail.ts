import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { Distance } from '@/domain/valueobject/location/distance';
import { RouteSequence } from '@/domain/valueobject/location/route-sequence';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import type { InfrastructurePlanStopAlertDetail } from './infrastructure-plan-stop-alert-detail';

/**
 * Read-only stop embedded in a daily plan.
 */
export class InfrastructurePlanStopDetail {
  constructor(
    public readonly sequence: RouteSequence,
    public readonly containerId: UllUUID,
    public readonly collectedKilograms: CollectedWeightKilograms,
    public readonly collectedLiters: CollectedVolumeLiters,
    public readonly distanceFromPreviousMeters: Distance,
    public readonly cumulativeDistanceMeters: Distance,
    public readonly containerActualLiters: number | null = null,
    public readonly alerts: InfrastructurePlanStopAlertDetail[] = [],
  ) {}
}
