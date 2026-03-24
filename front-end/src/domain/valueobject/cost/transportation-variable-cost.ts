import { Currency } from './currency';

/**
 * TransportationVariableCost
 *
 * Represents the variable transportation cost (per km or unit).
 */
export class TransportationVariableCost {
  /**
   * Mensaje de error cuando el coste variable de transporte no está definido.
   */
  private static readonly ERROR_AMOUNT_NOT_DEFINED = 'Transportation variable cost is not defined';
  /**
   * Mensaje de error cuando el coste variable de transporte es negativo.
   */
  private static readonly ERROR_AMOUNT_NEGATIVE = 'Transportation variable cost cannot be negative';

  /**
   * Cantidad numérica del coste variable de transporte (2 decimales).
   */
  readonly amount: number;
  /**
   * Moneda asociada al coste variable de transporte.
   */
  readonly currency: Currency;

  /**
   * Crea un nuevo coste variable de transporte.
   * @param amount Cantidad numérica (2 decimales).
   * @param currency Moneda asociada (opcional, por defecto EUR).
   */
  constructor(amount: number, currency?: Currency | string) {
    this.validateAmount(amount);
    if (currency == null) this.currency = new Currency();
    else if (typeof currency === 'string') this.currency = new Currency(currency);
    else this.currency = currency;
    this.amount = Math.round((amount + Number.EPSILON) * 100) / 100;
  }

  /**
   * Valida que la cantidad sea válida y no negativa.
   * @param amount Cantidad a validar.
   * @throws Si la cantidad no está definida o es negativa.
   */
  private validateAmount(amount: number) {
    if (Number.isNaN(amount)) throw new Error(TransportationVariableCost.ERROR_AMOUNT_NOT_DEFINED);
    if (amount < 0) throw new Error(TransportationVariableCost.ERROR_AMOUNT_NEGATIVE);
  }

  /**
   * Obtiene la cantidad (precisión de 2 decimales).
   * @returns La cantidad numérica del coste variable de transporte.
   */
  getAmount(): number { return this.amount; }

  /**
   * Obtiene la instancia de moneda.
   * @returns La moneda asociada al coste variable de transporte.
   */
  getCurrency(): Currency { return this.currency; }

  /**
   * Suma otro TransportationVariableCost (requiere misma moneda).
   * @param other Otro objeto TransportationVariableCost a sumar.
   * @returns Un nuevo TransportationVariableCost con la suma.
   */
  add(other: TransportationVariableCost): TransportationVariableCost {
    this.checkCurrencyCompatibility(other);
    return new TransportationVariableCost(this.amount + other.amount, this.currency);
  }

  /**
   * Resta otro TransportationVariableCost (piso en 0).
   * @param other Otro objeto TransportationVariableCost a restar.
   * @returns Un nuevo TransportationVariableCost con la resta.
   */
  subtract(other: TransportationVariableCost): TransportationVariableCost {
    this.checkCurrencyCompatibility(other);
    let result = this.amount - other.amount;
    if (result < 0) result = 0;
    return new TransportationVariableCost(result, this.currency);
  }

  /**
   * Compara si es mayor que otro TransportationVariableCost (requiere misma moneda).
   * @param other Otro objeto TransportationVariableCost para comparar.
   * @returns true si es mayor, false en caso contrario.
   */
  greaterThan(other: TransportationVariableCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount > other.amount;
  }

  /**
   * Compara si es menor que otro TransportationVariableCost (requiere misma moneda).
   * @param other Otro objeto TransportationVariableCost para comparar.
   * @returns true si es menor, false en caso contrario.
   */
  lessThan(other: TransportationVariableCost): boolean {
    this.checkCurrencyCompatibility(other);
    return this.amount < other.amount;
  }

  /**
   * Asegura que ambos costes usan la misma moneda; lanza excepción si no.
   * @param other Otro objeto TransportationVariableCost para comprobar compatibilidad.
   * @throws Si las monedas no son compatibles.
   */
  private checkCurrencyCompatibility(other: TransportationVariableCost) {
    if (!this.currency.equals(other.currency)) throw new Error('Cannot operate on costs with different currencies');
  }

  /**
   * Comprueba igualdad con tolerancia a redondeo.
   * @param other Objeto a comparar.
   * @returns true si son iguales, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof TransportationVariableCost)) return false;
    return Math.abs(this.amount - other.amount) < 0.005 && this.currency.equals(other.currency);
  }

  /**
   * Representación legible en texto.
   * @returns Cadena representando el objeto TransportationVariableCost.
   */
  toString(): string { return `TransportationVariableCost={amount=${this.amount.toFixed(2)}, currency='${this.currency.getCode()}'}`; }
}
