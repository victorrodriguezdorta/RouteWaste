
import type { ContainerRepository } from '@/application/repository/container-repository';
import type { ListContainersCommand, ListContainersResult, ListContainersUseCase } from '@/application/usecase/container-management/list-containers/list-containers-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the ListContainers use case.
 *
 * Delegates listing to `ContainerRepository`. Pagination parameters are forwarded
 * directly to the repository; repository must return an `Either<DataError, Container[]>`.
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
     * @returns Either a `DataError` or an array of `Container` entities.
     */
    async execute(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>> {
        const page = command?.page;
        const pageSize = command?.pageSize;
        return this.containerRepository.list({ page, pageSize });
    }
}
