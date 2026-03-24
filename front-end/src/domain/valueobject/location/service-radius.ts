/**
 * ServiceRadius
 *
 * Represents the maximum allowed service radius for a facility (meters).
 */
export class ServiceRadius {
  /**
   * Mensaje de error para valores negativos de radio de servicio.
   */
  private static readonly ERROR_NEGATIVE = 'Service radius cannot be negative';

  /**
   * Valor numérico del radio de servicio (en metros).
   */
  readonly value: number;

  /**
   * Create ServiceRadius (meters).
   * @param value radius in meters (>= 0)
   */
  constructor(value: number) {
    if (value < 0) throw new Error(ServiceRadius.ERROR_NEGATIVE);
    this.value = value;
  }

  /**
   * Devuelve una copia con el valor actualizado.
   * @param newValue Nuevo valor del radio de servicio (en metros).
   * @returns Nueva instancia de ServiceRadius con el valor actualizado.
   */
  setValue(newValue: number): ServiceRadius {
    return new ServiceRadius(newValue);
  }

  /**
   * Comprueba la igualdad por valor del radio.
   * @param other Objeto a comparar.
   * @returns True si ambos radios son iguales, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ServiceRadius)) return false;
    return Math.abs(this.value - other.value) < Number.EPSILON;
  }

  /**
   * Representación legible como string.
   * @returns Cadena representando el objeto ServiceRadius.
   */
  toString(): string {
    return `ServiceRadius={value=${this.value}}`;
  }
}
