// CreateContainerUseCase.ts
// Use case contract for creating a new container

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../domain/entity/Container';
import { Location } from '../../../domain/valueobject/location/Location';
import { WasteType } from '../../../domain/enumerate/WasteType';
import { WasteDemand } from '../../../domain/valueobject/demand/WasteDemand';

/**
 * Use case for registering a new container in the system.
 * Input: location, waste type, waste demand, service zone
 * Output: Container (entity)
 */

// Command object for input data
export interface CreateContainerCommand {
    location: Location;
    wasteType: WasteType;
    wasteDemand: WasteDemand;
    serviceZone?: string | null;
}

// Result type for the use case
export type CreateContainerResult = Container;

// Use case contract
export interface CreateContainerUseCase {
    /**
     * Handles the creation of a new container
     *
     * @param command Data needed to create the container
     * @returns Either a DataError or the created Container
     */
    execute(command: CreateContainerCommand): Promise<Either<DataError, CreateContainerResult>>;
}
