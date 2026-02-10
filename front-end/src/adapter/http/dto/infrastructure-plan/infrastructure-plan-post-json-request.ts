import type { CreateInfrastructurePlanCommand } from '../../../../application/usecase/infrastructure-plan-management/create-infrastructure-plan/create-infrastructure-plan-command';

/**
 * InfrastructurePlanPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new
 * InfrastructurePlan. Uses primitive types and simple objects so it can be
 * serialized directly in the HTTP request.
 */
export class InfrastructurePlanPostJsonRequest {
  period: string;
  maxBudget: { amount: number; currency?: string };
  servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null;

  constructor(period: string, maxBudget: { amount: number; currency?: string }, servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null) {
    this.period = period;
    this.maxBudget = maxBudget;
    this.servicePolicies = servicePolicies ?? null;
  }

  /** Map a `CreateInfrastructurePlanCommand` into a plain JSON request body. */
  public static toRequest(data: CreateInfrastructurePlanCommand): InfrastructurePlanPostJsonRequest {
    const period = data.period.getValue();
    const maxBudget = { amount: data.maxBudget.getAmount(), currency: data.maxBudget.getCurrency().getCode() };
    const servicePolicies = data.servicePolicies ? { maxServiceDistance: data.servicePolicies.maxServiceDistance ?? null, maxServiceTime: data.servicePolicies.maxServiceTime ?? null, maxInfrastructureCount: data.servicePolicies.maxInfrastructureCount ?? null, maxEmissions: data.servicePolicies.maxEmissions ?? null } : null;

    return new InfrastructurePlanPostJsonRequest(period, maxBudget, servicePolicies);
  }
}
