import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

import type { CreateFacilityCommand, CreateFacilityResult } from '@/application/usecase/facility-management/create-facility/create-facility-use-case';
import type { DeleteFacilityCommand, DeleteFacilityResult } from '@/application/usecase/facility-management/delete-facility/delete-facility-use-case';
import type { FilterFacilitiesCommand, FilterFacilitiesResult } from '@/application/usecase/facility-management/filter-facilities/filter-facilities-use-case';
import type { GetFacilityCommand, GetFacilityResult } from '@/application/usecase/facility-management/get-facility/get-facility-use-case';
import type { ListFacilitiesCommand, ListFacilitiesResult } from '@/application/usecase/facility-management/list-facilities/list-facilities-use-case';
import type { UpdateFacilityCommand, UpdateFacilityResult } from '@/application/usecase/facility-management/update-facility/update-facility-use-case';

/**
 * @brief Repository interface for Facility entity.
 * Defines methods for CRUD operations and filtering of facilities.
 */
export interface FacilityRepository {
  /**
   * @brief List all facilities (optional pagination).
   * @param command Optional pagination parameters.
   * @return Either a DataError or a list of Facility entities.
   */
  list(command?: ListFacilitiesCommand): Promise<Either<DataError, ListFacilitiesResult>>;

  /**
   * @brief Get a facility by id.
   * @param command Data containing the id of the facility to retrieve.
   * @return Either a DataError or the Facility entity.
   */
  getById(command: GetFacilityCommand): Promise<Either<DataError, GetFacilityResult>>;

  /**
   * @brief Create a new facility.
   * @param command Data required to create the facility.
   * @return Either a DataError or the created Facility entity.
   */
  create(command: CreateFacilityCommand): Promise<Either<DataError, CreateFacilityResult>>;

  /**
   * @brief Update an existing facility.
   * @param command Data required to update the facility.
   * @return Either a DataError or the updated Facility entity.
   */
  update(command: UpdateFacilityCommand): Promise<Either<DataError, UpdateFacilityResult>>;

  /**
   * @brief Delete a facility by id.
   * @param command Data containing the id of the facility to delete.
   * @return Either a DataError or a boolean indicating success.
   */
  delete(command: DeleteFacilityCommand): Promise<Either<DataError, DeleteFacilityResult>>;

  /**
   * @brief Filter facilities by criteria.
   * @param command Filter criteria for facilities.
   * @return Either a DataError or a list of matching Facility entities.
   */
  filter(command: FilterFacilitiesCommand): Promise<Either<DataError, FilterFacilitiesResult>>;
}
