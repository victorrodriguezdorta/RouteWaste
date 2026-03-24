import { Currency } from './currency';

/**
 * MaximumBudget
 *
 * Represents the maximum allowed budget.
 */
export class MaximumBudget {

  /**
   * Mensaje de error cuando el presupuesto máximo no está definido.
   */
  private static readonly ERROR_AMOUNT_NOT_DEFINED = 'Maximum budget is not defined';
  /**
   * Mensaje de error cuando el presupuesto máximo es negativo.
   */
  private static readonly ERROR_AMOUNT_NEGATIVE = 'Maximum budget cannot be negative';


  /**
   * Cantidad numérica del presupuesto máximo.
   */
  readonly amount: number;
  /**
   * Moneda asociada al presupuesto máximo.
   */
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

  /**
   * Obtiene la cantidad (precisión de 2 decimales).
   * @returns La cantidad numérica del presupuesto máximo.
   */
  getAmount(): number { return this.amount; }

  /**
   * Obtiene la instancia de moneda.
   * @returns La moneda asociada al presupuesto máximo.
   */
  getCurrency(): Currency { return this.currency; }

  /**
   * Suma otro MaximumBudget (requiere misma moneda).
   * @param other Otro objeto MaximumBudget a sumar.
   * @returns Un nuevo MaximumBudget con la suma.
   */
  add(other: MaximumBudget): MaximumBudget {
    this.checkCurrencyCompatibility(other);
    return new MaximumBudget(this.amount + other.amount, this.currency);
  }

  /**
   * Resta otro MaximumBudget (piso en 0).
   * @param other Otro objeto MaximumBudget a restar.
   * @returns Un nuevo MaximumBudget con la resta.
   */
  subtract(other: MaximumBudget): MaximumBudget {
    this.checkCurrencyCompatibility(other);
    let result = this.amount - other.amount;
    if (result < 0) result = 0;
    return new MaximumBudget(result, this.currency);
  }

  /**
   * Compara si es mayor que otro MaximumBudget (requiere misma moneda).
   * @param other Otro objeto MaximumBudget para comparar.
   * @returns true si es mayor, false en caso contrario.
   */
  greaterThan(other: MaximumBudget): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount > other.amount;
  }

  /**
   * Compara si es menor que otro MaximumBudget (requiere misma moneda).
   * @param other Otro objeto MaximumBudget para comparar.
   * @returns true si es menor, false en caso contrario.
   */
  lessThan(other: MaximumBudget): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount < other.amount;
  }

  /**
   * Asegura que ambos presupuestos usan la misma moneda; lanza excepción si no.
   * @param other Otro objeto MaximumBudget para comprobar compatibilidad.
   */
  private checkCurrencyCompatibility(other: MaximumBudget) {
    if (!this.currency.equals(other.currency)) throw new Error('Cannot operate on budgets with different currencies');
  }

  /**
   * Comprueba igualdad con tolerancia a redondeo.
   * @param other Objeto a comparar.
   * @returns true si son iguales, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof MaximumBudget)) return false;
    return Math.abs(this.amount - other.amount) < 0.005 && this.currency.equals(other.currency);
  }

  /**
   * Representación legible en texto.
   * @returns Cadena representando el objeto MaximumBudget.
   */
  toString(): string {
    return `MaximumBudget={amount=${this.amount.toFixed(2)}, currency='${this.currency.getCode()}'}`;
  }
}
