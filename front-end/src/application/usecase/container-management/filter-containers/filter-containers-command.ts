import { WasteType } from '../../../../domain/enumerate/waste-type';
import { ServiceZone } from '../../../../domain/enumerate/service-zone';

/**
 * Command object for filtering containers
 */
export interface FilterContainersCommand {
    wasteType?: WasteType;
    serviceZone?: ServiceZone;
    minDemand?: number;
    maxDemand?: number;
}
