import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateContainerCommand, CreateContainerResult } from '../usecase/ContainerManagement/CreateContainer/create-container-use-case';
import type { GetContainerCommand, GetContainerResult } from '../usecase/ContainerManagement/GetContainer/get-container-use-case';
import type { UpdateContainerCommand, UpdateContainerResult } from '../usecase/ContainerManagement/UpdateContainer/update-container-use-case';
import type { ListContainersCommand, ListContainersResult } from '../usecase/ContainerManagement/ListContainers/list-containers-use-case';
import type { FilterContainersCommand, FilterContainersResult } from '../usecase/ContainerManagement/FilterContainers/filter-containers-use-case';
import type { DeleteContainerCommand, DeleteContainerResult } from '../usecase/ContainerManagement/DeleteContainer/delete-container-use-case';

/**
 * @brief Repository interface for Container entity.
 * Defines methods for CRUD operations and filtering of containers.
 */
export interface ContainerRepository {
  /**
   * @brief List all containers (optional pagination).
   * @param command Optional pagination parameters.
   * @return Either a DataError or a list of Container entities.
   */
  list(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>>;

  /**
   * @brief Get a container by id.
   * @param command Data containing the id of the container to retrieve.
   * @return Either a DataError or the Container entity.
   */
  getById(command: GetContainerCommand): Promise<Either<DataError, GetContainerResult>>;

  /**
   * @brief Create a new container.
   * @param command Data required to create the container.
   * @return Either a DataError or the created Container entity.
   */
  create(command: CreateContainerCommand): Promise<Either<DataError, CreateContainerResult>>;

  /**
   * @brief Update an existing container.
   * @param command Data required to update the container.
   * @return Either a DataError or the updated Container entity.
   */
  update(command: UpdateContainerCommand): Promise<Either<DataError, UpdateContainerResult>>;

  /**
   * @brief Delete a container by id.
   * @param command Data containing the id of the container to delete.
   * @return Either a DataError or a boolean indicating success.
   */
  delete(command: DeleteContainerCommand): Promise<Either<DataError, DeleteContainerResult>>;

  /**
   * @brief Filter containers by criteria.
   * @param command Filter criteria for containers.
   * @return Either a DataError or a list of matching Container entities.
   */
  filter(command: FilterContainersCommand): Promise<Either<DataError, FilterContainersResult>>;
}
