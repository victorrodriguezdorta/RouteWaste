import type { CreateContainerCommand } from '@/application/model/container-management/create-container/create-container-command';

/**
 * ContainerPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Container.
 * Contains primitive types only so it can be serialized directly.
 */
export class ContainerPostJsonRequest {
  /** Human-readable name for the container. */
  name: string;

  /**
   * Geographic location of the container including latitude, longitude, postal address, and GIS reference.
   */
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /**
   * Type of waste the container is designated for.
   */
  wasteType: string;

  /**
   * Maximum capacity of the container in liters.
   */
  capacityLiters: { liters: number };

  /**
   * Approximate daily waste demand in liters per day.
   */
  dailyDemandLitersPerDay: { litersPerDay: number };

  /**
   * Service zone identifier for the container (optional).
   */
  serviceZone?: string | null;

  constructor(
    name: string,
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    wasteType: string,
    capacityLiters: { liters: number },
    dailyDemandLitersPerDay: { litersPerDay: number },
    serviceZone?: string | null
  ) {
    this.name = name;
    this.location = location;
    this.wasteType = wasteType;
    this.capacityLiters = capacityLiters;
    this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
    this.serviceZone = serviceZone ?? null;
  }

  /**
   * Map a `CreateContainerCommand` (domain input) into this DTO.
   * Extracts primitive values from `Location`, `ContainerCapacityLiters`, and `DailyWasteDemandLitersPerDay` value objects.
   * @param data The CreateContainerCommand containing domain objects to convert.
   * @returns A new ContainerPostJsonRequest instance with primitive values for serialization.
   */
  public static toRequest(data: CreateContainerCommand): ContainerPostJsonRequest {
    return new ContainerPostJsonRequest(
      data.name.getValue(),
      {
        latitude: data.location.latitude,
        longitude: data.location.longitude,
        postalAddress: data.location.postalAddress,
        gisReference: data.location.gisReference,
      },
      data.wasteType,
      {
        liters: data.capacityLiters.getLiters(),
      },
      {
        litersPerDay: data.dailyDemandLitersPerDay.getLitersPerDay(),
      },
      data.serviceZone ?? null
    );
  }
}
