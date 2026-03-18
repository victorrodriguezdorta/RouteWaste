import { Vehicle } from '@/domain/entity/vehicle';
import { TimeUnit, timeUnitFromString } from '@/domain/enumerate/time-unit';
import { VehicleType, vehicleTypeFromString } from '@/domain/enumerate/vehicle-type';
import { Currency } from '@/domain/valueobject/cost/currency';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { Capacity } from '@/domain/valueobject/demand/capacity';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';

/**
 * VehicleAdd
 * 
 * Data Transfer Object for adding a new Vehicle entity.
 * 
 * This DTO is designed to be used in Vue.js forms for creating new vehicles.
 * It contains primitive types that can be easily bound to v-model directives
 * and includes validation methods for form field validation.
 * 
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class VehicleAdd {
  /**
   * Type of the vehicle (e.g., COLLECTION_TRUCK, TRANSFER_TRUCK, SUPPORT_VEHICLE).
   */
  public vehicleType: string;

  /**
   * Numeric capacity value (must be >= 0).
   */
  public capacityValue: number;

  /**
   * Unit of measurement for capacity (e.g., 'tons', 'kg').
   */
  public capacityQuantityUnit: string;

  /**
   * Time unit for capacity rate (e.g., DAY, WEEK, MONTH, YEAR).
   */
  public capacityTimeUnit: string;

  /**
   * Cost per kilometer amount (must be >= 0).
   */
  public costPerKilometer: number;

  /**
   * ISO 4217 currency code for cost (e.g., 'EUR', 'USD').
   */
  public currencyCode: string;

  /**
   * Create a new VehicleAdd DTO.
   * 
   * @param vehicleType Type of the vehicle
   * @param capacityValue Numeric capacity value
   * @param capacityQuantityUnit Unit of measurement for capacity
   * @param capacityTimeUnit Time unit for capacity rate
   * @param costPerKilometer Cost per kilometer amount
   * @param currencyCode ISO 4217 currency code
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    vehicleType: string,
    capacityValue: number,
    capacityQuantityUnit: string,
    capacityTimeUnit: string,
    costPerKilometer: number,
    currencyCode: string
  ) {
    this.validate<string>(vehicleType, 'Vehicle type is not defined');
    this.validate<number>(capacityValue, 'Capacity value is not defined');
    this.validate<string>(capacityQuantityUnit, 'Capacity quantity unit is not defined');
    this.validate<string>(capacityTimeUnit, 'Capacity time unit is not defined');
    this.validate<number>(costPerKilometer, 'Cost per kilometer is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');

    this.vehicleType = vehicleType;
    this.capacityValue = capacityValue;
    this.capacityQuantityUnit = capacityQuantityUnit;
    this.capacityTimeUnit = capacityTimeUnit;
    this.costPerKilometer = costPerKilometer;
    this.currencyCode = currencyCode;
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
   * Validate vehicle type for form fields.
   * 
   * @param value Vehicle type string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateVehicleType(value: string): boolean | string {
    try {
      vehicleTypeFromString(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate capacity value for form fields.
   * 
   * @param value Capacity value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityValue(value: number): boolean | string {
    try {
      if (value < 0) {
        throw new Error('Capacity value must be greater than or equal to 0');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate capacity quantity unit for form fields.
   * 
   * @param value Quantity unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityQuantityUnit(value: string): boolean | string {
    try {
      new QuantityUnit(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate capacity time unit for form fields.
   * 
   * @param value Time unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityTimeUnit(value: string): boolean | string {
    try {
      timeUnitFromString(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate cost per kilometer for form fields.
   * 
   * @param value Cost per kilometer to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCostPerKilometer(value: number): boolean | string {
    try {
      new TransportationVariableCost(value);
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
   * Generate a random VehicleAdd instance for testing purposes.
   * 
   * @returns A new VehicleAdd instance with random valid values
   */
  static random(): VehicleAdd {
    const vehicleTypes = [VehicleType.COLLECTION_TRUCK, VehicleType.TRANSFER_TRUCK, VehicleType.SUPPORT_VEHICLE];
    const randomVehicleType = vehicleTypes[Math.floor(Math.random() * vehicleTypes.length)] as VehicleType;
    const randomCapacityValue = Math.floor(Math.random() * 100) + 1;
    const randomQuantityUnit = 'tons';
    const randomTimeUnit = TimeUnit.DAY;
    const randomCostPerKm = parseFloat((Math.random() * 10).toFixed(2));
    const randomCurrency = 'EUR';

    return new VehicleAdd(
      randomVehicleType as string,
      randomCapacityValue,
      randomQuantityUnit,
      randomTimeUnit,
      randomCostPerKm,
      randomCurrency
    );
  }

  /**
   * Convert this DTO to a Vehicle domain entity.
   * 
   * @param vehicleAdd VehicleAdd DTO instance
   * @returns A new Vehicle domain entity
   * @throws Error if any value object validation fails
   */
  static toVehicle(vehicleAdd: VehicleAdd): Vehicle {
    const vehicleType = vehicleTypeFromString(vehicleAdd.vehicleType);
    const quantityUnit = new QuantityUnit(vehicleAdd.capacityQuantityUnit);
    const timeUnit = timeUnitFromString(vehicleAdd.capacityTimeUnit);
    const capacity = new Capacity(vehicleAdd.capacityValue, quantityUnit, timeUnit);
    const cost = new TransportationVariableCost(vehicleAdd.costPerKilometer, vehicleAdd.currencyCode);

    return new Vehicle(vehicleType, capacity, cost);
  }
}
