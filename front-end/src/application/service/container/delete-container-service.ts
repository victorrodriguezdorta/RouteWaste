
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ContainerRepository } from '../../repository/container-repository';
import type { DeleteContainerCommand, DeleteContainerResult, DeleteContainerUseCase } from '../../usecase/container-management/delete-container/delete-container-use-case';

/**
 * @brief Service implementing the DeleteContainer use case.
 *
 * Delegates deletion to `ContainerRepository`. Repository should return a boolean
 * indicating success or an error wrapped in `Either`.
 */
export class DeleteContainerService implements DeleteContainerUseCase {
    /**
     * Repository used to perform delete operations.
     */
    private readonly containerRepository: ContainerRepository;

    /**
     * @brief Construct the service with a repository.
     * @param containerRepository Repository used to perform delete operations.
     */
    constructor(containerRepository: ContainerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * @brief Execute the delete container use case.
     * @param command Data containing the id of the container to delete.
     * @returns Either a `DataError` or a boolean indicating success.
     */
    async execute(command: DeleteContainerCommand): Promise<Either<DataError, DeleteContainerResult>> {
        return this.containerRepository.delete(command);
    }
}
