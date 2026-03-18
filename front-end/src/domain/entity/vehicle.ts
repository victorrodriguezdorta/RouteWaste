import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { Capacity } from '@/domain/valueobject/demand/capacity';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Vehicle
 *
 * Represents a transport unit (aggregate root).
 *
 * Encapsulates vehicle type, transport capacity and per-kilometer
 * cost information used for routing and costing calculations.
 */
export class Vehicle {
  /** The unique identifier of the vehicle. */
  readonly id: UllUUID;
  /** The type of vehicle (e.g., car, truck, van). */
  private vehicleType: VehicleType;
  /** The capacity of the vehicle for transporting goods. */
  private transportCapacity: Capacity;
  /** The cost per kilometer for operating this vehicle. */
  private costPerKilometer: TransportationVariableCost;

  /**
   * Create a new `Vehicle` aggregate.
   * @param vehicleType vehicle classification
   * @param transportCapacity capacity value object
   * @param costPerKilometer transportation variable cost value object
   * @param id optional explicit id (generated when omitted)
   */
  constructor(vehicleType: VehicleType, transportCapacity: Capacity, costPerKilometer: TransportationVariableCost, id?: UllUUID) {
    if (!vehicleType) throw new Error('Vehicle type is not defined');
    if (!transportCapacity) throw new Error('Vehicle capacity is not defined');
    if (!costPerKilometer) throw new Error('Transportation variable cost is not defined');
    this.id = id ?? UllUUID.random();
    this.vehicleType = vehicleType;
    this.transportCapacity = transportCapacity;
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
   * Return the transport capacity value object.
   * @returns The transport capacity.
   */
  getTransportCapacity(): Capacity { return this.transportCapacity; }

  /**
   * Update transport capacity. Throws when `c` is falsy.
   * @param c The new transport capacity.
   */
  updateTransportCapacity(c: Capacity): void { if (!c) throw new Error('Vehicle capacity is not defined'); this.transportCapacity = c; }

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
  toString(): string { return `Vehicle={id=${this.id}, type=${this.vehicleType}, capacity=${this.transportCapacity}, costPerKm=${this.costPerKilometer}}`; }
}
