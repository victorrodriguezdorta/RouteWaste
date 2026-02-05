
import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { DeleteContainerUseCase, DeleteContainerCommand, DeleteContainerResult } from '../../usecase/ContainerManagement/DeleteContainer/delete-container-use-case';
import type { ContainerRepository } from '../../repository/container-repository';

/**
 * @brief Service implementing the DeleteContainer use case.
 *
 * Delegates deletion to `ContainerRepository`. Repository should return a boolean
 * indicating success or an error wrapped in `Either`.
 */
export class DeleteContainerService implements DeleteContainerUseCase {
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
     * @return Either a `DataError` or a boolean indicating success.
     */
    async execute(command: DeleteContainerCommand): Promise<Either<DataError, DeleteContainerResult>> {
        return this.containerRepository.delete(command);
    }
}
