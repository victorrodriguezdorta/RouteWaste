import { StopType, isStopType, stopTypeFromString, stopTypeValues } from '@/domain/enumerate/stop-type';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'StopType parsing',
  values: stopTypeValues,
  fromString: stopTypeFromString,
  isValid: isStopType,
  sample: StopType.CONTAINER,
  undefinedMessage: 'Stop type is not defined',
});
