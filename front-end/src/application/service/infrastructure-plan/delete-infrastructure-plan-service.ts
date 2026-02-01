import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { DeleteInfrastructurePlanUseCase, DeleteInfrastructurePlanCommand, DeleteInfrastructurePlanResult } from '../../usecase/InfrastructurePlanManagement/delete-infrastructure-plan-use-case';
import { InfrastructurePlanRepository } from '../../repository/infrastructure-plan/infrastructure-plan-repository';

/**
 * @brief Service implementing the DeleteInfrastructurePlan use case.
 *
 * Delegates deletion to `InfrastructurePlanRepository`. Repository should return a boolean
 * indicating success or an error wrapped in `Either`.
 */
export class DeleteInfrastructurePlanService implements DeleteInfrastructurePlanUseCase {
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
     * @return Either a `DataError` or a boolean indicating success.
     */
    async execute(command: DeleteInfrastructurePlanCommand): Promise<Either<DataError, DeleteInfrastructurePlanResult>> {
        return this.infrastructurePlanRepository.delete(command.planId);
    }
}
