/**
 * Location
 *
 * Represents a physical location with latitude, longitude, postal address, and GIS reference.
 */

export class Location {
  /**
   * Mínima latitud permitida (-90.0).
   */
  private static readonly MIN_LAT = -90.0;
  /**
   * Máxima latitud permitida (90.0).
   */
  private static readonly MAX_LAT = 90.0;
  /**
   * Mínima longitud permitida (-180.0).
   */
  private static readonly MIN_LON = -180.0;
  /**
   * Máxima longitud permitida (180.0).
   */
  private static readonly MAX_LON = 180.0;
  /**
   * Longitud máxima permitida para la dirección postal.
   */
  private static readonly POSTAL_MAX = 150;
  /**
   * Longitud máxima permitida para la referencia GIS.
   */
  private static readonly GIS_MAX = 100;
  /**
   * Expresión regular para validar la dirección postal.
   */
  private static readonly POSTAL_REGEX = /^[A-Za-z0-9\s,.-]+$/;

  /**
   * Latitud en grados (-90..90).
   */
  readonly latitude: number;
  /**
   * Longitud en grados (-180..180).
   */
  readonly longitude: number;
  /**
   * Dirección postal validada.
   */
  readonly postalAddress: string;
  /**
   * Referencia GIS validada.
   */
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

  /**
   * Valida que la latitud y longitud estén dentro de los rangos permitidos.
   * @param lat Latitud a validar.
   * @param lon Longitud a validar.
   */
  private validateCoordinates(lat: number, lon: number) {
    if (lat < Location.MIN_LAT || lat > Location.MAX_LAT) throw new Error('Latitude must be between -90 and 90');
    if (lon < Location.MIN_LON || lon > Location.MAX_LON) throw new Error('Longitude must be between -180 and 180');
  }

  /**
   * Valida que la dirección postal cumpla con el formato y longitud permitidos.
   * @param addr Dirección postal a validar.
   */
  private validatePostalAddress(addr: string) {
    if (addr == null) throw new Error('Postal address is not defined');
    const length = addr.length;
    if (length === 0) throw new Error('Postal address cannot be empty');
    if (length > Location.POSTAL_MAX) throw new Error(`Postal address must be at most ${Location.POSTAL_MAX} characters`);
    if (!Location.POSTAL_REGEX.test(addr)) throw new Error('Postal address format is invalid');
  }

  /**
   * Valida que la referencia GIS no esté vacía y cumpla la longitud máxima.
   * @param gis Referencia GIS a validar.
   */
  private validateGISReference(gis: string) {
    if (gis == null) throw new Error('GIS reference is not defined');
    const length = gis.length;
    if (length === 0) throw new Error('GIS reference cannot be empty');
    if (length > Location.GIS_MAX) throw new Error(`GIS reference must be at most ${Location.GIS_MAX} characters`);
  }

  /**
   * Compara si dos ubicaciones son iguales.
   * @param other Otro objeto a comparar.
   * @returns True si todas las propiedades coinciden, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Location)) return false;
    return this.latitude === other.latitude && this.longitude === other.longitude && this.postalAddress === other.postalAddress && this.gisReference === other.gisReference;
  }

  /**
   * Devuelve una representación legible de la ubicación.
   * @returns Cadena con los valores de la ubicación.
   */
  toString(): string {
    return `Location={latitude=${this.latitude}, longitude=${this.longitude}, postalAddress='${this.postalAddress}', gisReference='${this.gisReference}'}`;
  }
}
