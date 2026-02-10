import type { CreateContainerCommand } from '../../../../application/usecase/container-management/create-container/create-container-command';

/**
 * ContainerPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Container.
 * Contains primitive types only so it can be serialized directly.
 */
export class ContainerPostJsonRequest {
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };
  wasteType: string;
  wasteDemand: { value: number; quantityUnit: string; timeUnit: string };
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
