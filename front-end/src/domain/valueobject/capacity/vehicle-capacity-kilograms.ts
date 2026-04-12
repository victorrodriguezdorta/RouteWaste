/**
 * VehicleCapacityKilograms
 *
 * Represents the maximum carrying capacity of a vehicle in kilograms.
 * Immutable value object that encapsulates the capacity value in kilograms.
 * The unit is always kilograms (fixed).
 */
export class VehicleCapacityKilograms {
  /**
   * Capacity value in kilograms.
   * Must be non-negative.
   */
  private readonly Kilograms: number;

  /**
   * Create a new `VehicleCapacityKilograms`.
   * @param Kilograms The capacity in kilograms (must be >= 0)
   * @throws Error if Kilograms is negative
   */
  constructor(Kilograms: number) {
    if (Kilograms < 0) {
      throw new Error('Vehicle capacity cannot be negative');
    }
    this.Kilograms = Kilograms;
  }

  /**
   * Get the capacity value in kilograms.
   * @returns The capacity in kilograms
   */
  getKilograms(): number {
    return this.Kilograms;
  }

  /**
   * Create a new instance with an updated value.
   * @param newKilograms The new capacity value in kilograms
   * @returns A new VehicleCapacityKilograms instance
   * @throws Error if newKilograms is negative
   */
  setKilograms(newKilograms: number): VehicleCapacityKilograms {
    return new VehicleCapacityKilograms(newKilograms);
  }

  /**
   * Compare if this capacity is greater than another.
   * @param other The other VehicleCapacityKilograms to compare
   * @returns true if this capacity is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: VehicleCapacityKilograms): boolean {
    if (!other) {
      throw new Error('Other VehicleCapacityKilograms cannot be null');
    }
    return this.Kilograms > other.Kilograms;
  }

  /**
   * Compare if this capacity is greater than or equal to another.
   * @param other The other VehicleCapacityKilograms to compare
   * @returns true if this capacity is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: VehicleCapacityKilograms): boolean {
    if (!other) {
      throw new Error('Other VehicleCapacityKilograms cannot be null');
    }
    return this.Kilograms >= other.Kilograms;
  }

  /**
   * Compare if this capacity is less than another.
   * @param other The other VehicleCapacityKilograms to compare
   * @returns true if this capacity is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: VehicleCapacityKilograms): boolean {
    if (!other) {
      throw new Error('Other VehicleCapacityKilograms cannot be null');
    }
    return this.Kilograms < other.Kilograms;
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
    if (!(otherObject instanceof VehicleCapacityKilograms)) {
      return false;
    }
    return this.Kilograms === otherObject.Kilograms;
  }

  /**
   * Get the hash code for this object.
   * @returns Hash code based on the kilograms value
   */
  hashCode(): number {
    return this.Kilograms;
  }

  /**
   * Get string representation.
   * @returns String representation of this capacity in kilograms
   */
  toString(): string {
    return `VehicleCapacityKilograms{Kilograms=${this.Kilograms}}`;
  }
}
