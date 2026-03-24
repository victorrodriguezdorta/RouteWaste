/**
 * Currency
 *
 * ISO 4217 currency code (e.g. EUR, USD).
 */
export class Currency {
  /** Regular expression pattern for validating ISO 4217 currency codes (3 uppercase letters) */
  private static readonly CODE_REGEX = /^[A-Z]{3}$/;

  /** ISO 4217 currency code (e.g. EUR, USD, GBP) */
  readonly code: string;

  /**
   * Create a Currency.
   * @param code ISO 4217 currency code (3 upper-case letters). Defaults to 'EUR' if undefined.
   */
  constructor(code?: string) {
    const value = code ?? 'EUR';
    this.validateCode(value);
    this.code = value;
  }


  /**
   * Validate a currency code.
   * @param code candidate currency code to validate
   * @throws Error if null/empty or does not match ISO 4217 pattern
   */
  private validateCode(code: string) {
    if (code == null) throw new Error('Currency is not defined');
    if (code.length === 0) throw new Error('Currency cannot be empty');
    if (!Currency.CODE_REGEX.test(code)) throw new Error('Currency format is invalid');
  }

  /**
   * Return the ISO currency code.
   * @returns The ISO 4217 currency code as a string
   */
  getCode(): string {
    return this.code;
  }

  /**
   * Return a new Currency with the provided code.
   * @param newCode ISO 4217 code
   * @returns A new Currency instance with the provided code
   */
  setCode(newCode: string): Currency {
    return new Currency(newCode);
  }

  /**
   * Equality check.
   * @param other another object
   * @returns true when other is a Currency with the same code
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Currency)) return false;
    return this.code === other.code;
  }

  /**
   * Human-readable representation.
   * @returns A formatted string representation of the Currency object
   */
  toString(): string {
    return `Currency={code='${this.code}'}`;
  }
}
