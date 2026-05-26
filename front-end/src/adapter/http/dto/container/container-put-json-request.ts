import type { UpdateContainerCommand } from '@/application/model/container-management/update-container/update-container-command';

/**
 * ContainerPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating a Container.
 * All fields are optional to allow partial updates; primitives are used for
 * straightforward serialization.
 */
export class ContainerPutJsonRequest {
  /** Human-readable name for the container */
  name?: string;

  /** Geographic location of the container including coordinates and address information */
  location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /** Type of waste that the container holds */
  wasteType?: string;

  /** Maximum capacity of the container in liters */
  capacityLiters?: { liters: number };

  /** Approximate daily waste demand in liters per day */
  dailyDemandLitersPerDay?: { litersPerDay: number };

  /** Service zone identifier for the container or null if not assigned */
  serviceZone?: string | null;

  constructor(
    name?: string,
    location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    wasteType?: string,
    capacityLiters?: { liters: number },
    dailyDemandLitersPerDay?: { litersPerDay: number },
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
   * Map an `UpdateContainerCommand` (domain partial update) into this DTO.
   *
   * @param data The UpdateContainerCommand containing the fields to update
   * @returns A new ContainerPutJsonRequest instance with the mapped data
   */
  public static toRequest(data: UpdateContainerCommand): ContainerPutJsonRequest {
    const f = data.updatedFields;
    return new ContainerPutJsonRequest(
      f.name?.getValue(),
      f.location
        ? {
            latitude: f.location.latitude,
            longitude: f.location.longitude,
            postalAddress: f.location.postalAddress,
            gisReference: f.location.gisReference,
          }
        : undefined,
      f.wasteType,
      f.capacityLiters
        ? {
            liters: f.capacityLiters.getLiters(),
          }
        : undefined,
      f.dailyDemandLitersPerDay
        ? {
            litersPerDay: f.dailyDemandLitersPerDay.getLitersPerDay(),
          }
        : undefined,
      f.serviceZone ?? null
    );
  }
}
