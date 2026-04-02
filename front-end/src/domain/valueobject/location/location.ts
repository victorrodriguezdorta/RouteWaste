import { UllGeolocationPoint } from '@ull-tfg/ull-tfg-typescript';

/**
 * Location
 *
 * Represents a physical location with latitude, longitude, postal address, and GIS reference.
 */
export class Location {
  /**
   * Maximum allowed length for the postal address.
   */
  private static readonly POSTAL_MAX = 150;
  /**
   * Maximum allowed length for the GIS reference.
   */
  private static readonly GIS_MAX = 100;
  /**
   * Regular expression used to validate the postal address.
   */
  private static readonly POSTAL_REGEX = /^[A-Za-z0-9\s,.-]+$/;

  /**
   * Validated postal address.
   */
  readonly postalAddress: string;
  /**
   * Validated GIS reference.
   */
  readonly gisReference: string;
  /**
   * Geolocation point used internally for validation and comparison.
   */
  private readonly point: UllGeolocationPoint;

  /**
   * Create a Location.
   * @param latitude latitude in degrees (-90..90)
   * @param longitude longitude in degrees (-180..180)
   * @param postalAddress postal address string (validated)
   * @param gisReference GIS reference string (validated)
   */
  constructor(latitude: number, longitude: number, postalAddress: string, gisReference: string) {
    this.point = new UllGeolocationPoint(longitude, latitude);
    this.validatePostalAddress(postalAddress);
    this.validateGISReference(gisReference);
    this.postalAddress = postalAddress;
    this.gisReference = gisReference;
  }

  /**
   * Latitude in degrees (-90..90).
   */
  get latitude(): number {
    return this.point.getLatitude();
  }

  /**
   * Longitude in degrees (-180..180).
   */
  get longitude(): number {
    return this.point.getLongitude();
  }

  /**
   * Validates that the postal address matches the allowed format and length.
   * @param addr Postal address to validate.
   */
  private validatePostalAddress(addr: string) {
    if (addr == null) throw new Error('Postal address is not defined');
    const length = addr.length;
    if (length === 0) throw new Error('Postal address cannot be empty');
    if (length > Location.POSTAL_MAX) throw new Error(`Postal address must be at most ${Location.POSTAL_MAX} characters`);
    if (!Location.POSTAL_REGEX.test(addr)) throw new Error('Postal address format is invalid');
  }

  /**
   * Validates that the GIS reference is not empty and does not exceed the maximum length.
   * @param gis GIS reference to validate.
   */
  private validateGISReference(gis: string) {
    if (gis == null) throw new Error('GIS reference is not defined');
    const length = gis.length;
    if (length === 0) throw new Error('GIS reference cannot be empty');
    if (length > Location.GIS_MAX) throw new Error(`GIS reference must be at most ${Location.GIS_MAX} characters`);
  }

  /**
   * Compares whether two locations are equal.
   * @param other Other object to compare.
   * @returns True if all properties match, false otherwise.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Location)) return false;
    return this.point.equals(other.point) && this.postalAddress === other.postalAddress && this.gisReference === other.gisReference;
  }

  /**
   * Returns a readable representation of the location.
   * @returns String containing the location values.
   */
  toString(): string {
    return `Location={latitude=${this.latitude}, longitude=${this.longitude}, postalAddress='${this.postalAddress}', gisReference='${this.gisReference}'}`;
  }
}
