import { TimeUnit } from '@/domain/enumerate/time-unit';
import { Capacity } from './capacity';
import { QuantityUnit } from './quantity-unit';

/**
 * WasteDemand
 *
 * Represents expected waste demand per time unit.
 */
export class WasteDemand {
  /**
   * Error: Waste demand cannot be negative.
   */
  private static readonly ERROR_DEMAND_NEGATIVE = 'Waste demand cannot be negative';
  /**
   * Error: Units cannot be null.
   */
  private static readonly ERROR_UNITS_NULL = 'Units cannot be null';
  /**
   * Error: Other WasteDemand cannot be null.
   */
  private static readonly ERROR_OTHER_DEMAND_NULL = 'Other WasteDemand cannot be null';
  /**
   * Error: Units must be the same.
   */
  private static readonly ERROR_UNITS_MUST_MATCH = 'Units must be the same';
  /**
   * Error: Units must be the same to compare WasteDemand and Capacity.
   */
  private static readonly ERROR_UNITS_MUST_MATCH_CAPACITY = 'Units must be the same to compare WasteDemand and Capacity';

  /**
   * Valor numérico de la demanda de residuo.
   */
  readonly value: number;
  /**
   * Unidad de cantidad (por ejemplo, toneladas).
   */
  readonly quantityUnit: QuantityUnit;
  /**
   * Unidad de tiempo (por ejemplo, día).
   */
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
  /**
   * Convenience constructor with defaults (tons/day).
   * @param value numeric demand value
   * @returns WasteDemand con unidades por defecto (toneladas/día)
   */
  static withDefaultUnit(value: number): WasteDemand {
    return new WasteDemand(value, new QuantityUnit('tons'), TimeUnit.DAY);
  }

  /**
   * Obtiene el valor numérico de la demanda.
   * @returns Valor numérico de la demanda.
   */
  getValue(): number { return this.value; }
  /**
   * Obtiene la unidad de cantidad.
   * @returns Unidad de cantidad.
   */
  getQuantityUnit(): QuantityUnit { return this.quantityUnit; }
  /**
   * Obtiene la unidad de tiempo.
   * @returns Unidad de tiempo.
   */
  getTimeUnit(): TimeUnit { return this.timeUnit; }

  /**
   * Devuelve una copia con el valor actualizado.
   * @param newValue Nuevo valor numérico de la demanda.
   * @returns Nueva instancia de WasteDemand con el valor actualizado.
   */
  setValue(newValue: number): WasteDemand { return new WasteDemand(newValue, this.quantityUnit, this.timeUnit); }
  /**
   * Devuelve una copia con la unidad de cantidad actualizada.
   * @param newQuantityUnit Nueva unidad de cantidad.
   * @returns Nueva instancia de WasteDemand con la unidad de cantidad actualizada.
   */
  setQuantityUnit(newQuantityUnit: QuantityUnit): WasteDemand { return new WasteDemand(this.value, newQuantityUnit, this.timeUnit); }
  /**
   * Devuelve una copia con la unidad de tiempo actualizada.
   * @param newTimeUnit Nueva unidad de tiempo.
   * @returns Nueva instancia de WasteDemand con la unidad de tiempo actualizada.
   */
  setTimeUnit(newTimeUnit: TimeUnit): WasteDemand { return new WasteDemand(this.value, this.quantityUnit, newTimeUnit); }

  /**
   * Suma otra instancia de WasteDemand con unidades coincidentes.
   * @param other Demanda a sumar.
   * @returns Nueva instancia de WasteDemand con el valor sumado.
   */
  add(other: WasteDemand): WasteDemand {
    this.validateSameUnit(other);
    return new WasteDemand(this.value + other.value, this.quantityUnit, this.timeUnit);
  }

  /**
   * Compare greater-than with another WasteDemand or Capacity. Units must match.
   * @param other WasteDemand or Capacity
   */
  /**
   * Compara si es mayor que otra WasteDemand o Capacity. Las unidades deben coincidir.
   * @param other WasteDemand o Capacity a comparar.
   * @returns true si es mayor, false en caso contrario.
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
  /**
   * Asegura que ambas instancias de WasteDemand usan las mismas unidades; lanza excepción si no.
   * @param other Otra instancia de WasteDemand para comparar unidades.
   */
  private validateSameUnit(other: WasteDemand) {
    if (other == null) throw new Error(WasteDemand.ERROR_OTHER_DEMAND_NULL);
    if (!this.quantityUnit.equals(other.quantityUnit) || this.timeUnit !== other.timeUnit) {
      throw new Error(WasteDemand.ERROR_UNITS_MUST_MATCH);
    }
  }

  /** Equality check for WasteDemand (value and units). */
  /**
   * Verifica igualdad entre WasteDemand (valor y unidades).
   * @param other Objeto a comparar.
   * @returns true si son iguales, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof WasteDemand)) return false;
    return Math.abs(this.value - other.value) < Number.EPSILON && this.quantityUnit.equals(other.quantityUnit) && this.timeUnit === other.timeUnit;
  }

  /** Human-readable representation. */
  /**
   * Representación legible de la instancia.
   * @returns Cadena representando la demanda de residuo.
   */
  toString(): string {
    return `WasteDemand=${this.value} ${this.quantityUnit.getValue()}/${this.timeUnit}`;
  }
}
