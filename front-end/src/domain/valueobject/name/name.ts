/**
 * Name
 *
 * Immutable value object for a human-readable entity name (mirrors backend `Name`).
 */
export class Name {
  /** Error message used when the name is null or blank. */
  private static readonly NOT_DEFINED = 'Name must not be null or blank';

  /** Trimmed name value. */
  private readonly value: string;

  constructor(value: string) {
    if (value == null || String(value).trim() === '') {
      throw new Error(Name.NOT_DEFINED);
    }
    this.value = String(value).trim();
  }

  /**
   * Return the stored name value.
   * @returns trimmed name string
   */
  getValue(): string {
    return this.value;
  }

  /**
   * Equality by trimmed name value.
   * @param other value to compare with
   * @returns true when both names have the same trimmed value
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Name)) return false;
    return this.value === other.value;
  }

  /**
   * Human-readable representation.
   * @returns formatted name string
   */
  toString(): string {
    return `Name={${this.value}}`;
  }
}
