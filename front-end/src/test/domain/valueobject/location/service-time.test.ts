import { describe, expect, it } from 'vitest';
import { ServiceTime } from '@/domain/valueobject/location/service-time';

describe('ServiceTime', () => {
  it('should create with zero and positive values', () => {
    expect(new ServiceTime(0).getValue()).toBe(0);
    expect(new ServiceTime(42.5).getValue()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new ServiceTime(-1)).toThrow('Service time cannot be negative');
  });

  it('should compare equality', () => {
    const left = new ServiceTime(10);
    const right = new ServiceTime(10);
    const other = new ServiceTime(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
