import type { UpdateFacilityCommand } from '@/application/usecase/facility-management/update-facility/update-facility-command';

/**
 * FacilityPutJsonRequest DTO
 *
 * Represents the JSON body sent to the backend when updating a Facility.
 * All fields are optional to allow partial updates; primitives are used for
 * straightforward serialization.
 */
export class FacilityPutJsonRequest {

  /**
   * Tipo de instalación (opcional).
   */
  facilityType?: string;

  /**
   * Ubicación de la instalación (opcional).
   */
  location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string };

  /**
   * Capacidad de la instalación (opcional).
   */
  capacity?: { value: number; quantityUnit: string; timeUnit: string };

  /**
   * Coste fijo de apertura (opcional).
   */
  openingFixedCost?: { amount: number; currency?: string };

  /**
   * Estado de la instalación (opcional).
   */
  status?: string;

  constructor(
    facilityType?: string,
    location?: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    capacity?: { value: number; quantityUnit: string; timeUnit: string },
    openingFixedCost?: { amount: number; currency?: string },
    status?: string
  ) {
    this.facilityType = facilityType;
    this.location = location;
    this.capacity = capacity;
    this.openingFixedCost = openingFixedCost;
    this.status = status;
  }

  /**
   * Mapea un `UpdateFacilityCommand` (actualización parcial de dominio) a este DTO.
   * @param data Comando de actualización de instalación.
   * @returns Instancia de FacilityPutJsonRequest con los campos actualizados.
   */
  public static toRequest(data: UpdateFacilityCommand): FacilityPutJsonRequest {
    const f = data.updatedFields;
    return new FacilityPutJsonRequest(
      f.facilityType,
      f.location
        ? {
            latitude: f.location.latitude,
            longitude: f.location.longitude,
            postalAddress: f.location.postalAddress,
            gisReference: f.location.gisReference,
          }
        : undefined,
      f.capacity
        ? { value: f.capacity.getValue(), quantityUnit: f.capacity.getQuantityUnit().getValue(), timeUnit: f.capacity.getTimeUnit().toString() }
        : undefined,
      f.openingFixedCost ? { amount: f.openingFixedCost.getAmount(), currency: f.openingFixedCost.getCurrency().getCode() } : undefined,
      f.status
    );
  }
}
