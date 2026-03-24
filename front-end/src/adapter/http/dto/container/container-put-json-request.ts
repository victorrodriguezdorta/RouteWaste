import type { UpdateContainerCommand } from '@/application/usecase/container-management/update-container/update-container-command';

/**
 * ContainerPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating a Container.
 * All fields are optional to allow partial updates; primitives are used for
 * straightforward serialization.
 */
export class ContainerPutJsonRequest {
  /** Geographic location of the container including coordinates and address information */
  location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /** Type of waste that the container holds */
  wasteType?: string;

  /** Demand specification for waste management including value, quantity unit, and time unit */
  wasteDemand?: { value: number; quantityUnit: string; timeUnit: string };

  /** Service zone identifier for the container or null if not assigned */
  serviceZone?: string | null;

  constructor(
    location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    wasteType?: string,
    wasteDemand?: { value: number; quantityUnit: string; timeUnit: string },
    serviceZone?: string | null
  ) {
    this.location = location;
    this.wasteType = wasteType;
    this.wasteDemand = wasteDemand;
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
      f.location
        ? {
            latitude: f.location.latitude,
            longitude: f.location.longitude,
            postalAddress: f.location.postalAddress,
            gisReference: f.location.gisReference,
          }
        : undefined,
      f.wasteType,
      f.wasteDemand
        ? {
            value: f.wasteDemand.getValue(),
            quantityUnit: f.wasteDemand.getQuantityUnit().getValue(),
            timeUnit: f.wasteDemand.getTimeUnit().toString(),
          }
        : undefined,
      f.serviceZone ?? null
    );
  }
}
