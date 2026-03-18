import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { ServicePolicies } from '@/domain/valueobject/policy/service-policies';
import { PlanningPeriod } from '@/domain/valueobject/time/planning-period';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

// Command object for input data
export interface UpdateInfrastructurePlanCommand {
    planId: UllUUID;
    updatedFields: Partial<{
        period: PlanningPeriod;
        maxBudget: MaximumBudget;
        servicePolicies: ServicePolicies | null;
    }>;
}
