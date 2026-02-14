import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../../domain/entity/container';
import { ServiceZone } from '../../../../domain/enumerate/service-zone';
import { TimeUnit } from '../../../../domain/enumerate/time-unit';
import { WasteType } from '../../../../domain/enumerate/waste-type';

/**
 * ContainerInfo
 * 
 * Data Transfer Object for displaying Container entity information.
 * 
 * This DTO is designed to be used in Vue.js views for showing container details
 * in a read-only format. It contains primitive types that can be easily displayed
 * in templates without requiring complex object navigation.
 * 
 * Unlike ContainerAdd and ContainerEdit, this DTO does not include validation methods
 * since it is only used for displaying information, not for capturing user input.
 * 
 * All attributes are public to allow direct access in Vue.js templates.
 */
export class ContainerInfo {
  /**
   * Unique identifier of the container.
   */
  public id: string;

  /**
   * Latitude coordinate of the container location.
   */
  public latitude: number;

  /**
   * Longitude coordinate of the container location.
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
   * Type of waste collected by this container.
   */
  public wasteType: string;

  /**
   * Numeric waste demand value.
   */
  public wasteDemandValue: number;

  /**
   * Unit of measurement for waste demand.
   */
  public wasteDemandQuantityUnit: string;

  /**
   * Time unit for waste demand rate.
   */
  public wasteDemandTimeUnit: string;

  /**
   * Service zone identifier. Optional attribute.
   */
  public serviceZone?: string;

  /**
   * Create a new ContainerInfo DTO.
   * 
   * @param id Unique identifier of the container
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
    id: string,
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
    this.validate<string>(id, 'Container id is not defined');
    this.validate<number>(latitude, 'Latitude is not defined');
    this.validate<number>(longitude, 'Longitude is not defined');
    this.validate<string>(postalAddress, 'Postal address is not defined');
    this.validate<string>(gisReference, 'GIS reference is not defined');
    this.validate<string>(wasteType, 'Waste type is not defined');
    this.validate<number>(wasteDemandValue, 'Waste demand value is not defined');
    this.validate<string>(wasteDemandQuantityUnit, 'Waste demand quantity unit is not defined');
    this.validate<string>(wasteDemandTimeUnit, 'Waste demand time unit is not defined');

    this.id = id;
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
   * Generate a random ContainerInfo instance for testing purposes.
   * 
   * @returns A new ContainerInfo instance with random valid values
   */
  static random(): ContainerInfo {
    const randomId = UllUUID.random().getValue();
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

    return new ContainerInfo(
      randomId,
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
   * Create a ContainerInfo DTO from a Container domain entity.
   * 
   * @param container Container domain entity
   * @returns A new ContainerInfo DTO with values from the domain entity
   */
  static fromContainer(container: Container): ContainerInfo {
    const location = container.getLocation();
    const wasteDemand = container.getWasteDemand();
    const serviceZone = container.getServiceZone();

    return new ContainerInfo(
      container.getId().getValue(),
      location.latitude,
      location.longitude,
      location.postalAddress,
      location.gisReference,
      container.getWasteType(),
      wasteDemand.getValue(),
      wasteDemand.getQuantityUnit().getValue(),
      wasteDemand.getTimeUnit(),
      serviceZone || undefined
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
   * Get a formatted string representing the waste demand with units.
   * 
   * @returns Formatted waste demand string (e.g., "50.5 tons/day")
   */
  getFormattedWasteDemand(): string {
    return `${this.wasteDemandValue} ${this.wasteDemandQuantityUnit}/${this.wasteDemandTimeUnit.toLowerCase()}`;
  }

  /**
   * Get a human-readable waste type label.
   * 
   * @returns Formatted waste type string
   */
  getFormattedWasteType(): string {
    return this.wasteType.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  /**
   * Get a human-readable service zone label.
   * 
   * @returns Formatted service zone string or 'Not Assigned' if undefined
   */
  getFormattedServiceZone(): string {
    if (!this.serviceZone) return 'Not Assigned';
    return this.serviceZone.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
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
