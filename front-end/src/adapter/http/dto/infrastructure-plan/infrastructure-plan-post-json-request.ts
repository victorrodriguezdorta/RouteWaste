import type { CreateInfrastructurePlanCommand } from '@/application/usecase/infrastructure-plan-management/create-infrastructure-plan/create-infrastructure-plan-command';

/**
 * InfrastructurePlanPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new
 * InfrastructurePlan. Uses primitive types and simple objects so it can be
 * serialized directly in the HTTP request.
 */

export class InfrastructurePlanPostJsonRequest {
  /**
   * Periodo de planificación en formato string (ejemplo: '2026' o '2026-Q1').
   */
  period: string;

  /**
   * Presupuesto máximo, incluyendo cantidad y moneda.
   */
  maxBudget: { amount: number; currency?: string };

  /**
   * Políticas de servicio opcionales para el plan de infraestructura.
   */
  servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null;

  constructor(period: string, maxBudget: { amount: number; currency?: string }, servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null) {
    this.period = period;
    this.maxBudget = maxBudget;
    this.servicePolicies = servicePolicies ?? null;
  }

  /**
   * Mapea un `CreateInfrastructurePlanCommand` a un cuerpo de solicitud JSON plano.
   * @param data Comando con los datos para crear el plan de infraestructura.
   * @returns Una instancia de InfrastructurePlanPostJsonRequest lista para ser serializada.
   */
  public static toRequest(data: CreateInfrastructurePlanCommand): InfrastructurePlanPostJsonRequest {
    const period = data.period.getValue();
    const maxBudget = { amount: data.maxBudget.getAmount(), currency: data.maxBudget.getCurrency().getCode() };
    const servicePolicies = data.servicePolicies ? { maxServiceDistance: data.servicePolicies.maxServiceDistance ?? null, maxServiceTime: data.servicePolicies.maxServiceTime ?? null, maxInfrastructureCount: data.servicePolicies.maxInfrastructureCount ?? null, maxEmissions: data.servicePolicies.maxEmissions ?? null } : null;

    return new InfrastructurePlanPostJsonRequest(period, maxBudget, servicePolicies);
  }
}
