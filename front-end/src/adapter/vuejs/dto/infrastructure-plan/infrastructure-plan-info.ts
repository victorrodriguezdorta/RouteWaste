import { InfrastructurePlan } from '@/domain/entity/infrastructure-plan';

/**
 * InfrastructurePlanInfo
 * 
 * Data Transfer Object for displaying InfrastructurePlan entity information.
 * 
 * This DTO is designed to be used in Vue.js views for displaying infrastructure plan details.
 * It includes all plan configuration data plus calculated metrics for facilities,
 * service assignments, and budget utilization.
 * 
 * This DTO is read-only and focused on presentation, providing formatted strings
 * and utility methods for the UI layer.
 * 
 * All attributes are public to allow direct binding with Vue.js display components.
 */
export class InfrastructurePlanInfo {
  /**
   * Unique identifier for the infrastructure plan.
   */
  public id: string;

  /**
   * Planning period identifier (format: YYYY or YYYY-Q[1-4]).
   */
  public period: string;

  /**
   * Maximum budget amount.
   */
  public maxBudget: number;

  /**
   * ISO 4217 currency code for budget and costs.
   */
  public currencyCode: string;

  /**
   * Maximum service distance in meters. Optional policy constraint.
   */
  public maxServiceDistance?: number;

  /**
   * Maximum service time in minutes. Optional policy constraint.
   */
  public maxServiceTime?: number;

  /**
   * Maximum infrastructure count. Optional policy constraint.
   */
  public maxInfrastructureCount?: number;

  /**
   * Maximum emissions allowed. Optional policy constraint.
   */
  public maxEmissions?: number;

  /**
   * Array of facility IDs selected in this plan.
   */
  public facilityIds: string[];

  /**
   * Array of service assignments (container -> facility mappings).
   */
  public serviceAssignments: Array<{ containerId: string; facilityId: string }>;

  /**
   * Estimated total cost amount for this plan.
   */
  public estimatedTotalCostAmount: number;

  /**
   * Currency code for the estimated total cost.
   */
  public estimatedTotalCostCurrency: string;

  /**
   * Create a new InfrastructurePlanInfo DTO.
   * 
   * @param id Unique identifier for the infrastructure plan
   * @param period Planning period identifier
   * @param maxBudget Maximum budget amount
   * @param currencyCode ISO 4217 currency code
   * @param maxServiceDistance Optional maximum service distance
   * @param maxServiceTime Optional maximum service time
   * @param maxInfrastructureCount Optional maximum infrastructure count
   * @param maxEmissions Optional maximum emissions
   * @param facilityIds Array of facility IDs
   * @param serviceAssignments Array of service assignment objects
   * @param estimatedTotalCostAmount Estimated total cost amount
   * @param estimatedTotalCostCurrency Currency code for estimated cost
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    id: string,
    period: string,
    maxBudget: number,
    currencyCode: string,
    maxServiceDistance: number | undefined,
    maxServiceTime: number | undefined,
    maxInfrastructureCount: number | undefined,
    maxEmissions: number | undefined,
    facilityIds: string[],
    serviceAssignments: Array<{ containerId: string; facilityId: string }>,
    estimatedTotalCostAmount: number,
    estimatedTotalCostCurrency: string
  ) {
    this.validate<string>(id, 'Infrastructure plan ID is not defined');
    this.validate<string>(period, 'Planning period is not defined');
    this.validate<number>(maxBudget, 'Maximum budget is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');
    this.validate<string[]>(facilityIds, 'Facility IDs are not defined');
    this.validate<Array<{ containerId: string; facilityId: string }>>(
      serviceAssignments,
      'Service assignments are not defined'
    );
    this.validate<number>(estimatedTotalCostAmount, 'Estimated total cost amount is not defined');
    this.validate<string>(estimatedTotalCostCurrency, 'Estimated total cost currency is not defined');

    this.id = id;
    this.period = period;
    this.maxBudget = maxBudget;
    this.currencyCode = currencyCode;
    this.maxServiceDistance = maxServiceDistance;
    this.maxServiceTime = maxServiceTime;
    this.maxInfrastructureCount = maxInfrastructureCount;
    this.maxEmissions = maxEmissions;
    this.facilityIds = facilityIds;
    this.serviceAssignments = serviceAssignments;
    this.estimatedTotalCostAmount = estimatedTotalCostAmount;
    this.estimatedTotalCostCurrency = estimatedTotalCostCurrency;
  }

  /**
   * Validate that an attribute is defined (not null or undefined).
   * 
   * @param attribute Attribute to validate
   * @param errorMessage Error message to throw if validation fails
   * @throws Error with the provided message if attribute is null or undefined
   */
  private validate<T>(
    attribute: T | undefined,
    errorMessage: string
  ): asserts attribute is T {
    if (attribute === undefined || attribute === null) {
      throw new Error(errorMessage);
    }
  }

