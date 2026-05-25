import type { BulkImportResult } from '@/adapter/http/dto/common/bulk-import-result';
import type { CreateContainerCommand, CreateContainerResult } from '@/application/usecase/container-management/create-container/create-container-use-case';
import type { DeleteContainerCommand, DeleteContainerResult } from '@/application/usecase/container-management/delete-container/delete-container-use-case';
import type { FilterContainersCommand, FilterContainersResult } from '@/application/usecase/container-management/filter-containers/filter-containers-use-case';
import type { GetContainerCommand, GetContainerResult } from '@/application/usecase/container-management/get-container/get-container-use-case';
import type { ListContainersCommand, ListContainersResult } from '@/application/usecase/container-management/list-containers/list-containers-use-case';
import type { UpdateContainerCommand, UpdateContainerResult } from '@/application/usecase/container-management/update-container/update-container-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

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

  /**
   * @brief Import containers from a JSON file.
   * @param file JSON file selected by the user.
   * @return Either a DataError or bulk import statistics.
   */
  importFromFile(file: File): Promise<Either<DataError, BulkImportResult>>;
}
