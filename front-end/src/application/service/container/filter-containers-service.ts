
import type { ContainerRepository } from '@/application/repository/container-repository';
import type { FilterContainersCommand, FilterContainersResult, FilterContainersUseCase } from '@/application/usecase/container-management/filter-containers/filter-containers-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the FilterContainers use case.
 *
 * Delegates filtering to `ContainerRepository`. The repository is expected to
 * accept the filter criteria and return matching containers wrapped in `Either`.
 */
export class FilterContainersService implements FilterContainersUseCase {
    /**
     * Repository used to filter containers.
     */
    private readonly containerRepository: ContainerRepository;

    /**
     * @brief Construct the service with a repository.
     * @param containerRepository Repository used to filter containers.
     */
    constructor(containerRepository: ContainerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * @brief Execute the filter containers use case.
     * @param command Filter criteria for containers.
     * @returns Either a `DataError` or an array of matching `Container` entities.
     */
    async execute(command: FilterContainersCommand): Promise<Either<DataError, FilterContainersResult>> {
        return this.containerRepository.filter(command);
    }
}
