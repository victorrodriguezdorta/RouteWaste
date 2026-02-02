import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateInfrastructurePlanUseCase, CreateInfrastructurePlanCommand, CreateInfrastructurePlanResult } from '../../usecase/InfrastructurePlanManagement/create-infrastructure-plan-use-case';
import type { InfrastructurePlanRepository } from '../../repository/infrastructure-plan-repository';

/**
 * @brief Service implementing the CreateInfrastructurePlan use case.
 *
 * Implements the application-level use case by delegating persistence/communication
 * details to a provided `InfrastructurePlanRepository`. Repository methods are
 * assumed to be available and provided later by the infrastructure layer.
 */
export class CreateInfrastructurePlanService implements CreateInfrastructurePlanUseCase {
    private readonly infrastructurePlanRepository: InfrastructurePlanRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param infrastructurePlanRepository Repository used to persist or forward create requests.
     */
    constructor(infrastructurePlanRepository: InfrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * @brief Execute the create infrastructure plan use case.
     * @param command Input data required to create an infrastructure plan.
     * @return Either a `DataError` or the created `InfrastructurePlan` entity.
     */
    async execute(command: CreateInfrastructurePlanCommand): Promise<Either<DataError, CreateInfrastructurePlanResult>> {
        return this.infrastructurePlanRepository.create(command);
    }
}
