import type { InfrastructurePlanRepository } from '@/application/repository/infrastructure-plan-repository';
import type { DeleteInfrastructurePlanCommand, DeleteInfrastructurePlanResult, DeleteInfrastructurePlanUseCase } from '@/application/usecase/infrastructure-plan-management/delete-infrastructure-plan/delete-infrastructure-plan-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the DeleteInfrastructurePlan use case.
 *
 * Delegates deletion to `InfrastructurePlanRepository`. Repository should return a boolean
 * indicating success or an error wrapped in `Either`.
 */
export class DeleteInfrastructurePlanService implements DeleteInfrastructurePlanUseCase {
    /**
     * Repository used to perform infrastructure plan deletion operations.
     */
    private readonly infrastructurePlanRepository: InfrastructurePlanRepository;

    /**
     * @brief Construct the service with a repository.
     * @param infrastructurePlanRepository Repository used to perform delete operations.
     */
    constructor(infrastructurePlanRepository: InfrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * @brief Execute the delete infrastructure plan use case.
     * @param command Data containing the id of the plan to delete.
     * @returns Either a `DataError` or a boolean indicating success.
     */
    async execute(command: DeleteInfrastructurePlanCommand): Promise<Either<DataError, DeleteInfrastructurePlanResult>> {
        return this.infrastructurePlanRepository.delete(command);
    }
}
