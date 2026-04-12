/**
 * ContainerCapacityLiters
 *
 * Represents the maximum capacity of a container in liters.
 * Immutable value object that encapsulates the capacity value in liters.
 * The unit is always liters (fixed).
 */
export class ContainerCapacityLiters {
  /**
   * Capacity value in liters.
   * Must be non-negative.
   */
  private readonly liters: number;

  /**
   * Create a new `ContainerCapacityLiters`.
   * @param liters The capacity in liters (must be >= 0)
   * @throws Error if liters is negative
   */
  constructor(liters: number) {
    if (liters < 0) {
      throw new Error('Container capacity cannot be negative');
    }
    this.liters = liters;
  }

  /**
   * Get the capacity value in liters.
   * @returns The capacity in liters
   */
  getLiters(): number {
    return this.liters;
  }

  /**
   * Create a new instance with an updated value.
   * @param newLiters The new capacity value in liters
   * @returns A new ContainerCapacityLiters instance
   * @throws Error if newLiters is negative
   */
  setLiters(newLiters: number): ContainerCapacityLiters {
    return new ContainerCapacityLiters(newLiters);
  }

  /**
   * Compare if this capacity is greater than another.
   * @param other The other ContainerCapacityLiters to compare
   * @returns true if this capacity is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: ContainerCapacityLiters): boolean {
    if (!other) {
      throw new Error('Other ContainerCapacityLiters cannot be null');
    }
    return this.liters > other.liters;
  }

  /**
   * Compare if this capacity is greater than or equal to another.
   * @param other The other ContainerCapacityLiters to compare
   * @returns true if this capacity is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: ContainerCapacityLiters): boolean {
    if (!other) {
      throw new Error('Other ContainerCapacityLiters cannot be null');
    }
    return this.liters >= other.liters;
  }

  /**
   * Compare if this capacity is less than another.
   * @param other The other ContainerCapacityLiters to compare
   * @returns true if this capacity is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: ContainerCapacityLiters): boolean {
    if (!other) {
      throw new Error('Other ContainerCapacityLiters cannot be null');
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
    if (!(otherObject instanceof ContainerCapacityLiters)) {
      return false;
    }
    return this.liters === otherObject.liters;
  }

  /**
   * Get the hash code for this object.
   * @returns A hash code based on the liters value
   */
  hashCode(): number {
    return this.liters;
  }

  /**
   * Get a string representation of this value object.
   * @returns A formatted string representation
   */
  toString(): string {
    return `ContainerCapacityLiters={liters=${this.liters}}`;
  }
}
