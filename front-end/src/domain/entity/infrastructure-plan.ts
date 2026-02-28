import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { MaximumBudget } from '../valueobject/cost/maximum-budget';
import { TotalCost } from '../valueobject/cost/total-cost';
import { ServicePolicies } from '../valueobject/policy/service-policies';
import { PlanningPeriod } from '../valueobject/time/planning-period';
import { Facility } from './facility';
import { ServiceAssignment } from './service-assignment';

/**
 * InfrastructurePlan
 *
 * Represents a complete infrastructure planning decision for a planning horizon.
 *
 * Holds selected facilities, service assignments, budgets and
 * service policies. Provides operations to modify the plan and to
 * re-evaluate estimated costs and validity.
 */
export class InfrastructurePlan {
  readonly id: UllUUID;
  private period: PlanningPeriod;
  private selectedFacilities: Facility[];
  private serviceAssignments: ServiceAssignment[];
  private servicePolicies?: ServicePolicies | null;
  private maxBudget: MaximumBudget;
  private estimatedTotalCost: TotalCost;

  /**
   * Create a new planning decision.
   * @param period planning period value object
   * @param maxBudget maximum budget value object
   * @param servicePolicies optional service policy configuration
   * @param id optional explicit id (generated when omitted)
   * @throws Error when required parameters are missing
   */
  constructor(period: PlanningPeriod, maxBudget: MaximumBudget, servicePolicies?: ServicePolicies | null, id?: UllUUID) {
    if (!period) throw new Error('Planning period is not defined');
    if (!maxBudget) throw new Error('Maximum budget is not defined');
    this.id = id ?? UllUUID.random();
    this.period = period;
    this.maxBudget = maxBudget;
    this.servicePolicies = servicePolicies ?? null;
    this.selectedFacilities = [];
    this.serviceAssignments = [];
    this.estimatedTotalCost = new TotalCost(0);
  }

  /** Return the plan identifier. */
  getId(): UllUUID { return this.id; }

  /** Return the planning period. */
  getPeriod(): PlanningPeriod { return this.period; }

  /** Return a copy of the selected facilities array. */
  getSelectedFacilities(): ReadonlyArray<Facility> { return this.selectedFacilities.slice(); }

  /** Return the array of service assignments. */
  getServiceAssignments(): ReadonlyArray<ServiceAssignment> { return this.serviceAssignments.slice(); }

  /** Return the maximum budget value object. */
  getMaxBudget(): MaximumBudget { return this.maxBudget; }

  /** Return the estimated total cost value object. */
  getEstimatedTotalCost(): TotalCost { return this.estimatedTotalCost; }

  /** Return optional service policies or null. */
  getServicePolicies(): ServicePolicies | null { return this.servicePolicies ?? null; }

  /** Add a facility to the plan if not already present. */
  addFacility(facility: Facility): void {
    if (facility && !this.selectedFacilities.some(f => f.equals(facility))) {
      this.selectedFacilities.push(facility);
    }
  }

  /**
   * Add a service assignment to the plan.
   * Recalculates costs after adding.
   * @throws Error when assignment is null
   */
  addServiceAssignment(assignment: ServiceAssignment): void {
    if (!assignment) throw new Error('Invalid assignment');
    assignment.facility.assignWasteDemand(assignment.container.getWasteDemand());
    this.serviceAssignments.push(assignment);
    this.recalculateTotalCost();
  }

  /** Recalculate estimated total cost and validate against budget. */
  recalculateTotalCost(): void {
    let total = 0.0;
    for (const facility of this.selectedFacilities) {
      total += facility.getOpeningFixedCost().getAmount();
    }
    for (const assignment of this.serviceAssignments) {
      total += assignment.transportCost.getAmount();
    }
    const newCost = new TotalCost(total);
    if (newCost.getAmount() > this.maxBudget.getAmount()) {
      throw new Error('Total cost exceeds maximum budget');
    }
    this.estimatedTotalCost = newCost;
  }

  /** Validate the plan: capacities and statuses of assigned facilities. */
  isPlanValid(): boolean {
    for (const assignment of this.serviceAssignments) {
      const facility = assignment.facility;
      if (facility.getStatus() === undefined) return false;
      const totalDemand = facility.getAssignedWasteDemand();
      if (totalDemand.greaterThan(facility.getCapacity())) return false;
    }
    return true;
  }

  /** Update the planning period. */
  updatePeriod(newPeriod: PlanningPeriod): void { if (!newPeriod) throw new Error('Planning period is not defined'); this.period = newPeriod; }

  /** Update maximum budget and validate current estimated cost. */
  updateMaxBudget(newMaxBudget: MaximumBudget): void { if (!newMaxBudget) throw new Error('Maximum budget is not defined'); this.maxBudget = newMaxBudget; if (this.estimatedTotalCost.getAmount() > this.maxBudget.getAmount()) throw new Error('Total cost exceeds maximum budget'); }

  /** Replace service policies (nullable). */
  updateServicePolicies(newServicePolicies?: ServicePolicies | null): void { this.servicePolicies = newServicePolicies ?? null; }

  /** Equality by id. */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof InfrastructurePlan)) return false; return this.id.equals(other.id); }

  /** Human-readable representation for debugging. */
  toString(): string { return `InfrastructurePlan={id=${this.id}, period=${this.period}, facilities=${this.selectedFacilities}, assignments=${this.serviceAssignments}, totalCost=${this.estimatedTotalCost}}`; }
}
