import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Container
 *
 * Represents a physical waste collection point (entity).
 *
 * Stores location, waste type, capacity in liters, and daily demand in liters per day.
 * Operations allow updating fields and querying identifiers used by higher-level
 * domain logic.
 */
export class Container {
  /** Unique identifier for the container */
  readonly id: UllUUID;

  /** Human-readable name for the container */
  private name: Name;

  /** Geographic location of the container */
  private location: Location;

  /** Type/category of waste collected by this container */
  private wasteType: WasteType;

  /** Maximum capacity of the container in liters */
  private capacityLiters: ContainerCapacityLiters;

  /** Approximate daily waste demand in liters per day */
  private dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay;

  /** Optional service zone identifier for this container */
  private serviceZone?: ServiceZone | null;

  /**
   * Create a new `Container`.
   * @param name human-readable name
   * @param location geographic location of the container
   * @param wasteType type/category of waste collected
   * @param capacityLiters maximum capacity of the container in liters
   * @param dailyDemandLitersPerDay approximate daily waste demand in liters per day
   * @param serviceZone optional service zone identifier
   * @param id optional explicit id (generated when omitted)
   * @throws Error when required parameters are missing
   */
  constructor(
    name: Name,
    location: Location,
    wasteType: WasteType,
    capacityLiters: ContainerCapacityLiters,
    dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay,
    serviceZone?: ServiceZone | null,
    id?: UllUUID
  ) {
    if (!name) throw new Error('Container name is not defined');
    if (!location) throw new Error('Container location is not defined');
    if (!wasteType) throw new Error('Waste type is not defined');
    if (!capacityLiters) throw new Error('Container capacity in liters is not defined');
    if (!dailyDemandLitersPerDay) throw new Error('Daily waste demand in liters is not defined');
    this.id = id ?? UllUUID.random();
    this.name = name;
    this.location = location;
    this.wasteType = wasteType;
    this.capacityLiters = capacityLiters;
    this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
    this.serviceZone = serviceZone ?? null;
  }

  /**
   * Return the container identifier.
   * @returns The unique identifier of this container
   */
  getId(): UllUUID { return this.id; }

  /**
   * Return the container display name.
   */
  getName(): Name { return this.name; }

  /**
   * Update the container name.
   */
  updateName(name: Name): void { if (!name) throw new Error('Container name is not defined'); this.name = name; }

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
   * Return the container capacity in liters.
   * @returns The maximum capacity of this container in liters
   */
  getCapacityLiters(): ContainerCapacityLiters { return this.capacityLiters; }

  /**
   * Update the container capacity in liters.
   * @param capacityLiters The new capacity in liters
   * @throws Error when `capacityLiters` is falsy
   */
  updateCapacityLiters(capacityLiters: ContainerCapacityLiters): void { if (!capacityLiters) throw new Error('Container capacity in liters is not defined'); this.capacityLiters = capacityLiters; }

  /**
   * Return the approximate daily waste demand in liters per day.
   * @returns The daily waste demand for this container
   */
  getDailyDemandLitersPerDay(): DailyWasteDemandLitersPerDay { return this.dailyDemandLitersPerDay; }

  /**
   * Update the approximate daily waste demand in liters per day.
   * @param dailyDemandLitersPerDay The new daily demand in liters per day
   * @throws Error when `dailyDemandLitersPerDay` is falsy
   */
  updateDailyDemandLitersPerDay(dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay): void { if (!dailyDemandLitersPerDay) throw new Error('Daily waste demand in liters is not defined'); this.dailyDemandLitersPerDay = dailyDemandLitersPerDay; }

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
  toString(): string { return `Container={id=${this.id}, name=${this.name}, location=${this.location}, wasteType=${this.wasteType}, capacityLiters=${this.capacityLiters}, dailyDemandLitersPerDay=${this.dailyDemandLitersPerDay}, serviceZone=${this.serviceZone}}`; }
}
