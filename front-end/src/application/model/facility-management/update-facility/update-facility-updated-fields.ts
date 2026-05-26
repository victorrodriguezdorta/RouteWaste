import type { FacilityStatus } from '@/domain/enumerate/facility-status';
import type { FacilityType } from '@/domain/enumerate/facility-type';
import type { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import type { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import type { UnloadingTime } from '@/domain/valueobject/capacity/unloading-time';
import type { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';
import type { Location } from '@/domain/valueobject/location/location';
import type { Name } from '@/domain/valueobject/name/name';

/** Fields that may be updated on an existing facility. */
export interface UpdateFacilityUpdatedFields {
    name?: Name;
    facilityType?: FacilityType;
    location?: Location;
    storageCapacity?: StorageCapacityKilograms;
    processingCapacity?: ProcessingCapacityKilogramsPerDay;
    unloadingTime?: UnloadingTime;
    openingFixedCost?: OpeningFixedCost;
    status?: FacilityStatus;
}
