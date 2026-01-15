/**
 * ServiceRadius
 *
 * Represents the maximum allowed service radius for a facility (meters).
 */
export class ServiceRadius {
  private static readonly ERROR_NEGATIVE = 'Service radius cannot be negative';
  readonly value: number;

  /**
   * Create ServiceRadius (meters).
   * @param value radius in meters (>= 0)
   */
  constructor(value: number) {
    if (value < 0) throw new Error(ServiceRadius.ERROR_NEGATIVE);
    this.value = value;
  }

  /** Return a copy with updated value. */
  setValue(newValue: number): ServiceRadius {
    return new ServiceRadius(newValue);
  }

  /** Equality check by radius value. */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ServiceRadius)) return false;
    return Math.abs(this.value - other.value) < Number.EPSILON;
  }

  /** Human-readable representation. */
  toString(): string {
    return `ServiceRadius={value=${this.value}}`;
  }
}
