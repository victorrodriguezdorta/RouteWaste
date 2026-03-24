import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { Capacity } from '@/domain/valueobject/demand/capacity';


// Command object for input data
export interface CreateVehicleCommand {
    vehicleType: VehicleType;
    transportCapacity: Capacity;
    costPerKilometer: TransportationVariableCost;
}
