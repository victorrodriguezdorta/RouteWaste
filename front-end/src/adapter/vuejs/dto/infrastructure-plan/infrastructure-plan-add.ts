import { InfrastructurePlan } from '../../../../domain/entity/infrastructure-plan';
import { Currency } from '../../../../domain/valueobject/cost/currency';
import { MaximumBudget } from '../../../../domain/valueobject/cost/maximum-budget';
import { ServicePolicies } from '../../../../domain/valueobject/policy/service-policies';
import { PlanningPeriod } from '../../../../domain/valueobject/time/planning-period';

/**
 * InfrastructurePlanAdd
 * 
 * Data Transfer Object for adding a new InfrastructurePlan entity.
 * 
 * This DTO is designed to be used in Vue.js forms for creating new infrastructure plans.
 * It contains primitive types that can be easily bound to v-model directives
 * and includes validation methods for form field validation.
 * 
 * Note: This DTO focuses on the basic plan configuration. Facilities and service assignments
 * are typically managed through separate operations after plan creation.
 * 
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class InfrastructurePlanAdd {
  /**
   * Planning period identifier (format: YYYY or YYYY-Q[1-4]).
   */
  public period: string;

  /**
   * Maximum budget amount (must be >= 0).
   */
  public maxBudget: number;

  /**
   * ISO 4217 currency code for budget (e.g., 'EUR', 'USD').
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
   * Create a new InfrastructurePlanAdd DTO.
   * 
   * @param period Planning period identifier
   * @param maxBudget Maximum budget amount
   * @param currencyCode ISO 4217 currency code
   * @param maxServiceDistance Optional maximum service distance
   * @param maxServiceTime Optional maximum service time
   * @param maxInfrastructureCount Optional maximum infrastructure count
   * @param maxEmissions Optional maximum emissions
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    period: string,
    maxBudget: number,
    currencyCode: string,
    maxServiceDistance?: number,
    maxServiceTime?: number,
    maxInfrastructureCount?: number,
    maxEmissions?: number
  ) {
    this.validate<string>(period, 'Planning period is not defined');
    this.validate<number>(maxBudget, 'Maximum budget is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');

    this.period = period;
    this.maxBudget = maxBudget;
    this.currencyCode = currencyCode;
    this.maxServiceDistance = maxServiceDistance;
    this.maxServiceTime = maxServiceTime;
    this.maxInfrastructureCount = maxInfrastructureCount;
    this.maxEmissions = maxEmissions;
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
   * Validate planning period for form fields.
   * 
   * @param value Planning period string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidatePeriod(value: string): boolean | string {
    try {
      new PlanningPeriod(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate maximum budget for form fields.
   * 
   * @param value Maximum budget to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateMaxBudget(value: number): boolean | string {
    try {
      new MaximumBudget(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate currency code for form fields.
   * 
   * @param value Currency code string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCurrencyCode(value: string): boolean | string {
    try {
      new Currency(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate max service distance for form fields.
   * 
   * @param value Max service distance to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateMaxServiceDistance(value: number): boolean | string {
    try {
      if (value != null && value < 0) {
        throw new Error('Maximum service distance cannot be negative');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate max service time for form fields.
   * 
   * @param value Max service time to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateMaxServiceTime(value: number): boolean | string {
    try {
      if (value != null && value < 0) {
        throw new Error('Maximum service time cannot be negative');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate max infrastructure count for form fields.
   * 
   * @param value Max infrastructure count to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateMaxInfrastructureCount(value: number): boolean | string {
    try {
      if (value != null && value < 0) {
        throw new Error('Maximum infrastructure count cannot be negative');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate max emissions for form fields.
   * 
   * @param value Max emissions to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateMaxEmissions(value: number): boolean | string {
    try {
      if (value != null && value < 0) {
        throw new Error('Maximum emissions cannot be negative');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Generate a random InfrastructurePlanAdd instance for testing purposes.
   * 
   * @returns A new InfrastructurePlanAdd instance with random valid values
   */
  static random(): InfrastructurePlanAdd {
    const currentYear = new Date().getFullYear();
    const quarter = Math.floor(Math.random() * 4) + 1;
    const randomPeriod = Math.random() > 0.5 ? `${currentYear}` : `${currentYear}-Q${quarter}`;
    const randomMaxBudget = parseFloat((Math.random() * 1000000 + 100000).toFixed(2));
    const randomCurrency = 'EUR';
    const randomMaxServiceDistance = Math.random() > 0.3 ? Math.floor(Math.random() * 50000) + 5000 : undefined;
    const randomMaxServiceTime = Math.random() > 0.3 ? Math.floor(Math.random() * 120) + 10 : undefined;
    const randomMaxInfrastructureCount = Math.random() > 0.3 ? Math.floor(Math.random() * 50) + 5 : undefined;
    const randomMaxEmissions = Math.random() > 0.3 ? parseFloat((Math.random() * 10000).toFixed(2)) : undefined;

    return new InfrastructurePlanAdd(
      randomPeriod,
      randomMaxBudget,
      randomCurrency,
      randomMaxServiceDistance,
      randomMaxServiceTime,
      randomMaxInfrastructureCount,
      randomMaxEmissions
    );
  }

  /**
   * Convert this DTO to an InfrastructurePlan domain entity.
   * 
   * @param infrastructurePlanAdd InfrastructurePlanAdd DTO instance
   * @returns A new InfrastructurePlan domain entity
   * @throws Error if any value object validation fails
   */
  static toInfrastructurePlan(infrastructurePlanAdd: InfrastructurePlanAdd): InfrastructurePlan {
    const period = new PlanningPeriod(infrastructurePlanAdd.period);
    const maxBudget = new MaximumBudget(infrastructurePlanAdd.maxBudget, infrastructurePlanAdd.currencyCode);
    
    // Create service policies if any policy value is defined
    let servicePolicies: ServicePolicies | null = null;
    if (
      infrastructurePlanAdd.maxServiceDistance != null ||
      infrastructurePlanAdd.maxServiceTime != null ||
      infrastructurePlanAdd.maxInfrastructureCount != null ||
      infrastructurePlanAdd.maxEmissions != null
    ) {
      servicePolicies = new ServicePolicies(
        infrastructurePlanAdd.maxServiceDistance,
        infrastructurePlanAdd.maxServiceTime,
        infrastructurePlanAdd.maxInfrastructureCount,
        infrastructurePlanAdd.maxEmissions
      );
    }

    return new InfrastructurePlan(period, maxBudget, servicePolicies);
  }
}
