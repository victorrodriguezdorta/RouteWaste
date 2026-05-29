import { describe, expect, it } from 'vitest';
import { ServiceRadius } from '@/domain/valueobject/location/service-radius';

describe('ServiceRadius', () => {
  it('should create with zero and positive values', () => {
    expect(new ServiceRadius(0).value).toBe(0);
    expect(new ServiceRadius(42.5).value).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new ServiceRadius(-1)).toThrow('Service radius cannot be negative');
  });

  it('should compare equality', () => {
    const left = new ServiceRadius(10);
    const right = new ServiceRadius(10);
    const other = new ServiceRadius(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
