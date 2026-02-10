
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ContainerRepository } from '../../repository/container-repository';
import type { UpdateContainerCommand, UpdateContainerResult, UpdateContainerUseCase } from '../../usecase/container-management/update-container/update-container-use-case';

/**
 * @brief Service implementing the UpdateContainer use case.
 *
 * Delegates update operation to a `ContainerRepository` implementation. The repository
 * is responsible for performing the actual API call or persistence operation.
 */
export class UpdateContainerService implements UpdateContainerUseCase {
    private readonly containerRepository: ContainerRepository;

    /**
     * @brief Construct the service with a repository.
     * @param containerRepository Repository used to perform update operations.
     */
    constructor(containerRepository: ContainerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * @brief Execute the update container use case.
     * @param command Data required to update the container (id + partial fields).
     * @return Either a `DataError` or the updated `Container` entity.
     */
    async execute(command: UpdateContainerCommand): Promise<Either<DataError, UpdateContainerResult>> {
        return this.containerRepository.update(command);
    }
}
