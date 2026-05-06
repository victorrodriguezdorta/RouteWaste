/**
 * CollectedWeightKilograms
 *
 * Represents the weight of waste collected in kilograms.
 * Immutable value object that encapsulates a weight quantity.
 * Used in daily plans and stops to track collected waste weight.
 */
export class CollectedWeightKilograms {
  /**
   * Weight value in kilograms.
   * Must be non-negative.
   */
  private readonly kilograms: number;

  /**
   * Private constructor. Use static factories `fromKilograms` instead.
   * @param kilograms The weight in kilograms (must be >= 0)
   */
  private constructor(kilograms: number) {
    this.validate(kilograms);
    this.kilograms = kilograms;
  }

  /**
   * Validate that weight is not negative.
   * @param kilograms The weight value to validate
   * @throws Error if kilograms is negative
   */
  private validate(kilograms: number): void {
    if (kilograms < 0) {
      throw new Error('Collected weight cannot be negative');
    }
  }

  /**
   * Create a CollectedWeightKilograms from kilograms.
   * @param kilograms Weight in kilograms
   * @returns A new CollectedWeightKilograms instance
   * @throws Error if kilograms is negative
   */
  static fromKilograms(kilograms: number): CollectedWeightKilograms {
    return new CollectedWeightKilograms(kilograms);
  }

  /**
   * Create a CollectedWeightKilograms from grams (1 kg = 1000 g).
   * @param grams Weight in grams
   * @returns A new CollectedWeightKilograms instance
   */
  static fromGrams(grams: number): CollectedWeightKilograms {
    return new CollectedWeightKilograms(grams / 1000.0);
  }

  /**
   * Create a CollectedWeightKilograms from tons (1 t = 1000 kg).
   * @param tons Weight in metric tons
   * @returns A new CollectedWeightKilograms instance
   */
  static fromTons(tons: number): CollectedWeightKilograms {
    return new CollectedWeightKilograms(tons * 1000.0);
  }

  /**
   * Get the weight in kilograms.
   * @returns The weight in kilograms
   */
  getKilograms(): number {
    return this.kilograms;
  }

  /**
   * Get the weight in grams.
   * @returns The weight in grams
   */
  getGrams(): number {
    return this.kilograms * 1000.0;
  }

  /**
   * Get the weight in metric tons.
   * @returns The weight in metric tons
   */
  getTons(): number {
    return this.kilograms / 1000.0;
  }

  /**
   * Get numeric kilograms value.
   * @returns The kilograms value
   */
  getValue(): number {
    return this.kilograms;
  }

  /**
   * Create a new instance with an updated value.
   * @param newKilograms The new weight value in kilograms
   * @returns A new CollectedWeightKilograms instance
   * @throws Error if newKilograms is negative
   */
  setValue(newKilograms: number): CollectedWeightKilograms {
    return CollectedWeightKilograms.fromKilograms(newKilograms);
  }

  /**
   * Add another collected weight to this one.
   * @param other The other CollectedWeightKilograms to add
   * @returns A new CollectedWeightKilograms with the sum
   * @throws Error if other is null or undefined
   */
  add(other: CollectedWeightKilograms): CollectedWeightKilograms {
    if (!other) {
      throw new Error('Other CollectedWeightKilograms cannot be null');
    }
    return CollectedWeightKilograms.fromKilograms(this.kilograms + other.kilograms);
  }

  /**
   * Compare if this weight is greater than another.
   * @param other The other CollectedWeightKilograms to compare
   * @returns true if this weight is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: CollectedWeightKilograms): boolean {
    if (!other) {
      throw new Error('Other CollectedWeightKilograms cannot be null');
    }
    return this.kilograms > other.kilograms;
  }

  /**
   * Compare if this weight is greater than or equal to another.
   * @param other The other CollectedWeightKilograms to compare
   * @returns true if this weight is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: CollectedWeightKilograms): boolean {
    if (!other) {
      throw new Error('Other CollectedWeightKilograms cannot be null');
    }
    return this.kilograms >= other.kilograms;
  }

  /**
   * Compare if this weight is less than another.
   * @param other The other CollectedWeightKilograms to compare
   * @returns true if this weight is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: CollectedWeightKilograms): boolean {
    if (!other) {
      throw new Error('Other CollectedWeightKilograms cannot be null');
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
    if (!(otherObject instanceof CollectedWeightKilograms)) {
      return false;
    }
    return this.kilograms === otherObject.kilograms;
  }

  /**
   * Get the hash code for this object.
   * @returns A numeric hash code
   */
  hashCode(): number {
    return Number(this.kilograms);
  }

  /**
   * Human-readable representation.
   * @returns A formatted string representation
   */
  toString(): string {
    return `CollectedWeightKilograms={kilograms=${this.kilograms.toFixed(2)} kg, tons=${this.getTons().toFixed(2)} t}`;
  }
}
