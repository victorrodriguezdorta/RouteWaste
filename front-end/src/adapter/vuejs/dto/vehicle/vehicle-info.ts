import { Vehicle } from '@/domain/entity/vehicle';
import { TimeUnit } from '@/domain/enumerate/time-unit';
import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * VehicleInfo
 * 
 * Data Transfer Object for displaying Vehicle entity information.
 * 
 * This DTO is designed to be used in Vue.js views for showing vehicle details
 * in a read-only format. It contains primitive types that can be easily displayed
 * in templates without requiring complex object navigation.
 * 
 * Unlike VehicleAdd and VehicleEdit, this DTO does not include validation methods
 * since it is only used for displaying information, not for capturing user input.
 * 
 * All attributes are public to allow direct access in Vue.js templates.
 */
export class VehicleInfo {
  /**
   * Unique identifier of the vehicle.
   */
  public id: string;

  /**
   * Type of the vehicle (e.g., COLLECTION_TRUCK, TRANSFER_TRUCK, SUPPORT_VEHICLE).
   */
  public vehicleType: string;

  /**
   * Numeric capacity value.
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
   * Cost per kilometer amount.
   */
  public costPerKilometer: number;

  /**
   * ISO 4217 currency code for cost (e.g., 'EUR', 'USD').
   */
  public currencyCode: string;

  /**
   * Create a new VehicleInfo DTO.
   * 
   * @param id Unique identifier of the vehicle
   * @param vehicleType Type of the vehicle
   * @param capacityValue Numeric capacity value
   * @param capacityQuantityUnit Unit of measurement for capacity
   * @param capacityTimeUnit Time unit for capacity rate
   * @param costPerKilometer Cost per kilometer amount
   * @param currencyCode ISO 4217 currency code
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    id: string,
    vehicleType: string,
    capacityValue: number,
    capacityQuantityUnit: string,
    capacityTimeUnit: string,
    costPerKilometer: number,
    currencyCode: string
  ) {
    this.validate<string>(id, 'Vehicle id is not defined');
    this.validate<string>(vehicleType, 'Vehicle type is not defined');
    this.validate<number>(capacityValue, 'Capacity value is not defined');
    this.validate<string>(capacityQuantityUnit, 'Capacity quantity unit is not defined');
    this.validate<string>(capacityTimeUnit, 'Capacity time unit is not defined');
    this.validate<number>(costPerKilometer, 'Cost per kilometer is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');

    this.id = id;
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
   * Generate a random VehicleInfo instance for testing purposes.
   * 
   * @returns A new VehicleInfo instance with random valid values
   */
  static random(): VehicleInfo {
    const randomId = UllUUID.random().getValue();
    const vehicleTypes = [VehicleType.COLLECTION_TRUCK, VehicleType.TRANSFER_TRUCK, VehicleType.SUPPORT_VEHICLE];
    const randomVehicleType = vehicleTypes[Math.floor(Math.random() * vehicleTypes.length)] as VehicleType;
    const randomCapacityValue = Math.floor(Math.random() * 100) + 1;
    const randomQuantityUnit = 'tons';
    const randomTimeUnit = TimeUnit.DAY;
    const randomCostPerKm = parseFloat((Math.random() * 10).toFixed(2));
    const randomCurrency = 'EUR';

    return new VehicleInfo(
      randomId,
      randomVehicleType as string,
      randomCapacityValue,
      randomQuantityUnit,
      randomTimeUnit,
      randomCostPerKm,
      randomCurrency
    );
  }

  /**
   * Create a VehicleInfo DTO from a Vehicle domain entity.
   * 
   * @param vehicle Vehicle domain entity
   * @returns A new VehicleInfo DTO with values from the domain entity
   */
  static fromVehicle(vehicle: Vehicle): VehicleInfo {
    return new VehicleInfo(
      vehicle.getId().getValue(),
      vehicle.getVehicleType(),
      vehicle.getTransportCapacity().getValue(),
      vehicle.getTransportCapacity().getQuantityUnit().getValue(),
      vehicle.getTransportCapacity().getTimeUnit(),
      vehicle.getCostPerKilometer().getAmount(),
      vehicle.getCostPerKilometer().getCurrency().getCode()
    );
  }

  /**
   * Get a formatted string representing the full capacity with units.
   * 
   * @returns Formatted capacity string (e.g., "50 tons/day")
   */
  getFormattedCapacity(): string {
    return `${this.capacityValue} ${this.capacityQuantityUnit}/${this.capacityTimeUnit.toLowerCase()}`;
  }

  /**
   * Get a formatted string representing the cost with currency.
   * 
   * @returns Formatted cost string (e.g., "1.50 EUR/km")
   */
  getFormattedCost(): string {
    return `${this.costPerKilometer.toFixed(2)} ${this.currencyCode}/km`;
  }

  /**
   * Get a human-readable vehicle type label.
   * 
   * @returns Formatted vehicle type string
   */
  getFormattedVehicleType(): string {
    return this.vehicleType.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
