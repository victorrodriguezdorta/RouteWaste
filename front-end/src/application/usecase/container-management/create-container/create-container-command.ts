import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Location } from '@/domain/valueobject/location/location';

/**
 * Command object for the container creation use case
 */
export interface CreateContainerCommand {
    location: Location;
    wasteType: WasteType;
    capacityLiters: ContainerCapacityLiters;
    dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay;
    serviceZone?: ServiceZone | null;
}
