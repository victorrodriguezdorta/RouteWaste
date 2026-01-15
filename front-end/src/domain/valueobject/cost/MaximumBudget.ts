import { Currency } from './Currency';

/**
 * MaximumBudget
 *
 * Represents the maximum allowed budget.
 */
export class MaximumBudget {
  private static readonly ERROR_AMOUNT_NOT_DEFINED = 'Maximum budget is not defined';
  private static readonly ERROR_AMOUNT_NEGATIVE = 'Maximum budget cannot be negative';

  readonly amount: number;
  readonly currency: Currency;

  /**
   * Create a MaximumBudget.
   * @param amount numeric amount (>= 0)
   * @param currency Currency instance or ISO code string (defaults to EUR)
   */
  constructor(amount: number, currency?: Currency | string) {
    this.validateAmount(amount);
    if (currency == null) this.currency = new Currency();
    else if (typeof currency === 'string') this.currency = new Currency(currency);
    else this.currency = currency;
    this.amount = Math.round((amount + Number.EPSILON) * 100) / 100;
  }

  /**
   * Validate numeric amount.
   * @param amount candidate amount
   * @throws Error when NaN or negative
   */
  private validateAmount(amount: number) {
    if (Number.isNaN(amount)) throw new Error(MaximumBudget.ERROR_AMOUNT_NOT_DEFINED);
    if (amount < 0) throw new Error(MaximumBudget.ERROR_AMOUNT_NEGATIVE);
  }

  /** Get amount (2-decimal precision). */
  getAmount(): number { return this.amount; }
  /** Get currency instance. */
  getCurrency(): Currency { return this.currency; }

  /** Add another MaximumBudget (same currency required). */
  add(other: MaximumBudget): MaximumBudget {
    this.checkCurrencyCompatibility(other);
    return new MaximumBudget(this.amount + other.amount, this.currency);
  }

  /** Subtract another MaximumBudget (floored to 0). */
  subtract(other: MaximumBudget): MaximumBudget {
    this.checkCurrencyCompatibility(other);
    let result = this.amount - other.amount;
    if (result < 0) result = 0;
    return new MaximumBudget(result, this.currency);
  }

  /** Compare greater-than with another MaximumBudget (same currency required). */
  greaterThan(other: MaximumBudget): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount > other.amount;
  }

  /** Compare less-than with another MaximumBudget (same currency required). */
  lessThan(other: MaximumBudget): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount < other.amount;
  }

  /** Ensure both budgets use the same currency; throws if not. */
  private checkCurrencyCompatibility(other: MaximumBudget) {
    if (!this.currency.equals(other.currency)) throw new Error('Cannot operate on budgets with different currencies');
  }

  /** Equality with tolerance for rounding differences. */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof MaximumBudget)) return false;
    return Math.abs(this.amount - other.amount) < 0.005 && this.currency.equals(other.currency);
  }

  /** Human-readable representation. */
  toString(): string {
    return `MaximumBudget={amount=${this.amount.toFixed(2)}, currency='${this.currency.getCode()}'}`;
  }
}
