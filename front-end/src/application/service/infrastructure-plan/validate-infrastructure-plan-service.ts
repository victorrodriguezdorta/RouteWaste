import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { InfrastructurePlanRepository } from '../../repository/infrastructure-plan-repository';
import type { ValidateInfrastructurePlanCommand, ValidateInfrastructurePlanResult, ValidateInfrastructurePlanUseCase } from '../../usecase/infrastructure-plan-management/validate-infrastructure-plan/validate-infrastructure-plan-use-case';

/**
 * @brief Service implementing the ValidateInfrastructurePlan use case.
 *
 * Delegates validation logic to `InfrastructurePlanRepository`. Repository is
 * expected to run domain validations and return a boolean wrapped in `Either`.
 */
export class ValidateInfrastructurePlanService implements ValidateInfrastructurePlanUseCase {
    private readonly infrastructurePlanRepository: InfrastructurePlanRepository;

    /**
     * @brief Construct the service with a repository.
     * @param infrastructurePlanRepository Repository used to perform validation.
     */
    constructor(infrastructurePlanRepository: InfrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * @brief Execute the validate infrastructure plan use case.
     * @param command Data containing the id of the plan to validate.
     * @return Either a `DataError` or a boolean indicating if the plan is valid.
     */
    async execute(command: ValidateInfrastructurePlanCommand): Promise<Either<DataError, ValidateInfrastructurePlanResult>> {
        return this.infrastructurePlanRepository.validate(command);
    }
}
