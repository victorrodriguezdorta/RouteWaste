import type { VehicleType } from '@/domain/enumerate/vehicle-type';
import type { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import type { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import type { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import type { Name } from '@/domain/valueobject/name/name';

/** Fields that may be updated on an existing vehicle. */
export interface UpdateVehicleUpdatedFields {
    name?: Name;
    vehicleType?: VehicleType;
    capacityKilograms?: VehicleCapacityKilograms;
    capacityLiters?: VehicleCapacityLiters;
    costPerKilometer?: TransportationVariableCost;
}
