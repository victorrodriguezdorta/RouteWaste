import type { ServiceZone } from '@/domain/enumerate/service-zone';
import type { WasteType } from '@/domain/enumerate/waste-type';
import type { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import type { Location } from '@/domain/valueobject/location/location';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Command object for updating a container
 */
export interface UpdateContainerCommand {
    containerId: UllUUID;
    updatedFields: Partial<{
        location: Location;
        wasteType: WasteType;
        wasteDemand: WasteDemand;
        serviceZone: ServiceZone | null;
    }>;
}
