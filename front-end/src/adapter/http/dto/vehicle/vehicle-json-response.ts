import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

import { Vehicle } from '@/domain/entity/vehicle';
import { timeUnitFromString } from '@/domain/enumerate/time-unit';
import { vehicleTypeFromString } from '@/domain/enumerate/vehicle-type';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { Capacity } from '@/domain/valueobject/demand/capacity';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';

/**
 * VehicleJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for a Vehicle resource.
 * This DTO uses primitive types (strings, numbers) for transport and exposes a
 * static `toVehicle` helper to convert the JSON payload into the domain
 * `Vehicle` entity by constructing the required value objects.
 */
export class VehicleJsonResponse {
  /** Resource identifier as string (UUID). */
  id: string;
  /** Optional vehicle type as string (enum key). */
  vehicleType?: string;
  /** Transport capacity encoded with primitive parts. */
  transportCapacity: {
    value: number;
    quantityUnit: string;
    timeUnit: string;
  };
  /** Cost per kilometer encoded as primitives. */
  costPerKilometer: {
    amount: number;
    currency?: string;
  };

  /**
   * Create a JSON representation instance.
   * @param id resource id
   * @param transportCapacity capacity pieces (value, quantityUnit, timeUnit)
   * @param costPerKilometer cost pieces (amount and optional currency code)
   * @param vehicleType optional vehicle type string
   */
  constructor(
    id: string,
    transportCapacity: { value: number; quantityUnit: string; timeUnit: string },
    costPerKilometer: { amount: number; currency?: string },
    vehicleType?: string
  ) {
    this.id = id;
    this.vehicleType = vehicleType;
    this.transportCapacity = transportCapacity;
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
    const quantityUnit = new QuantityUnit(data.transportCapacity.quantityUnit);
    const timeUnit = timeUnitFromString(data.transportCapacity.timeUnit);
    const capacity = new Capacity(data.transportCapacity.value, quantityUnit, timeUnit);
    const cost = new TransportationVariableCost(data.costPerKilometer.amount, data.costPerKilometer.currency);
    const vehicleType = data.vehicleType ? vehicleTypeFromString(data.vehicleType) : undefined;

    if (vehicleType === undefined) {
      throw new Error('Vehicle type is required in response');
    }

    return new Vehicle(vehicleType, capacity, cost, id);
  }
}
