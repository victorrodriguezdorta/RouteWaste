import { describe, expect, it } from 'vitest';
import {
  InfrastructurePlanValidityState,
  infrastructurePlanValidityStateColor,
  infrastructurePlanValidityStateFromString,
} from '@/domain/enumerate/infrastructure-plan-validity-state';
import { infrastructurePlanPlanStatusChipColor } from '@/domain/enumerate/infrastructure-plan-execution-state';

describe('InfrastructurePlanValidityState', () => {
  it('should parse known values', () => {
    expect(infrastructurePlanValidityStateFromString('VALID')).toBe(InfrastructurePlanValidityState.VALID);
    expect(infrastructurePlanValidityStateFromString('obsolete')).toBe(InfrastructurePlanValidityState.OBSOLETE);
    expect(infrastructurePlanValidityStateFromString('running')).toBe(InfrastructurePlanValidityState.RUNNING);
  });

  it('should default to VALID for missing or unknown values', () => {
    expect(infrastructurePlanValidityStateFromString(undefined)).toBe(InfrastructurePlanValidityState.VALID);
    expect(infrastructurePlanValidityStateFromString(null)).toBe(InfrastructurePlanValidityState.VALID);
    expect(infrastructurePlanValidityStateFromString('UNKNOWN')).toBe(InfrastructurePlanValidityState.VALID);
  });

  it('should expose Vuetify colors per validity state', () => {
    expect(infrastructurePlanValidityStateColor(InfrastructurePlanValidityState.VALID)).toBe('success');
    expect(infrastructurePlanValidityStateColor(InfrastructurePlanValidityState.OBSOLETE)).toBe('error');
    expect(infrastructurePlanValidityStateColor(InfrastructurePlanValidityState.RUNNING)).toBe('warning');
  });

  it('should resolve combined plan status chip color', () => {
    expect(
      infrastructurePlanPlanStatusChipColor(
        InfrastructurePlanValidityState.VALID,
        'FAILED',
      ),
    ).toBe('error');
    expect(
      infrastructurePlanPlanStatusChipColor(InfrastructurePlanValidityState.RUNNING, 'COMPLETED'),
    ).toBe('warning');
  });
});
