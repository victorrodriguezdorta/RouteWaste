import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ListInfrastructurePlansUseCase, ListInfrastructurePlansCommand, ListInfrastructurePlansResult } from '../../usecase/InfrastructurePlanManagement/ListInfrastructurePlans/list-infrastructure-plans-use-case';
import type { InfrastructurePlanRepository } from '../../repository/infrastructure-plan-repository';

/**
 * @brief Service implementing the ListInfrastructurePlans use case.
 *
 * Delegates listing to `InfrastructurePlanRepository`. Pagination parameters are forwarded
 * directly to the repository; repository must return an `Either<DataError, InfrastructurePlan[]>`.
 */
export class ListInfrastructurePlansService implements ListInfrastructurePlansUseCase {
    private readonly infrastructurePlanRepository: InfrastructurePlanRepository;

    /**
     * @brief Construct the service with a repository.
     * @param infrastructurePlanRepository Repository used to list infrastructure plans.
     */
    constructor(infrastructurePlanRepository: InfrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * @brief Execute the list infrastructure plans use case.
     * @param command Optional pagination parameters.
     * @return Either a `DataError` or an array of `InfrastructurePlan` entities.
     */
    async execute(command?: ListInfrastructurePlansCommand): Promise<Either<DataError, ListInfrastructurePlansResult>> {
        const page = command?.page;
        const pageSize = command?.pageSize;
        return this.infrastructurePlanRepository.list({ page, pageSize });
    }
}
