import { describe, expect, it } from 'vitest';
import { TotalCost } from '@/domain/valueobject/cost/total-cost';

describe('TotalCost', () => {
  it('should add costs with same currency', () => {
    expect(new TotalCost(10).add(new TotalCost(2.5)).getAmount()).toBe(12.5);
  });

  it('should reject different currencies on add', () => {
    expect(() => new TotalCost(10, 'EUR').add(new TotalCost(1, 'USD'))).toThrow(
      'Cannot operate on costs with different currencies',
    );
  });

  it('should reject invalid amounts', () => {
    expect(() => new TotalCost(-1)).toThrow('Total cost cannot be negative');
  });
});
