import { Facility } from '@/domain/entity/facility';
import { FacilityStatus } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';
import { TimeUnit } from '@/domain/enumerate/time-unit';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * FacilityInfo
 * 
 * Data Transfer Object for displaying Facility entity information.
 * 
 * This DTO is designed to be used in Vue.js views for showing facility details
 * in a read-only format. It contains primitive types that can be easily displayed
 * in templates without requiring complex object navigation.
 * 
 * Unlike FacilityAdd and FacilityEdit, this DTO does not include validation methods
 * since it is only used for displaying information, not for capturing user input.
 * 
 * This DTO includes assignedWasteDemand to show current facility utilization.
 * 
 * All attributes are public to allow direct access in Vue.js templates.
 */
export class FacilityInfo {
  /**
   * Unique identifier of the facility.
   */
  public id: string;

  /**
   * Type of the facility (e.g., OPERATIONAL_BASE, TRANSFER_STATION, TREATMENT_PLANT).
   */
  public facilityType: string;

  /**
   * Latitude coordinate of the facility location.
   */
  public latitude: number;

  /**
   * Longitude coordinate of the facility location.
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
   * Numeric capacity value.
   */
  public capacityValue: number;

  /**
   * Unit of measurement for capacity.
   */
  public capacityQuantityUnit: string;

  /**
   * Time unit for capacity rate.
   */
  public capacityTimeUnit: string;

  /**
   * Opening fixed cost amount.
   */
  public openingFixedCost: number;

  /**
   * ISO 4217 currency code for opening cost.
   */
  public currencyCode: string;

  /**
   * Current status of the facility.
   */
  public status: string;

  /**
   * Current assigned waste demand value.
   */
  public assignedWasteDemandValue: number;

  /**
   * Unit of measurement for assigned waste demand.
   */
  public assignedWasteDemandQuantityUnit: string;

  /**
   * Time unit for assigned waste demand rate.
   */
  public assignedWasteDemandTimeUnit: string;

  /**
   * Create a new FacilityInfo DTO.
   * 
   * @param id Unique identifier of the facility
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
   * @param assignedWasteDemandValue Current assigned waste demand value
   * @param assignedWasteDemandQuantityUnit Unit of measurement for assigned demand
   * @param assignedWasteDemandTimeUnit Time unit for assigned demand rate
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    id: string,
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
    status: string,
    assignedWasteDemandValue: number,
    assignedWasteDemandQuantityUnit: string,
    assignedWasteDemandTimeUnit: string
  ) {
    this.validate<string>(id, 'Facility id is not defined');
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
    this.validate<number>(assignedWasteDemandValue, 'Assigned waste demand value is not defined');
    this.validate<string>(assignedWasteDemandQuantityUnit, 'Assigned waste demand quantity unit is not defined');
    this.validate<string>(assignedWasteDemandTimeUnit, 'Assigned waste demand time unit is not defined');

    this.id = id;
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
    this.assignedWasteDemandValue = assignedWasteDemandValue;
    this.assignedWasteDemandQuantityUnit = assignedWasteDemandQuantityUnit;
    this.assignedWasteDemandTimeUnit = assignedWasteDemandTimeUnit;
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
   * Generate a random FacilityInfo instance for testing purposes.
   * 
   * @returns A new FacilityInfo instance with random valid values
   */
  static random(): FacilityInfo {
    const randomId = UllUUID.random().getValue();
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
    const randomAssignedDemand = parseFloat((Math.random() * randomCapacityValue * 0.8).toFixed(2));

    return new FacilityInfo(
      randomId,
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
      randomStatus as string,
      randomAssignedDemand,
      randomQuantityUnit,
      randomTimeUnit
    );
  }

  /**
   * Create a FacilityInfo DTO from a Facility domain entity.
   * 
   * @param facility Facility domain entity
   * @returns A new FacilityInfo DTO with values from the domain entity
   */
  static fromFacility(facility: Facility): FacilityInfo {
    const location = facility.getLocation();
    const capacity = facility.getCapacity();
    const openingFixedCost = facility.getOpeningFixedCost();
    const assignedWasteDemand = facility.getAssignedWasteDemand();

    return new FacilityInfo(
      facility.getId().getValue(),
      facility.getFacilityType(),
      location.latitude,
      location.longitude,
      location.postalAddress,
      location.gisReference,
      capacity.getValue(),
      capacity.getQuantityUnit().getValue(),
      capacity.getTimeUnit(),
      openingFixedCost.getAmount(),
      openingFixedCost.getCurrency().getCode(),
      facility.getStatus(),
      assignedWasteDemand.getValue(),
      assignedWasteDemand.getQuantityUnit().getValue(),
      assignedWasteDemand.getTimeUnit()
    );
  }

