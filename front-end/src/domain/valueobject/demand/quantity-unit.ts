/**
 * QuantityUnit
 *
 * Represents a unit of measurement for quantities (only letters allowed).
 */
export class QuantityUnit {
  private static readonly UNIT_REGEX = /^[a-zA-Z]+$/;
  readonly value: string;

  /**
   * Create a QuantityUnit.
   * @param value alphabetic unit string (e.g., 'tons')
   */
  constructor(value: string) {
    this.validate(value);
    this.value = value;
  }

  /**
   * Validate unit string.
   * @param value candidate unit
   * @throws Error when null/empty or contains non-letters
   */
  private validate(value: string) {
    if (value == null) throw new Error('Quantity unit is not defined');
    if (value.length === 0) throw new Error('Quantity unit cannot be empty');
    if (!QuantityUnit.UNIT_REGEX.test(value)) throw new Error('Quantity unit format is invalid');
  }

  /** Get raw unit string. */
  getValue(): string {
    return this.value;
  }

  /** Return a new QuantityUnit with given value. */
  setValue(newValue: string): QuantityUnit {
    return new QuantityUnit(newValue);
  }

  /** Equality check by unit string. */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof QuantityUnit)) return false;
    return this.value === other.value;
  }

  /** Human-readable representation. */
  toString(): string {
    return `QuantityUnit={value='${this.value}'}`;
  }
}
