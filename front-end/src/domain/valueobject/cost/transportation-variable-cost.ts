import { Currency } from './currency';

/**
 * TransportationVariableCost
 *
 * Represents the variable transportation cost (per km or unit).
 */
export class TransportationVariableCost {
  private static readonly ERROR_AMOUNT_NOT_DEFINED = 'Transportation variable cost is not defined';
  private static readonly ERROR_AMOUNT_NEGATIVE = 'Transportation variable cost cannot be negative';

  readonly amount: number; // 2 decimals
  readonly currency: Currency;

  constructor(amount: number, currency?: Currency | string) {
    this.validateAmount(amount);
    if (currency == null) this.currency = new Currency();
    else if (typeof currency === 'string') this.currency = new Currency(currency);
    else this.currency = currency;
    this.amount = Math.round((amount + Number.EPSILON) * 100) / 100;
  }

  private validateAmount(amount: number) {
    if (Number.isNaN(amount)) throw new Error(TransportationVariableCost.ERROR_AMOUNT_NOT_DEFINED);
    if (amount < 0) throw new Error(TransportationVariableCost.ERROR_AMOUNT_NEGATIVE);
  }

  getAmount(): number { return this.amount; }
  getCurrency(): Currency { return this.currency; }

  add(other: TransportationVariableCost): TransportationVariableCost {
    this.checkCurrencyCompatibility(other);
    return new TransportationVariableCost(this.amount + other.amount, this.currency);
  }

  subtract(other: TransportationVariableCost): TransportationVariableCost {
    this.checkCurrencyCompatibility(other);
    let result = this.amount - other.amount;
    if (result < 0) result = 0;
    return new TransportationVariableCost(result, this.currency);
  }

  greaterThan(other: TransportationVariableCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount > other.amount;
  }

  lessThan(other: TransportationVariableCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount < other.amount;
  }

  private checkCurrencyCompatibility(other: TransportationVariableCost) {
    if (!this.currency.equals(other.currency)) throw new Error('Cannot operate on costs with different currencies');
  }

  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof TransportationVariableCost)) return false;
    return Math.abs(this.amount - other.amount) < 0.005 && this.currency.equals(other.currency);
  }

  toString(): string { return `TransportationVariableCost={amount=${this.amount.toFixed(2)}, currency='${this.currency.getCode()}'}`; }
}
