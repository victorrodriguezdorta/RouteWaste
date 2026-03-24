import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { Capacity } from '@/domain/valueobject/demand/capacity';

/**
 * Use case for updating an existing vehicle in the system.
 * Input: vehicleId and fields to update
 * Output: Vehicle (entity)
 */

// Command object for input data
export interface UpdateVehicleCommand {
    vehicleId: UllUUID;
    updatedFields: Partial<{
        vehicleType: VehicleType;
        transportCapacity: Capacity;
        costPerKilometer: TransportationVariableCost;
    }>;
}
