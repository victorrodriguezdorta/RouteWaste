// UpdateVehicleUseCase.ts
// Use case contract for updating a vehicle

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Vehicle } from '../../../domain/entity/vehicle';
import { VehicleType } from '../../../domain/enumerate/vehicle-type';
import { Capacity } from '../../../domain/valueobject/demand/capacity';
import { TransportationVariableCost } from '../../../domain/valueobject/cost/transportation-variable-cost';

/**
 * Use case for updating an existing vehicle in the system.
 * Input: vehicleId and fields to update
 * Output: Vehicle (entity)
 */

// Command object for input data
export interface UpdateVehicleCommand {
    vehicleId: string;
    updatedFields: Partial<{
        vehicleType: VehicleType;
        transportCapacity: Capacity;
        costPerKilometer: TransportationVariableCost;
    }>;
}

// Result type for the use case
export type UpdateVehicleResult = Vehicle;

// Use case contract
export interface UpdateVehicleUseCase {
    /**
     * Handles the update of an existing vehicle
     *
     * @param command Data needed to update the vehicle
     * @returns Either a DataError or the updated Vehicle
     */
    execute(command: UpdateVehicleCommand): Promise<Either<DataError, UpdateVehicleResult>>;
}
