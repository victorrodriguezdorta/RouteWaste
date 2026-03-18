import type { InfrastructurePlanRepository } from '@/application/repository/infrastructure-plan-repository';
import type { GetInfrastructurePlanCommand, GetInfrastructurePlanResult, GetInfrastructurePlanUseCase } from '@/application/usecase/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the GetInfrastructurePlan use case.
 *
 * Delegates retrieval to `InfrastructurePlanRepository` which is expected to provide a
 * `getById` method returning an `Either<DataError, InfrastructurePlan>`.
 */
export class GetInfrastructurePlanService implements GetInfrastructurePlanUseCase {
    private readonly infrastructurePlanRepository: InfrastructurePlanRepository;

    /**
     * @brief Construct the service with a repository.
     * @param infrastructurePlanRepository Repository used to query plans by id.
     */
    constructor(infrastructurePlanRepository: InfrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * @brief Execute the get infrastructure plan use case.
     * @param command Data containing the id of the plan to retrieve.
     * @return Either a `DataError` or the `InfrastructurePlan` entity.
     */
    async execute(command: GetInfrastructurePlanCommand): Promise<Either<DataError, GetInfrastructurePlanResult>> {
        return this.infrastructurePlanRepository.getById(command);
    }
}
