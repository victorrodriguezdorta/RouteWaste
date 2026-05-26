import type { CreateContainerCommand } from '@/application/model/container-management/create-container/create-container-command';
import type { CreateContainerResult } from '@/application/model/container-management/create-container/create-container-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for registering a new container in the system.
 */
export interface CreateContainerUseCase {
    /**
     * Handles the creation of a new container
     * @param command Data needed to create the container
     * @returns Either a DataError or the created Container
     */
    execute(command: CreateContainerCommand): Promise<Either<DataError, CreateContainerResult>>;
}

export type { CreateContainerCommand } from '@/application/model/container-management/create-container/create-container-command';
export type { CreateContainerResult } from '@/application/model/container-management/create-container/create-container-result';
