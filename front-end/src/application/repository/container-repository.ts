import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { ServiceAssignment } from '../../domain/entity/service-assignment';
import { WasteDemand } from '../../domain/valueobject/demand/waste-demand';
import { Distance } from '../../domain/valueobject/location/distance';
import { ServiceTime } from '../../domain/valueobject/location/service-time';
import { TransportationVariableCost } from '../../domain/valueobject/cost/transportation-variable-cost';

import type { CreateContainerCommand, CreateContainerResult } from '../usecase/ContainerManagement/create-container-use-case';
import type { GetContainerCommand, GetContainerResult } from '../usecase/ContainerManagement/get-container-use-case';
import type { UpdateContainerCommand, UpdateContainerResult } from '../usecase/ContainerManagement/update-container-use-case';
import type { ListContainersCommand, ListContainersResult } from '../usecase/ContainerManagement/list-containers-use-case';
import type { FilterContainersCommand, FilterContainersResult } from '../usecase/ContainerManagement/filter-containers-use-case';
import type { DeleteContainerCommand, DeleteContainerResult } from '../usecase/ContainerManagement/delete-container-use-case';

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
   * @brief Assign a container to a facility (specific use case).
   * @param containerId The id of the container to assign.
   * @param facilityId The id of the facility.
   * @param wasteDemand Waste demand value object.
   * @param distance Distance value object.
   * @param serviceTime Service time value object.
   * @param transportCost Transportation variable cost value object.
   * @return Either a DataError or the ServiceAssignment entity.
   * @note You can adapt the parameters to a command if you have a use case for this.
   */
  assignContainerToFacility(
    containerId: string,
    facilityId: string,
    wasteDemand: WasteDemand,
    distance: Distance,
    serviceTime: ServiceTime,
    transportCost: TransportationVariableCost
  ): Promise<Either<DataError, ServiceAssignment>>;
}
