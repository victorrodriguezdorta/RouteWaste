import { VehicleType, isVehicleType, vehicleTypeFromString, vehicleTypeValues } from '@/domain/enumerate/vehicle-type';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'VehicleType parsing',
  values: vehicleTypeValues,
  fromString: vehicleTypeFromString,
  isValid: isVehicleType,
  sample: VehicleType.COLLECTION_TRUCK,
  undefinedMessage: 'Vehicle type is not defined',
});
