import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { Distance } from '@/domain/valueobject/location/distance';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import type { InfrastructurePlanStopDetail } from './infrastructure-plan-stop-detail';
import type { InfrastructurePlanVehicleDetail } from './infrastructure-plan-vehicle-detail';

/**
 * Read-only daily plan used by the infrastructure plan detail views.
 */
export class InfrastructurePlanDailyPlanDetail {
  constructor(
    public readonly id: UllUUID | null,
    public readonly infrastructurePlanId: UllUUID | null,
    public readonly facilityId: UllUUID,
    public readonly serviceDate: string,
    public readonly planDay: number,
    public readonly vehicleId: UllUUID,
    public readonly totalCollectedKilograms: CollectedWeightKilograms,
    public readonly totalCollectedLiters: CollectedVolumeLiters,
    public readonly totalDistanceMeters: Distance,
    public readonly stops: InfrastructurePlanStopDetail[],
    public readonly vehicle: InfrastructurePlanVehicleDetail | null = null,
  ) {}
}
