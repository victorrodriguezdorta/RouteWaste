import { describe, expect, it } from 'vitest';
import {
  FacilityStatus,
  facilityStatusAllowsServiceAssignments,
  facilityStatusColor,
  facilityStatusFromString,
  facilityStatusIsDiscarded,
  facilityStatusValues,
  isFacilityStatus,
} from '@/domain/enumerate/facility-status';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'FacilityStatus parsing',
  values: facilityStatusValues,
  fromString: facilityStatusFromString,
  isValid: isFacilityStatus,
  sample: FacilityStatus.OPEN,
  undefinedMessage: 'Facility status is not defined',
});

describe('FacilityStatus helpers', () => {
  it('should detect discarded status', () => {
    expect(facilityStatusIsDiscarded(FacilityStatus.DISCARDED)).toBe(true);
    expect(facilityStatusIsDiscarded(FacilityStatus.OPEN)).toBe(false);
  });

  it('should allow assignments only for planned or open', () => {
    expect(facilityStatusAllowsServiceAssignments(FacilityStatus.PLANNED)).toBe(true);
    expect(facilityStatusAllowsServiceAssignments(FacilityStatus.OPEN)).toBe(true);
    expect(facilityStatusAllowsServiceAssignments(FacilityStatus.CANDIDATE)).toBe(false);
  });

  it('should map ui colors', () => {
    expect(facilityStatusColor(FacilityStatus.OPEN)).toBe('green');
    expect(facilityStatusColor('discarded')).toBe('red');
  });
});
