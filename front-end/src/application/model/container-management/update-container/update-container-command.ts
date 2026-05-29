import type { ServiceZone } from '@/domain/enumerate/service-zone';
import type { WasteType } from '@/domain/enumerate/waste-type';
import type { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import type { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import type { Location } from '@/domain/valueobject/location/location';
import type { Name } from '@/domain/valueobject/name/name';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Command object for updating a container
 */
export interface UpdateContainerCommand {
    containerId: UllUUID;
    updatedFields: Partial<{
        name: Name;
        location: Location;
        wasteType: WasteType;
        capacityLiters: ContainerCapacityLiters;
        dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay;
        serviceZone: ServiceZone | null;
    }>;
}
