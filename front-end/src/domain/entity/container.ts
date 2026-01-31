import { generateUniqueId } from './utils';
import { Location } from '../valueobject/location/location';
import { WasteDemand } from '../valueobject/demand/waste-demand';
import { WasteType } from '../enumerate/waste-type';
import { ServiceZone } from '../enumerate/service-zone';

/**
 * Container
 *
 * Represents a physical waste collection point (entity).
 *
 * Stores location, waste type and expected demand. Operations allow
 * updating fields and querying identifiers used by higher-level
 * domain logic.
 */
export class Container {
  readonly id: string;
  private location: Location;
  private wasteType: WasteType;
  private wasteDemand: WasteDemand;
  private serviceZone?: ServiceZone | null;

  /**
   * Create a new `Container`.
   * @param location geographic location of the container
   * @param wasteType type/category of waste collected
   * @param wasteDemand expected waste demand object
   * @param serviceZone optional service zone identifier
   * @param id optional explicit id (generated when omitted)
   * @throws Error when required parameters are missing
   */
  constructor(location: Location, wasteType: WasteType, wasteDemand: WasteDemand, serviceZone?: ServiceZone | null, id?: string) {
    if (!location) throw new Error('Container location is not defined');
    if (!wasteType) throw new Error('Waste type is not defined');
    if (!wasteDemand) throw new Error('Waste demand is not defined');
    this.id = id ?? generateUniqueId();
    this.location = location;
    this.wasteType = wasteType;
    this.wasteDemand = wasteDemand;
    this.serviceZone = serviceZone ?? null;
  }

  /** Return the container identifier. */
  getId(): string { return this.id; }

  /** Return the container location value object. */
  getLocation(): Location { return this.location; }

  /** Update the container location. @throws Error when `location` is falsy. */
  updateLocation(location: Location): void { if (!location) throw new Error('Container location is not defined'); this.location = location; }

  /** Return the waste type enumeration for this container. */
  getWasteType(): WasteType { return this.wasteType; }

  /** Update the waste type. @throws Error when `wt` is falsy. */
  updateWasteType(wt: WasteType): void { if (!wt) throw new Error('Waste type is not defined'); this.wasteType = wt; }

  /** Return the current waste demand value object. */
  getWasteDemand(): WasteDemand { return this.wasteDemand; }

  /** Update the waste demand. @throws Error when `wd` is falsy. */
  updateWasteDemand(wd: WasteDemand): void { if (!wd) throw new Error('Waste demand is not defined'); this.wasteDemand = wd; }

  /** Return the optional service zone or null when not set. */
  getServiceZone(): ServiceZone | null { return this.serviceZone ?? null; }

  /** Update the service zone (nullable). */
  updateServiceZone(zone?: ServiceZone | null): void { this.serviceZone = zone ?? null; }

  /**
   * Equality by identity (id field).
   * @param other candidate object to compare
   */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof Container)) return false; return this.id === other.id; }

  /** Human-readable representation for debugging. */
  toString(): string { return `Container={id=${this.id}, location=${this.location}, wasteType=${this.wasteType}, wasteDemand=${this.wasteDemand}, serviceZone=${this.serviceZone}}`; }
}
