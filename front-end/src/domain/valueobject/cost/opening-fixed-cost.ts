import { Currency } from './currency';

/**
 * OpeningFixedCost
 *
 * Represents the fixed cost incurred when opening a facility.
 */
export class OpeningFixedCost {
  /**
   * Mensaje de error cuando el monto no está definido.
   */
  private static readonly ERROR_AMOUNT_NOT_DEFINED = 'Opening fixed cost is not defined';
  /**
   * Mensaje de error cuando el monto es negativo.
   */
  private static readonly ERROR_AMOUNT_NEGATIVE = 'Opening fixed cost cannot be negative';

  /**
   * Monto del coste fijo de apertura (2 decimales).
   */
  readonly amount: number;
  /**
   * Moneda del coste fijo de apertura.
   */
  readonly currency: Currency;

  constructor(amount: number, currency?: Currency | string) {
    this.validateAmount(amount);
    if (currency == null) this.currency = new Currency();
    else if (typeof currency === 'string') this.currency = new Currency(currency);
    else this.currency = currency;
    this.amount = Math.round((amount + Number.EPSILON) * 100) / 100;
  }


  /**
   * Valida que el monto esté definido y no sea negativo.
   * @param amount Monto a validar.
   */
  private validateAmount(amount: number) {
    if (Number.isNaN(amount)) throw new Error(OpeningFixedCost.ERROR_AMOUNT_NOT_DEFINED);
    if (amount < 0) throw new Error(OpeningFixedCost.ERROR_AMOUNT_NEGATIVE);
  }


  /**
   * Devuelve el monto del coste fijo de apertura.
   * @returns Monto como número.
   */
  getAmount(): number { return this.amount; }

  /**
   * Devuelve la moneda del coste fijo de apertura.
   * @returns Instancia de Currency.
   */
  getCurrency(): Currency { return this.currency; }


  /**
   * Suma otro coste fijo de apertura (debe ser la misma moneda).
   * @param other Otro OpeningFixedCost a sumar.
   * @returns Nuevo OpeningFixedCost con la suma.
   */
  add(other: OpeningFixedCost): OpeningFixedCost {
    this.checkCurrencyCompatibility(other);
    return new OpeningFixedCost(this.amount + other.amount, this.currency);
  }


  /**
   * Resta otro coste fijo de apertura (debe ser la misma moneda).
   * @param other Otro OpeningFixedCost a restar.
   * @returns Nuevo OpeningFixedCost con la diferencia (mínimo 0).
   */
  subtract(other: OpeningFixedCost): OpeningFixedCost {
    this.checkCurrencyCompatibility(other);
    let result = this.amount - other.amount;
    if (result < 0) result = 0;
    return new OpeningFixedCost(result, this.currency);
  }


  /**
   * Indica si este coste fijo de apertura es mayor que otro (misma moneda).
   * @param other Otro OpeningFixedCost a comparar.
   * @returns True si es mayor, false en caso contrario.
   */
  greaterThan(other: OpeningFixedCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount > other.amount;
  }


  /**
   * Indica si este coste fijo de apertura es menor que otro (misma moneda).
   * @param other Otro OpeningFixedCost a comparar.
   * @returns True si es menor, false en caso contrario.
   */
  lessThan(other: OpeningFixedCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount < other.amount;
  }


  /**
   * Verifica que la moneda de ambos costes sea la misma.
   * @param other Otro OpeningFixedCost a comparar.
   */
  private checkCurrencyCompatibility(other: OpeningFixedCost) {
    if (!this.currency.equals(other.currency)) throw new Error('Cannot operate on costs with different currencies');
  }


  /**
   * Compara si dos OpeningFixedCost son iguales (monto y moneda).
   * @param other Otro objeto a comparar.
   * @returns True si ambos son iguales, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof OpeningFixedCost)) return false;
    return Math.abs(this.amount - other.amount) < 0.005 && this.currency.equals(other.currency);
  }

  /**
   * Devuelve una representación legible del coste fijo de apertura.
   * @returns Cadena con el monto y la moneda.
   */
  toString(): string { return `OpeningFixedCost={amount=${this.amount.toFixed(2)}, currency='${this.currency.getCode()}'}`; }
}
