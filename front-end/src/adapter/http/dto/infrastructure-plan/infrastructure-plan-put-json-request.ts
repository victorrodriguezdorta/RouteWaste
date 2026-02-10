import type { UpdateInfrastructurePlanCommand } from '../../../../application/usecase/infrastructure-plan-management/update-infrastructure-plan/update-infrastructure-plan-command';

/**
 * InfrastructurePlanPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating an existing
 * InfrastructurePlan. Only updated fields are included.
 */
export class InfrastructurePlanPutJsonRequest {
  period?: string;
  maxBudget?: { amount: number; currency?: string } | null;
  servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null;

  constructor(period?: string, maxBudget?: { amount: number; currency?: string } | null, servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null) {
    this.period = period;
    this.maxBudget = maxBudget ?? null;
    this.servicePolicies = servicePolicies ?? null;
  }

  /** Map an `UpdateInfrastructurePlanCommand` into a partial JSON request body. */
  public static toRequest(data: UpdateInfrastructurePlanCommand): InfrastructurePlanPutJsonRequest {
    const updated = data.updatedFields;
    const period = updated.period ? updated.period.getValue() : undefined;
    const maxBudget = updated.maxBudget ? { amount: updated.maxBudget.getAmount(), currency: updated.maxBudget.getCurrency().getCode() } : undefined;
    const servicePolicies = updated.servicePolicies ? { maxServiceDistance: updated.servicePolicies.maxServiceDistance ?? null, maxServiceTime: updated.servicePolicies.maxServiceTime ?? null, maxInfrastructureCount: updated.servicePolicies.maxInfrastructureCount ?? null, maxEmissions: updated.servicePolicies.maxEmissions ?? null } : undefined;

    return new InfrastructurePlanPutJsonRequest(period, maxBudget, servicePolicies);
  }
}
