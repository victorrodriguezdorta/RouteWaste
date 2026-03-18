import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import { Location } from '@/domain/valueobject/location/location';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

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
  /** Unique identifier for the container */
  readonly id: UllUUID;

  /** Geographic location of the container */
  private location: Location;

  /** Type/category of waste collected by this container */
  private wasteType: WasteType;

  /** Expected waste demand object for the container */
  private wasteDemand: WasteDemand;

  /** Optional service zone identifier for this container */
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
  constructor(location: Location, wasteType: WasteType, wasteDemand: WasteDemand, serviceZone?: ServiceZone | null, id?: UllUUID) {
    if (!location) throw new Error('Container location is not defined');
    if (!wasteType) throw new Error('Waste type is not defined');
    if (!wasteDemand) throw new Error('Waste demand is not defined');
    this.id = id ?? UllUUID.random();
    this.location = location;
    this.wasteType = wasteType;
    this.wasteDemand = wasteDemand;
    this.serviceZone = serviceZone ?? null;
  }

  /**
   * Return the container identifier.
   * @returns The unique identifier of this container
   */
  getId(): UllUUID { return this.id; }

  /**
   * Return the container location value object.
   * @returns The geographic location of this container
   */
  getLocation(): Location { return this.location; }

  /**
   * Update the container location.
   * @param location The new location for this container
   * @throws Error when `location` is falsy
   */
  updateLocation(location: Location): void { if (!location) throw new Error('Container location is not defined'); this.location = location; }

  /**
   * Return the waste type enumeration for this container.
   * @returns The waste type of this container
   */
  getWasteType(): WasteType { return this.wasteType; }

  /**
   * Update the waste type.
   * @param wt The new waste type for this container
   * @throws Error when `wt` is falsy
   */
  updateWasteType(wt: WasteType): void { if (!wt) throw new Error('Waste type is not defined'); this.wasteType = wt; }

  /**
   * Return the current waste demand value object.
   * @returns The waste demand for this container
   */
  getWasteDemand(): WasteDemand { return this.wasteDemand; }

  /**
   * Update the waste demand.
   * @param wd The new waste demand for this container
   * @throws Error when `wd` is falsy
   */
  updateWasteDemand(wd: WasteDemand): void { if (!wd) throw new Error('Waste demand is not defined'); this.wasteDemand = wd; }

  /**
   * Return the optional service zone or null when not set.
   * @returns The service zone or null if not set
   */
  getServiceZone(): ServiceZone | null { return this.serviceZone ?? null; }

  /**
   * Update the service zone (nullable).
   * @param zone The new service zone for this container
   */
  updateServiceZone(zone?: ServiceZone | null): void { this.serviceZone = zone ?? null; }

  /**
   * Equality comparison by identity (id field).
   * @param other Candidate object to compare with this container
   * @returns True if both containers have the same identifier, false otherwise
   */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof Container)) return false; return this.id.equals(other.id); }

  /**
   * Human-readable representation for debugging.
   * @returns A string representation of this container
   */
  toString(): string { return `Container={id=${this.id}, location=${this.location}, wasteType=${this.wasteType}, wasteDemand=${this.wasteDemand}, serviceZone=${this.serviceZone}}`; }
}
