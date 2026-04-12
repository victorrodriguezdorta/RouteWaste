/**
 * DailyWasteDemandLitersPerDay
 *
 * Represents the approximate waste demand of a container per day in liters.
 * Immutable value object that encapsulates the daily demand value in liters per day.
 * The unit is always liters per day (fixed).
 */
export class DailyWasteDemandLitersPerDay {
  /**
   * Demand value in liters per day.
   * Must be non-negative.
   */
  private readonly litersPerDay: number;

  /**
   * Create a new `DailyWasteDemandLitersPerDay`.
   * @param litersPerDay The demand in liters per day (must be >= 0)
   * @throws Error if litersPerDay is negative
   */
  constructor(litersPerDay: number) {
    if (litersPerDay < 0) {
      throw new Error('Daily waste demand cannot be negative');
    }
    this.litersPerDay = litersPerDay;
  }

  /**
   * Get the demand value in liters per day.
   * @returns The demand in liters per day
   */
  getLitersPerDay(): number {
    return this.litersPerDay;
  }

  /**
   * Create a new instance with an updated value.
   * @param newLitersPerDay The new demand value in liters per day
   * @returns A new DailyWasteDemandLitersPerDay instance
   * @throws Error if newLitersPerDay is negative
   */
  setLitersPerDay(newLitersPerDay: number): DailyWasteDemandLitersPerDay {
    return new DailyWasteDemandLitersPerDay(newLitersPerDay);
  }

  /**
   * Add two DailyWasteDemandLitersPerDay instances.
   * @param other The other DailyWasteDemandLitersPerDay to add
   * @returns A new DailyWasteDemandLitersPerDay with the sum
   * @throws Error if other is null or undefined\n   
   */
   add(other: DailyWasteDemandLitersPerDay): DailyWasteDemandLitersPerDay {
    if (!other) {
      throw new Error('Other DailyWasteDemandLitersPerDay cannot be null');
    }
    return new DailyWasteDemandLitersPerDay(this.litersPerDay + other.litersPerDay);
  }

  /**
   * Compare if this demand is greater than another.
   * @param other The other DailyWasteDemandLitersPerDay to compare
   * @returns true if this demand is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: DailyWasteDemandLitersPerDay): boolean {
    if (!other) {
      throw new Error('Other DailyWasteDemandLitersPerDay cannot be null');
    }
    return this.litersPerDay > other.litersPerDay;
  }

  /**
   * Compare if this demand is greater than or equal to another.
   * @param other The other DailyWasteDemandLitersPerDay to compare
   * @returns true if this demand is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: DailyWasteDemandLitersPerDay): boolean {
    if (!other) {
      throw new Error('Other DailyWasteDemandLitersPerDay cannot be null');
    }
    return this.litersPerDay >= other.litersPerDay;
  }

  /**
   * Compare if this demand is less than another.
   * @param other The other DailyWasteDemandLitersPerDay to compare
   * @returns true if this demand is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: DailyWasteDemandLitersPerDay): boolean {
    if (!other) {
      throw new Error('Other DailyWasteDemandLitersPerDay cannot be null');
    }
    return this.litersPerDay < other.litersPerDay;
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
    if (!(otherObject instanceof DailyWasteDemandLitersPerDay)) {
      return false;
    }
    return this.litersPerDay === otherObject.litersPerDay;
  }

  /**
   * Get the hash code for this object.
   * @returns A hash code based on the litersPerDay value
   */
  hashCode(): number {
    return this.litersPerDay;
  }

  /**
   * Get a string representation of this value object.
   * @returns A formatted string representation
   */
  toString(): string {
    return `DailyWasteDemandLitersPerDay={litersPerDay=${this.litersPerDay}}`;
  }
}
