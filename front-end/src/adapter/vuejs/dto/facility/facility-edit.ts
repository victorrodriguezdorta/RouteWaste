import { Facility } from '@/domain/entity/facility';
import { FacilityStatus, facilityStatusFromString } from '@/domain/enumerate/facility-status';
import { FacilityType, facilityTypeFromString } from '@/domain/enumerate/facility-type';
import { timeUnitFromString } from '@/domain/enumerate/time-unit';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import { UnloadingTime } from '@/domain/valueobject/capacity/unloading-time';
import { Currency } from '@/domain/valueobject/cost/currency';
import { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * FacilityEdit
 * 
 * Data Transfer Object for editing an existing Facility entity.
 * 
 * This DTO is designed to be used in Vue.js forms for updating existing waste management facilities.
 * It contains primitive types that can be easily bound to v-model directives
 * and includes validation methods for form field validation.
 * 
 * Unlike FacilityAdd, this DTO includes an 'id' field to identify the facility being edited.
 * 
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class FacilityEdit {
  /**
   * Unique identifier of the facility being edited.
   */
  public id: string;

  /**
   * Human-readable facility name.
   */
  public name: string;

  /**
   * Type of the facility (e.g., OPERATIONAL_BASE, TRANSFER_STATION, TREATMENT_PLANT).
   */
  public facilityType: string;

  /**
   * Latitude coordinate of the facility location (-90 to 90).
   */
  public latitude: number;

  /**
   * Longitude coordinate of the facility location (-180 to 180).
   */
  public longitude: number;

  /**
   * Postal address of the facility location.
   */
  public postalAddress: string;

  /**
   * GIS reference identifier for the location.
   */
  public gisReference: string;

  /**
   * Storage capacity in kilograms (must be >= 0).
   */
  public storageCapacity: number;

  /**
   * Processing capacity in kilograms per day (must be >= 0).
   */
  public processingCapacity: number;

  /**
   * Unloading time in minutes (must be >= 0).
   */
  public unloadingTime: number;

  /**
   * Opening fixed cost amount (must be >= 0).
   */
  public openingFixedCost: number;

  /**
   * ISO 4217 currency code for opening cost (e.g., 'EUR', 'USD').
   */
  public currencyCode: string;

  /**
   * Current status of the facility (e.g., CANDIDATE, PLANNED, OPEN, DISCARDED).
   */
  public status: string;

  /**
   * Create a new FacilityEdit DTO.
   * 
   * @param id Unique identifier of the facility
   * @param name Human-readable name
   * @param facilityType Type of the facility
   * @param latitude Latitude coordinate
   * @param longitude Longitude coordinate
   * @param postalAddress Postal address
   * @param gisReference GIS reference
   * @param storageCapacity Storage capacity in kilograms
   * @param processingCapacity Processing capacity in kilograms per day
   * @param unloadingTime Unloading time in minutes
   * @param openingFixedCost Opening fixed cost amount
   * @param currencyCode ISO 4217 currency code
   * @param status Current facility status
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    id: string,
    name: string,
    facilityType: string,
    latitude: number,
    longitude: number,
    postalAddress: string,
    gisReference: string,
    storageCapacity: number,
    processingCapacity: number,
    unloadingTime: number,
    openingFixedCost: number,
    currencyCode: string,
    status: string
  ) {
    this.validate<string>(id, 'Facility id is not defined');
    this.validate<string>(name, 'Name is not defined');
    this.validate<string>(facilityType, 'Facility type is not defined');
    this.validate<number>(latitude, 'Latitude is not defined');
    this.validate<number>(longitude, 'Longitude is not defined');
    this.validate<string>(postalAddress, 'Postal address is not defined');
    this.validate<string>(gisReference, 'GIS reference is not defined');
    this.validate<number>(storageCapacity, 'Storage capacity is not defined');
    this.validate<number>(processingCapacity, 'Processing capacity is not defined');
    this.validate<number>(unloadingTime, 'Unloading time is not defined');
    this.validate<number>(openingFixedCost, 'Opening fixed cost is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');
    this.validate<string>(status, 'Facility status is not defined');

    this.id = id;
    this.name = name;
    this.facilityType = facilityType;
    this.latitude = latitude;
    this.longitude = longitude;
    this.postalAddress = postalAddress;
    this.gisReference = gisReference;
    this.storageCapacity = storageCapacity;
    this.processingCapacity = processingCapacity;
    this.unloadingTime = unloadingTime;
    this.openingFixedCost = openingFixedCost;
    this.currencyCode = currencyCode;
    this.status = status;
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
   * Validate facility id for form fields.
   * 
   * @param value Facility id string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateId(value: string): boolean | string {
    try {
      new UllUUID(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate facility type for form fields.
   * 
   * @param value Facility type string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateFacilityType(value: string): boolean | string {
    try {
      facilityTypeFromString(value);
      return true;
    } catch (error: any) {
      return error.message;
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
   * Validate capacity value for form fields.
   * 
   * @param value Capacity value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityValue(value: number): boolean | string {
    try {
      if (value < 0) {
        throw new Error('Capacity value must be greater than or equal to 0');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate capacity quantity unit for form fields.
   * 
   * @param value Quantity unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityQuantityUnit(value: string): boolean | string {
    try {
      new QuantityUnit(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate capacity time unit for form fields.
   * 
   * @param value Time unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCapacityTimeUnit(value: string): boolean | string {
    try {
      timeUnitFromString(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate opening fixed cost for form fields.
   * 
   * @param value Opening fixed cost to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateOpeningFixedCost(value: number): boolean | string {
    try {
      new OpeningFixedCost(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate currency code for form fields.
   * 
   * @param value Currency code string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCurrencyCode(value: string): boolean | string {
    try {
      new Currency(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate facility status for form fields.
   * 
   * @param value Facility status string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateStatus(value: string): boolean | string {
    try {
      facilityStatusFromString(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Generate a random FacilityEdit instance for testing purposes.
   * 
   * @returns A new FacilityEdit instance with random valid values
   */
  static random(): FacilityEdit {
    const randomId = UllUUID.random().getValue();
    const facilityTypes = [FacilityType.OPERATIONAL_BASE, FacilityType.TRANSFER_STATION, FacilityType.TREATMENT_PLANT];
    const randomFacilityType = facilityTypes[Math.floor(Math.random() * facilityTypes.length)] as FacilityType;
    const randomLatitude = parseFloat((Math.random() * 180 - 90).toFixed(6));
    const randomLongitude = parseFloat((Math.random() * 360 - 180).toFixed(6));
    const randomPostalAddress = `${Math.floor(Math.random() * 1000)} Industrial Park, City`;
    const randomGisReference = `GIS-FAC-${Math.floor(Math.random() * 100000)}`;
    const randomCapacityValue = Math.floor(Math.random() * 1000) + 100;
    const randomProcessingCapacity = Math.floor(Math.random() * 500) + 50;
    const randomUnloadingTime = Math.floor(Math.random() * 480) + 30; // 30-510 minutes
    const randomOpeningFixedCost = parseFloat((Math.random() * 100000 + 10000).toFixed(2));
    const randomCurrency = 'EUR';
    const statuses = [FacilityStatus.CANDIDATE, FacilityStatus.PLANNED, FacilityStatus.OPEN, FacilityStatus.DISCARDED];
    const randomStatus = statuses[Math.floor(Math.random() * statuses.length)] as FacilityStatus;

    return new FacilityEdit(
      randomId,
      `Facility ${Math.floor(Math.random() * 10000)}`,
      randomFacilityType as string,
      randomLatitude,
      randomLongitude,
      randomPostalAddress,
      randomGisReference,
      randomCapacityValue,
      randomProcessingCapacity,
      randomUnloadingTime,
      randomOpeningFixedCost,
      randomCurrency,
      randomStatus as string
    );
  }

  /**
   * Convert this DTO to a Facility domain entity.
   * 
   * @param facilityEdit FacilityEdit DTO instance
   * @returns A new Facility domain entity with the specified id
   * @throws Error if any value object validation fails
   */
  static toFacility(facilityEdit: FacilityEdit): Facility {
    const name = new Name(facilityEdit.name);
    const facilityType = facilityTypeFromString(facilityEdit.facilityType);
    const location = new Location(
      facilityEdit.latitude,
      facilityEdit.longitude,
      facilityEdit.postalAddress,
      facilityEdit.gisReference
    );
    const storageCapacity = new StorageCapacityKilograms(facilityEdit.storageCapacity);
    const processingCapacity = new ProcessingCapacityKilogramsPerDay(facilityEdit.processingCapacity);
    const unloadingTime = new UnloadingTime(facilityEdit.unloadingTime);
    const openingFixedCost = new OpeningFixedCost(facilityEdit.openingFixedCost, facilityEdit.currencyCode);
    const status = facilityStatusFromString(facilityEdit.status);
    const id = new UllUUID(facilityEdit.id);

    return new Facility(name, facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status, id);
  }

  /**
   * Create a FacilityEdit DTO from a Facility domain entity.
   * 
   * @param facility Facility domain entity
   * @returns A new FacilityEdit DTO with values from the domain entity
   */
  static fromFacility(facility: Facility): FacilityEdit {
    const location = facility.getLocation();
    const storageCapacity = facility.getStorageCapacity();
    const processingCapacity = facility.getProcessingCapacity();
    const unloadingTime = facility.getUnloadingTime();
    const openingFixedCost = facility.getOpeningFixedCost();

    return new FacilityEdit(
      facility.getId().getValue(),
      facility.getName().getValue(),
      facility.getFacilityType(),
      location.latitude,
      location.longitude,
      location.postalAddress,
      location.gisReference,
      storageCapacity.getKilograms(),
      processingCapacity.getKilogramsPerDay(),
      unloadingTime.getMinutes(),
      openingFixedCost.getAmount(),
      openingFixedCost.getCurrency().getCode(),
      facility.getStatus()
    );
  }
}
