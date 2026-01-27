// UpdateContainerUseCase.ts
// Use case contract for updating a container

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../domain/entity/Container';
import { Location } from '../../../domain/valueobject/location/Location';
import { WasteType } from '../../../domain/enumerate/WasteType';
import { WasteDemand } from '../../../domain/valueobject/demand/WasteDemand';
import { ServiceZone } from '../../../domain/enumerate/ServiceZone';

/**
 * Use case for updating an existing container in the system.
 * Input: containerId and fields to update
 * Output: Container (entity)
 */

// Command object for input data
export interface UpdateContainerCommand {
    containerId: string;
    updatedFields: Partial<{
        location: Location;
        wasteType: WasteType;
        wasteDemand: WasteDemand;
        serviceZone: ServiceZone | null;
    }>;
}

// Result type for the use case
export type UpdateContainerResult = Container;

// Use case contract
export interface UpdateContainerUseCase {
    /**
     * Handles the update of an existing container
     *
     * @param command Data needed to update the container
     * @returns Either a DataError or the updated Container
     */
    execute(command: UpdateContainerCommand): Promise<Either<DataError, UpdateContainerResult>>;
}
