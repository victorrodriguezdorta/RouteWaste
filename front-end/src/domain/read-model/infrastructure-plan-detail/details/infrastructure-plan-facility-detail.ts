import { FacilityStatus } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import type { InfrastructurePlanContainerDetail } from './infrastructure-plan-container-detail';
import type { InfrastructurePlanDailyPlanDetail } from './infrastructure-plan-daily-plan-detail';

/**
 * Read-only facility node used by the infrastructure plan detail views.
 */
export class InfrastructurePlanFacilityDetail {
  constructor(
    public readonly id: UllUUID,
    public readonly name: Name,
    public readonly facilityType: FacilityType,
    public readonly status: FacilityStatus,
    public readonly location: Location,
    public readonly storageCapacity: StorageCapacityKilograms,
    public readonly processingCapacity: ProcessingCapacityKilogramsPerDay,
    public readonly assignedContainers: InfrastructurePlanContainerDetail[],
    public readonly dailyPlans: InfrastructurePlanDailyPlanDetail[],
  ) {}
}
