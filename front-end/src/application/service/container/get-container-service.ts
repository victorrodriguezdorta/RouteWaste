
import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { GetContainerUseCase, GetContainerCommand, GetContainerResult } from '../../usecase/ContainerManagement/get-container-use-case';
import type { ContainerRepository } from '../../repository/container-repository';

/**
 * @brief Service implementing the GetContainer use case.
 *
 * Delegates retrieval to `ContainerRepository` which is expected to provide a
 * `getById` method returning an `Either<DataError, Container>`.
 */
export class GetContainerService implements GetContainerUseCase {
    private readonly containerRepository: ContainerRepository;

    /**
     * @brief Construct the service with a repository.
     * @param containerRepository Repository used to query containers by id.
     */
    constructor(containerRepository: ContainerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * @brief Execute the get container use case.
     * @param command Data containing the id of the container to retrieve.
     * @return Either a `DataError` or the `Container` entity.
     */
    async execute(command: GetContainerCommand): Promise<Either<DataError, GetContainerResult>> {
        return this.containerRepository.getById(command);
    }
}
