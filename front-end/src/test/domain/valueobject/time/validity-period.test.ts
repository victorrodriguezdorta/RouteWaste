import { describe, expect, it } from 'vitest';
import { ValidityPeriod } from '@/domain/valueobject/time/validity-period';

describe('ValidityPeriod', () => {
  it('should accept open-ended periods', () => {
    const start = new Date('2026-01-01');
    const period = new ValidityPeriod(start);
    expect(period.isOpenEnded()).toBe(true);
    expect(period.contains(new Date('2026-06-01'))).toBe(true);
  });

  it('should validate bounded periods', () => {
    const start = new Date('2026-01-01');
    const end = new Date('2026-12-31');
    const period = new ValidityPeriod(start, end);

    expect(period.contains(new Date('2026-06-01'))).toBe(true);
    expect(period.contains(new Date('2025-12-31'))).toBe(false);
    expect(() => new ValidityPeriod(start, new Date('2025-01-01'))).toThrow(
      'End date of validity period cannot be before start date',
    );
  });
});
