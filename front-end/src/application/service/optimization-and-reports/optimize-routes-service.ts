import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { OptimizeRoutesUseCase, OptimizeRoutesCommand, OptimizeRoutesResult } from '../../usecase/OptimizationAndReports/OptimizeRoutes/optimize-routes-use-case';
import type { OptimizationAndReportsRepository } from '../../repository/optimization-and-reports-repository';

/**
 * @brief Service implementing the OptimizeRoutes use case.
 *
 * Delegates route optimization to an `OptimizationRepository` which should
 * implement optimization algorithms and return optimized routes wrapped in `Either`.
 */
export class OptimizeRoutesService implements OptimizeRoutesUseCase {
    private readonly optimizationRepository: OptimizationAndReportsRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param optimizationRepository Repository used to perform route optimization.
     */
    constructor(optimizationRepository: OptimizationAndReportsRepository) {
        this.optimizationRepository = optimizationRepository;
    }

    /**
     * @brief Execute the optimize routes use case.
     * @param command Input data with containers, facilities, vehicles and params.
     * @return Either a `DataError` or the optimized routes result.
     */
    async execute(command: OptimizeRoutesCommand): Promise<Either<DataError, OptimizeRoutesResult>> {
        return this.optimizationRepository.optimizeRoutes(command);
    }
}
