import { describe, expect, it } from 'vitest';

/**
 * Shared assertions for domain enums that expose values/fromString/is* helpers.
 */
export function describeStandardEnum<E extends string>(config: {
  label: string;
  values: () => E[];
  fromString: (s?: string) => E;
  isValid: (s?: string) => boolean;
  sample: E;
  undefinedMessage: string;
  invalidMessage?: string;
}): void {
  const { label, values, fromString, isValid, sample, undefinedMessage, invalidMessage } = config;

  describe(label, () => {
    it('should expose all enum values', () => {
      const all = values();
      expect(all.length).toBeGreaterThan(0);
      expect(new Set(all).size).toBe(all.length);
      expect(all).toContain(sample);
    });

    it('should parse from string (trim and case insensitive)', () => {
      expect(fromString(sample.toLowerCase())).toBe(sample);
      expect(fromString(`  ${sample}  `)).toBe(sample);
    });

    it('should reject undefined or blank input', () => {
      expect(() => fromString()).toThrow(undefinedMessage);
      expect(() => fromString('   ')).toThrow();
    });

    it('should reject invalid values', () => {
      expect(() => fromString('NOT_A_REAL_VALUE_XYZ')).toThrow(invalidMessage ?? /invalid|not defined/i);
      expect(isValid('NOT_A_REAL_VALUE_XYZ')).toBe(false);
      expect(isValid(undefined)).toBe(false);
      expect(isValid(sample)).toBe(true);
    });
  });
}
