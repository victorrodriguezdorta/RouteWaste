import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateInfrastructurePlanCommand, CreateInfrastructurePlanResult } from '../usecase/InfrastructurePlanManagement/CreateInfrastructurePlan/create-infrastructure-plan-use-case';
import type { GetInfrastructurePlanCommand, GetInfrastructurePlanResult } from '../usecase/InfrastructurePlanManagement/GetInfrastructurePlan/get-infrastructure-plan-use-case';
import type { UpdateInfrastructurePlanCommand, UpdateInfrastructurePlanResult } from '../usecase/InfrastructurePlanManagement/UpdateInfrastructurePlan/update-infrastructure-plan-use-case';
import type { ListInfrastructurePlansCommand, ListInfrastructurePlansResult } from '../usecase/InfrastructurePlanManagement/ListInfrastructurePlans/list-infrastructure-plans-use-case';
import type { DeleteInfrastructurePlanCommand, DeleteInfrastructurePlanResult } from '../usecase/InfrastructurePlanManagement/DeleteInfrastructurePlan/delete-infrastructure-plan-use-case';
import type { ValidateInfrastructurePlanCommand, ValidateInfrastructurePlanResult } from '../usecase/InfrastructurePlanManagement/ValidateInfrastructurePlan/validate-infrastructure-plan-use-case';

/**
 * @brief Repository interface for InfrastructurePlan entity.
 * Defines methods for CRUD operations and filtering of infrastructure plans.
 */
export interface InfrastructurePlanRepository {
  /**
   * @brief List all infrastructure plans (optional pagination).
   * @param command Optional pagination parameters.
   * @return Either a DataError or a list of InfrastructurePlan entities.
   */
  list(command?: ListInfrastructurePlansCommand): Promise<Either<DataError, ListInfrastructurePlansResult>>;

  /**
   * @brief Get an infrastructure plan by id.
   * @param command Data containing the id of the infrastructure plan to retrieve.
   * @return Either a DataError or the InfrastructurePlan entity.
   */
  getById(command: GetInfrastructurePlanCommand): Promise<Either<DataError, GetInfrastructurePlanResult>>;

  /**
   * @brief Create a new infrastructure plan.
   * @param command Data required to create the infrastructure plan.
   * @return Either a DataError or the created InfrastructurePlan entity.
   */
  create(command: CreateInfrastructurePlanCommand): Promise<Either<DataError, CreateInfrastructurePlanResult>>;

  /**
   * @brief Update an existing infrastructure plan.
   * @param command Data required to update the infrastructure plan.
   * @return Either a DataError or the updated InfrastructurePlan entity.
   */
  update(command: UpdateInfrastructurePlanCommand): Promise<Either<DataError, UpdateInfrastructurePlanResult>>;

  /**
   * @brief Delete an infrastructure plan by id.
   * @param command Data containing the id of the infrastructure plan to delete.
   * @return Either a DataError or a boolean indicating success.
   */
  delete(command: DeleteInfrastructurePlanCommand): Promise<Either<DataError, DeleteInfrastructurePlanResult>>;
  /**
   * @brief Validate an infrastructure plan.
   * @param command Data containing the id of the plan to validate.
   * @return Either a DataError or a boolean indicating if the plan is valid.
   */
  validate(command: ValidateInfrastructurePlanCommand): Promise<Either<DataError, ValidateInfrastructurePlanResult>>;
}
