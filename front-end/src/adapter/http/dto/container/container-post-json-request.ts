import type { CreateContainerCommand } from '@/application/usecase/container-management/create-container/create-container-command';

/**
 * ContainerPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Container.
 * Contains primitive types only so it can be serialized directly.
 */
export class ContainerPostJsonRequest {
  /**
   * Geographic location of the container including latitude, longitude, postal address, and GIS reference.
   */
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /**
   * Type of waste the container is designated for.
   */
  wasteType: string;

  /**
   * Waste demand specification including quantity value, unit, and time unit.
   */
  wasteDemand: { value: number; quantityUnit: string; timeUnit: string };

  /**
   * Service zone identifier for the container (optional).
   */
  serviceZone?: string | null;

  constructor(
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    wasteType: string,
    wasteDemand: { value: number; quantityUnit: string; timeUnit: string },
    serviceZone?: string | null
  ) {
    this.location = location;
    this.wasteType = wasteType;
    this.wasteDemand = wasteDemand;
    this.serviceZone = serviceZone ?? null;
  }

  /**
   * Map a `CreateContainerCommand` (domain input) into this DTO.
   * Extracts primitive values from `Location` and `WasteDemand` value objects.
   * @param data The CreateContainerCommand containing domain objects to convert.
   * @returns A new ContainerPostJsonRequest instance with primitive values for serialization.
   */
  public static toRequest(data: CreateContainerCommand): ContainerPostJsonRequest {
    return new ContainerPostJsonRequest(
      {
        latitude: data.location.latitude,
        longitude: data.location.longitude,
        postalAddress: data.location.postalAddress,
        gisReference: data.location.gisReference,
      },
      data.wasteType,
      {
        value: data.wasteDemand.getValue(),
        quantityUnit: data.wasteDemand.getQuantityUnit().getValue(),
        timeUnit: data.wasteDemand.getTimeUnit().toString(),
      },
      data.serviceZone ?? null
    );
  }
}
