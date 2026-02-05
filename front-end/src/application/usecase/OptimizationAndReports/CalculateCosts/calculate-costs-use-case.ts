// Use case contract for calculating costs

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CalculateCostsCommand } from './calculate-costs-command';
import type { CostCalculationResult } from './calculate-costs-result';

/**
 * Use case contract for calculating costs
 */
export interface CalculateCostsUseCase {
    /**
     * Handles cost calculation
     * @param command Data needed to calculate costs
     * @returns Either a DataError or the calculation result
     */
    execute(command: CalculateCostsCommand): Promise<Either<DataError, CostCalculationResult>>;
}

// Re-export types for convenience
export type { CalculateCostsCommand } from './calculate-costs-command';
export type { CostCalculationResult } from './calculate-costs-result';
