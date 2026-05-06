/**
 * CollectedVolumeLiters
 *
 * Represents the volume of waste collected in liters.
 * Immutable value object that encapsulates a volume quantity.
 * Used in daily plans and stops to track collected waste volume.
 */
export class CollectedVolumeLiters {
  /**
   * Volume value in liters.
   * Must be non-negative.
   */
  private readonly liters: number;

  /**
   * Private constructor. Use static factories `fromLiters` instead.
   * @param liters The volume in liters (must be >= 0)
   */
  private constructor(liters: number) {
    this.validate(liters);
    this.liters = liters;
  }

  /**
   * Validate that volume is not negative.
   * @param liters The volume value to validate
   * @throws Error if liters is negative
   */
  private validate(liters: number): void {
    if (liters < 0) {
      throw new Error('Collected volume cannot be negative');
    }
  }

  /**
   * Create a CollectedVolumeLiters from liters.
   * @param liters Volume in liters
   * @returns A new CollectedVolumeLiters instance
   * @throws Error if liters is negative
   */
  static fromLiters(liters: number): CollectedVolumeLiters {
    return new CollectedVolumeLiters(liters);
  }

  /**
   * Create a CollectedVolumeLiters from cubic meters (1 m³ = 1000 L).
   * @param cubicMeters Volume in cubic meters
   * @returns A new CollectedVolumeLiters instance
   */
  static fromCubicMeters(cubicMeters: number): CollectedVolumeLiters {
    return new CollectedVolumeLiters(cubicMeters * 1000.0);
  }

  /**
   * Get the volume in liters.
   * @returns The volume in liters
   */
  getLiters(): number {
    return this.liters;
  }

  /**
   * Get the volume in cubic meters.
   * @returns The volume in cubic meters
   */
  getCubicMeters(): number {
    return this.liters / 1000.0;
  }

  /**
   * Get numeric liters value.
   * @returns The liters value
   */
  getValue(): number {
    return this.liters;
  }

  /**
   * Create a new instance with an updated value.
   * @param newLiters The new volume value in liters
   * @returns A new CollectedVolumeLiters instance
   * @throws Error if newLiters is negative
   */
  setValue(newLiters: number): CollectedVolumeLiters {
    return CollectedVolumeLiters.fromLiters(newLiters);
  }

  /**
   * Add another collected volume to this one.
   * @param other The other CollectedVolumeLiters to add
   * @returns A new CollectedVolumeLiters with the sum
   * @throws Error if other is null or undefined
   */
  add(other: CollectedVolumeLiters): CollectedVolumeLiters {
    if (!other) {
      throw new Error('Other CollectedVolumeLiters cannot be null');
    }
    return CollectedVolumeLiters.fromLiters(this.liters + other.liters);
  }

  /**
   * Compare if this volume is greater than another.
   * @param other The other CollectedVolumeLiters to compare
   * @returns true if this volume is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: CollectedVolumeLiters): boolean {
    if (!other) {
      throw new Error('Other CollectedVolumeLiters cannot be null');
    }
    return this.liters > other.liters;
  }

  /**
   * Compare if this volume is greater than or equal to another.
   * @param other The other CollectedVolumeLiters to compare
   * @returns true if this volume is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: CollectedVolumeLiters): boolean {
    if (!other) {
      throw new Error('Other CollectedVolumeLiters cannot be null');
    }
    return this.liters >= other.liters;
  }

  /**
   * Compare if this volume is less than another.
   * @param other The other CollectedVolumeLiters to compare
   * @returns true if this volume is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: CollectedVolumeLiters): boolean {
    if (!other) {
      throw new Error('Other CollectedVolumeLiters cannot be null');
    }
    return this.liters < other.liters;
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
    if (!(otherObject instanceof CollectedVolumeLiters)) {
      return false;
    }
    return this.liters === otherObject.liters;
  }

  /**
   * Get the hash code for this object.
   * @returns A numeric hash code
   */
  hashCode(): number {
    return Number(this.liters);
  }

  /**
   * Human-readable representation.
   * @returns A formatted string representation
   */
  toString(): string {
    return `CollectedVolumeLiters={liters=${this.liters.toFixed(2)} L, cubicMeters=${this.getCubicMeters().toFixed(2)} m³}`;
  }
}
