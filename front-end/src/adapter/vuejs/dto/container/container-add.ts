import { Container } from '@/domain/entity/container';
import { ServiceZone, serviceZoneFromString } from '@/domain/enumerate/service-zone';
import { TimeUnit, timeUnitFromString } from '@/domain/enumerate/time-unit';
import { WasteType, wasteTypeFromString } from '@/domain/enumerate/waste-type';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import { Location } from '@/domain/valueobject/location/location';

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
   * Type of waste collected by this container (e.g., ORGANIC, PACKAGING, GLASS).
   */
  public wasteType: string;

  /**
   * Numeric waste demand value (must be >= 0).
   */
  public wasteDemandValue: number;

  /**
   * Unit of measurement for waste demand (e.g., 'tons', 'kg').
   */
  public wasteDemandQuantityUnit: string;

  /**
   * Time unit for waste demand rate (e.g., DAY, WEEK, MONTH, YEAR).
   */
  public wasteDemandTimeUnit: string;

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
   * @param wasteType Type of waste
   * @param wasteDemandValue Numeric demand value
   * @param wasteDemandQuantityUnit Unit of measurement for demand
   * @param wasteDemandTimeUnit Time unit for demand rate
   * @param serviceZone Optional service zone identifier
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    latitude: number,
    longitude: number,
    postalAddress: string,
    gisReference: string,
    wasteType: string,
    wasteDemandValue: number,
    wasteDemandQuantityUnit: string,
    wasteDemandTimeUnit: string,
    serviceZone?: string
  ) {
    this.validate<number>(latitude, 'Latitude is not defined');
    this.validate<number>(longitude, 'Longitude is not defined');
    this.validate<string>(postalAddress, 'Postal address is not defined');
    this.validate<string>(gisReference, 'GIS reference is not defined');
    this.validate<string>(wasteType, 'Waste type is not defined');
    this.validate<number>(wasteDemandValue, 'Waste demand value is not defined');
    this.validate<string>(wasteDemandQuantityUnit, 'Waste demand quantity unit is not defined');
    this.validate<string>(wasteDemandTimeUnit, 'Waste demand time unit is not defined');

    this.latitude = latitude;
    this.longitude = longitude;
    this.postalAddress = postalAddress;
    this.gisReference = gisReference;
    this.wasteType = wasteType;
    this.wasteDemandValue = wasteDemandValue;
    this.wasteDemandQuantityUnit = wasteDemandQuantityUnit;
    this.wasteDemandTimeUnit = wasteDemandTimeUnit;
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
      const POSTAL_REGEX = /^[A-Za-z0-9\s,.-]+$/;
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
   * Validate waste demand value for form fields.
   * 
   * @param value Waste demand value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateWasteDemandValue(value: number): boolean | string {
    try {
      if (value < 0) {
        throw new Error('Waste demand cannot be negative');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate waste demand quantity unit for form fields.
   * 
   * @param value Quantity unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateWasteDemandQuantityUnit(value: string): boolean | string {
    try {
      new QuantityUnit(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate waste demand time unit for form fields.
   * 
   * @param value Time unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateWasteDemandTimeUnit(value: string): boolean | string {
    try {
      timeUnitFromString(value);
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
    const randomDemandValue = parseFloat((Math.random() * 100).toFixed(2));
    const randomQuantityUnit = 'tons';
    const randomTimeUnit = TimeUnit.DAY;
    const serviceZones = [ServiceZone.NEIGHBORHOOD, ServiceZone.DISTRICT, ServiceZone.GEOGRAPHICAL_AREA];
    const randomServiceZone = Math.random() > 0.3 ? (serviceZones[Math.floor(Math.random() * serviceZones.length)] as ServiceZone) : undefined;

    return new ContainerAdd(
      randomLatitude,
      randomLongitude,
      randomPostalAddress,
      randomGisReference,
      randomWasteType as string,
      randomDemandValue,
      randomQuantityUnit,
      randomTimeUnit,
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
    const location = new Location(
      containerAdd.latitude,
      containerAdd.longitude,
      containerAdd.postalAddress,
      containerAdd.gisReference
    );
    const wasteType = wasteTypeFromString(containerAdd.wasteType);
    const quantityUnit = new QuantityUnit(containerAdd.wasteDemandQuantityUnit);
    const timeUnit = timeUnitFromString(containerAdd.wasteDemandTimeUnit);
    const wasteDemand = new WasteDemand(containerAdd.wasteDemandValue, quantityUnit, timeUnit);
    const serviceZone = containerAdd.serviceZone ? serviceZoneFromString(containerAdd.serviceZone) : null;

    return new Container(location, wasteType, wasteDemand, serviceZone);
  }
}
