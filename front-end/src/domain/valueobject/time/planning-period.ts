/**
 * PlanningPeriod
 *
 * Represents the planning horizon (e.g., "2026" or "2026-Q1").
 */

export class PlanningPeriod {
  /**
   * Expresión regular para validar el formato del periodo de planificación (YYYY o YYYY-Q[1-4]).
   */
  private static readonly PERIOD_REGEX = /^\d{4}(-Q[1-4])?$/;

  /**
   * Mensaje de error cuando el periodo de planificación no está definido.
   */
  private static readonly ERROR_NOT_DEFINED = 'Planning period is not defined';

  /**
   * Mensaje de error cuando el formato del periodo de planificación es inválido.
   */
  private static readonly ERROR_FORMAT_INVALID = 'Planning period format is invalid. Expected formats: YYYY or YYYY-Q[1-4]';

  /**
   * Valor del periodo de planificación en formato string.
   */
  readonly value: string;

  /**
   * Create a PlanningPeriod.
   * @param value string in format YYYY or YYYY-Q[1-4]
   */
  constructor(value: string) {
    if (value == null) throw new Error(PlanningPeriod.ERROR_NOT_DEFINED);
    if (!PlanningPeriod.PERIOD_REGEX.test(value)) throw new Error(PlanningPeriod.ERROR_FORMAT_INVALID);
    this.value = value;
  }


  /**
   * Devuelve el valor bruto del periodo de planificación.
   * @returns El periodo de planificación como string.
   */
  getValue(): string { return this.value; }


  /**
   * Comprueba la igualdad por valor del periodo de planificación.
   * @param other Otro objeto a comparar.
   * @returns true si ambos objetos representan el mismo periodo de planificación, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof PlanningPeriod)) return false;
    return this.value === other.value;
  }

  /**
   * Representación legible del periodo de planificación.
   * @returns Una cadena representando el objeto PlanningPeriod.
   */
  toString(): string { return `PlanningPeriod={value='${this.value}'}`; }
}
