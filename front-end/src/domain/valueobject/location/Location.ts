/**
 * Location
 *
 * Represents a physical location with latitude, longitude, postal address, and GIS reference.
 */
export class Location {
  private static readonly MIN_LAT = -90.0;
  private static readonly MAX_LAT = 90.0;
  private static readonly MIN_LON = -180.0;
  private static readonly MAX_LON = 180.0;
  private static readonly POSTAL_MAX = 150;
  private static readonly GIS_MAX = 100;
  private static readonly POSTAL_REGEX = /^[A-Za-z0-9\s,.-]+$/;

  readonly latitude: number;
  readonly longitude: number;
  readonly postalAddress: string;
  readonly gisReference: string;

  /**
   * Create a Location.
   * @param latitude latitude in degrees (-90..90)
   * @param longitude longitude in degrees (-180..180)
   * @param postalAddress postal address string (validated)
   * @param gisReference GIS reference string (validated)
   */
  constructor(latitude: number, longitude: number, postalAddress: string, gisReference: string) {
    this.validateCoordinates(latitude, longitude);
    this.validatePostalAddress(postalAddress);
    this.validateGISReference(gisReference);
    this.latitude = latitude;
    this.longitude = longitude;
    this.postalAddress = postalAddress;
    this.gisReference = gisReference;
  }

  /** Validate latitude and longitude ranges. */
  private validateCoordinates(lat: number, lon: number) {
    if (lat < Location.MIN_LAT || lat > Location.MAX_LAT) throw new Error('Latitude must be between -90 and 90');
    if (lon < Location.MIN_LON || lon > Location.MAX_LON) throw new Error('Longitude must be between -180 and 180');
  }

  /** Validate postal address compliance and length. */
  private validatePostalAddress(addr: string) {
    if (addr == null) throw new Error('Postal address is not defined');
    const length = addr.length;
    if (length === 0) throw new Error('Postal address cannot be empty');
    if (length > Location.POSTAL_MAX) throw new Error(`Postal address must be at most ${Location.POSTAL_MAX} characters`);
    if (!Location.POSTAL_REGEX.test(addr)) throw new Error('Postal address format is invalid');
  }

  /** Validate GIS reference non-empty and max length. */
  private validateGISReference(gis: string) {
    if (gis == null) throw new Error('GIS reference is not defined');
    const length = gis.length;
    if (length === 0) throw new Error('GIS reference cannot be empty');
    if (length > Location.GIS_MAX) throw new Error(`GIS reference must be at most ${Location.GIS_MAX} characters`);
  }

  /** Equality check comparing coordinates and textual fields. */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Location)) return false;
    return this.latitude === other.latitude && this.longitude === other.longitude && this.postalAddress === other.postalAddress && this.gisReference === other.gisReference;
  }

  /** Human-readable representation. */
  toString(): string {
    return `Location={latitude=${this.latitude}, longitude=${this.longitude}, postalAddress='${this.postalAddress}', gisReference='${this.gisReference}'}`;
  }
}
