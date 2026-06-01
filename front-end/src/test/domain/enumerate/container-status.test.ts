import { describe, expect, it } from 'vitest';
import {
  ContainerStatus,
  containerStatusColor,
  containerStatusFromString,
  containerStatusValues,
  isContainerStatus,
} from '@/domain/enumerate/container-status';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'ContainerStatus parsing',
  values: containerStatusValues,
  fromString: containerStatusFromString,
  isValid: isContainerStatus,
  sample: ContainerStatus.CORRECT,
  undefinedMessage: 'Container status is not defined',
});

describe('ContainerStatus colors', () => {
  it('should map statuses to Vuetify chip colors', () => {
    expect(containerStatusColor(ContainerStatus.CORRECT)).toBe('success');
    expect(containerStatusColor(ContainerStatus.OVERFLOWED)).toBe('error');
    expect(containerStatusColor(null)).toBe('grey');
  });
});
