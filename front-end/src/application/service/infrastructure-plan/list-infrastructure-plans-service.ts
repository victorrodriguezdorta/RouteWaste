import type { InfrastructurePlanRepository } from '@/application/repository/infrastructure-plan-repository';
import type { ListInfrastructurePlansCommand, ListInfrastructurePlansResult, ListInfrastructurePlansUseCase } from '@/application/usecase/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the ListInfrastructurePlans use case.
 *
 * Delegates listing to `InfrastructurePlanRepository`. Pagination parameters are forwarded
 * directly to the repository; repository must return an `Either<DataError, InfrastructurePlan[]>`.
 */
export class ListInfrastructurePlansService implements ListInfrastructurePlansUseCase {
    /**
     * Repositorio utilizado para listar los planes de infraestructura.
     */
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
     * @returns Either a `DataError` or an array of `InfrastructurePlan` entities.
     */
    async execute(command?: ListInfrastructurePlansCommand): Promise<Either<DataError, ListInfrastructurePlansResult>> {
        /**
         * Ejecuta el caso de uso para listar los planes de infraestructura.
         * @param command Parámetros opcionales de paginación.
         * @returns Un objeto Either que contiene un DataError o un array de entidades InfrastructurePlan.
         */
        // Forward all received parameters (pagination + sorting) to the repository
        return this.infrastructurePlanRepository.list(command);
    }
}
