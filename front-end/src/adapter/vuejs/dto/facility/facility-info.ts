import { Facility } from '@/domain/entity/facility';
import { FacilityStatus } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';
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
 * This DTO includes currentFillingLevel to show current facility utilization.
 * 
 * All attributes are public to allow direct access in Vue.js templates.
 */
export class FacilityInfo {
  /**
   * Unique identifier of the facility.
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
   * Storage capacity in kilograms.
   */
  public storageCapacity: number;

  /**
   * Processing capacity in kilograms per day.
   */
  public processingCapacity: number;

  /**
   * Unloading time in minutes.
   */
  public unloadingTime: number;

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
   * Current filling level in liters per day.
   */
  public currentFillingLevel: number;

  /**
   * Create a new FacilityInfo DTO.
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
   * @param currentFillingLevel Current filling level in liters per day
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
    status: string,
    currentFillingLevel: number
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
    this.validate<number>(currentFillingLevel, 'Current filling level is not defined');

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
    this.currentFillingLevel = currentFillingLevel;
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
    const randomStorageCapacity = Math.floor(Math.random() * 5000) + 1000;
    const randomProcessingCapacity = Math.floor(Math.random() * 1000) + 100;
    const randomUnloadingTime = Math.floor(Math.random() * 120) + 15;
    const randomOpeningFixedCost = parseFloat((Math.random() * 100000 + 10000).toFixed(2));
    const randomCurrency = 'EUR';
    const statuses = [FacilityStatus.CANDIDATE, FacilityStatus.PLANNED, FacilityStatus.OPEN, FacilityStatus.DISCARDED];
    const randomStatus = statuses[Math.floor(Math.random() * statuses.length)] as FacilityStatus;
    const randomFillingLevel = parseFloat((Math.random() * randomStorageCapacity * 0.8).toFixed(2));

    return new FacilityInfo(
      randomId,
      `Facility ${Math.floor(Math.random() * 10000)}`,
      randomFacilityType as string,
      randomLatitude,
      randomLongitude,
      randomPostalAddress,
      randomGisReference,
      randomStorageCapacity,
      randomProcessingCapacity,
      randomUnloadingTime,
      randomOpeningFixedCost,
      randomCurrency,
      randomStatus as string,
      randomFillingLevel
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
    const storageCapacity = facility.getStorageCapacity();
    const processingCapacity = facility.getProcessingCapacity();
    const unloadingTime = facility.getUnloadingTime();
    const openingFixedCost = facility.getOpeningFixedCost();
    const currentFillingLevel = facility.getCurrentFillingLevel();

    return new FacilityInfo(
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
      facility.getStatus(),
      currentFillingLevel.getLitersPerDay()
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
   * Get a formatted string representing the storage capacity.
   * 
   * @returns Formatted storage capacity string (e.g., "5000 kg")
   */
  getFormattedStorageCapacity(): string {
    return `${this.storageCapacity} kg`;
  }

  /**
   * Get a formatted string representing the processing capacity.
   * 
   * @returns Formatted processing capacity string (e.g., "500 kg/day")
   */
  getFormattedProcessingCapacity(): string {
    return `${this.processingCapacity} kg/day`;
  }

  /**
   * Get a formatted string representing the unloading time.
   * 
   * @returns Formatted unloading time string (e.g., "45 minutes")
   */
  getFormattedUnloadingTime(): string {
    return `${this.unloadingTime} minutes`;
  }

  /**
   * Get a formatted string representing the current filling level.
   * 
   * @returns Formatted filling level string (e.g., "2500 liters/day")
   */
  getFormattedCurrentFillingLevel(): string {
    return `${this.currentFillingLevel.toFixed(2)} liters/day`;
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
