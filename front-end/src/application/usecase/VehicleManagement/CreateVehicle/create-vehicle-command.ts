import { VehicleType } from '../../../../domain/enumerate/vehicle-type';
import { Capacity } from '../../../../domain/valueobject/demand/capacity';
import { TransportationVariableCost } from '../../../../domain/valueobject/cost/transportation-variable-cost';


// Command object for input data
export interface CreateVehicleCommand {
    vehicleType: VehicleType;
    transportCapacity: Capacity;
    costPerKilometer: TransportationVariableCost;
}
