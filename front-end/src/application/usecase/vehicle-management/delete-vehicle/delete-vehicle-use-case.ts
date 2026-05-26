import type { DeleteVehicleCommand } from '@/application/model/vehicle-management/delete-vehicle/delete-vehicle-command';
import type { DeleteVehicleResult } from '@/application/model/vehicle-management/delete-vehicle/delete-vehicle-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for deleting a vehicle from the system.
 */
export interface DeleteVehicleUseCase {
    /**
     * Handles the deletion of a vehicle
     *
     * @param command Data needed to delete the vehicle
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteVehicleCommand): Promise<Either<DataError, DeleteVehicleResult>>;
}

export type { DeleteVehicleCommand } from '@/application/model/vehicle-management/delete-vehicle/delete-vehicle-command';
export type { DeleteVehicleResult } from '@/application/model/vehicle-management/delete-vehicle/delete-vehicle-result';
