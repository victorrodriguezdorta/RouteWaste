import type { CreateFacilityCommand } from '../../../../application/usecase/facility-management/create-facility/create-facility-command';

/**
 * FacilityPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Facility.
 * Uses primitive types for all fields so it can be serialized directly.
 */
export class FacilityPostJsonRequest {
  facilityType: string;
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };
  capacity: { value: number; quantityUnit: string; timeUnit: string };
  openingFixedCost: { amount: number; currency?: string };
  status: string;

  constructor(
    facilityType: string,
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    capacity: { value: number; quantityUnit: string; timeUnit: string },
    openingFixedCost: { amount: number; currency?: string },
    status: string
  ) {
    this.facilityType = facilityType;
    this.location = location;
    this.capacity = capacity;
    this.openingFixedCost = openingFixedCost;
    this.status = status;
  }

  /**
   * Map a `CreateFacilityCommand` (domain input) into this DTO.
   */
  public static toRequest(data: CreateFacilityCommand): FacilityPostJsonRequest {
    return new FacilityPostJsonRequest(
      data.facilityType,
      {
        latitude: data.location.latitude,
        longitude: data.location.longitude,
        postalAddress: data.location.postalAddress,
        gisReference: data.location.gisReference,
      },
      {
        value: data.capacity.getValue(),
        quantityUnit: data.capacity.getQuantityUnit().getValue(),
        timeUnit: data.capacity.getTimeUnit().toString(),
      },
      {
        amount: data.openingFixedCost.getAmount(),
        currency: data.openingFixedCost.getCurrency().getCode(),
      },
      data.status
    );
  }
}
