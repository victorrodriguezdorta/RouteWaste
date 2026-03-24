import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import { Distance } from '@/domain/valueobject/location/distance';
import { ServiceTime } from '@/domain/valueobject/location/service-time';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';


// Command object for input data
export interface AssignContainerToFacilityCommand {
    containerId: UllUUID;
    facilityId: UllUUID;
    wasteDemand: WasteDemand;
    distance: Distance;
    serviceTime: ServiceTime;
    transportCost: TransportationVariableCost;
}