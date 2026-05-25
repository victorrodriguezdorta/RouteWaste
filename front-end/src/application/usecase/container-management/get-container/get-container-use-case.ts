import type { GetContainerCommand } from '@/application/model/container-management/get-container/get-container-command';
import type { GetContainerResult } from '@/application/model/container-management/get-container/get-container-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for getting a single container by id
 */
export interface GetContainerUseCase {
    /**
     * Handles retrieving a single container by id
     * @param command Data needed to retrieve the container
     * @returns Either a DataError or the Container entity
     */
    execute(command: GetContainerCommand): Promise<Either<DataError, GetContainerResult>>;
}

export type { GetContainerCommand } from '@/application/model/container-management/get-container/get-container-command';
export type { GetContainerResult } from '@/application/model/container-management/get-container/get-container-result';
