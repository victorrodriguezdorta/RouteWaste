import type { UpdateVehicleCommand } from '@/application/model/vehicle-management/update-vehicle/update-vehicle-command';
import type { UpdateVehicleResult } from '@/application/model/vehicle-management/update-vehicle/update-vehicle-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for updating a vehicle
 */
export interface UpdateVehicleUseCase {
    /**
     * Handles the update of an existing vehicle
     *
     * @param command Data needed to update the vehicle
     * @returns Either a DataError or the updated Vehicle
     */
    execute(command: UpdateVehicleCommand): Promise<Either<DataError, UpdateVehicleResult>>;
}

export type { UpdateVehicleCommand } from '@/application/model/vehicle-management/update-vehicle/update-vehicle-command';
export type { UpdateVehicleResult } from '@/application/model/vehicle-management/update-vehicle/update-vehicle-result';
