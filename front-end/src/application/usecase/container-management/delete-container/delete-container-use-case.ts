import type { DeleteContainerCommand } from '@/application/model/container-management/delete-container/delete-container-command';
import type { DeleteContainerResult } from '@/application/model/container-management/delete-container/delete-container-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for deleting a container
 */
export interface DeleteContainerUseCase {
    /**
     * Handles the deletion of a container
     * @param command Data needed to delete the container
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteContainerCommand): Promise<Either<DataError, DeleteContainerResult>>;
}

export type { DeleteContainerCommand } from '@/application/model/container-management/delete-container/delete-container-command';
export type { DeleteContainerResult } from '@/application/model/container-management/delete-container/delete-container-result';
