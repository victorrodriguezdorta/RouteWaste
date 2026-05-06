/**
 * RouteSequence
 *
 * Represents the sequence number of a stop in a route.
 * Immutable value object that encapsulates the sequence position.
 * Used to track the order of stops in a daily plan route.
 */
export class RouteSequence {
  /**
   * Sequence value (order position in route).
   * Must be a positive integer (>= 1).
   */
  private readonly sequence: number;

  /**
   * Private constructor. Use static factory `of` instead.
   * @param sequence The sequence number (must be >= 1)
   */
  private constructor(sequence: number) {
    this.validate(sequence);
    this.sequence = sequence;
  }

  /**
   * Validate that sequence is a positive integer.
   * @param sequence The sequence value to validate
   * @throws Error if sequence is less than 1
   */
  private validate(sequence: number): void {
    if (!Number.isInteger(sequence) || sequence < 1) {
      throw new Error('Route sequence must be a positive integer (>= 1)');
    }
  }

  /**
   * Create a RouteSequence from a sequence number.
   * @param sequence Sequence number (>= 1)
   * @returns A new RouteSequence instance
   * @throws Error if sequence is invalid
   */
  static of(sequence: number): RouteSequence {
    return new RouteSequence(sequence);
  }

  /**
   * Get the sequence value.
   * @returns The sequence number
   */
  getSequence(): number {
    return this.sequence;
  }

  /**
   * Get numeric sequence value.
   * @returns The sequence value
   */
  getValue(): number {
    return this.sequence;
  }

  /**
   * Create a new instance with an updated sequence.
   * @param newSequence The new sequence value
   * @returns A new RouteSequence instance
   * @throws Error if newSequence is invalid
   */
  setValue(newSequence: number): RouteSequence {
    return RouteSequence.of(newSequence);
  }

  /**
   * Get the next sequence in order.
   * @returns A new RouteSequence with sequence + 1
   */
  getNext(): RouteSequence {
    return RouteSequence.of(this.sequence + 1);
  }

  /**
   * Get the previous sequence in order.
   * @returns A new RouteSequence with sequence - 1
   * @throws Error if sequence is 1 (no previous)
   */
  getPrevious(): RouteSequence {
    if (this.sequence === 1) {
      throw new Error('Cannot get previous sequence from sequence 1');
    }
    return RouteSequence.of(this.sequence - 1);
  }

  /**
   * Compare if this sequence is greater than another.
   * @param other The other RouteSequence to compare
   * @returns true if this sequence is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: RouteSequence): boolean {
    if (!other) {
      throw new Error('Other RouteSequence cannot be null');
    }
    return this.sequence > other.sequence;
  }

  /**
   * Compare if this sequence is greater than or equal to another.
   * @param other The other RouteSequence to compare
   * @returns true if this sequence is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: RouteSequence): boolean {
    if (!other) {
      throw new Error('Other RouteSequence cannot be null');
    }
    return this.sequence >= other.sequence;
  }

  /**
   * Compare if this sequence is less than another.
   * @param other The other RouteSequence to compare
   * @returns true if this sequence is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: RouteSequence): boolean {
    if (!other) {
      throw new Error('Other RouteSequence cannot be null');
    }
    return this.sequence < other.sequence;
  }

  /**
   * Compare equality with another object.
   * @param otherObject The object to compare with
   * @returns true if the objects are equal, false otherwise
   */
  equals(otherObject: unknown): boolean {
    if (this === otherObject) {
      return true;
    }
    if (!(otherObject instanceof RouteSequence)) {
      return false;
    }
    return this.sequence === otherObject.sequence;
  }

  /**
   * Get the hash code for this object.
   * @returns A numeric hash code
   */
  hashCode(): number {
    return this.sequence;
  }

  /**
   * Human-readable representation.
   * @returns A formatted string representation
   */
  toString(): string {
    return `RouteSequence={sequence=${this.sequence}}`;
  }
}
