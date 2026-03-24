/**
 * QuantityUnit
 *
 * Represents a unit of measurement for quantities (only letters allowed).
 */
export class QuantityUnit {
  /**
   * Expresión regular para validar que la unidad solo contiene letras.
   */
  private static readonly UNIT_REGEX = /^[a-zA-Z]+$/;

  /**
   * Valor de la unidad de cantidad (solo letras).
   */
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


  /**
   * Devuelve el valor de la unidad como string.
   * @returns El valor de la unidad de cantidad.
   */
  getValue(): string {
    return this.value;
  }


  /**
   * Devuelve una nueva instancia de QuantityUnit con el valor proporcionado.
   * @param newValue Nuevo valor para la unidad de cantidad.
   * @returns Nueva instancia de QuantityUnit.
   */
  setValue(newValue: string): QuantityUnit {
    return new QuantityUnit(newValue);
  }


  /**
   * Comprueba la igualdad con otra instancia de QuantityUnit.
   * @param other Objeto a comparar.
   * @returns true si ambos objetos son QuantityUnit y tienen el mismo valor, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof QuantityUnit)) return false;
    return this.value === other.value;
  }

  /**
   * Devuelve una representación legible de la unidad de cantidad.
   * @returns Cadena representando la instancia QuantityUnit.
   */
  toString(): string {
    return `QuantityUnit={value='${this.value}'}`;
  }
}
