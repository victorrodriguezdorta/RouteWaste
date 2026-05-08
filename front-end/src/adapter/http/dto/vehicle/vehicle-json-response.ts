import { Vehicle } from '@/domain/entity/vehicle';
import { vehicleTypeFromString } from '@/domain/enumerate/vehicle-type';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * VehicleJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for a Vehicle resource.
 * This DTO uses primitive types (strings, numbers) and maps the backend's new
 * capacity structure (capacityKilograms and CapacityLiters) into the domain
 * `Vehicle` entity with separate capacity value objects.
 */
export class VehicleJsonResponse {
  /** Resource identifier as string (UUID). */
  id: string;
  /** Optional vehicle type as string (enum key). */
  vehicleType?: string;
  /** Capacity in kilograms. */
  capacityKilograms: {
    Kilograms: number;
  };
  /** Capacity in liters. */
  capacityLiters: {
    liters: number;
  };
  /** Cost per kilometer encoded as primitives. */
  costPerKilometer: {
    amount: number;
    currency?: string;
  };

  /**
   * Create a JSON representation instance.
   * @param id resource id
   * @param capacityKilograms capacity in kilograms 
   * @param capacityLiters capacity in liters
   * @param costPerKilometer cost pieces (amount and optional currency code)
   * @param vehicleType optional vehicle type string
   */
  constructor(
    id: string,
    capacityKilograms: { Kilograms: number },
    capacityLiters: { liters: number },
    costPerKilometer: { amount: number; currency?: string },
    vehicleType?: string
  ) {
    this.id = id;
    this.vehicleType = vehicleType;
    this.capacityKilograms = capacityKilograms;
    this.capacityLiters = capacityLiters;
    this.costPerKilometer = costPerKilometer;
  }

  /**
   * Convert the JSON response into a domain `Vehicle` entity.
   * This method instantiates the required value objects and performs
   * basic validation (e.g. vehicle type must be present).
   * @param data JSON response
   * @returns Vehicle domain entity
   */
  public static toVehicle(data: VehicleJsonResponse): Vehicle {
    const id = new UllUUID(data.id);
    const capacityKg = new VehicleCapacityKilograms(data.capacityKilograms.Kilograms);
    const capacityL = new VehicleCapacityLiters(data.capacityLiters.liters);
    const cost = new TransportationVariableCost(data.costPerKilometer.amount, data.costPerKilometer.currency);
    const vehicleType = data.vehicleType ? vehicleTypeFromString(data.vehicleType) : undefined;

    if (vehicleType === undefined) {
      throw new Error('Vehicle type is required in response');
    }

    return new Vehicle(vehicleType, capacityKg, capacityL, cost, id);
  }
}
