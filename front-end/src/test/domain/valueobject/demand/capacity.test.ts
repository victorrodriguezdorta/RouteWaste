import { describe, expect, it } from 'vitest';
import { TimeUnit } from '@/domain/enumerate/time-unit';
import { Capacity } from '@/domain/valueobject/demand/capacity';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';

describe('Capacity', () => {
  it('should compare capacities with same units', () => {
    const unit = new QuantityUnit('tons');
    const larger = new Capacity(10, unit, TimeUnit.DAY);
    const smaller = new Capacity(5, unit, TimeUnit.DAY);

    expect(larger.greaterThan(smaller)).toBe(true);
    expect(larger.equals(new Capacity(10, unit, TimeUnit.DAY))).toBe(true);
  });

  it('should reject mismatched units on compare', () => {
    const a = new Capacity(1, new QuantityUnit('tons'), TimeUnit.DAY);
    const b = new Capacity(1, new QuantityUnit('kg'), TimeUnit.DAY);
    expect(() => a.greaterThan(b)).toThrow('Units must be the same');
  });

  it('should reject negative values', () => {
    expect(() => new Capacity(-1, new QuantityUnit('tons'), TimeUnit.DAY)).toThrow('Capacity value must be greater than or equal to 0');
  });
});
