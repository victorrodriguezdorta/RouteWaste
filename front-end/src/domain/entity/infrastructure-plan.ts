import { DailyPlan } from '@/domain/entity/daily-plan';
import { Facility } from '@/domain/entity/facility';
import { ServiceAssignment } from '@/domain/entity/service-assignment';
import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { TotalCost } from '@/domain/valueobject/cost/total-cost';
import { Distance } from '@/domain/valueobject/location/distance';
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
  /** Daily plans generated for this infrastructure plan. */
  private dailyPlans: DailyPlan[];
  /** Total weight collected across all daily plans. */
  private totalCollectedKilograms: CollectedWeightKilograms;
  /** Total volume collected across all daily plans. */
  private totalCollectedLiters: CollectedVolumeLiters;
  /** Total distance traveled across all daily plans. */
  private totalDistanceMeters: Distance;
  /** Number of days in the planning horizon. */
  private numberOfDays?: number | null;
  /** Average pickup time in minutes. */
  private averagePickupTimeMinutes?: number | null;
  /** Timestamp when the algorithm execution was performed (ISO 8601 format). */
  private executedAt?: string | null;

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
    this.dailyPlans = [];
    this.estimatedTotalCost = new TotalCost(0);
    this.totalCollectedKilograms = CollectedWeightKilograms.fromKilograms(0);
    this.totalCollectedLiters = CollectedVolumeLiters.fromLiters(0);
    this.totalDistanceMeters = Distance.fromMeters(0);
  }

  /**
   * Restore constructor for creating an InfrastructurePlan from persistence.
   * @param id the plan identifier
   * @param period planning period value object
   * @param maxBudget maximum budget value object
   * @param servicePolicies optional service policy configuration
   * @param dailyPlans optional list of daily plans
   * @param selectedFacilities optional list of selected facilities
   * @param serviceAssignments optional list of service assignments
   * @param estimatedTotalCost optional estimated total cost
   * @param totalCollectedKilograms optional total collected kilograms
   * @param totalCollectedLiters optional total collected liters
   * @param totalDistanceMeters optional total distance meters
   * @param numberOfDays optional number of days
   * @param averagePickupTimeMinutes optional average pickup time in minutes
   * @param executedAt optional execution timestamp
   * @returns A new InfrastructurePlan instance with all attributes set
   */
  static restore(
    id: UllUUID,
    period: PlanningPeriod,
    maxBudget: MaximumBudget,
    servicePolicies?: ServicePolicies | null,
    dailyPlans?: DailyPlan[],
    selectedFacilities?: Facility[],
    serviceAssignments?: ServiceAssignment[],
    estimatedTotalCost?: TotalCost,
    totalCollectedKilograms?: CollectedWeightKilograms,
    totalCollectedLiters?: CollectedVolumeLiters,
    totalDistanceMeters?: Distance,
    numberOfDays?: number,
    averagePickupTimeMinutes?: number,
    executedAt?: string
  ): InfrastructurePlan {
    const plan = new InfrastructurePlan(period, maxBudget, servicePolicies, id);
    plan.dailyPlans = dailyPlans ? [...dailyPlans] : [];
    plan.selectedFacilities = selectedFacilities ? [...selectedFacilities] : [];
    plan.serviceAssignments = serviceAssignments ? [...serviceAssignments] : [];
    if (estimatedTotalCost) plan.estimatedTotalCost = estimatedTotalCost;
    if (totalCollectedKilograms) plan.totalCollectedKilograms = totalCollectedKilograms;
    if (totalCollectedLiters) plan.totalCollectedLiters = totalCollectedLiters;
    if (totalDistanceMeters) plan.totalDistanceMeters = totalDistanceMeters;
    plan.numberOfDays = numberOfDays ?? null;
    plan.averagePickupTimeMinutes = averagePickupTimeMinutes ?? null;
    plan.executedAt = executedAt ?? null;
    return plan;
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
   * Aggregates daily waste demand from all assigned containers and assigns to facility.
   * Recalculates costs after adding.
   * @param assignment the service assignment to add
   * @throws Error when assignment is null
   */
  addServiceAssignment(assignment: ServiceAssignment): void {
    if (!assignment) throw new Error('Invalid assignment');
    
    // Sum daily waste demand from all assigned containers
    const containers = assignment.getAssignedContainers();
    let totalDemand = containers[0]?.getDailyDemandLitersPerDay();
    for (let i = 1; i < containers.length; i++) {
      const containerDemand = containers[i].getDailyDemandLitersPerDay();
      totalDemand = totalDemand.add(containerDemand);
    }
    
    // Assign aggregated demand to facility
    assignment.getFacility().assignWasteDemand(totalDemand);
    this.serviceAssignments.push(assignment);
    this.recalculateTotalCost();
  }

  /** Recalculate estimated total cost and validate against budget. */
  recalculateTotalCost(): void {
    let total = 0.0;
    // Sum opening fixed costs from all selected facilities
    for (const facility of this.selectedFacilities) {
      total += facility.getOpeningFixedCost().getAmount();
    }
    // TODO: Add transport cost calculation per assignment (requires transport cost VO)
    // for (const assignment of this.serviceAssignments) {
    //   total += assignment.calculateTransportCost().getAmount();
    // }
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
      const facility = assignment.getFacility();
      if (facility.getStatus() === undefined) return false;
      const totalDemand = facility.getAssignedWasteDemand();
      if (totalDemand.greaterThan(facility.getCapacity())) return false;
    }
    return true;
  }

  /**
   * Add a daily plan to this infrastructure plan.
   * @param dailyPlan the daily plan to add
   * @throws Error when dailyPlan is null
   */
  addDailyPlan(dailyPlan: DailyPlan | undefined): void {
    if (!dailyPlan) throw new Error('Daily plan is invalid');
    this.dailyPlans.push(dailyPlan);
  }

  /**
   * Clear all daily plans from this infrastructure plan.
   */
  clearDailyPlans(): void {
    this.dailyPlans = [];
  }

  /**
   * Get the daily plans associated with this infrastructure plan.
   * @returns a readonly copy of the daily plans
   */
  getDailyPlans(): ReadonlyArray<DailyPlan> {
    return this.dailyPlans.slice();
  }

  /**
   * Get the daily plan identifiers as UUIDs.
   * @returns an array of daily plan identifiers
   */
  getDailyPlanIds(): UllUUID[] {
    return this.dailyPlans.map(dp => dp.getId());
  }

  /**
   * Get the total collected kilograms.
   * @returns the total collected weight
   */
  getTotalCollectedKilograms(): CollectedWeightKilograms {
    return this.totalCollectedKilograms;
  }

  /**
   * Get the total collected liters.
   * @returns the total collected volume
   */
  getTotalCollectedLiters(): CollectedVolumeLiters {
    return this.totalCollectedLiters;
  }

  /**
   * Get the total distance traveled.
   * @returns the total distance in meters
   */
  getTotalDistanceMeters(): Distance {
    return this.totalDistanceMeters;
  }

  /**
   * Get the number of days in the planning horizon.
   * @returns the number of days or null if not set
   */
  getNumberOfDays(): number | null {
    return this.numberOfDays ?? null;
  }

  /**
   * Get the average pickup time in minutes.
   * @returns the average pickup time or null if not set
   */
  getAveragePickupTimeMinutes(): number | null {
    return this.averagePickupTimeMinutes ?? null;
  }

  /**
   * Get the execution timestamp (ISO 8601 format).
   * @returns the execution timestamp or null if not set
   */
  getExecutedAt(): string | null {
    return this.executedAt ?? null;
  }

  /**
   * Update algorithm metrics based on daily plans execution.
   * @param kilograms total collected weight
   * @param liters total collected volume
   * @param distance total distance traveled
   */
  updateAlgorithmMetrics(
    kilograms: CollectedWeightKilograms,
    liters: CollectedVolumeLiters,
    distance: Distance
  ): void {
    if (kilograms) this.totalCollectedKilograms = kilograms;
    if (liters) this.totalCollectedLiters = liters;
    if (distance) this.totalDistanceMeters = distance;
  }

  /**
   * Update the algorithm execution metadata.
   * @param numberOfDays the number of days
   * @param averagePickupTimeMinutes the average pickup time in minutes
   * @param executedAt the execution timestamp (ISO 8601)
   */
  updateExecutionMetadata(numberOfDays?: number, averagePickupTimeMinutes?: number, executedAt?: string): void {
    if (numberOfDays !== undefined) this.numberOfDays = numberOfDays;
    if (averagePickupTimeMinutes !== undefined) this.averagePickupTimeMinutes = averagePickupTimeMinutes;
    if (executedAt !== undefined) this.executedAt = executedAt;
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
