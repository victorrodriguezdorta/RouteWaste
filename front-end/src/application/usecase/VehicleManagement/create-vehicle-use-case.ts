// CreateVehicleUseCase.ts
// Use case contract for creating a new vehicle

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Vehicle } from '../../../domain/entity/vehicle';
import { VehicleType } from '../../../domain/enumerate/vehicle-type';
import { Capacity } from '../../../domain/valueobject/demand/capacity';
import { TransportationVariableCost } from '../../../domain/valueobject/cost/transportation-variable-cost';

/**
 * Use case for registering a new vehicle in the system.
 * Input: vehicleType, transportCapacity, costPerKilometer
 * Output: Vehicle (entity)
 */

// Command object for input data
export interface CreateVehicleCommand {
    vehicleType: VehicleType;
    transportCapacity: Capacity;
    costPerKilometer: TransportationVariableCost;
}

// Result type for the use case
export type CreateVehicleResult = Vehicle;

// Use case contract
export interface CreateVehicleUseCase {
    /**
     * Handles the creation of a new vehicle
     *
     * @param command Data needed to create the vehicle
     * @returns Either a DataError or the created Vehicle
     */
    execute(command: CreateVehicleCommand): Promise<Either<DataError, CreateVehicleResult>>;
}
