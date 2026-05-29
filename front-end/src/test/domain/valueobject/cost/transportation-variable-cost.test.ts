import { describe, expect, it } from 'vitest';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';

describe('TransportationVariableCost', () => {
  it('should add variable costs', () => {
    const total = new TransportationVariableCost(1.2).add(new TransportationVariableCost(0.8));
    expect(total.getAmount()).toBe(2);
  });

  it('should reject negative amounts', () => {
    expect(() => new TransportationVariableCost(-1)).toThrow('Transportation variable cost cannot be negative');
  });
});
