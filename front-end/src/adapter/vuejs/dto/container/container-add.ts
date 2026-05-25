import { Container } from '@/domain/entity/container';
import { ServiceZone, serviceZoneFromString } from '@/domain/enumerate/service-zone';
import { WasteType, wasteTypeFromString } from '@/domain/enumerate/waste-type';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';

/**
 * ContainerAdd
 * 
 * Data Transfer Object for adding a new Container entity.
 * 
 * This DTO is designed to be used in Vue.js forms for creating new waste containers.
 * It contains primitive types that can be easily bound to v-model directives
 * and includes validation methods for form field validation.
 * 
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class ContainerAdd {
  /**
   * Latitude coordinate of the container location (-90 to 90).
   */
  public latitude: number;

  /**
   * Longitude coordinate of the container location (-180 to 180).
   */
  public longitude: number;

  /**
   * Postal address of the container location.
   */
  public postalAddress: string;

  /**
   * GIS reference identifier for the location.
   */
  public gisReference: string;

  /**
   * Human-readable name for the container.
   */
  public name: string;

  /**
   * Type of waste collected by this container (e.g., ORGANIC, PACKAGING, GLASS).
   */
  public wasteType: string;

  /**
   * Container capacity in liters (must be > 0).
   */
  public capacityLiters: number;

  /**
   * Daily waste demand in liters per day (must be > 0).
   */
  public dailyDemandLitersPerDay: number;

  /**
   * Service zone identifier (e.g., NEIGHBORHOOD, DISTRICT). Optional attribute.
   */
  public serviceZone?: string;

  /**
   * Create a new ContainerAdd DTO.
   * 
   * @param latitude Latitude coordinate
   * @param longitude Longitude coordinate
   * @param postalAddress Postal address
   * @param gisReference GIS reference
   * @param name Human-readable name
   * @param wasteType Type of waste
   * @param capacityLiters Container capacity in liters
   * @param dailyDemandLitersPerDay Daily waste demand in liters per day
   * @param serviceZone Optional service zone identifier
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    latitude: number,
    longitude: number,
    postalAddress: string,
    gisReference: string,
    name: string,
    wasteType: string,
    capacityLiters: number,
    dailyDemandLitersPerDay: number,
    serviceZone?: string
  ) {
    this.validate<number>(latitude, 'Latitude is not defined');
    this.validate<number>(longitude, 'Longitude is not defined');
    this.validate<string>(postalAddress, 'Postal address is not defined');
    this.validate<string>(gisReference, 'GIS reference is not defined');
    this.validate<string>(name, 'Name is not defined');
    this.validate<string>(wasteType, 'Waste type is not defined');
    this.validate<number>(capacityLiters, 'Capacity in liters is not defined');
    this.validate<number>(dailyDemandLitersPerDay, 'Daily demand in liters per day is not defined');

    this.latitude = latitude;
    this.longitude = longitude;
    this.postalAddress = postalAddress;
    this.gisReference = gisReference;
    this.name = name;
    this.wasteType = wasteType;
    this.capacityLiters = capacityLiters;
    this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
    this.serviceZone = serviceZone;
  }

  /**
   * Validate that an attribute is defined (not null or undefined).
   * 
   * @param attribute Attribute to validate
   * @param errorMessage Error message to throw if validation fails
   * @throws Error with the provided message if attribute is null or undefined
   */
  private validate<T>(
    attribute: T | undefined,
    errorMessage: string
  ): asserts attribute is T {
    if (attribute === undefined || attribute === null) {
      throw new Error(errorMessage);
    }
  }

  /**
   * Validate latitude coordinate for form fields.
   * 
   * @param value Latitude value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateLatitude(value: number): boolean | string {
    try {
      if (value < -90 || value > 90) {
        throw new Error('Latitude must be between -90 and 90');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate longitude coordinate for form fields.
   * 
   * @param value Longitude value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateLongitude(value: number): boolean | string {
    try {
      if (value < -180 || value > 180) {
        throw new Error('Longitude must be between -180 and 180');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate postal address for form fields.
   * 
   * @param value Postal address string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidatePostalAddress(value: string): boolean | string {
    try {
      const POSTAL_MAX = 150;
      const POSTAL_REGEX = /^[\p{L}\p{N}\s,.-]+$/u;
      if (!value || value.length === 0) {
        throw new Error('Postal address cannot be empty');
      }
      if (value.length > POSTAL_MAX) {
        throw new Error(`Postal address must be at most ${POSTAL_MAX} characters`);
      }
      if (!POSTAL_REGEX.test(value)) {
        throw new Error('Postal address format is invalid');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate GIS reference for form fields.
   * 
   * @param value GIS reference string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateGISReference(value: string): boolean | string {
    try {
      const GIS_MAX = 100;
      if (!value || value.length === 0) {
        throw new Error('GIS reference cannot be empty');
      }
      if (value.length > GIS_MAX) {
        throw new Error(`GIS reference must be at most ${GIS_MAX} characters`);
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate container name for form fields.
   *
   * @param value Container name string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateName(value: string): boolean | string {
    try {
      new Name(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate waste type for form fields.
   * 
   * @param value Waste type string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateWasteType(value: string): boolean | string {
    try {
      wasteTypeFromString(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate container capacity liters for form fields.
   * 
   * @param value Capacity value in liters to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityLiters(value: number): boolean | string {
    try {
      if (value <= 0) {
        throw new Error('Container capacity must be greater than 0 liters');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate daily waste demand liters per day for form fields.
   * 
   * @param value Daily demand value in liters/day to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateDailyDemandLitersPerDay(value: number): boolean | string {
    try {
      if (value <= 0) {
        throw new Error('Daily waste demand must be greater than 0 liters/day');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate service zone for form fields.
   * 
   * @param value Service zone string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateServiceZone(value: string): boolean | string {
    try {
      if (!value) return true; // Optional field
      serviceZoneFromString(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Generate a random ContainerAdd instance for testing purposes.
   * 
   * @returns A new ContainerAdd instance with random valid values
   */
  static random(): ContainerAdd {
    const randomLatitude = parseFloat((Math.random() * 180 - 90).toFixed(6));
    const randomLongitude = parseFloat((Math.random() * 360 - 180).toFixed(6));
    const randomPostalAddress = `${Math.floor(Math.random() * 1000)} Main St, City`;
    const randomGisReference = `GIS-${Math.floor(Math.random() * 100000)}`;
    const wasteTypes = [WasteType.ORGANIC, WasteType.PACKAGING, WasteType.PAPER_CARDBOARD, WasteType.GLASS, WasteType.RESIDUAL];
    const randomWasteType = wasteTypes[Math.floor(Math.random() * wasteTypes.length)] as WasteType;
    const randomCapacityLiters = parseFloat((Math.random() * 500 + 50).toFixed(2)); // 50-550 liters
    const randomDailyDemandLitersPerDay = parseFloat((Math.random() * 100 + 10).toFixed(2)); // 10-110 liters/day
    const serviceZones = [ServiceZone.NEIGHBORHOOD, ServiceZone.DISTRICT, ServiceZone.GEOGRAPHICAL_AREA];
    const randomServiceZone = Math.random() > 0.3 ? (serviceZones[Math.floor(Math.random() * serviceZones.length)] as ServiceZone) : undefined;

    return new ContainerAdd(
      randomLatitude,
      randomLongitude,
      randomPostalAddress,
      randomGisReference,
      `Container ${Math.floor(Math.random() * 10000)}`,
      randomWasteType as string,
      randomCapacityLiters,
      randomDailyDemandLitersPerDay,
      randomServiceZone as string | undefined
    );
  }

  /**
   * Convert this DTO to a Container domain entity.
   * 
   * @param containerAdd ContainerAdd DTO instance
   * @returns A new Container domain entity
   * @throws Error if any value object validation fails
   */
  static toContainer(containerAdd: ContainerAdd): Container {
    const name = new Name(containerAdd.name);
    const location = new Location(
      containerAdd.latitude,
      containerAdd.longitude,
      containerAdd.postalAddress,
      containerAdd.gisReference
    );
    const wasteType = wasteTypeFromString(containerAdd.wasteType);
    const capacityLiters = new ContainerCapacityLiters(containerAdd.capacityLiters);
    const dailyDemandLitersPerDay = new DailyWasteDemandLitersPerDay(containerAdd.dailyDemandLitersPerDay);
    const serviceZone = containerAdd.serviceZone ? serviceZoneFromString(containerAdd.serviceZone) : null;

    return new Container(name, location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone);
  }
}
