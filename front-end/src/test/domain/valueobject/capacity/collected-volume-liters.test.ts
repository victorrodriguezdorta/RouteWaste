import { describe, expect, it } from 'vitest';
import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';

describe('CollectedVolumeLiters', () => {
  it('should create with zero and positive values', () => {
    expect(CollectedVolumeLiters.fromLiters(0).getLiters()).toBe(0);
    expect(CollectedVolumeLiters.fromLiters(42.5).getLiters()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => CollectedVolumeLiters.fromLiters(-1)).toThrow('Collected volume cannot be negative');
  });

  it('should compare equality', () => {
    const left = CollectedVolumeLiters.fromLiters(10);
    const right = CollectedVolumeLiters.fromLiters(10);
    const other = CollectedVolumeLiters.fromLiters(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
