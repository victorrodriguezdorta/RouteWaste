import { describe, expect, it } from 'vitest';
import { Distance } from '@/domain/valueobject/location/distance';

describe('Distance', () => {
  it('should create with zero and positive values', () => {
    expect(Distance.fromMeters(0).toMeters()).toBe(0);
    expect(Distance.fromMeters(42.5).toMeters()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => Distance.fromMeters(-1)).toThrow('Distance cannot be negative');
  });

  it('should compare equality', () => {
    const left = Distance.fromMeters(10);
    const right = Distance.fromMeters(10);
    const other = Distance.fromMeters(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
