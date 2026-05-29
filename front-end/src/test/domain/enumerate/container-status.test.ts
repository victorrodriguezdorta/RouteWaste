import { ContainerStatus, containerStatusFromString, containerStatusValues, isContainerStatus } from '@/domain/enumerate/container-status';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'ContainerStatus parsing',
  values: containerStatusValues,
  fromString: containerStatusFromString,
  isValid: isContainerStatus,
  sample: ContainerStatus.CORRECT,
  undefinedMessage: 'Container status is not defined',
});
