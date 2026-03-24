
import type { ContainerRepository } from '@/application/repository/container-repository';
import type { GetContainerCommand, GetContainerResult, GetContainerUseCase } from '@/application/usecase/container-management/get-container/get-container-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the GetContainer use case.
 *
 * Delegates retrieval to `ContainerRepository` which is expected to provide a
 * `getById` method returning an `Either<DataError, Container>`.
 */
export class GetContainerService implements GetContainerUseCase {
    /**
     * Repositorio utilizado para consultar contenedores por id.
     */
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
     * @returns Either a `DataError` or the `Container` entity.
     */
    async execute(command: GetContainerCommand): Promise<Either<DataError, GetContainerResult>> {
        return this.containerRepository.getById(command);
    }
}
