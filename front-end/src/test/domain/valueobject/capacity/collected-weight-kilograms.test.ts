import { describe, expect, it } from 'vitest';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';

describe('CollectedWeightKilograms', () => {
  it('should create with zero and positive values', () => {
    expect(CollectedWeightKilograms.fromKilograms(0).getKilograms()).toBe(0);
    expect(CollectedWeightKilograms.fromKilograms(42.5).getKilograms()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => CollectedWeightKilograms.fromKilograms(-1)).toThrow('Collected weight cannot be negative');
  });

  it('should compare equality', () => {
    const left = CollectedWeightKilograms.fromKilograms(10);
    const right = CollectedWeightKilograms.fromKilograms(10);
    const other = CollectedWeightKilograms.fromKilograms(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
