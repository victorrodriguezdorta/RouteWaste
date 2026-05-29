import { describe, expect, it } from 'vitest';
import { TimeUnit } from '@/domain/enumerate/time-unit';
import { Capacity } from '@/domain/valueobject/demand/capacity';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';

describe('WasteDemand', () => {
  it('should create with default units', () => {
    const demand = WasteDemand.withDefaultUnit(12);
    expect(demand.getValue()).toBe(12);
    expect(demand.getQuantityUnit().getValue()).toBe('tons');
    expect(demand.getTimeUnit()).toBe(TimeUnit.DAY);
  });

  it('should add demands with same units', () => {
    const unit = new QuantityUnit('tons');
    const total = new WasteDemand(5, unit, TimeUnit.DAY).add(new WasteDemand(3, unit, TimeUnit.DAY));
    expect(total.getValue()).toBe(8);
  });

  it('should compare against capacity with matching units', () => {
    const unit = new QuantityUnit('tons');
    const demand = new WasteDemand(6, unit, TimeUnit.DAY);
    const capacity = new Capacity(10, unit, TimeUnit.DAY);
    expect(capacity.greaterThan(demand) || !demand.greaterThan(capacity)).toBe(true);
    expect(demand.greaterThan(capacity)).toBe(false);
  });
});
