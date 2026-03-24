import type { ContainerRepository } from '@/application/repository/container-repository';
import type { CreateContainerCommand, CreateContainerResult, CreateContainerUseCase } from '@/application/usecase/container-management/create-container/create-container-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the CreateContainer use case.
 *
 * Implements the application-level use case by delegating persistence/communication
 * details to a provided `ContainerRepository`. Repository methods are assumed to
 * be available and will be provided later by the professor's infrastructure.
 */
export class CreateContainerService implements CreateContainerUseCase {
    /**
     * Repository instance for container persistence operations.
     */
    private readonly containerRepository: ContainerRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param containerRepository Repository used to persist or forward create requests.
     */
    constructor(containerRepository: ContainerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * @brief Execute the create container use case.
     * @param command Input data required to create a container.
     * @returns Either a `DataError` or the created `Container` entity.
     */
    async execute(command: CreateContainerCommand): Promise<Either<DataError, CreateContainerResult>> {
        return this.containerRepository.create(command);
    }
}
