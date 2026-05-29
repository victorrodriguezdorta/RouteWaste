import { describe, expect, it } from 'vitest';
import { Currency } from '@/domain/valueobject/cost/currency';

describe('Currency', () => {
  it('should default to EUR', () => {
    expect(new Currency().getCode()).toBe('EUR');
  });

  it('should accept valid ISO codes', () => {
    expect(new Currency('USD').getCode()).toBe('USD');
  });

  it('should reject invalid codes', () => {
    expect(() => new Currency('EU')).toThrow('Currency format is invalid');
    expect(() => new Currency('')).toThrow('Currency cannot be empty');
  });
});
