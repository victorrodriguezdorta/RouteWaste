import { describe, expect, it } from 'vitest';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';

describe('QuantityUnit', () => {
  it('should accept alphabetic units', () => {
    expect(new QuantityUnit('tons').getValue()).toBe('tons');
  });

  it('should reject invalid units', () => {
    expect(() => new QuantityUnit('')).toThrow('Quantity unit cannot be empty');
    expect(() => new QuantityUnit('kg1')).toThrow('Quantity unit format is invalid');
  });
});
