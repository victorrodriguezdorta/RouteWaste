import { describe, expect, it } from 'vitest';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';

describe('StorageCapacityKilograms', () => {
  it('should create with zero and positive values', () => {
    expect(new StorageCapacityKilograms(0).getKilograms()).toBe(0);
    expect(new StorageCapacityKilograms(42.5).getKilograms()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new StorageCapacityKilograms(-1)).toThrow('Storage capacity cannot be negative');
  });

  it('should compare equality', () => {
    const left = new StorageCapacityKilograms(10);
    const right = new StorageCapacityKilograms(10);
    const other = new StorageCapacityKilograms(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
