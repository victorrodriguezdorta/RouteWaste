import type { CreateVehicleCommand } from '../../../../application/usecase/vehicle-management/create-vehicle/create-vehicle-command';

/**
 * VehiclePostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Vehicle.
 * Uses primitive types for all fields so it can be serialized directly to JSON.
 */
export class VehiclePostJsonRequest {
  /** Vehicle type as enum key string. */
  vehicleType: string;
  /** Transport capacity expressed with primitive parts. */
  transportCapacity: {
    value: number;
    quantityUnit: string;
    timeUnit: string;
  };
  /** Cost per kilometer expressed with primitive parts. */
  costPerKilometer: {
    amount: number;
    currency?: string;
  };

  /**
   * Build a request DTO instance.
   * @param vehicleType vehicle type string
   * @param transportCapacity capacity parts
   * @param costPerKilometer cost parts
   */
  constructor(
    vehicleType: string,
    transportCapacity: { value: number; quantityUnit: string; timeUnit: string },
    costPerKilometer: { amount: number; currency?: string }
  ) {
    this.vehicleType = vehicleType;
    this.transportCapacity = transportCapacity;
    this.costPerKilometer = costPerKilometer;
  }

  /**
   * Map a `CreateVehicleCommand` (domain input) to this DTO.
   * Extracts primitive values from domain value objects to produce a
   * serializable payload for a POST request.
   * @param data domain create command
   */
  public static toRequest(data: CreateVehicleCommand): VehiclePostJsonRequest {
    return new VehiclePostJsonRequest(
      data.vehicleType,
      {
        value: data.transportCapacity.getValue(),
        quantityUnit: data.transportCapacity.getQuantityUnit().getValue(),
        timeUnit: data.transportCapacity.getTimeUnit().toString(),
      },
      {
        amount: data.costPerKilometer.getAmount(),
        currency: data.costPerKilometer.getCurrency().getCode(),
      }
    );
  }
}
