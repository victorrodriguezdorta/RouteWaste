/**
 * Name
 *
 * Immutable value object for a human-readable entity name (mirrors backend `Name`).
 */
export class Name {
  private static readonly NOT_DEFINED = 'Name must not be null or blank';

  private readonly value: string;

  constructor(value: string) {
    if (value == null || String(value).trim() === '') {
      throw new Error(Name.NOT_DEFINED);
    }
    this.value = String(value).trim();
  }

  getValue(): string {
    return this.value;
  }

  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Name)) return false;
    return this.value === other.value;
  }

  toString(): string {
    return `Name={${this.value}}`;
  }
}
