import { describe, expect, it } from 'vitest';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';

describe('VehicleCapacityLiters', () => {
  it('should create with zero and positive values', () => {
    expect(new VehicleCapacityLiters(0).getLiters()).toBe(0);
    expect(new VehicleCapacityLiters(42.5).getLiters()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new VehicleCapacityLiters(-1)).toThrow('Vehicle capacity cannot be negative');
  });

  it('should compare equality', () => {
    const left = new VehicleCapacityLiters(10);
    const right = new VehicleCapacityLiters(10);
    const other = new VehicleCapacityLiters(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
