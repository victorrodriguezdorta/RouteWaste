import { describe, expect, it } from 'vitest';
import { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';

describe('OpeningFixedCost', () => {
  it('should create and add costs', () => {
    const total = new OpeningFixedCost(100).add(new OpeningFixedCost(50));
    expect(total.getAmount()).toBe(150);
  });

  it('should reject negative amounts', () => {
    expect(() => new OpeningFixedCost(-0.01)).toThrow('Opening fixed cost cannot be negative');
  });
});
