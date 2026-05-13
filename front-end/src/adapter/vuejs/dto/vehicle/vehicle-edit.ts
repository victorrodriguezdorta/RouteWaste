import { Vehicle } from "@/domain/entity/vehicle";
import {
  VehicleType,
  vehicleTypeFromString,
} from "@/domain/enumerate/vehicle-type";
import { VehicleCapacityKilograms } from "@/domain/valueobject/capacity/vehicle-capacity-kilograms";
import { VehicleCapacityLiters } from "@/domain/valueobject/capacity/vehicle-capacity-liters";
import { Currency } from "@/domain/valueobject/cost/currency";
import { TransportationVariableCost } from "@/domain/valueobject/cost/transportation-variable-cost";
import { Name } from "@/domain/valueobject/name/name";
import { UllUUID } from "@ull-tfg/ull-tfg-typescript";

/**
 * VehicleEdit
 *
 * Data Transfer Object for editing an existing Vehicle entity.
 *
 * This DTO is designed to be used in Vue.js forms for updating existing vehicles.
 * It contains primitive types that can be easily bound to v-model directives
 * and includes validation methods for form field validation.
 *
 * Unlike VehicleAdd, this DTO includes an 'id' field to identify the vehicle being edited.
 *
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class VehicleEdit {
  /**
   * Unique identifier of the vehicle being edited.
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
   * Capacity in kilograms (must be >= 0).
   */
  public capacityKilograms: number;

  /**
   * Capacity in liters (must be >= 0).
   */
  public capacityLiters: number;

  /**
   * Cost per kilometer amount (must be >= 0).
   */
  public costPerKilometer: number;

  /**
   * ISO 4217 currency code for cost (e.g., 'EUR', 'USD').
   */
  public currencyCode: string;

  /**
   * Create a new VehicleEdit DTO.
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
    currencyCode: string,
  ) {
    this.validate<string>(id, "Vehicle id is not defined");
    this.validate<string>(name, "Name is not defined");
    this.validate<string>(vehicleType, "Vehicle type is not defined");
    this.validate<number>(capacityKilograms, "Capacity in kilograms is not defined");
    this.validate<number>(capacityLiters, "Capacity in liters is not defined");
    this.validate<number>(
      costPerKilometer,
      "Cost per kilometer is not defined",
    );
    this.validate<string>(currencyCode, "Currency code is not defined");

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
    errorMessage: string,
  ): asserts attribute is T {
    if (attribute === undefined || attribute === null) {
      throw new Error(errorMessage);
    }
  }

  /**
   * Validate vehicle id for form fields.
   *
   * @param value Vehicle id string to validate
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
   * Validate capacity in kilograms for form fields.
   *
   * @param value Capacity in kilograms to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityKilograms(value: number): boolean | string {
    try {
      if (value < 0) {
        throw new Error("Capacity in kilograms must be greater than or equal to 0");
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate capacity in liters for form fields.
   *
   * @param value Capacity in liters to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityLiters(value: number): boolean | string {
    try {
      if (value < 0) {
        throw new Error("Capacity in liters must be greater than or equal to 0");
      }
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
   * Generate a random VehicleEdit instance for testing purposes.
   *
   * @returns A new VehicleEdit instance with random valid values
   */
  static random(): VehicleEdit {
    const randomId = UllUUID.random().getValue();
    const vehicleTypes = [
      VehicleType.COLLECTION_TRUCK,
      VehicleType.TRANSFER_TRUCK,
      VehicleType.SUPPORT_VEHICLE,
    ];
    const randomVehicleType = vehicleTypes[
      Math.floor(Math.random() * vehicleTypes.length)
    ] as VehicleType;
    const randomCapacityKg = Math.floor(Math.random() * 5000) + 1;
    const randomCapacityL = Math.floor(Math.random() * 10000) + 1;
    const randomCostPerKm = parseFloat((Math.random() * 10).toFixed(2));
    const randomCurrency = "EUR";

    return new VehicleEdit(
      randomId,
      `Vehicle ${Math.floor(Math.random() * 10000)}`,
      randomVehicleType as string,
      randomCapacityKg,
      randomCapacityL,
      randomCostPerKm,
      randomCurrency,
    );
  }

  /**
   * Convert this DTO to a Vehicle domain entity.
   *
   * @param vehicleEdit VehicleEdit DTO instance
   * @returns A new Vehicle domain entity with the specified id
   * @throws Error if any value object validation fails
   */
  static toVehicle(vehicleEdit: VehicleEdit): Vehicle {
    const name = new Name(vehicleEdit.name);
    const vehicleType = vehicleTypeFromString(vehicleEdit.vehicleType);
    const capacityKg = new VehicleCapacityKilograms(vehicleEdit.capacityKilograms);
    const capacityL = new VehicleCapacityLiters(vehicleEdit.capacityLiters);
    const cost = new TransportationVariableCost(
      vehicleEdit.costPerKilometer,
      vehicleEdit.currencyCode,
    );
    const id = new UllUUID(vehicleEdit.id);

    return new Vehicle(name, vehicleType, capacityKg, capacityL, cost, id);
  }

  /**
   * Create a VehicleEdit DTO from a Vehicle domain entity.
   *
   * @param vehicle Vehicle domain entity
   * @returns A new VehicleEdit DTO with values from the domain entity
   */
  static fromVehicle(vehicle: Vehicle): VehicleEdit {
    return new VehicleEdit(
      vehicle.getId().getValue(),
      vehicle.getName().getValue(),
      vehicle.getVehicleType(),
      vehicle.getCapacityKilograms().getKilograms(),
      vehicle.getCapacityLiters().getLiters(),
      vehicle.getCostPerKilometer().getAmount(),
      vehicle.getCostPerKilometer().getCurrency().getCode(),
    );
  }

  /**
   * Convert the DTO to an UpdateVehicleCommand for use case layer.
   *
   * @returns Command object for updating a vehicle
   */
  toUpdateVehicleCommand(): any {
    return {
      vehicleId: new UllUUID(this.id),
      updatedFields: {
        name: new Name(this.name),
        vehicleType: vehicleTypeFromString(this.vehicleType),
        capacityKilograms: new VehicleCapacityKilograms(this.capacityKilograms),
        capacityLiters: new VehicleCapacityLiters(this.capacityLiters),
        costPerKilometer: new TransportationVariableCost(
          this.costPerKilometer,
          this.currencyCode
        ),
      },
    };
  }
}
