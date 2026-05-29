import { describe, expect, it } from 'vitest';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';

describe('VehicleCapacityKilograms', () => {
  it('should create with zero and positive values', () => {
    expect(new VehicleCapacityKilograms(0).getKilograms()).toBe(0);
    expect(new VehicleCapacityKilograms(42.5).getKilograms()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new VehicleCapacityKilograms(-1)).toThrow('Vehicle capacity cannot be negative');
  });

  it('should compare equality', () => {
    const left = new VehicleCapacityKilograms(10);
    const right = new VehicleCapacityKilograms(10);
    const other = new VehicleCapacityKilograms(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
