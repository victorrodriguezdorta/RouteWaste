import type { UpdateContainerCommand } from '../../../../application/usecase/container-management/update-container/update-container-command';

/**
 * ContainerPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating a Container.
 * All fields are optional to allow partial updates; primitives are used for
 * straightforward serialization.
 */
export class ContainerPutJsonRequest {
  location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string };
  wasteType?: string;
  wasteDemand?: { value: number; quantityUnit: string; timeUnit: string };
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
