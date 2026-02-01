import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CalculateCostsUseCase, CalculateCostsCommand, CalculateCostsResult } from '../../usecase/OptimizationAndReports/calculate-costs-use-case';
import { OptimizationRepository } from '../../repository/optimization/optimization-repository';

/**
 * @brief Service implementing the CalculateCosts use case.
 *
 * Delegates cost calculation to an `OptimizationRepository` which is expected
 * to provide a `calculateCosts` method returning an `Either<DataError, number>`.
 */
export class CalculateCostsService implements CalculateCostsUseCase {
    private readonly optimizationRepository: OptimizationRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param optimizationRepository Repository used to perform cost calculations.
     */
    constructor(optimizationRepository: OptimizationRepository) {
        this.optimizationRepository = optimizationRepository;
    }

    /**
     * @brief Execute the calculate costs use case.
     * @param command Input data required to compute costs.
     * @return Either a `DataError` or the computed cost value.
     */
    async execute(command: CalculateCostsCommand): Promise<Either<DataError, CalculateCostsResult>> {
        return this.optimizationRepository.calculateCosts(command);
    }
}
