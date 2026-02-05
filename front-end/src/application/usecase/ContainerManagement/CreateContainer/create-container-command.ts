import { Location } from '../../../../domain/valueobject/location/location';
import { WasteType } from '../../../../domain/enumerate/waste-type';
import { WasteDemand } from '../../../../domain/valueobject/demand/waste-demand';
import { ServiceZone } from '../../../../domain/enumerate/service-zone';

/**
 * Command object for the container creation use case
 */
export interface CreateContainerCommand {
    location: Location;
    wasteType: WasteType;
    wasteDemand: WasteDemand;
    serviceZone?: ServiceZone | null;
}
