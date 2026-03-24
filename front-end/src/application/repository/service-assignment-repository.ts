import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

import type { AssignContainerToFacilityCommand, AssignContainerToFacilityResult } from '@/application/usecase/service-assignment-management/assign-container-to-facility/assign-container-to-facility-use-case';
import type { ListServiceAssignmentsCommand, ListServiceAssignmentsResult } from '@/application/usecase/service-assignment-management/list-service-assignments/list-service-assignments-use-case';
import type { RemoveServiceAssignmentCommand, RemoveServiceAssignmentResult } from '@/application/usecase/service-assignment-management/remove-service-assignment/remove-service-assignment-use-case';
import type { UpdateServiceAssignmentCommand, UpdateServiceAssignmentResult } from '@/application/usecase/service-assignment-management/update-service-assignment/update-service-assignment-use-case';

/**
 * @brief Repository interface for ServiceAssignment entity.
 * Defines methods for CRUD operations and assignment logic.
 */
export interface ServiceAssignmentRepository {
  /**
   * @brief List all service assignments (optional pagination and filters).
   * @param command Optional pagination and filter parameters.
   * @return Either a DataError or a list of ServiceAssignment entities.
   */
  list(command?: ListServiceAssignmentsCommand): Promise<Either<DataError, ListServiceAssignmentsResult>>;

  /**
   * @brief Assign a container to a facility.
   * @param command Data required to create the service assignment.
   * @return Either a DataError or the created ServiceAssignment entity.
   */
  assignContainerToFacility(command: AssignContainerToFacilityCommand): Promise<Either<DataError, AssignContainerToFacilityResult>>;

  /**
   * @brief Update an existing service assignment.
   * @param command Data required to update the assignment.
   * @return Either a DataError or the updated ServiceAssignment entity.
   */
  update(command: UpdateServiceAssignmentCommand): Promise<Either<DataError, UpdateServiceAssignmentResult>>;

  /**
   * @brief Remove a service assignment by id.
   * @param command Data containing the id of the assignment to remove.
   * @return Either a DataError or a boolean indicating success.
   */
  delete(command: RemoveServiceAssignmentCommand): Promise<Either<DataError, RemoveServiceAssignmentResult>>;
}
