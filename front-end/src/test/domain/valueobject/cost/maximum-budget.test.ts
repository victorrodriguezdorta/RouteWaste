import { describe, expect, it } from 'vitest';
import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';

describe('MaximumBudget', () => {
  it('should round amount to two decimals', () => {
    expect(new MaximumBudget(10.556).getAmount()).toBe(10.56);
  });

  it('should add budgets with same currency', () => {
    const sum = new MaximumBudget(10).add(new MaximumBudget(5));
    expect(sum.getAmount()).toBe(15);
  });

  it('should reject invalid amounts', () => {
    expect(() => new MaximumBudget(-1)).toThrow('Maximum budget cannot be negative');
    expect(() => new MaximumBudget(Number.NaN)).toThrow('Maximum budget is not defined');
  });
});
