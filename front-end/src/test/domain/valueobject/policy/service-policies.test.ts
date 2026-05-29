import { describe, expect, it } from 'vitest';
import { ServicePolicies } from '@/domain/valueobject/policy/service-policies';

describe('ServicePolicies', () => {
  it('should detect configured policies', () => {
    expect(new ServicePolicies().hasAnyPolicy()).toBe(false);
    expect(new ServicePolicies(1000, 30, 5, 10).hasAnyPolicy()).toBe(true);
  });

  it('should reject negative policy values', () => {
    expect(() => new ServicePolicies(-1)).toThrow('Policy values cannot be negative');
  });

  it('should validate distance and time limits', () => {
    const policies = new ServicePolicies(500, 60);
    expect(policies.validateServiceAssignment(400, 30)).toBeNull();
    expect(policies.validateServiceAssignment(600, 30)).toContain('Service distance');
    expect(policies.isCompliant(400, 90, 1, 0)).toBe(false);
  });
});
