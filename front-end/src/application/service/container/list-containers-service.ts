
import type { ContainerRepository } from '@/application/repository/container-repository';
import type { ListContainersCommand, ListContainersResult, ListContainersUseCase } from '@/application/usecase/container-management/list-containers/list-containers-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the ListContainers use case.
 *
 * Delegates listing to `ContainerRepository`. Pagination, sorting and filtering
 * parameters are forwarded directly to the repository.
 */
export class ListContainersService implements ListContainersUseCase {
    /**
     * Repositorio utilizado para listar contenedores.
     * @remarks
     * Este atributo almacena la instancia del repositorio que implementa la lógica de acceso a datos para contenedores.
     */
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
     * @returns Either a `DataError` or a paginated set of `Container` entities.
     */
    async execute(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>> {
        return this.containerRepository.list(command);
    }
}
