import type { InfrastructurePlanRepository } from '@/application/repository/infrastructure-plan-repository';
import type { UpdateInfrastructurePlanCommand, UpdateInfrastructurePlanResult, UpdateInfrastructurePlanUseCase } from '@/application/usecase/infrastructure-plan-management/update-infrastructure-plan/update-infrastructure-plan-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the UpdateInfrastructurePlan use case.
 *
 * Delegates update operation to a `InfrastructurePlanRepository` implementation.
 */
export class UpdateInfrastructurePlanService implements UpdateInfrastructurePlanUseCase {
    /** The repository used to perform infrastructure plan update operations. */
    private readonly infrastructurePlanRepository: InfrastructurePlanRepository;

    /**
     * @brief Construct the service with a repository.
     * @param infrastructurePlanRepository Repository used to perform update operations.
     */
    constructor(infrastructurePlanRepository: InfrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * @brief Execute the update infrastructure plan use case.
     * @param command Data required to update the infrastructure plan (id + partial fields).
     * @returns Either a `DataError` or the updated `InfrastructurePlan` entity.
     */
    async execute(command: UpdateInfrastructurePlanCommand): Promise<Either<DataError, UpdateInfrastructurePlanResult>> {
        return this.infrastructurePlanRepository.update(command);
    }
}
