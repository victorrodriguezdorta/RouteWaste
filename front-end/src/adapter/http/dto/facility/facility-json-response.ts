import { Facility } from '@/domain/entity/facility';
import { facilityStatusFromString } from '@/domain/enumerate/facility-status';
import { facilityTypeFromString } from '@/domain/enumerate/facility-type';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import { UnloadingTime } from '@/domain/valueobject/capacity/unloading-time';
import { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * FacilityJsonResponse DTO
 *
 * Represents the JSON payload returned by the backend API for a Facility
 * resource. Uses primitive fields for serialization and provides a helper
 * `toFacility` to convert the payload into the domain `Facility` entity.
 * 
 * Handles both nested VO structures from backend and flat structures.
 */
export class FacilityJsonResponse {
  /**
   * Identificador único de la instalación.
   */
  id: string;
  /**
   * Tipo de instalación.
   */
  facilityType: string;
  /**
   * Ubicación de la instalación (latitud, longitud, dirección postal, referencia GIS).
   */
  location: { latitude: number; longitude: number; postalAddress: string; gisReference: string };
  /**
   * Capacidad de almacenamiento - puede ser número o objeto con propiedad 'value'.
   */
  storageCapacity: number | { value: number };
  /**
   * Capacidad de procesamiento - puede ser número o objeto con propiedad 'value'.
   */
  processingCapacity: number | { value: number };
  /**
   * Tiempo de descarga - puede ser número o objeto con propiedad 'timeValue'.
   */
  unloadingTime: number | { timeValue: number };
  /**
   * Coste fijo de apertura de la instalación.
   */
  openingFixedCost: { amount: number; currency?: string };
  /**
   * Estado de la instalación.
   */
  status: string;
  /**
   * Nivel de llenado actual - puede ser número u objeto con propiedad 'value' (del backend) o 'wasteDemandValue' (alternativa).
   */
  currentFillingLevel?: number | { value: number } | { wasteDemandValue: number };

  /**
   * Create a new FacilityJsonResponse instance.
   * 
   * @param id Unique facility identifier
   * @param facilityType Type of facility
   * @param location Location object with coordinates and address
   * @param storageCapacity Storage capacity in kilograms (number or VO object)
   * @param processingCapacity Processing capacity in kilograms per day (number or VO object)
   * @param unloadingTime Unloading time in minutes (number or VO object)
   * @param openingFixedCost Opening cost object with amount and currency
   * @param status Facility status
   * @param currentFillingLevel Current filling level in liters per day (number or VO object)
   */
  constructor(
    id: string,
    facilityType: string,
    location: { latitude: number; longitude: number; postalAddress: string; gisReference: string },
    storageCapacity: number | { value: number },
    processingCapacity: number | { value: number },
    unloadingTime: number | { timeValue: number },
    openingFixedCost: { amount: number; currency?: string },
    status: string,
    currentFillingLevel?: number | { value: number } | { wasteDemandValue: number }
  ) {
    this.id = id;
    this.facilityType = facilityType;
    this.location = location;
    this.storageCapacity = storageCapacity;
    this.processingCapacity = processingCapacity;
    this.unloadingTime = unloadingTime;
    this.openingFixedCost = openingFixedCost;
    this.status = status;
    this.currentFillingLevel = currentFillingLevel;
  }

  /**
   * Extract numeric value from storage capacity (handles both formats).
   * @private
   */
  private static extractStorageCapacity(value: number | { value: number }): number {
    return typeof value === 'number' ? value : value.value;
  }

  /**
   * Extract numeric value from processing capacity (handles both formats).
   * @private
   */
  private static extractProcessingCapacity(value: number | { value: number }): number {
    return typeof value === 'number' ? value : value.value;
  }

  /**
   * Extract numeric value from unloading time (handles both formats).
   * @private
   */
  private static extractUnloadingTime(value: number | { timeValue: number }): number {
    return typeof value === 'number' ? value : value.timeValue;
  }

  /**
   * Extract numeric value from filling level (handles both formats).
   * Backend returns {value: number} for currentFillingLevel
   * @private
   */
  private static extractFillingLevel(value?: number | { value: number } | { wasteDemandValue: number }): number {
    if (!value) return 0;
    if (typeof value === 'number') return value;
    // Handle both {value: number} (from backend) and {wasteDemandValue: number} (alternative format)
    return (value as any).value ?? (value as any).wasteDemandValue ?? 0;
  }

  /**
   * Convierte la respuesta JSON en una entidad de dominio `Facility`.
   * Construye los value objects Location, StorageCapacityKilograms, ProcessingCapacityKilogramsPerDay,
   * UnloadingTime y OpeningFixedCost, y parsea los enums de tipo y estado.
   * 
   * Maneja tanto objetos VOs anidados del backend como valores simples.
   * 
   * @param data Objeto FacilityJsonResponse recibido del backend.
   * @returns Instancia de Facility del dominio.
   */
  public static toFacility(data: FacilityJsonResponse): Facility {
    try {
      console.log('[FacilityJsonResponse.toFacility] Starting deserialization:', data);
      
      const id = new UllUUID(data.id);
      const loc = new Location(
        data.location.latitude,
        data.location.longitude,
        data.location.postalAddress,
        data.location.gisReference
      );
      
      // Extract values from both nested VO objects and simple numbers
      const storageCapacityValue = this.extractStorageCapacity(data.storageCapacity);
      const processingCapacityValue = this.extractProcessingCapacity(data.processingCapacity);
      const unloadingTimeValue = this.extractUnloadingTime(data.unloadingTime);
      const fillingLevelValue = this.extractFillingLevel(data.currentFillingLevel);
      
      console.log('[FacilityJsonResponse.toFacility] Extracted values:', { 
        storageCapacityValue, 
        processingCapacityValue, 
        unloadingTimeValue, 
        fillingLevelValue 
      });
      
      const storageCapacity = new StorageCapacityKilograms(storageCapacityValue);
      const processingCapacity = new ProcessingCapacityKilogramsPerDay(processingCapacityValue);
      const unloadingTime = new UnloadingTime(unloadingTimeValue);
      const openingCost = new OpeningFixedCost(data.openingFixedCost.amount, data.openingFixedCost.currency || 'EUR');
      const facilityType = facilityTypeFromString(data.facilityType);
      const status = facilityStatusFromString(data.status);
      const currentFillingLevel = new DailyWasteDemandLitersPerDay(fillingLevelValue);

      console.log('[FacilityJsonResponse.toFacility] Created VOs, constructing Facility');

      const facility = new Facility(
        facilityType,
        loc,
        storageCapacity,
        processingCapacity,
        unloadingTime,
        openingCost,
        status,
        id,
        currentFillingLevel
      );
      
      console.log('[FacilityJsonResponse.toFacility] Successfully created Facility:', facility);
      return facility;
    } catch (error) {
      console.error('[FacilityJsonResponse.toFacility] Error during deserialization:', error, 'Data:', data);
      throw error;
    }
  }
}