  /**
   * Get a formatted string representing the complete location.
   * 
   * @returns Formatted location string with coordinates and address
   */
  getFormattedLocation(): string {
    return `${this.postalAddress} (${this.latitude.toFixed(6)}, ${this.longitude.toFixed(6)})`;
  }

  /**
   * Get a formatted string representing the capacity with units.
   * 
   * @returns Formatted capacity string (e.g., "500 tons/day")
   */
  getFormattedCapacity(): string {
    return `${this.capacityValue} ${this.capacityQuantityUnit}/${this.capacityTimeUnit.toLowerCase()}`;
  }

  /**
   * Get a formatted string representing the assigned waste demand with units.
   * 
   * @returns Formatted assigned demand string (e.g., "350 tons/day")
   */
  getFormattedAssignedWasteDemand(): string {
    return `${this.assignedWasteDemandValue} ${this.assignedWasteDemandQuantityUnit}/${this.assignedWasteDemandTimeUnit.toLowerCase()}`;
  }

  /**
   * Get a formatted string representing the opening cost with currency.
   * 
   * @returns Formatted cost string (e.g., "50000.00 EUR")
   */
  getFormattedOpeningCost(): string {
    return `${this.openingFixedCost.toFixed(2)} ${this.currencyCode}`;
  }

  /**
   * Get a human-readable facility type label.
   * 
   * @returns Formatted facility type string
   */
  getFormattedFacilityType(): string {
    return this.facilityType.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  /**
   * Get a human-readable status label.
   * 
   * @returns Formatted status string
   */
  getFormattedStatus(): string {
    return this.status.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  /**
   * Calculate the utilization percentage of the facility.
   * 
   * @returns Utilization percentage (0-100)
   */
  getUtilizationPercentage(): number {
    if (this.capacityValue === 0) return 0;
    return parseFloat(((this.assignedWasteDemandValue / this.capacityValue) * 100).toFixed(2));
  }

  /**
   * Calculate the remaining available capacity.
   * 
   * @returns Remaining capacity value
   */
  getRemainingCapacity(): number {
    return Math.max(0, this.capacityValue - this.assignedWasteDemandValue);
  }

  /**
   * Get a formatted string representing the remaining capacity with units.
   * 
   * @returns Formatted remaining capacity string
   */
  getFormattedRemainingCapacity(): string {
    return `${this.getRemainingCapacity()} ${this.capacityQuantityUnit}/${this.capacityTimeUnit.toLowerCase()}`;
  }

  /**
   * Check if the facility is at full capacity.
   * 
   * @returns true if facility is at or over capacity
   */
  isAtFullCapacity(): boolean {
    return this.assignedWasteDemandValue >= this.capacityValue;
  }

  /**
   * Check if the facility is operational (OPEN status).
   * 
   * @returns true if facility status is OPEN
   */
  isOperational(): boolean {
    return this.status === FacilityStatus.OPEN;
  }

  /**
   * Check if the facility is discarded.
   * 
   * @returns true if facility status is DISCARDED
   */
  isDiscarded(): boolean {
    return this.status === FacilityStatus.DISCARDED;
  }

  /**
   * Get coordinates as an array [latitude, longitude].
   * Useful for map libraries that expect coordinates in this format.
   * 
   * @returns Array with [latitude, longitude]
   */
  getCoordinatesArray(): [number, number] {
    return [this.latitude, this.longitude];
  }

  /**
   * Get coordinates as an object with lat/lng properties.
   * Useful for map libraries like Leaflet or Google Maps.
   * 
   * @returns Object with lat and lng properties
   */
  getCoordinatesObject(): { lat: number; lng: number } {
    return {
      lat: this.latitude,
      lng: this.longitude
    };
  }
}
