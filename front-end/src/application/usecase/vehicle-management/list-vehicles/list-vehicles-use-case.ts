import type { ListVehiclesCommand } from '@/application/model/vehicle-management/list-vehicles/list-vehicles-command';
import type { ListVehiclesResult } from '@/application/model/vehicle-management/list-vehicles/list-vehicles-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for listing all vehicles in the system.
 */
export interface ListVehiclesUseCase {
    /**
     * Handles listing all vehicles
     *
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of Vehicle entities
     */
    execute(command?: ListVehiclesCommand): Promise<Either<DataError, ListVehiclesResult>>;
}

export type { ListVehiclesCommand } from '@/application/model/vehicle-management/list-vehicles/list-vehicles-command';
export type { ListVehiclesResult } from '@/application/model/vehicle-management/list-vehicles/list-vehicles-result';
