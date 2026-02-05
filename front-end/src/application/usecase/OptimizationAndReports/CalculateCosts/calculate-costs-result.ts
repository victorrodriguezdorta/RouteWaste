import { TotalCost } from '../../../../domain/valueobject/cost/total-cost';
import { OpeningFixedCost } from '../../../../domain/valueobject/cost/opening-fixed-cost';
import { TransportationVariableCost } from '../../../../domain/valueobject/cost/transportation-variable-cost';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Represents a cost calculation result
 */
export interface CostCalculationResult {
    totalCost: TotalCost;
    openingCosts: OpeningFixedCost;
    transportationCosts: TransportationVariableCost;
    breakdown: {
        facilityId: UllUUID;
        cost: number;
    }[];
}
