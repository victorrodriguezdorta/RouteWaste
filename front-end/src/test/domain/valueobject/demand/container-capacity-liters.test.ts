import { describe, expect, it } from 'vitest';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';

describe('ContainerCapacityLiters', () => {
  it('should create with zero and positive values', () => {
    expect(new ContainerCapacityLiters(0).getLiters()).toBe(0);
    expect(new ContainerCapacityLiters(42.5).getLiters()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new ContainerCapacityLiters(-1)).toThrow('Container capacity cannot be negative');
  });

  it('should compare equality', () => {
    const left = new ContainerCapacityLiters(10);
    const right = new ContainerCapacityLiters(10);
    const other = new ContainerCapacityLiters(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
