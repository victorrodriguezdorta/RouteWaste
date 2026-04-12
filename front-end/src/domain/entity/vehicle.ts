import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Vehicle
 *
 * Represents a transport unit (aggregate root).
 *
 * Encapsulates vehicle type, two separate capacities (kilograms and liters),
 * and per-kilometer cost information used for routing and costing calculations.
 */
export class Vehicle {
  /** The unique identifier of the vehicle. */
  readonly id: UllUUID;
  /** The type of vehicle (e.g., COLLECTION_TRUCK, TRANSFER_TRUCK). */
  private vehicleType: VehicleType;
  /** The capacity of the vehicle in kilograms. */
  private capacityKilograms: VehicleCapacityKilograms;
  /** The capacity of the vehicle in liters. */
  private capacityLiters: VehicleCapacityLiters;
  /** The cost per kilometer for operating this vehicle. */
  private costPerKilometer: TransportationVariableCost;

  /**
   * Create a new `Vehicle` aggregate.
   * @param vehicleType vehicle classification
   * @param capacityKilograms capacity in kilograms value object
   * @param capacityLiters capacity in liters value object
   * @param costPerKilometer transportation variable cost value object
   * @param id optional explicit id (generated when omitted)
   */
  constructor(
    vehicleType: VehicleType,
    capacityKilograms: VehicleCapacityKilograms,
    capacityLiters: VehicleCapacityLiters,
    costPerKilometer: TransportationVariableCost,
    id?: UllUUID
  ) {
    if (!vehicleType) throw new Error('Vehicle type is not defined');
    if (!capacityKilograms) throw new Error('Vehicle capacity in kilograms is not defined');
    if (!capacityLiters) throw new Error('Vehicle capacity in liters is not defined');
    if (!costPerKilometer) throw new Error('Transportation variable cost is not defined');
    this.id = id ?? UllUUID.random();
    this.vehicleType = vehicleType;
    this.capacityKilograms = capacityKilograms;
    this.capacityLiters = capacityLiters;
    this.costPerKilometer = costPerKilometer;
  }

  /**
   * Return the vehicle identifier.
   * @returns The unique identifier of this vehicle.
   */
  getId(): UllUUID { return this.id; }

  /**
   * Return the vehicle type.
   * @returns The type of the vehicle.
   */
  getVehicleType(): VehicleType { return this.vehicleType; }

  /**
   * Update the vehicle type. Throws when `t` is falsy.
   * @param t The new vehicle type.
   */
  updateVehicleType(t: VehicleType): void { if (!t) throw new Error('Vehicle type is not defined'); this.vehicleType = t; }

  /**
   * Return the vehicle capacity in kilograms.
   * @returns The capacity in kilograms.
   */
  getCapacityKilograms(): VehicleCapacityKilograms { return this.capacityKilograms; }

  /**
   * Update vehicle capacity in kilograms. Throws when `c` is falsy.
   * @param c The new capacity in kilograms.
   */
  updateCapacityKilograms(c: VehicleCapacityKilograms): void { if (!c) throw new Error('Vehicle capacity in kilograms is not defined'); this.capacityKilograms = c; }

  /**
   * Return the vehicle capacity in liters.
   * @returns The capacity in liters.
   */
  getCapacityLiters(): VehicleCapacityLiters { return this.capacityLiters; }

  /**
   * Update vehicle capacity in liters. Throws when `c` is falsy.
   * @param c The new capacity in liters.
   */
  updateCapacityLiters(c: VehicleCapacityLiters): void { if (!c) throw new Error('Vehicle capacity in liters is not defined'); this.capacityLiters = c; }

  /**
   * Return the per-kilometer cost value object.
   * @returns The cost per kilometer.
   */
  getCostPerKilometer(): TransportationVariableCost { return this.costPerKilometer; }

  /**
   * Update per-kilometer cost. Throws when `c` is falsy.
   * @param c The new cost per kilometer.
   */
  updateCostPerKilometer(c: TransportationVariableCost): void { if (!c) throw new Error('Transportation variable cost is not defined'); this.costPerKilometer = c; }

  /**
   * Equality by id.
   * @param other The vehicle to compare with.
   * @returns True if both vehicles have the same id, false otherwise.
   */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof Vehicle)) return false; return this.id.equals(other.id); }

  /**
   * Human-readable representation for debugging.
   * @returns A string representation of the vehicle.
   */
  toString(): string { return `Vehicle={id=${this.id}, type=${this.vehicleType}, capacityKg=${this.capacityKilograms}, capacityL=${this.capacityLiters}, costPerKm=${this.costPerKilometer}}`; }
}
