import { Currency } from './currency';

/**
 * TotalCost
 *
 * Represents the total cost with amount and currency.
 */
export class TotalCost {
  private static readonly ERROR_AMOUNT_NOT_DEFINED = 'Total cost is not defined';
  private static readonly ERROR_AMOUNT_NEGATIVE = 'Total cost cannot be negative';

  readonly amount: number; // stored with 2 decimals
  readonly currency: Currency;

  /**
   * Create a TotalCost.
   * @param amount numeric amount (must be >= 0)
   * @param currency either a Currency instance or ISO code string; defaults to EUR
   */
  constructor(amount: number, currency?: Currency | string) {
    this.validateAmount(amount);
    if (currency == null) {
      this.currency = new Currency();
    } else if (typeof currency === 'string') {
      this.currency = new Currency(currency);
    } else {
      this.currency = currency;
    }
    this.amount = Math.round((amount + Number.EPSILON) * 100) / 100;
  }

  /**
   * Validate the numeric amount.
   * @param amount candidate amount
   * @throws Error when NaN or negative
   */
  private validateAmount(amount: number) {
    if (Number.isNaN(amount)) throw new Error(TotalCost.ERROR_AMOUNT_NOT_DEFINED);
    if (amount < 0) throw new Error(TotalCost.ERROR_AMOUNT_NEGATIVE);
  }

  /**
   * Get amount (2-decimal precision).
   */
  getAmount(): number { return this.amount; }
  /**
   * Get currency instance.
   */
  getCurrency(): Currency { return this.currency; }

  /**
   * Add another TotalCost with the same currency.
   * @param other cost to add
   */
  add(other: TotalCost): TotalCost {
    this.checkCurrencyCompatibility(other);
    return new TotalCost(this.amount + other.amount, this.currency);
  }

  /**
   * Subtract another TotalCost (result floored at 0).
   * @param other cost to subtract
   */
  subtract(other: TotalCost): TotalCost {
    this.checkCurrencyCompatibility(other);
    let result = this.amount - other.amount;
    if (result < 0) result = 0;
    return new TotalCost(result, this.currency);
  }

  /**
   * Compare greater-than with another TotalCost (same currency required).
   */
  greaterThan(other: TotalCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount > other.amount;
  }

  /**
   * Compare less-than with another TotalCost (same currency required).
   */
  lessThan(other: TotalCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount < other.amount;
  }

  /**
   * Ensure both costs use the same currency.
   * @throws Error when currencies differ
   */
  private checkCurrencyCompatibility(other: TotalCost | { getCurrency(): Currency }) {
    if (!this.currency.equals((other as any).getCurrency())) {
      throw new Error('Cannot operate on costs with different currencies');
    }
  }

  /**
   * Equality with tolerance for floating rounding (0.005).
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof TotalCost)) return false;
    return Math.abs(this.amount - other.amount) < 0.005 && this.currency.equals(other.currency);
  }

  /**
   * Human-readable representation with two decimals.
   */
  toString(): string {
    return `TotalCost={amount=${this.amount.toFixed(2)}, currency='${this.currency.getCode()}'}`;
  }
}