  /**
   * Create an InfrastructurePlanInfo DTO from an InfrastructurePlan domain entity.
   * 
   * @param infrastructurePlan InfrastructurePlan domain entity
   * @returns A new InfrastructurePlanInfo DTO with values extracted from the entity
   */
  static fromInfrastructurePlan(infrastructurePlan: InfrastructurePlan): InfrastructurePlanInfo {
    const servicePolicies = infrastructurePlan.getServicePolicies();
    const selectedFacilities = infrastructurePlan.getSelectedFacilities();
    const assignments = infrastructurePlan.getServiceAssignments();

    // Extract facility IDs
    const facilityIds = selectedFacilities.map(facility => facility.getId().getValue());

    // Extract service assignments as simple objects
    const serviceAssignments = assignments.map(assignment => ({
      containerId: assignment.container.getId().getValue(),
      facilityId: assignment.facility.getId().getValue(),
    }));

    // Extract estimated cost data
    const estimatedCost = infrastructurePlan.getEstimatedTotalCost();

    return new InfrastructurePlanInfo(
      infrastructurePlan.getId().getValue(),
      infrastructurePlan.getPeriod().getValue(),
      infrastructurePlan.getMaxBudget().getAmount(),
      infrastructurePlan.getMaxBudget().getCurrency().getCode(),
      servicePolicies?.maxServiceDistance ?? undefined,
      servicePolicies?.maxServiceTime ?? undefined,
      servicePolicies?.maxInfrastructureCount ?? undefined,
      servicePolicies?.maxEmissions ?? undefined,
      facilityIds,
      serviceAssignments,
      estimatedCost.getAmount(),
      estimatedCost.getCurrency().getCode()
    );
  }

  /**
   * Get formatted planning period string.
   * 
   * @returns Human-readable period representation
   */
  getFormattedPeriod(): string {
    if (this.period.includes('-Q')) {
      const parts = this.period.split('-');
      const year = parts[0];
      const quarter = parts[1];
      if (quarter) {
        return `${year} Quarter ${quarter.substring(1)}`;
      }
    }
    return `Year ${this.period}`;
  }

  /**
   * Get formatted maximum budget string.
   * 
   * @returns Formatted budget with currency (e.g., "€1,000,000.00")
   */
  getFormattedMaxBudget(): string {
    return this.formatCurrency(this.maxBudget, this.currencyCode);
  }

  /**
   * Get formatted estimated total cost string.
   * 
   * @returns Formatted cost with currency
   */
  getFormattedEstimatedCost(): string {
    return this.formatCurrency(this.estimatedTotalCostAmount, this.estimatedTotalCostCurrency);
  }

  /**
   * Get formatted service policies string.
   * 
   * @returns Formatted policies or "No policies defined"
   */
  getFormattedServicePolicies(): string {
    if (!this.hasPolicies()) {
      return 'No policies defined';
    }

    const policies: string[] = [];
    if (this.maxServiceDistance != null) {
      policies.push(`Max distance: ${this.maxServiceDistance} meters`);
    }
    if (this.maxServiceTime != null) {
      policies.push(`Max time: ${this.maxServiceTime} minutes`);
    }
    if (this.maxInfrastructureCount != null) {
      policies.push(`Max infrastructure: ${this.maxInfrastructureCount}`);
    }
    if (this.maxEmissions != null) {
      policies.push(`Max emissions: ${this.maxEmissions}`);
    }

    return policies.join(', ');
  }

  /**
   * Check if estimated cost is within maximum budget.
   * 
   * @returns true if within budget, false otherwise
   */
  isWithinBudget(): boolean {
    return this.estimatedTotalCostAmount <= this.maxBudget;
  }

  /**
   * Calculate budget utilization percentage.
   * 
   * @returns Percentage of budget used (0-100+)
   */
  getBudgetUtilizationPercentage(): number {
    if (this.maxBudget === 0) return 0;
    return parseFloat(((this.estimatedTotalCostAmount / this.maxBudget) * 100).toFixed(2));
  }

  /**
   * Get the number of selected facilities in this plan.
   * 
   * @returns Count of facilities
   */
  getFacilityCount(): number {
    return this.facilityIds.length;
  }

  /**
   * Get the number of service assignments in this plan.
   * 
   * @returns Count of assignments
   */
  getAssignmentCount(): number {
    return this.serviceAssignments.length;
  }

  /**
   * Check if this plan has any service policies defined.
   * 
   * @returns true if at least one policy is defined, false otherwise
   */
  hasPolicies(): boolean {
    return (
      this.maxServiceDistance != null ||
      this.maxServiceTime != null ||
      this.maxInfrastructureCount != null ||
      this.maxEmissions != null
    );
  }

  /**
   * Get budget status as a descriptive string.
   * 
   * @returns Budget status description
   */
  getBudgetStatus(): string {
    const utilization = this.getBudgetUtilizationPercentage();
    if (utilization > 100) {
      return 'Over budget';
    } else if (utilization > 90) {
      return 'Near budget limit';
    } else if (utilization > 50) {
      return 'Within budget';
    } else {
      return 'Under budget';
    }
  }

  /**
   * Get remaining budget amount.
   * 
   * @returns Remaining budget (may be negative if over budget)
   */
  getRemainingBudget(): number {
    return parseFloat((this.maxBudget - this.estimatedTotalCostAmount).toFixed(2));
  }

  /**
   * Get formatted remaining budget string.
   * 
   * @returns Formatted remaining budget with currency
   */
  getFormattedRemainingBudget(): string {
    const remaining = this.getRemainingBudget();
    return this.formatCurrency(remaining, this.currencyCode);
  }

  /**
   * Format a currency amount with the given currency code.
   * 
   * @param amount Numeric amount to format
   * @param currencyCode ISO 4217 currency code
   * @returns Formatted currency string
   */
  private formatCurrency(amount: number, currencyCode: string): string {
    const currencySymbols: { [key: string]: string } = {
      EUR: '€',
      USD: '$',
      GBP: '£',
      JPY: '¥',
    };

    const symbol = currencySymbols[currencyCode] || currencyCode;
    const formattedAmount = amount.toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

    return `${symbol}${formattedAmount}`;
  }
}
