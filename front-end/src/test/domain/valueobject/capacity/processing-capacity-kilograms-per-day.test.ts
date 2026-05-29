import { describe, expect, it } from 'vitest';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';

describe('ProcessingCapacityKilogramsPerDay', () => {
  it('should create with zero and positive values', () => {
    expect(new ProcessingCapacityKilogramsPerDay(0).getKilogramsPerDay()).toBe(0);
    expect(new ProcessingCapacityKilogramsPerDay(42.5).getKilogramsPerDay()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new ProcessingCapacityKilogramsPerDay(-1)).toThrow('Processing capacity cannot be negative');
  });

  it('should compare equality', () => {
    const left = new ProcessingCapacityKilogramsPerDay(10);
    const right = new ProcessingCapacityKilogramsPerDay(10);
    const other = new ProcessingCapacityKilogramsPerDay(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
});
