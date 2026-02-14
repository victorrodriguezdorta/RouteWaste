import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { InfrastructurePlan } from '../../../../domain/entity/infrastructure-plan';
import { Currency } from '../../../../domain/valueobject/cost/currency';
import { MaximumBudget } from '../../../../domain/valueobject/cost/maximum-budget';
import { ServicePolicies } from '../../../../domain/valueobject/policy/service-policies';
import { PlanningPeriod } from '../../../../domain/valueobject/time/planning-period';

/**
 * InfrastructurePlanEdit
 * 
 * Data Transfer Object for editing an existing InfrastructurePlan entity.
 * 
 * This DTO is designed to be used in Vue.js forms for updating infrastructure plans.
 * It extends the functionality of InfrastructurePlanAdd by including the entity ID
 * and a method to create a DTO from an existing InfrastructurePlan entity.
 * 
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class InfrastructurePlanEdit {
  /**
   * Unique identifier for the infrastructure plan.
   */
  public id: string;

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
   * Create a new InfrastructurePlanEdit DTO.
   * 
   * @param id Unique identifier for the infrastructure plan
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
    id: string,
    period: string,
    maxBudget: number,
    currencyCode: string,
    maxServiceDistance?: number,
    maxServiceTime?: number,
    maxInfrastructureCount?: number,
    maxEmissions?: number
  ) {
    this.validate<string>(id, 'Infrastructure plan ID is not defined');
    this.validate<string>(period, 'Planning period is not defined');
    this.validate<number>(maxBudget, 'Maximum budget is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');

    this.id = id;
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
   * Validate infrastructure plan ID for form fields.
   * 
   * @param value ID string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateId(value: string): boolean | string {
    try {
      new UllUUID(value);
      return true;
    } catch (error: any) {
      return error.message;
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
   * Generate a random InfrastructurePlanEdit instance for testing purposes.
   * 
   * @returns A new InfrastructurePlanEdit instance with random valid values
   */
  static random(): InfrastructurePlanEdit {
    const randomId = UllUUID.random().getValue();
    const currentYear = new Date().getFullYear();
    const quarter = Math.floor(Math.random() * 4) + 1;
    const randomPeriod = Math.random() > 0.5 ? `${currentYear}` : `${currentYear}-Q${quarter}`;
    const randomMaxBudget = parseFloat((Math.random() * 1000000 + 100000).toFixed(2));
    const randomCurrency = 'EUR';
    const randomMaxServiceDistance = Math.random() > 0.3 ? Math.floor(Math.random() * 50000) + 5000 : undefined;
    const randomMaxServiceTime = Math.random() > 0.3 ? Math.floor(Math.random() * 120) + 10 : undefined;
    const randomMaxInfrastructureCount = Math.random() > 0.3 ? Math.floor(Math.random() * 50) + 5 : undefined;
    const randomMaxEmissions = Math.random() > 0.3 ? parseFloat((Math.random() * 10000).toFixed(2)) : undefined;

    return new InfrastructurePlanEdit(
      randomId,
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
   * @param infrastructurePlanEdit InfrastructurePlanEdit DTO instance
   * @returns A new InfrastructurePlan domain entity with the specified ID
   * @throws Error if any value object validation fails
   */
  static toInfrastructurePlan(infrastructurePlanEdit: InfrastructurePlanEdit): InfrastructurePlan {
    const id = new UllUUID(infrastructurePlanEdit.id);
    const period = new PlanningPeriod(infrastructurePlanEdit.period);
    const maxBudget = new MaximumBudget(infrastructurePlanEdit.maxBudget, infrastructurePlanEdit.currencyCode);
    
    // Create service policies if any policy value is defined
    let servicePolicies: ServicePolicies | null = null;
    if (
      infrastructurePlanEdit.maxServiceDistance != null ||
      infrastructurePlanEdit.maxServiceTime != null ||
      infrastructurePlanEdit.maxInfrastructureCount != null ||
      infrastructurePlanEdit.maxEmissions != null
    ) {
      servicePolicies = new ServicePolicies(
        infrastructurePlanEdit.maxServiceDistance,
        infrastructurePlanEdit.maxServiceTime,
        infrastructurePlanEdit.maxInfrastructureCount,
        infrastructurePlanEdit.maxEmissions
      );
    }

    return new InfrastructurePlan(period, maxBudget, servicePolicies, id);
  }

  /**
   * Create an InfrastructurePlanEdit DTO from an InfrastructurePlan domain entity.
   * 
   * @param infrastructurePlan InfrastructurePlan domain entity
   * @returns A new InfrastructurePlanEdit DTO with values extracted from the entity
   */
  static fromInfrastructurePlan(infrastructurePlan: InfrastructurePlan): InfrastructurePlanEdit {
    const servicePolicies = infrastructurePlan.getServicePolicies();

    return new InfrastructurePlanEdit(
      infrastructurePlan.getId().getValue(),
      infrastructurePlan.getPeriod().getValue(),
      infrastructurePlan.getMaxBudget().getAmount(),
      infrastructurePlan.getMaxBudget().getCurrency().getCode(),
      servicePolicies?.maxServiceDistance ?? undefined,
      servicePolicies?.maxServiceTime ?? undefined,
      servicePolicies?.maxInfrastructureCount ?? undefined,
      servicePolicies?.maxEmissions ?? undefined
    );
  }
}
