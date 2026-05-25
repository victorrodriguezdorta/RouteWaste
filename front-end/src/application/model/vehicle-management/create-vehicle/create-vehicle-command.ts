import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import type { Name } from '@/domain/valueobject/name/name';


// Command object for input data
export interface CreateVehicleCommand {
    name: Name;
    vehicleType: VehicleType;
    capacityKilograms: VehicleCapacityKilograms;
    capacityLiters: VehicleCapacityLiters;
    costPerKilometer: TransportationVariableCost;
}
