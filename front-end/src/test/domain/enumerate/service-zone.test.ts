import { ServiceZone, isServiceZone, serviceZoneFromString, serviceZoneValues } from '@/domain/enumerate/service-zone';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'ServiceZone parsing',
  values: serviceZoneValues,
  fromString: serviceZoneFromString,
  isValid: isServiceZone,
  sample: ServiceZone.NEIGHBORHOOD,
  undefinedMessage: 'Service zone is not defined',
});
