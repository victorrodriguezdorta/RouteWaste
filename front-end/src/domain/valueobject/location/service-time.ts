/**
 * ServiceTime
 *
 * Represents travel plus service time (e.g., in minutes).
 */
export class ServiceTime {
  /** Error message for negative service time values. */
  private static readonly ERROR_NEGATIVE = 'Service time cannot be negative';

  /** The service time value in minutes. */
  readonly value: number;

  /**
   * Create ServiceTime (stored internally in minutes).
   * @param value non-negative numeric time in minutes
   */
  constructor(value: number) {
    if (value < 0) throw new Error(ServiceTime.ERROR_NEGATIVE);
    this.value = value;
  }

  /**
   * Create from hours (converted to minutes).
   * @param hours The number of hours to convert to minutes.
   * @returns A new ServiceTime instance with the hours converted to minutes.
   */
  static fromHours(hours: number): ServiceTime {
    return new ServiceTime(hours * 60.0);
  }

  /**
   * Create from seconds (converted to minutes).
   * @param seconds The number of seconds to convert to minutes.
   * @returns A new ServiceTime instance with the seconds converted to minutes.
   */
  static fromSeconds(seconds: number): ServiceTime {
    return new ServiceTime(seconds / 60.0);
  }

  /**
   * Returns the service time in minutes.
   * @returns The service time value in minutes.
   */
  getValue(): number {
    return this.value;
  }

  /**
   * Returns the service time in hours.
   * @returns The service time value in hours.
   */
  getValueInHours(): number {
    return this.value / 60.0;
  }

  /**
   * Returns the service time in seconds.
   * @returns The service time value in seconds.
   */
  getValueInSeconds(): number {
    return this.value * 60.0;
  }

  /**
   * Return a copy with updated time value (minutes).
   * @param newValue The new time value in minutes.
   * @returns A new ServiceTime instance with the updated value.
   */
  setValue(newValue: number): ServiceTime {
    return new ServiceTime(newValue);
  }

  /**
   * Return a copy with updated time (hours).
   * @param hours The new time value in hours.
   * @returns A new ServiceTime instance with the updated value.
   */
  setValueInHours(hours: number): ServiceTime {
    return ServiceTime.fromHours(hours);
  }

  /**
   * Return a copy with updated time (seconds).
   * @param seconds The new time value in seconds.
   * @returns A new ServiceTime instance with the updated value.
   */
  setValueInSeconds(seconds: number): ServiceTime {
    return ServiceTime.fromSeconds(seconds);
  }

  /**
   * Equality check by numeric value (tolerance for floating point).
   * @param other The other object to compare with.
   * @returns True if both are ServiceTime instances with the same value (within epsilon tolerance).
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ServiceTime)) return false;
    return Math.abs(this.value - other.value) < Number.EPSILON;
  }

  /**
   * Human-readable representation.
   * @returns Una cadena que representa el objeto ServiceTime.
   */
  toString(): string {
    return `ServiceTime={value=${this.value}}`;
  }
}
