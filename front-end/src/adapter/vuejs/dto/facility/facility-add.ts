import { Facility } from '../../../../domain/entity/facility';
import { FacilityStatus, facilityStatusFromString } from '../../../../domain/enumerate/facility-status';
import { FacilityType, facilityTypeFromString } from '../../../../domain/enumerate/facility-type';
import { TimeUnit, timeUnitFromString } from '../../../../domain/enumerate/time-unit';
import { Currency } from '../../../../domain/valueobject/cost/currency';
import { OpeningFixedCost } from '../../../../domain/valueobject/cost/opening-fixed-cost';
import { Capacity } from '../../../../domain/valueobject/demand/capacity';
import { QuantityUnit } from '../../../../domain/valueobject/demand/quantity-unit';
import { Location } from '../../../../domain/valueobject/location/location';

/**
 * FacilityAdd
 * 
 * Data Transfer Object for adding a new Facility entity.
 * 
 * This DTO is designed to be used in Vue.js forms for creating new waste management facilities.
 * It contains primitive types that can be easily bound to v-model directives
 * and includes validation methods for form field validation.
 * 
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class FacilityAdd {
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
   * Numeric capacity value (must be >= 0).
   */
  public capacityValue: number;

  /**
   * Unit of measurement for capacity (e.g., 'tons', 'kg').
   */
  public capacityQuantityUnit: string;

  /**
   * Time unit for capacity rate (e.g., DAY, WEEK, MONTH, YEAR).
   */
  public capacityTimeUnit: string;

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
   * Create a new FacilityAdd DTO.
   * 
   * @param facilityType Type of the facility
   * @param latitude Latitude coordinate
   * @param longitude Longitude coordinate
   * @param postalAddress Postal address
   * @param gisReference GIS reference
   * @param capacityValue Numeric capacity value
   * @param capacityQuantityUnit Unit of measurement for capacity
   * @param capacityTimeUnit Time unit for capacity rate
   * @param openingFixedCost Opening fixed cost amount
   * @param currencyCode ISO 4217 currency code
   * @param status Current facility status
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    facilityType: string,
    latitude: number,
    longitude: number,
    postalAddress: string,
    gisReference: string,
    capacityValue: number,
    capacityQuantityUnit: string,
    capacityTimeUnit: string,
    openingFixedCost: number,
    currencyCode: string,
    status: string
  ) {
    this.validate<string>(facilityType, 'Facility type is not defined');
    this.validate<number>(latitude, 'Latitude is not defined');
    this.validate<number>(longitude, 'Longitude is not defined');
    this.validate<string>(postalAddress, 'Postal address is not defined');
    this.validate<string>(gisReference, 'GIS reference is not defined');
    this.validate<number>(capacityValue, 'Capacity value is not defined');
    this.validate<string>(capacityQuantityUnit, 'Capacity quantity unit is not defined');
    this.validate<string>(capacityTimeUnit, 'Capacity time unit is not defined');
    this.validate<number>(openingFixedCost, 'Opening fixed cost is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');
    this.validate<string>(status, 'Facility status is not defined');

    this.facilityType = facilityType;
    this.latitude = latitude;
    this.longitude = longitude;
    this.postalAddress = postalAddress;
    this.gisReference = gisReference;
    this.capacityValue = capacityValue;
    this.capacityQuantityUnit = capacityQuantityUnit;
    this.capacityTimeUnit = capacityTimeUnit;
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
   * Generate a random FacilityAdd instance for testing purposes.
   * 
   * @returns A new FacilityAdd instance with random valid values
   */
  static random(): FacilityAdd {
    const facilityTypes = [FacilityType.OPERATIONAL_BASE, FacilityType.TRANSFER_STATION, FacilityType.TREATMENT_PLANT];
    const randomFacilityType = facilityTypes[Math.floor(Math.random() * facilityTypes.length)] as FacilityType;
    const randomLatitude = parseFloat((Math.random() * 180 - 90).toFixed(6));
    const randomLongitude = parseFloat((Math.random() * 360 - 180).toFixed(6));
    const randomPostalAddress = `${Math.floor(Math.random() * 1000)} Industrial Park, City`;
    const randomGisReference = `GIS-FAC-${Math.floor(Math.random() * 100000)}`;
    const randomCapacityValue = Math.floor(Math.random() * 1000) + 100;
    const randomQuantityUnit = 'tons';
    const randomTimeUnit = TimeUnit.DAY;
    const randomOpeningFixedCost = parseFloat((Math.random() * 100000 + 10000).toFixed(2));
    const randomCurrency = 'EUR';
    const statuses = [FacilityStatus.CANDIDATE, FacilityStatus.PLANNED, FacilityStatus.OPEN, FacilityStatus.DISCARDED];
    const randomStatus = statuses[Math.floor(Math.random() * statuses.length)] as FacilityStatus;

    return new FacilityAdd(
      randomFacilityType as string,
      randomLatitude,
      randomLongitude,
      randomPostalAddress,
      randomGisReference,
      randomCapacityValue,
      randomQuantityUnit,
      randomTimeUnit,
      randomOpeningFixedCost,
      randomCurrency,
      randomStatus as string
    );
  }

  /**
   * Convert this DTO to a Facility domain entity.
   * 
   * @param facilityAdd FacilityAdd DTO instance
   * @returns A new Facility domain entity
   * @throws Error if any value object validation fails
   */
  static toFacility(facilityAdd: FacilityAdd): Facility {
    const facilityType = facilityTypeFromString(facilityAdd.facilityType);
    const location = new Location(
      facilityAdd.latitude,
      facilityAdd.longitude,
      facilityAdd.postalAddress,
      facilityAdd.gisReference
    );
    const quantityUnit = new QuantityUnit(facilityAdd.capacityQuantityUnit);
    const timeUnit = timeUnitFromString(facilityAdd.capacityTimeUnit);
    const capacity = new Capacity(facilityAdd.capacityValue, quantityUnit, timeUnit);
    const openingFixedCost = new OpeningFixedCost(facilityAdd.openingFixedCost, facilityAdd.currencyCode);
    const status = facilityStatusFromString(facilityAdd.status);

    return new Facility(facilityType, location, capacity, openingFixedCost, status);
  }
}
