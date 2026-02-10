import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { OptimizationAndReportsRepository } from '../../repository/optimization-and-reports-repository';
import type { CalculateCostsCommand, CalculateCostsUseCase, CostCalculationResult } from '../../usecase/optimization-and-reports/calculate-costs/calculate-costs-use-case';


/**
 * @brief Service implementing the CalculateCosts use case.
 *
 * Delegates cost calculation to an `OptimizationRepository` which is expected
 * to provide a `calculateCosts` method returning an `Either<DataError, number>`.
 */
export class CalculateCostsService implements CalculateCostsUseCase {
    private readonly optimizationRepository: OptimizationAndReportsRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param optimizationRepository Repository used to perform cost calculations.
     */
    constructor(optimizationRepository: OptimizationAndReportsRepository) {
        this.optimizationRepository = optimizationRepository;
    }

    /**
     * @brief Execute the calculate costs use case.
     * @param command Input data required to compute costs.
     * @return Either a `DataError` or the computed cost value.
     */
    async execute(command: CalculateCostsCommand): Promise<Either<DataError, CostCalculationResult>> {
        return this.optimizationRepository.calculateCosts(command);
    }
}
