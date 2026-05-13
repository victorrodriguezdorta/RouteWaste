import { Container } from '@/domain/entity/container';
import { serviceZoneFromString } from '@/domain/enumerate/service-zone';
import { wasteTypeFromString } from '@/domain/enumerate/waste-type';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * ContainerJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for a Container
 * resource. Uses primitive fields for serialization and provides a helper
 * `toContainer` to convert the payload into the domain `Container` entity.
 */
export class ContainerJsonResponse {
  /** Unique identifier of the container */
  id: string;

  /** Human-readable name */
  name: string;

  /** Geographic location information of the container */
  location: {
    latitude: number;
    longitude: number;
    postalAddress: string;
    gisReference: string;
  };

  /** Type/category of waste collected by this container */
  wasteType: string;

  /** Maximum capacity of the container in liters */
  capacityLiters: {
    liters: number;
  };

  /** Approximate daily waste demand in liters per day */
  dailyDemandLitersPerDay: {
    litersPerDay: number;
  };

  /** Optional service zone identifier for this container */
  serviceZone?: string | null;

  constructor(
    id: string,
    name: string,
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    wasteType: string,
    capacityLiters: { liters: number },
    dailyDemandLitersPerDay: { litersPerDay: number },
    serviceZone?: string | null
  ) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.wasteType = wasteType;
    this.capacityLiters = capacityLiters;
    this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
    this.serviceZone = serviceZone ?? null;
  }

  /**
   * Convert JSON response into domain `Container` entity.
   * Builds the required value objects (`Location`, `ContainerCapacityLiters`, `DailyWasteDemandLitersPerDay`)
   * and parses enumerations for `WasteType` and `ServiceZone`.
   * @param data The JSON response data to convert
   * @returns A new Container domain entity
   */
  public static toContainer(data: ContainerJsonResponse): Container {
    const id = new UllUUID(data.id);
    const name = new Name(data.name);
    const loc = new Location(
      data.location.latitude,
      data.location.longitude,
      data.location.postalAddress,
      data.location.gisReference
    );
    const wasteType = wasteTypeFromString(data.wasteType);
    const capacityLiters = new ContainerCapacityLiters(data.capacityLiters.liters);
    const dailyDemandLitersPerDay = new DailyWasteDemandLitersPerDay(data.dailyDemandLitersPerDay.litersPerDay);
    const serviceZone = data.serviceZone ? serviceZoneFromString(data.serviceZone) : undefined;

    return new Container(name, loc, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone, id);
  }
}
