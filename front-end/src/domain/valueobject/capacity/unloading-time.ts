/**
 * UnloadingTime
 *
 * Represents the time required to unload a truck at a facility in minutes.
 * Immutable value object that encapsulates the unloading time value in minutes.
 * The unit is always minutes (fixed).
 */
export class UnloadingTime {
  /**
   * Unloading time value in minutes.
   * Must be non-negative.
   */
  private readonly minutes: number;

  /**
   * Create a new `UnloadingTime`.
   * @param minutes The unloading time in minutes (must be >= 0)
   * @throws Error if minutes is negative
   */
  constructor(minutes: number) {
    if (minutes < 0) {
      throw new Error('Unloading time cannot be negative');
    }
    this.minutes = minutes;
  }

  /**
   * Get the unloading time in minutes.
   * @returns The unloading time in minutes
   */
  getMinutes(): number {
    return this.minutes;
  }

  /**
   * Get the unloading time in seconds.
   * Convenience method for cases where seconds are needed.
   * @returns The unloading time in seconds
   */
  getSeconds(): number {
    return this.minutes * 60;
  }

  /**
   * Create a new instance with an updated value.
   * @param newMinutes The new unloading time value in minutes
   * @returns A new UnloadingTime instance
   * @throws Error if newMinutes is negative
   */
  setMinutes(newMinutes: number): UnloadingTime {
    return new UnloadingTime(newMinutes);
  }

  /**
   * Compare if this time is greater than another.
   * @param other The other UnloadingTime to compare
   * @returns true if this time is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: UnloadingTime): boolean {
    if (!other) {
      throw new Error('Other UnloadingTime cannot be null');
    }
    return this.minutes > other.minutes;
  }

  /**
   * Compare if this time is greater than or equal to another.
   * @param other The other UnloadingTime to compare
   * @returns true if this time is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: UnloadingTime): boolean {
    if (!other) {
      throw new Error('Other UnloadingTime cannot be null');
    }
    return this.minutes >= other.minutes;
  }

  /**
   * Compare if this time is less than another.
   * @param other The other UnloadingTime to compare
   * @returns true if this time is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: UnloadingTime): boolean {
    if (!other) {
      throw new Error('Other UnloadingTime cannot be null');
    }
    return this.minutes < other.minutes;
  }

  /**
   * Compare equality with another object.
   * @param otherObject The object to compare with
   * @returns true if the objects are equal, false otherwise
   */
  equals(otherObject: unknown): boolean {
    if (this === otherObject) {
      return true;
    }
    if (!(otherObject instanceof UnloadingTime)) {
      return false;
    }
    return this.minutes === otherObject.minutes;
  }

  /**
   * Get the hash code for this object.
   * @returns A hash code based on the minutes value
   */
  hashCode(): number {
    return this.minutes !== 0 ? this.minutes : 0;
  }

  /**
   * Get the string representation of this object.
   * @returns A string representation
   */
  toString(): string {
    return `UnloadingTime={minutes=${this.minutes}}`;
  }
}
