
import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ListContainersUseCase, ListContainersCommand, ListContainersResult } from '../../usecase/ContainerManagement/list-containers-use-case';
import type { ContainerRepository } from '../../repository/container-repository';

/**
 * @brief Service implementing the ListContainers use case.
 *
 * Delegates listing to `ContainerRepository`. Pagination parameters are forwarded
 * directly to the repository; repository must return an `Either<DataError, Container[]>`.
 */
export class ListContainersService implements ListContainersUseCase {
    private readonly containerRepository: ContainerRepository;

    /**
     * @brief Construct the service with a repository.
     * @param containerRepository Repository used to list containers.
     */
    constructor(containerRepository: ContainerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * @brief Execute the list containers use case.
     * @param command Optional pagination parameters.
     * @return Either a `DataError` or an array of `Container` entities.
     */
    async execute(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>> {
        const page = command?.page;
        const pageSize = command?.pageSize;
        return this.containerRepository.list({ page, pageSize });
    }
}
