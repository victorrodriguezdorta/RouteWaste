import type { ListContainersCommand } from '@/application/model/container-management/list-containers/list-containers-command';
import type { ListContainersResult } from '@/application/model/container-management/list-containers/list-containers-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for listing all containers
 */
export interface ListContainersUseCase {
    /**
     * Handles listing all containers
     * @param command Optional pagination parameters
     * @returns Either a DataError or a paginated list of Container entities
     */
    execute(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>>;
}

export type { ListContainersCommand } from '@/application/model/container-management/list-containers/list-containers-command';
export type { ListContainersResult } from '@/application/model/container-management/list-containers/list-containers-result';
