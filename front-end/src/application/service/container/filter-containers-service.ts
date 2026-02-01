
import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { FilterContainersUseCase, FilterContainersCommand, FilterContainersResult } from '../../usecase/ContainerManagement/filter-containers-use-case';
import { ContainerRepository } from '../../repository/container/container-repository';

/**
 * @brief Service implementing the FilterContainers use case.
 *
 * Delegates filtering to `ContainerRepository`. The repository is expected to
 * accept the filter criteria and return matching containers wrapped in `Either`.
 */
export class FilterContainersService implements FilterContainersUseCase {
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
     * @return Either a `DataError` or an array of matching `Container` entities.
     */
    async execute(command: FilterContainersCommand): Promise<Either<DataError, FilterContainersResult>> {
        return this.containerRepository.filter(command);
    }
}
