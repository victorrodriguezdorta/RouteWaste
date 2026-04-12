import { Facility } from '@/domain/entity/facility';
import { ServiceAssignment } from '@/domain/entity/service-assignment';
import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { TotalCost } from '@/domain/valueobject/cost/total-cost';
import { ServicePolicies } from '@/domain/valueobject/policy/service-policies';
import { PlanningPeriod } from '@/domain/valueobject/time/planning-period';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

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
  /** The unique identifier for this infrastructure plan. */
  readonly id: UllUUID;
  /** The planning period for this infrastructure plan. */
  private period: PlanningPeriod;
  /** The facilities selected for this infrastructure plan. */
  private selectedFacilities: Facility[];
  /** The service assignments configured for this plan. */
  private serviceAssignments: ServiceAssignment[];
  /** Optional service policy configuration for this plan. */
  private servicePolicies?: ServicePolicies | null;
  /** The maximum budget allocated for this infrastructure plan. */
  private maxBudget: MaximumBudget;
  /** The estimated total cost of this infrastructure plan. */
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

  /**
   * Return the plan identifier.
   * @returns the unique identifier for this plan
   */
  getId(): UllUUID { return this.id; }

  /**
   * Return the planning period.
   * @returns the planning period value object
   */
  getPeriod(): PlanningPeriod { return this.period; }

  /**
   * Return a copy of the selected facilities array.
   * @returns a readonly copy of the selected facilities
   */
  getSelectedFacilities(): ReadonlyArray<Facility> { return this.selectedFacilities.slice(); }

  /**
   * Return the array of service assignments.
   * @returns a readonly copy of the service assignments
   */
  getServiceAssignments(): ReadonlyArray<ServiceAssignment> { return this.serviceAssignments.slice(); }

  /**
   * Return the maximum budget value object.
   * @returns the maximum budget value object
   */
  getMaxBudget(): MaximumBudget { return this.maxBudget; }

  /**
   * Return the estimated total cost value object.
   * @returns the estimated total cost value object
   */
  getEstimatedTotalCost(): TotalCost { return this.estimatedTotalCost; }

  /**
   * Return optional service policies or null.
   * @returns the service policies or null if not configured
   */
  getServicePolicies(): ServicePolicies | null { return this.servicePolicies ?? null; }

  /**
   * Add a facility to the plan if not already present.
   * @param facility the facility to add
   */
  addFacility(facility: Facility): void {
    if (facility && !this.selectedFacilities.some(f => f.equals(facility))) {
      this.selectedFacilities.push(facility);
    }
  }

  /**
   * Add a service assignment to the plan.
   * Recalculates costs after adding.
   * @param assignment the service assignment to add
   * @throws Error when assignment is null
   */
  addServiceAssignment(assignment: ServiceAssignment): void {
    if (!assignment) throw new Error('Invalid assignment');
    assignment.facility.assignWasteDemand(assignment.wasteDemand);
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

  /**
   * Validate the plan: capacities and statuses of assigned facilities.
   * @returns true if the plan is valid, false otherwise
   */
  isPlanValid(): boolean {
    for (const assignment of this.serviceAssignments) {
      const facility = assignment.facility;
      if (facility.getStatus() === undefined) return false;
      const totalDemand = facility.getAssignedWasteDemand();
      if (totalDemand.greaterThan(facility.getCapacity())) return false;
    }
    return true;
  }

  /**
   * Update the planning period.
   * @param newPeriod the new planning period
   * @throws Error when newPeriod is not defined
   */
  updatePeriod(newPeriod: PlanningPeriod): void { if (!newPeriod) throw new Error('Planning period is not defined'); this.period = newPeriod; }

  /**
   * Update maximum budget and validate current estimated cost.
   * @param newMaxBudget the new maximum budget
   * @throws Error when newMaxBudget is not defined or when cost exceeds budget
   */
  updateMaxBudget(newMaxBudget: MaximumBudget): void { if (!newMaxBudget) throw new Error('Maximum budget is not defined'); this.maxBudget = newMaxBudget; if (this.estimatedTotalCost.getAmount() > this.maxBudget.getAmount()) throw new Error('Total cost exceeds maximum budget'); }

  /**
   * Replace service policies (nullable).
   * @param newServicePolicies the new service policies or null
   */
  updateServicePolicies(newServicePolicies?: ServicePolicies | null): void { this.servicePolicies = newServicePolicies ?? null; }

  /**
   * Equality by id.
   * @param other the object to compare
   * @returns true if the objects are equal by id, false otherwise
   */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof InfrastructurePlan)) return false; return this.id.equals(other.id); }

  /**
   * Human-readable representation for debugging.
   * @returns a string representation of this plan
   */
  toString(): string { return `InfrastructurePlan={id=${this.id}, period=${this.period}, facilities=${this.selectedFacilities}, assignments=${this.serviceAssignments}, totalCost=${this.estimatedTotalCost}}`; }
}
