import { describe, expect, it } from 'vitest';
import {
  InfrastructurePlanValidityState,
  infrastructurePlanValidityStateFromString,
} from '@/domain/enumerate/infrastructure-plan-validity-state';

describe('InfrastructurePlanValidityState', () => {
  it('should parse known values', () => {
    expect(infrastructurePlanValidityStateFromString('VALID')).toBe(InfrastructurePlanValidityState.VALID);
    expect(infrastructurePlanValidityStateFromString('obsolete')).toBe(InfrastructurePlanValidityState.OBSOLETE);
  });

  it('should default to VALID for missing or unknown values', () => {
    expect(infrastructurePlanValidityStateFromString(undefined)).toBe(InfrastructurePlanValidityState.VALID);
    expect(infrastructurePlanValidityStateFromString(null)).toBe(InfrastructurePlanValidityState.VALID);
    expect(infrastructurePlanValidityStateFromString('UNKNOWN')).toBe(InfrastructurePlanValidityState.VALID);
  });
});
