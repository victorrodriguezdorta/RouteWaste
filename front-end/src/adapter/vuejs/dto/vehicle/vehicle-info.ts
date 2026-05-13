import { Vehicle } from '@/domain/entity/vehicle';
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
   * Human-readable vehicle name.
   */
  public name: string;

  /**
   * Type of the vehicle (e.g., COLLECTION_TRUCK, TRANSFER_TRUCK, SUPPORT_VEHICLE).
   */
  public vehicleType: string;

  /**
   * Capacity in kilograms.
   */
  public capacityKilograms: number;

  /**
   * Capacity in liters.
   */
  public capacityLiters: number;

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
   * @param name Human-readable name
   * @param vehicleType Type of the vehicle
   * @param capacityKilograms Capacity in kilograms
   * @param capacityLiters Capacity in liters
   * @param costPerKilometer Cost per kilometer amount
   * @param currencyCode ISO 4217 currency code
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    id: string,
    name: string,
    vehicleType: string,
    capacityKilograms: number,
    capacityLiters: number,
    costPerKilometer: number,
    currencyCode: string
  ) {
    this.validate<string>(id, 'Vehicle id is not defined');
    this.validate<string>(name, 'Name is not defined');
    this.validate<string>(vehicleType, 'Vehicle type is not defined');
    this.validate<number>(capacityKilograms, 'Capacity in kilograms is not defined');
    this.validate<number>(capacityLiters, 'Capacity in liters is not defined');
    this.validate<number>(costPerKilometer, 'Cost per kilometer is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');

    this.id = id;
    this.name = name;
    this.vehicleType = vehicleType;
    this.capacityKilograms = capacityKilograms;
    this.capacityLiters = capacityLiters;
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
    const randomCapacityKg = Math.floor(Math.random() * 5000) + 1;
    const randomCapacityL = Math.floor(Math.random() * 10000) + 1;
    const randomCostPerKm = parseFloat((Math.random() * 10).toFixed(2));
    const randomCurrency = 'EUR';

    return new VehicleInfo(
      randomId,
      `Vehicle ${Math.floor(Math.random() * 10000)}`,
      randomVehicleType as string,
      randomCapacityKg,
      randomCapacityL,
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
      vehicle.getName().getValue(),
      vehicle.getVehicleType(),
      vehicle.getCapacityKilograms().getKilograms(),
      vehicle.getCapacityLiters().getLiters(),
      vehicle.getCostPerKilometer().getAmount(),
      vehicle.getCostPerKilometer().getCurrency().getCode()
    );
  }
}

