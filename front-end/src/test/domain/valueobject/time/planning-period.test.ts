import { describe, expect, it } from 'vitest';
import { PlanningPeriod } from '@/domain/valueobject/time/planning-period';

describe('PlanningPeriod', () => {
  it('should accept year and quarter formats', () => {
    expect(new PlanningPeriod('2026').getValue()).toBe('2026');
    expect(new PlanningPeriod('2026-Q2').getValue()).toBe('2026-Q2');
  });

  it('should reject invalid formats', () => {
    expect(() => new PlanningPeriod('26')).toThrow('Planning period format is invalid');
    expect(() => new PlanningPeriod('2026-Q5')).toThrow('Planning period format is invalid');
  });
});
