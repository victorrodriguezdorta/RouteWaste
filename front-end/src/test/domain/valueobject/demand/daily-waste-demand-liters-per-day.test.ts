import { describe, expect, it } from 'vitest';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';

describe('DailyWasteDemandLitersPerDay', () => {
  it('should create with zero and positive values', () => {
    expect(new DailyWasteDemandLitersPerDay(0).getLitersPerDay()).toBe(0);
    expect(new DailyWasteDemandLitersPerDay(42.5).getLitersPerDay()).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => new DailyWasteDemandLitersPerDay(-1)).toThrow('Daily waste demand cannot be negative');
  });

  it('should compare equality', () => {
    const left = new DailyWasteDemandLitersPerDay(10);
    const right = new DailyWasteDemandLitersPerDay(10);
    const other = new DailyWasteDemandLitersPerDay(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
  it('should add two demands', () => {
    const a = new DailyWasteDemandLitersPerDay(10);
    const b = new DailyWasteDemandLitersPerDay(5);
    expect(a.add(b).getLitersPerDay()).toBe(15);
  });});
