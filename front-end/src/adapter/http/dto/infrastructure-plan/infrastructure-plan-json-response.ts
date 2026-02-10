import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../../domain/entity/container';
import { Facility } from '../../../../domain/entity/facility';
import { InfrastructurePlan } from '../../../../domain/entity/infrastructure-plan';
import { MaximumBudget } from '../../../../domain/valueobject/cost/maximum-budget';
import { ServicePolicies } from '../../../../domain/valueobject/policy/service-policies';
import { PlanningPeriod } from '../../../../domain/valueobject/time/planning-period';
import { ContainerJsonResponse } from '../container/container-json-response';
import { FacilityJsonResponse } from '../facility/facility-json-response';

/**
 * InfrastructurePlanJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for an
 * InfrastructurePlan resource. Uses primitive fields and nested DTOs for
 * facilities and containers. Provides `toInfrastructurePlan` to convert the
 * payload into the domain `InfrastructurePlan` entity.
 */
export class InfrastructurePlanJsonResponse {
  id: string;
  period: string;
  selectedFacilities?: FacilityJsonResponse[];
  serviceAssignments?: Array<{ container: ContainerJsonResponse; facility: FacilityJsonResponse }>;
  servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null;
  maxBudget: { amount: number; currency?: string };
  estimatedTotalCost?: { amount: number; currency?: string };

  constructor(
    id: string,
    period: string,
    maxBudget: { amount: number; currency?: string },
    selectedFacilities?: FacilityJsonResponse[],
    serviceAssignments?: Array<{ container: ContainerJsonResponse; facility: FacilityJsonResponse }>,
    servicePolicies?: { maxServiceDistance?: number | null; maxServiceTime?: number | null; maxInfrastructureCount?: number | null; maxEmissions?: number | null } | null,
    estimatedTotalCost?: { amount: number; currency?: string }
  ) {
    this.id = id;
    this.period = period;
    this.selectedFacilities = selectedFacilities;
    this.serviceAssignments = serviceAssignments;
    this.servicePolicies = servicePolicies ?? null;
    this.maxBudget = maxBudget;
    this.estimatedTotalCost = estimatedTotalCost;
  }

  /**
   * Convert the JSON response into a domain `InfrastructurePlan` entity.
   * Nested facilities and containers (if present) are converted using their
   * respective DTO helpers and added to the plan. Assignments are applied
   * via `assignContainerToFacility` which will perform domain validations.
   */
  public static toInfrastructurePlan(data: InfrastructurePlanJsonResponse): InfrastructurePlan {
    const id = new UllUUID(data.id);
    const period = new PlanningPeriod(data.period);
    const maxBudget = new MaximumBudget(data.maxBudget.amount, data.maxBudget.currency);
    const servicePolicies = data.servicePolicies ? new ServicePolicies(data.servicePolicies.maxServiceDistance ?? null, data.servicePolicies.maxServiceTime ?? null, data.servicePolicies.maxInfrastructureCount ?? null, data.servicePolicies.maxEmissions ?? null) : undefined;

    const plan = new InfrastructurePlan(period, maxBudget, servicePolicies, id);

    // Add selected facilities if present
    if (data.selectedFacilities) {
      for (const f of data.selectedFacilities) {
        const facility: Facility = FacilityJsonResponse.toFacility(f);
        plan.addFacility(facility);
      }
    }

    // Apply service assignments if present
    if (data.serviceAssignments) {
      for (const a of data.serviceAssignments) {
        const container: Container = ContainerJsonResponse.toContainer(a.container);
        const facility: Facility = FacilityJsonResponse.toFacility(a.facility);
        plan.assignContainerToFacility(container, facility);
      }
    }

    // estimatedTotalCost is computed by domain; ignore provided value or leave as is
    return plan;
  }
}
