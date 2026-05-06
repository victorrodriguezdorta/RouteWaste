import type { DeleteInfrastructurePlanCommand, DeleteInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/delete-infrastructure-plan/delete-infrastructure-plan-use-case';
import type { GetInfrastructurePlanCommand, GetInfrastructurePlanResult } from '@/application/usecase/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-use-case';
import type { ListInfrastructurePlansCommand, ListInfrastructurePlansResult } from '@/application/usecase/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Repository interface for infrastructure plan HTTP operations.
 * The backend now exposes list/detail/delete only, so the repository mirrors
 * that read/delete surface.
 */
export interface InfrastructurePlanRepository {
  /**
   * @brief List all infrastructure plans (optional pagination).
   * @param command Optional pagination parameters.
  * @return Either a DataError or the paginated list response.
   */
  list(command?: ListInfrastructurePlansCommand): Promise<Either<DataError, ListInfrastructurePlansResult>>;

  /**
   * @brief Get an infrastructure plan by id.
   * @param command Data containing the id of the infrastructure plan to retrieve.
  * @return Either a DataError or the infrastructure plan detail response.
   */
  getById(command: GetInfrastructurePlanCommand): Promise<Either<DataError, GetInfrastructurePlanResult>>;

  /**
   * @brief Delete an infrastructure plan by id.
   * @param command Data containing the id of the infrastructure plan to delete.
   * @return Either a DataError or a boolean indicating success.
   */
  delete(command: DeleteInfrastructurePlanCommand): Promise<Either<DataError, DeleteInfrastructurePlanResult>>;
}
