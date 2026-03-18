import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { ServicePolicies } from '@/domain/valueobject/policy/service-policies';
import { PlanningPeriod } from '@/domain/valueobject/time/planning-period';

// Command object for input data
export interface CreateInfrastructurePlanCommand {
    period: PlanningPeriod;
    maxBudget: MaximumBudget;
    servicePolicies?: ServicePolicies | null;
}