// CalculateCostsUseCase.ts
// Use case contract for calculating costs

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { TotalCost } from '../../../domain/valueobject/cost/total-cost';
import { OpeningFixedCost } from '../../../domain/valueobject/cost/opening-fixed-cost';
import { TransportationVariableCost } from '../../../domain/valueobject/cost/transportation-variable-cost';

/**
 * Represents a cost calculation result
 */
export interface CostCalculationResult {
    totalCost: TotalCost;
    openingCosts: OpeningFixedCost;
    transportationCosts: TransportationVariableCost;
    breakdown: {
        facilityId: string;
        cost: number;
    }[];
}

/**
 * Use case for calculating costs of an infrastructure plan or route.
 * Input: planId or route details
 * Output: CostCalculationResult
 */

// Command object for input data
export interface CalculateCostsCommand {
    planId?: string;
    facilityIds?: string[];
    includeTransportation?: boolean;
}

// Result type for the use case
export type CalculateCostsResult = CostCalculationResult;

// Use case contract
export interface CalculateCostsUseCase {
    /**
     * Handles the calculation of costs
     *
     * @param command Data needed to calculate costs
     * @returns Either a DataError or the cost calculation result
     */
    execute(command: CalculateCostsCommand): Promise<Either<DataError, CalculateCostsResult>>;
}
