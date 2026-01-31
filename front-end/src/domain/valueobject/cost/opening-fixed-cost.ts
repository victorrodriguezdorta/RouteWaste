import { Currency } from './currency';

/**
 * OpeningFixedCost
 *
 * Represents the fixed cost incurred when opening a facility.
 */
export class OpeningFixedCost {
  private static readonly ERROR_AMOUNT_NOT_DEFINED = 'Opening fixed cost is not defined';
  private static readonly ERROR_AMOUNT_NEGATIVE = 'Opening fixed cost cannot be negative';

  readonly amount: number; // stored with 2 decimals
  readonly currency: Currency;

  constructor(amount: number, currency?: Currency | string) {
    this.validateAmount(amount);
    if (currency == null) this.currency = new Currency();
    else if (typeof currency === 'string') this.currency = new Currency(currency);
    else this.currency = currency;
    this.amount = Math.round((amount + Number.EPSILON) * 100) / 100;
  }

  private validateAmount(amount: number) {
    if (Number.isNaN(amount)) throw new Error(OpeningFixedCost.ERROR_AMOUNT_NOT_DEFINED);
    if (amount < 0) throw new Error(OpeningFixedCost.ERROR_AMOUNT_NEGATIVE);
  }

  getAmount(): number { return this.amount; }
  getCurrency(): Currency { return this.currency; }

  add(other: OpeningFixedCost): OpeningFixedCost {
    this.checkCurrencyCompatibility(other);
    return new OpeningFixedCost(this.amount + other.amount, this.currency);
  }

  subtract(other: OpeningFixedCost): OpeningFixedCost {
    this.checkCurrencyCompatibility(other);
    let result = this.amount - other.amount;
    if (result < 0) result = 0;
    return new OpeningFixedCost(result, this.currency);
  }

  greaterThan(other: OpeningFixedCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount > other.amount;
  }

  lessThan(other: OpeningFixedCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount < other.amount;
  }

  private checkCurrencyCompatibility(other: OpeningFixedCost) {
    if (!this.currency.equals(other.currency)) throw new Error('Cannot operate on costs with different currencies');
  }

  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof OpeningFixedCost)) return false;
    return Math.abs(this.amount - other.amount) < 0.005 && this.currency.equals(other.currency);
  }

  toString(): string { return `OpeningFixedCost={amount=${this.amount.toFixed(2)}, currency='${this.currency.getCode()}'}`; }
}
