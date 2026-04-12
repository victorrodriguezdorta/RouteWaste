import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { VehicleCapacityKilograms } from '@/domain/valueobject/capacity/vehicle-capacity-kilograms';
import { VehicleCapacityLiters } from '@/domain/valueobject/capacity/vehicle-capacity-liters';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';


// Command object for input data
export interface CreateVehicleCommand {
    vehicleType: VehicleType;
    capacityKilograms: VehicleCapacityKilograms;
    capacityLiters: VehicleCapacityLiters;
    costPerKilometer: TransportationVariableCost;
}
