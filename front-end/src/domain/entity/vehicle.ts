import { generateUniqueId } from './utils';
import { VehicleType } from '../enumerate/vehicle-type';
import { Capacity } from '../valueobject/demand/capacity';
import { TransportationVariableCost } from '../valueobject/cost/transportation-variable-cost';

/**
 * Vehicle
 *
 * Represents a transport unit (aggregate root).
 *
 * Encapsulates vehicle type, transport capacity and per-kilometer
 * cost information used for routing and costing calculations.
 */
export class Vehicle {
  readonly id: string;
  private vehicleType: VehicleType;
  private transportCapacity: Capacity;
  private costPerKilometer: TransportationVariableCost;

  /**
   * Create a new `Vehicle` aggregate.
   * @param vehicleType vehicle classification
   * @param transportCapacity capacity value object
   * @param costPerKilometer transportation variable cost value object
   * @param id optional explicit id (generated when omitted)
   */
  constructor(vehicleType: VehicleType, transportCapacity: Capacity, costPerKilometer: TransportationVariableCost, id?: string) {
    if (!vehicleType) throw new Error('Vehicle type is not defined');
    if (!transportCapacity) throw new Error('Vehicle capacity is not defined');
    if (!costPerKilometer) throw new Error('Transportation variable cost is not defined');
    this.id = id ?? generateUniqueId();
    this.vehicleType = vehicleType;
    this.transportCapacity = transportCapacity;
    this.costPerKilometer = costPerKilometer;
  }

  /** Return the vehicle identifier. */
  getId(): string { return this.id; }

  /** Return the vehicle type. */
  getVehicleType(): VehicleType { return this.vehicleType; }

  /** Update the vehicle type. Throws when `t` is falsy. */
  updateVehicleType(t: VehicleType): void { if (!t) throw new Error('Vehicle type is not defined'); this.vehicleType = t; }

  /** Return the transport capacity value object. */
  getTransportCapacity(): Capacity { return this.transportCapacity; }

  /** Update transport capacity. Throws when `c` is falsy. */
  updateTransportCapacity(c: Capacity): void { if (!c) throw new Error('Vehicle capacity is not defined'); this.transportCapacity = c; }

  /** Return the per-kilometer cost value object. */
  getCostPerKilometer(): TransportationVariableCost { return this.costPerKilometer; }

  /** Update per-kilometer cost. Throws when `c` is falsy. */
  updateCostPerKilometer(c: TransportationVariableCost): void { if (!c) throw new Error('Transportation variable cost is not defined'); this.costPerKilometer = c; }

  /** Equality by id. */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof Vehicle)) return false; return this.id === other.id; }

  /** Human-readable representation for debugging. */
  toString(): string { return `Vehicle={id=${this.id}, type=${this.vehicleType}, capacity=${this.transportCapacity}, costPerKm=${this.costPerKilometer}}`; }
}
