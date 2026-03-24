
import type { ContainerRepository } from '@/application/repository/container-repository';
import type { UpdateContainerCommand, UpdateContainerResult, UpdateContainerUseCase } from '@/application/usecase/container-management/update-container/update-container-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the UpdateContainer use case.
 *
 * Delegates update operation to a `ContainerRepository` implementation. The repository
 * is responsible for performing the actual API call or persistence operation.
 */
export class UpdateContainerService implements UpdateContainerUseCase {
    /**
     * Repositorio utilizado para realizar operaciones de actualización de contenedores.
     */
    private readonly containerRepository: ContainerRepository;

    /**
     * @brief Construct the service with a repository.
     * @param containerRepository Repository used to perform update operations.
     */
    constructor(containerRepository: ContainerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * Ejecuta el caso de uso para actualizar un contenedor.
     * @param command Datos requeridos para actualizar el contenedor (id y campos parciales).
     * @returns Either un `DataError` o la entidad `Container` actualizada.
     */
    async execute(command: UpdateContainerCommand): Promise<Either<DataError, UpdateContainerResult>> {
        return this.containerRepository.update(command);
    }
}
