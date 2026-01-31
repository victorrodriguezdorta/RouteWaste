/**
 * ServiceTime
 *
 * Represents travel plus service time (e.g., in minutes).
 */
export class ServiceTime {
  private static readonly ERROR_NEGATIVE = 'Service time cannot be negative';
  readonly value: number;

  /**
   * Create ServiceTime (stored internally in minutes).
   * @param value non-negative numeric time in minutes
   */
  constructor(value: number) {
    if (value < 0) throw new Error(ServiceTime.ERROR_NEGATIVE);
    this.value = value;
  }

  /** Create from hours (converted to minutes) */
  static fromHours(hours: number): ServiceTime {
    return new ServiceTime(hours * 60.0);
  }

  /** Create from seconds (converted to minutes) */
  static fromSeconds(seconds: number): ServiceTime {
    return new ServiceTime(seconds / 60.0);
  }

  /** Returns the service time in minutes */
  getValue(): number {
    return this.value;
  }

  /** Returns the service time in hours */
  getValueInHours(): number {
    return this.value / 60.0;
  }

  /** Returns the service time in seconds */
  getValueInSeconds(): number {
    return this.value * 60.0;
  }

  /** Return a copy with updated time value (minutes). */
  setValue(newValue: number): ServiceTime {
    return new ServiceTime(newValue);
  }

  /** Return a copy with updated time (hours) */
  setValueInHours(hours: number): ServiceTime {
    return ServiceTime.fromHours(hours);
  }

  /** Return a copy with updated time (seconds) */
  setValueInSeconds(seconds: number): ServiceTime {
    return ServiceTime.fromSeconds(seconds);
  }

  /** Equality check by numeric value (tolerance for floating point). */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ServiceTime)) return false;
    return Math.abs(this.value - other.value) < Number.EPSILON;
  }

  /** Human-readable representation. */
  toString(): string {
    return `ServiceTime={value=${this.value}}`;
  }
}
