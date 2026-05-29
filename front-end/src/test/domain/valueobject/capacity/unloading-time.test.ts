import { describe, expect, it } from 'vitest';
import { UnloadingTime } from '@/domain/valueobject/capacity/unloading-time';

describe('UnloadingTime', () => {
  it('should create with zero and positive values', () => {
    expect(new UnloadingTime(0).getMinutes()).toBe(0);
    expect(new UnloadingTime(42.5).getMinutes()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new UnloadingTime(-1)).toThrow('Unloading time cannot be negative');
  });

  it('should compare equality', () => {
    const left = new UnloadingTime(10);
    const right = new UnloadingTime(10);
    const other = new UnloadingTime(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
