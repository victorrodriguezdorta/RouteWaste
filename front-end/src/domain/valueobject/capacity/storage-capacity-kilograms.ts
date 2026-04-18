/**
 * StorageCapacityKilograms
 *
 * Represents the maximum storage capacity of a facility in kilograms.
 * Immutable value object that encapsulates the capacity value in kilograms.
 * The unit is always kilograms (fixed).
 */
export class StorageCapacityKilograms {
  /**
   * Capacity value in kilograms.
   * Must be non-negative.
   */
  private readonly kilograms: number;

  /**
   * Create a new `StorageCapacityKilograms`.
   * @param kilograms The capacity in kilograms (must be >= 0)
   * @throws Error if kilograms is negative
   */
  constructor(kilograms: number) {
    if (kilograms < 0) {
      throw new Error('Storage capacity cannot be negative');
    }
    this.kilograms = kilograms;
  }

  /**
   * Get the capacity value in kilograms.
   * @returns The capacity in kilograms
   */
  getKilograms(): number {
    return this.kilograms;
  }

  /**
   * Create a new instance with an updated value.
   * @param newKilograms The new capacity value in kilograms
   * @returns A new StorageCapacityKilograms instance
   * @throws Error if newKilograms is negative
   */
  setKilograms(newKilograms: number): StorageCapacityKilograms {
    return new StorageCapacityKilograms(newKilograms);
  }

  /**
   * Compare if this capacity is greater than another.
   * @param other The other StorageCapacityKilograms to compare
   * @returns true if this capacity is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: StorageCapacityKilograms): boolean {
    if (!other) {
      throw new Error('Other StorageCapacityKilograms cannot be null');
    }
    return this.kilograms > other.kilograms;
  }

  /**
   * Compare if this capacity is greater than or equal to another.
   * @param other The other StorageCapacityKilograms to compare
   * @returns true if this capacity is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: StorageCapacityKilograms): boolean {
    if (!other) {
      throw new Error('Other StorageCapacityKilograms cannot be null');
    }
    return this.kilograms >= other.kilograms;
  }

  /**
   * Compare if this capacity is less than another.
   * @param other The other StorageCapacityKilograms to compare
   * @returns true if this capacity is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: StorageCapacityKilograms): boolean {
    if (!other) {
      throw new Error('Other StorageCapacityKilograms cannot be null');
    }
    return this.kilograms < other.kilograms;
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
    if (!(otherObject instanceof StorageCapacityKilograms)) {
      return false;
    }
    return this.kilograms === otherObject.kilograms;
  }

  /**
   * Get the hash code for this object.
   * @returns A hash code based on the kilograms value
   */
  hashCode(): number {
    return this.kilograms !== 0 ? this.kilograms : 0;
  }

  /**
   * Get the string representation of this object.
   * @returns A string representation
   */
  toString(): string {
    return `StorageCapacityKilograms={kilograms=${this.kilograms}}`;
  }
}
