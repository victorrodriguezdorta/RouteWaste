import type { CreateFacilityCommand } from '@/application/usecase/facility-management/create-facility/create-facility-command';

/**
 * FacilityPostJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when creating a new Facility.
 * Uses primitive types for all fields so it can be serialized directly.
 */
export class FacilityPostJsonRequest {
  /**
   * Tipo de la instalación (facility).
   */
  facilityType: string;

  /**
   * Ubicación de la instalación, incluyendo latitud, longitud, dirección postal y referencia GIS.
   */
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /**
   * Capacidad de la instalación, incluyendo valor, unidad de cantidad y unidad de tiempo.
   */
  capacity: { value: number; quantityUnit: string; timeUnit: string };

  /**
   * Coste fijo de apertura, incluyendo cantidad y moneda.
   */
  openingFixedCost: { amount: number; currency?: string };

  /**
   * Estado de la instalación.
   */
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
   * Mapea un `CreateFacilityCommand` (entrada de dominio) a este DTO.
   * @param data Comando de creación de instalación a mapear.
   * @returns Una instancia de FacilityPostJsonRequest con los datos mapeados.
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
