import { QuantityUnit } from './QuantityUnit';
import { TimeUnit } from '../../enumerate/TimeUnit';

/**
 * Capacity
 *
 * Represents an amount per time unit (e.g., tons per day).
 */
export class Capacity {
  private static readonly ERROR_VALUE_NEGATIVE = 'Capacity value must be greater than or equal to 0';
  private static readonly ERROR_QUANTITY_UNIT_NOT_DEFINED = 'Quantity unit is not defined';
  private static readonly ERROR_TIME_UNIT_NOT_DEFINED = 'Time unit is not defined';
  private static readonly ERROR_OTHER_CAPACITY_NULL = 'Other Capacity cannot be null';
  private static readonly ERROR_UNITS_MUST_MATCH = 'Units must be the same';

  readonly value: number;
  readonly quantityUnit: QuantityUnit;
  readonly timeUnit: TimeUnit;

  /**
   * Create a Capacity.
   * @param value numeric quantity (>= 0)
   * @param quantityUnit unit of quantity (e.g., tons)
   * @param timeUnit time unit (e.g., DAY)
   */
  constructor(value: number, quantityUnit: QuantityUnit, timeUnit: TimeUnit) {
    if (value < 0) throw new Error(Capacity.ERROR_VALUE_NEGATIVE);
    if (!quantityUnit) throw new Error(Capacity.ERROR_QUANTITY_UNIT_NOT_DEFINED);
    if (!timeUnit) throw new Error(Capacity.ERROR_TIME_UNIT_NOT_DEFINED);
    this.value = value;
    this.quantityUnit = quantityUnit;
    this.timeUnit = timeUnit;
  }

  /** Get numeric value. */
  getValue(): number {
    return this.value;
  }

  /** Get quantity unit. */
  getQuantityUnit(): QuantityUnit {
    return this.quantityUnit;
  }

  /** Get time unit. */
  getTimeUnit(): TimeUnit {
    return this.timeUnit;
  }

  /** Return a copy with updated value. */
  setValue(newValue: number): Capacity {
    return new Capacity(newValue, this.quantityUnit, this.timeUnit);
  }

  /** Return a copy with updated quantity unit. */
  setQuantityUnit(newQuantityUnit: QuantityUnit): Capacity {
    return new Capacity(this.value, newQuantityUnit, this.timeUnit);
  }

  /** Return a copy with updated time unit. */
  setTimeUnit(newTimeUnit: TimeUnit): Capacity {
    return new Capacity(this.value, this.quantityUnit, newTimeUnit);
  }

  /**
   * Compare greater-than with other Capacity. Units must match.
   * @throws Error when other is null or units differ
   */
  greaterThan(other: Capacity): boolean {
    if (!other) throw new Error(Capacity.ERROR_OTHER_CAPACITY_NULL);
    if (!this.quantityUnit.equals(other.quantityUnit) || this.timeUnit !== other.timeUnit) {
      throw new Error(Capacity.ERROR_UNITS_MUST_MATCH);
    }
    return this.value > other.value;
  }

  /** Equality check for Capacity (value and units). */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Capacity)) return false;
    return Math.abs(this.value - other.value) < Number.EPSILON && this.quantityUnit.equals(other.quantityUnit) && this.timeUnit === other.timeUnit;
  }

  /** Human-readable representation. */
  toString(): string {
    return `Capacity={value=${this.value}, unit=${this.quantityUnit.getValue()}/${this.timeUnit.toString().toLowerCase()}}`;
  }
}
