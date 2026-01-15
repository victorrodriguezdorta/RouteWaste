import { generateUniqueId } from './utils';
import { FacilityType } from '../enumerate/FacilityType';
import { FacilityStatus, facilityStatusIsDiscarded } from '../enumerate/FacilityStatus';
import { Location } from '../valueobject/location/Location';
import { Capacity } from '../valueobject/demand/Capacity';
import { WasteDemand } from '../valueobject/demand/WasteDemand';
import { OpeningFixedCost } from '../valueobject/cost/OpeningFixedCost';

/**
 * Facility
 *
 * Represents a waste management infrastructure (aggregate root).
 *
 * Manages assigned demand, capacity and operational status. Provides
 * helpers to accept demand assignments from `Container` entities and
 * to update facility attributes used by the application layer.
 */
export class Facility {
  readonly id: string;
  private facilityType: FacilityType;
  private location: Location;
  private capacity: Capacity;
  private openingFixedCost: OpeningFixedCost;
  private status: FacilityStatus;
  private assignedWasteDemand: WasteDemand;

  /**
   * Create a new `Facility` aggregate.
   * @param facilityType facility classification
   * @param location facility geographic location
   * @param capacity capacity value object
   * @param openingFixedCost fixed opening cost value object
   * @param status current facility status
   * @param id optional explicit id (generated if omitted)
   * @throws Error when required parameters are missing
   */
  constructor(facilityType: FacilityType, location: Location, capacity: Capacity, openingFixedCost: OpeningFixedCost, status: FacilityStatus, id?: string) {
    if (!facilityType) throw new Error('Facility type is not defined');
    if (!location) throw new Error('Facility location is not defined');
    if (!capacity) throw new Error('Facility capacity is not defined');
    if (!openingFixedCost) throw new Error('Opening fixed cost is not defined');
    if (!status) throw new Error('Facility status is not defined');

    this.id = id ?? generateUniqueId();
    this.facilityType = facilityType;
    this.location = location;
    this.capacity = capacity;
    this.openingFixedCost = openingFixedCost;
    this.status = status;
    this.assignedWasteDemand = WasteDemand.withDefaultUnit(0);
  }

  /** Return the facility identifier. */
  getId(): string { return this.id; }

  /**
   * Assign additional waste demand to the facility.
   * @param demand demand value object to add
   * @throws Error when facility is discarded or capacity would be exceeded
   */
  assignWasteDemand(demand: WasteDemand): void {
    if (facilityStatusIsDiscarded(this.status)) throw new Error('Facility is discarded and cannot receive assignments');
    const newTotal = this.assignedWasteDemand.add(demand);
    if (newTotal.greaterThan(this.capacity)) throw new Error('Facility capacity exceeded');
    this.assignedWasteDemand = newTotal;
  }

  getFacilityType(): FacilityType { return this.facilityType; }
  getLocation(): Location { return this.location; }
  getCapacity(): Capacity { return this.capacity; }
  getOpeningFixedCost(): OpeningFixedCost { return this.openingFixedCost; }
  getStatus(): FacilityStatus { return this.status; }
  getAssignedWasteDemand(): WasteDemand { return this.assignedWasteDemand; }

  /** Update facility status. Throws when `status` is falsy. */
  updateStatus(status: FacilityStatus): void {
    if (!status) throw new Error('Facility status is not defined');
    this.status = status;
  }

  /** Update facility type. Throws when `ft` is falsy. */
  updateFacilityType(ft: FacilityType): void {
    if (!ft) throw new Error('Facility type is not defined');
    this.facilityType = ft;
  }

  /** Update facility location. Throws when `loc` is falsy. */
  updateLocation(loc: Location): void {
    if (!loc) throw new Error('Facility location is not defined');
    this.location = loc;
  }

  /** Update facility capacity. Throws when `cap` is falsy. */
  updateCapacity(cap: Capacity): void {
    if (!cap) throw new Error('Facility capacity is not defined');
    this.capacity = cap;
  }

  /** Update opening fixed cost. Throws when `cost` is falsy. */
  updateOpeningFixedCost(cost: OpeningFixedCost): void {
    if (!cost) throw new Error('Opening fixed cost is not defined');
    this.openingFixedCost = cost;
  }

  /** Equality by id. */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Facility)) return false;
    return this.id === other.id;
  }

  /** Human-readable representation for debugging. */
  toString(): string {
    return `Facility={id=${this.id}, type=${this.facilityType}, location=${this.location}, capacity=${this.capacity}, assignedDemand=${this.assignedWasteDemand}, openingCost=${this.openingFixedCost}, status=${this.status}`;
  }
}
