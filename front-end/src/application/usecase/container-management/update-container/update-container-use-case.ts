import type { UpdateContainerCommand } from '@/application/model/container-management/update-container/update-container-command';
import type { UpdateContainerResult } from '@/application/model/container-management/update-container/update-container-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for updating a container
 */
export interface UpdateContainerUseCase {
    /**
     * Handles the update of an existing container
     * @param command Data needed to update the container
     * @returns Either a DataError or the updated Container
     */
    execute(command: UpdateContainerCommand): Promise<Either<DataError, UpdateContainerResult>>;
}

export type { UpdateContainerCommand } from '@/application/model/container-management/update-container/update-container-command';
export type { UpdateContainerResult } from '@/application/model/container-management/update-container/update-container-result';
