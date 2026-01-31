import { generateUniqueId } from './utils';
import { PlanningPeriod } from '../valueobject/time/planning-period';
import { MaximumBudget } from '../valueobject/cost/maximum-budget';
import { ServicePolicies } from '../valueobject/policy/service-policies';
import { Facility } from './facility';
import { Container } from './container';
import { TotalCost } from '../valueobject/cost/total-cost';

/**
 * InfrastructurePlan
 *
 * Represents a complete infrastructure planning decision for a planning horizon.
 *
 * Holds selected facilities, container->facility assignments, budgets and
 * service policies. Provides operations to modify the plan and to
 * re-evaluate estimated costs and validity.
 */
export class InfrastructurePlan {
  readonly id: string;
  private period: PlanningPeriod;
  private selectedFacilities: Facility[];
  private serviceAssignments: Map<Container, Facility>;
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
  constructor(period: PlanningPeriod, maxBudget: MaximumBudget, servicePolicies?: ServicePolicies | null, id?: string) {
    if (!period) throw new Error('Planning period is not defined');
    if (!maxBudget) throw new Error('Maximum budget is not defined');
    this.id = id ?? generateUniqueId();
    this.period = period;
    this.maxBudget = maxBudget;
    this.servicePolicies = servicePolicies ?? null;
    this.selectedFacilities = [];
    this.serviceAssignments = new Map();
    this.estimatedTotalCost = new TotalCost(0);
  }

  /** Return the plan identifier. */
  getId(): string { return this.id; }

  /** Return the planning period. */
  getPeriod(): PlanningPeriod { return this.period; }

  /** Return a copy of the selected facilities array. */
  getSelectedFacilities(): ReadonlyArray<Facility> { return this.selectedFacilities.slice(); }

  /** Return the map of service assignments. */
  getServiceAssignments(): ReadonlyMap<Container, Facility> { return this.serviceAssignments; }

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
   * Create an assignment between a container and a facility and
   * recalculate costs. Throws on invalid input or when facility
   * capacity/status prevents the assignment.
   */
  assignContainerToFacility(container: Container, facility: Facility): void {
    if (!container || !facility) throw new Error('Invalid assignment');
    // Check facility capacity and status
    facility.assignWasteDemand(container.getWasteDemand());
    this.serviceAssignments.set(container, facility);
    // Recalculate costs
    this.recalculateTotalCost();
  }

  /** Recalculate estimated total cost and validate against budget. */
  recalculateTotalCost(): void {
    let total = 0.0;
    for (const facility of this.selectedFacilities) {
      total += facility.getOpeningFixedCost().getAmount();
    }
    for (const facility of this.serviceAssignments.values()) {
      total += facility.getOpeningFixedCost().getAmount();
    }
    const newCost = new TotalCost(total);
    // Compare numeric amounts against maxBudget
    if (newCost.getAmount() > this.maxBudget.getAmount()) {
      throw new Error('Total cost exceeds maximum budget');
    }
    this.estimatedTotalCost = newCost;
  }

  /** Validate the plan: capacities and statuses of assigned facilities. */
  isPlanValid(): boolean {
    for (const facility of this.serviceAssignments.values()) {
      if (facility.getStatus() === undefined) return false;
      // check discarded
      // facility status helper exists in enum if needed elsewhere
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
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof InfrastructurePlan)) return false; return this.id === other.id; }

  /** Human-readable representation for debugging. */
  toString(): string { return `InfrastructurePlan={id=${this.id}, period=${this.period}, facilities=${this.selectedFacilities}, assignments=${Array.from(this.serviceAssignments.keys())}, totalCost=${this.estimatedTotalCost}}`; }
}
