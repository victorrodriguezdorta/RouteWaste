import { TimeUnit, isTimeUnit, timeUnitFromString, timeUnitValues } from '@/domain/enumerate/time-unit';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'TimeUnit parsing',
  values: timeUnitValues,
  fromString: timeUnitFromString,
  isValid: isTimeUnit,
  sample: TimeUnit.DAY,
  undefinedMessage: 'Time unit is not defined',
});
