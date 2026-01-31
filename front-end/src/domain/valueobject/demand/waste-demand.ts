import { QuantityUnit } from './quantity-unit';
import { TimeUnit } from '../../enumerate/time-unit';
import { Capacity } from './capacity';

/**
 * WasteDemand
 *
 * Represents expected waste demand per time unit.
 */
export class WasteDemand {
  private static readonly ERROR_DEMAND_NEGATIVE = 'Waste demand cannot be negative';
  private static readonly ERROR_UNITS_NULL = 'Units cannot be null';
  private static readonly ERROR_OTHER_DEMAND_NULL = 'Other WasteDemand cannot be null';
  private static readonly ERROR_UNITS_MUST_MATCH = 'Units must be the same';
  private static readonly ERROR_UNITS_MUST_MATCH_CAPACITY = 'Units must be the same to compare WasteDemand and Capacity';

  readonly value: number;
  readonly quantityUnit: QuantityUnit;
  readonly timeUnit: TimeUnit;

  constructor(value: number, quantityUnit: QuantityUnit, timeUnit: TimeUnit) {
    if (value < 0) throw new Error(WasteDemand.ERROR_DEMAND_NEGATIVE);
    if (!quantityUnit || !timeUnit) throw new Error(WasteDemand.ERROR_UNITS_NULL);
    this.value = value;
    this.quantityUnit = quantityUnit;
    this.timeUnit = timeUnit;
  }

  /**
   * Convenience constructor with defaults (tons/day).
   * @param value numeric demand value
   */
  static withDefaultUnit(value: number): WasteDemand {
    return new WasteDemand(value, new QuantityUnit('tons'), TimeUnit.DAY);
  }

  /** Get numeric demand value. */
  getValue(): number { return this.value; }
  /** Get quantity unit. */
  getQuantityUnit(): QuantityUnit { return this.quantityUnit; }
  /** Get time unit. */
  getTimeUnit(): TimeUnit { return this.timeUnit; }

  /** Return a copy with updated value. */
  setValue(newValue: number): WasteDemand { return new WasteDemand(newValue, this.quantityUnit, this.timeUnit); }
  /** Return a copy with updated quantity unit. */
  setQuantityUnit(newQuantityUnit: QuantityUnit): WasteDemand { return new WasteDemand(this.value, newQuantityUnit, this.timeUnit); }
  /** Return a copy with updated time unit. */
  setTimeUnit(newTimeUnit: TimeUnit): WasteDemand { return new WasteDemand(this.value, this.quantityUnit, newTimeUnit); }

  /**
   * Add another WasteDemand with matching units.
   * @param other demand to add
   */
  add(other: WasteDemand): WasteDemand {
    this.validateSameUnit(other);
    return new WasteDemand(this.value + other.value, this.quantityUnit, this.timeUnit);
  }

  /**
   * Compare greater-than with another WasteDemand or Capacity. Units must match.
   * @param other WasteDemand or Capacity
   */
  greaterThan(other: WasteDemand | Capacity): boolean {
    if (other == null) throw new Error(WasteDemand.ERROR_OTHER_DEMAND_NULL);
    if (other instanceof Capacity) {
      if (!this.quantityUnit.equals(other.getQuantityUnit()) || this.timeUnit !== other.getTimeUnit()) {
        throw new Error(WasteDemand.ERROR_UNITS_MUST_MATCH_CAPACITY);
      }
      return this.value > other.getValue();
    }
    // other is WasteDemand
    this.validateSameUnit(other as WasteDemand);
    return this.value > (other as WasteDemand).value;
  }

  /** Ensure both WasteDemand instances use the same units; throws otherwise. */
  private validateSameUnit(other: WasteDemand) {
    if (other == null) throw new Error(WasteDemand.ERROR_OTHER_DEMAND_NULL);
    if (!this.quantityUnit.equals(other.quantityUnit) || this.timeUnit !== other.timeUnit) {
      throw new Error(WasteDemand.ERROR_UNITS_MUST_MATCH);
    }
  }

  /** Equality check for WasteDemand (value and units). */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof WasteDemand)) return false;
    return Math.abs(this.value - other.value) < Number.EPSILON && this.quantityUnit.equals(other.quantityUnit) && this.timeUnit === other.timeUnit;
  }

  /** Human-readable representation. */
  toString(): string {
    return `WasteDemand=${this.value} ${this.quantityUnit.getValue()}/${this.timeUnit}`;
  }
}
