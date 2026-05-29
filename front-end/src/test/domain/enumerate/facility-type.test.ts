import { FacilityType, facilityTypeFromString, facilityTypeValues, isFacilityType } from '@/domain/enumerate/facility-type';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'FacilityType parsing',
  values: facilityTypeValues,
  fromString: facilityTypeFromString,
  isValid: isFacilityType,
  sample: FacilityType.TRANSFER_STATION,
  undefinedMessage: 'Facility type is not defined',
});
