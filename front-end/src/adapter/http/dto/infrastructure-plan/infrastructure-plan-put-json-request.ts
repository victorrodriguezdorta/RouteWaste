import type { UpdateInfrastructurePlanCommand } from '@/application/usecase/infrastructure-plan-management/update-infrastructure-plan/update-infrastructure-plan-command';

/**
 * InfrastructurePlanPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating an existing
 * InfrastructurePlan. Only updated fields are included.
 */
export class InfrastructurePlanPutJsonRequest {
  /**
   * Periodo de planificación de la infraestructura.
   */
  period?: string;
  /**
   * Presupuesto máximo para la infraestructura.
   */
  maxBudget?: { amount: number; currency?: string } | null;
  /**
   * Políticas de servicio aplicadas a la infraestructura.
   */
  servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null;

  constructor(period?: string, maxBudget?: { amount: number; currency?: string } | null, servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null) {
    this.period = period;
    this.maxBudget = maxBudget ?? null;
    this.servicePolicies = servicePolicies ?? null;
  }

  /**
   * Mapea un `UpdateInfrastructurePlanCommand` a un cuerpo de solicitud JSON parcial.
   * @param data Comando con los campos actualizados de la infraestructura.
   * @returns Instancia de InfrastructurePlanPutJsonRequest con los campos actualizados.
   */
  public static toRequest(data: UpdateInfrastructurePlanCommand): InfrastructurePlanPutJsonRequest {
    const updated = data.updatedFields;
    const period = updated.period ? updated.period.getValue() : undefined;
    const maxBudget = updated.maxBudget ? { amount: updated.maxBudget.getAmount(), currency: updated.maxBudget.getCurrency().getCode() } : undefined;
    const servicePolicies = updated.servicePolicies ? { maxServiceDistance: updated.servicePolicies.maxServiceDistance ?? null, maxServiceTime: updated.servicePolicies.maxServiceTime ?? null, maxInfrastructureCount: updated.servicePolicies.maxInfrastructureCount ?? null, maxEmissions: updated.servicePolicies.maxEmissions ?? null } : undefined;

    return new InfrastructurePlanPutJsonRequest(period, maxBudget, servicePolicies);
  }
}
