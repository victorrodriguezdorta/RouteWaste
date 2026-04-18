import { FacilityStatus } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import { UnloadingTime } from '@/domain/valueobject/capacity/unloading-time';
import { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';
import { Location } from '@/domain/valueobject/location/location';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

// Command object for input data
export interface UpdateFacilityCommand {
    facilityId: UllUUID;
    updatedFields: Partial<{
        facilityType: FacilityType;
        location: Location;
        storageCapacity: StorageCapacityKilograms;
        processingCapacity: ProcessingCapacityKilogramsPerDay;
        unloadingTime: UnloadingTime;
        openingFixedCost: OpeningFixedCost;
        status: FacilityStatus;
    }>;
}