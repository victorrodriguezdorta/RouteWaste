import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import { Location } from '@/domain/valueobject/location/location';

/**
 * Command object for the container creation use case
 */
export interface CreateContainerCommand {
    location: Location;
    wasteType: WasteType;
    wasteDemand: WasteDemand;
    serviceZone?: ServiceZone | null;
}
