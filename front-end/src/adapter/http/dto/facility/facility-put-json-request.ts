import type { UpdateFacilityCommand } from '@/application/model/facility-management/update-facility/update-facility-command';

/**
 * FacilityPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating a Facility.
 * All fields are optional to allow partial updates. VO fields use the structure
 * that matches backend VOs for proper Jackson deserialization.
 */
export class FacilityPutJsonRequest {

  /**
   * Human-readable facility name (optional).
   */
  name?: string;

  /**
   * Type of facility (optional).
   */
  facilityType?: string;

  /**
   * Geographic location of the facility (optional).
   */
  location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /**
   * Storage capacity value object (optional) - matches backend's StorageCapacityKilograms structure.
   */
  storageCapacity?: { value: number };

  /**
   * Processing capacity value object (optional) - matches backend's ProcessingCapacityKilogramsPerDay structure.
   */
  processingCapacity?: { value: number };

  /**
   * Unloading time value object (optional) - matches backend's UnloadingTime structure.
   */
  unloadingTime?: { timeValue: number };

  /**
   * Opening fixed cost with amount and currency (optional).
   */
  openingFixedCost?: { amount: number; currency: string };

  /**
   * Status of the facility (optional).
   */
  status?: string;

  constructor(
    name?: string,
    facilityType?: string,
    location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    storageCapacity?: { value: number },
    processingCapacity?: { value: number },
    unloadingTime?: { timeValue: number },
    openingFixedCost?: { amount: number; currency: string },
    status?: string
  ) {
    this.name = name;
    this.facilityType = facilityType;
    this.location = location;
    this.storageCapacity = storageCapacity;
    this.processingCapacity = processingCapacity;
    this.unloadingTime = unloadingTime;
    this.openingFixedCost = openingFixedCost;
    this.status = status;
  }

  /**
   * Map an `UpdateFacilityCommand` (domain partial update) into this DTO.
   *
   * @param data The UpdateFacilityCommand containing the fields to update
   * @returns A new FacilityPutJsonRequest instance with the mapped data
   */
  public static toRequest(data: UpdateFacilityCommand): FacilityPutJsonRequest {
    const f = data.updatedFields;
    return new FacilityPutJsonRequest(
      f.name?.getValue(),
      f.facilityType,
      f.location
        ? {
            latitude: f.location.latitude,
            longitude: f.location.longitude,
            postalAddress: f.location.postalAddress,
            gisReference: f.location.gisReference,
          }
        : undefined,
      // Wrap in backend VO structure
      f.storageCapacity
        ? { value: f.storageCapacity.getKilograms() }
        : undefined,
      // Wrap in backend VO structure
      f.processingCapacity
        ? { value: f.processingCapacity.getKilogramsPerDay() }
        : undefined,
      // Wrap in backend VO structure
      f.unloadingTime
        ? { timeValue: f.unloadingTime.getMinutes() }
        : undefined,
      f.openingFixedCost
        ? {
            amount: f.openingFixedCost.getAmount(),
            currency: f.openingFixedCost.getCurrency().getCode(),
          }
        : undefined,
      f.status
    );
  }
}
