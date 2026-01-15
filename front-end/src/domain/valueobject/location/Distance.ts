/**
 * Distance
 *
 * Represents a distance in meters (SI base unit). Immutable value object.
 */
export class Distance {
  private static readonly ERROR_NEGATIVE = 'Distance cannot be negative';
  private readonly meters: number;

  /**
   * Private constructor. Use static factories `fromMeters`, `fromKilometers`, `fromMiles`.
   * @param meters distance in meters
   */
  private constructor(meters: number) {
    this.validate(meters);
    this.meters = meters;
  }

  /** Validate that distance is not negative. */
  private validate(value: number) {
    if (value < 0) throw new Error(Distance.ERROR_NEGATIVE);
  }

  /** Create a Distance from meters. */
  static fromMeters(meters: number): Distance {
    return new Distance(meters);
  }

  /** Create a Distance from kilometers. */
  static fromKilometers(km: number): Distance {
    return new Distance(km * 1000.0);
  }

  /** Create a Distance from miles. */
  static fromMiles(miles: number): Distance {
    return new Distance(miles * 1609.34);
  }

  /** Return distance in meters. */
  toMeters(): number {
    return this.meters;
  }

  /** Return distance in kilometers. */
  toKilometers(): number {
    return this.meters / 1000.0;
  }

  /** Return distance in miles. */
  toMiles(): number {
    return this.meters / 1609.34;
  }

  /** Get numeric meters value. */
  getValue(): number {
    return this.meters;
  }

  /** Return a copy with updated meters. */
  setValue(newMeters: number): Distance {
    return Distance.fromMeters(newMeters);
  }

  /** Equality by meters value (with floating tolerance). */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Distance)) return false;
    return Math.abs(this.meters - other.meters) < Number.EPSILON;
  }

  /** Human-readable representation. */
  toString(): string {
    return `Distance={meters=${this.meters.toFixed(2)} m, kilometers=${this.toKilometers().toFixed(2)} km}`;
  }
}
