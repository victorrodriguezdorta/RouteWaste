import type { CreateFacilityCommand } from '@/application/model/facility-management/create-facility/create-facility-command';

/**
 * FacilityPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Facility.
 * Contains VO objects that match the backend's expected structure for JSON deserialization.
 */
export class FacilityPostJsonRequest {
  /**
   * Nombre de la instalación.
   */
  name: string;

  /**
   * Tipo de la instalación.
   */
  facilityType: string;

  /**
   * Ubicación de la instalación.
   */
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /**
   * Storage capacity value object - matches backend's StorageCapacityKilograms structure.
   * Contains the `value` property that Jackson deserializes to the backend VO.
   */
  storageCapacity: { value: number };

  /**
   * Processing capacity value object - matches backend's ProcessingCapacityKilogramsPerDay structure.
   * Contains the `value` property that Jackson deserializes to the backend VO.
   */
  processingCapacity: { value: number };

  /**
   * Unloading time value object - matches backend's UnloadingTime structure.
   * Contains the `timeValue` property that Jackson deserializes to the backend VO.
   */
  unloadingTime: { timeValue: number };

  /**
   * Coste fijo de apertura.
   */
  openingFixedCost: { amount: number; currency?: string };

  /**
   * Estado de la instalación.
   */
  status: string;

  constructor(
    name: string,
    facilityType: string,
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    storageCapacity: { value: number },
    processingCapacity: { value: number },
    unloadingTime: { timeValue: number },
    openingFixedCost: { amount: number; currency?: string },
    status: string
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
   * Map a `CreateFacilityCommand` (domain input) into this DTO.
   * Extracts values from VOs and wraps them in the VO structure that matches the backend.
   * @param data The CreateFacilityCommand containing domain objects to convert.
   * @returns A new FacilityPostJsonRequest instance with VO structures.
   */
  public static toRequest(data: CreateFacilityCommand): FacilityPostJsonRequest {
    return new FacilityPostJsonRequest(
      data.name.getValue(),
      data.facilityType,
      {
        latitude: data.location.latitude,
        longitude: data.location.longitude,
        postalAddress: data.location.postalAddress,
        gisReference: data.location.gisReference,
      },
      // Wrap in backend VO structure
      { value: data.storageCapacity.getKilograms() },
      // Wrap in backend VO structure
      { value: data.processingCapacity.getKilogramsPerDay() },
      // Wrap in backend VO structure
      { timeValue: data.unloadingTime.getMinutes() },
      {
        amount: data.openingFixedCost.amount,
        currency: data.openingFixedCost.currency.getCode(),
      },
      data.status
    );
  }
}
