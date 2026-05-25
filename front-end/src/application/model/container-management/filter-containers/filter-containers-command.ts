import { ServiceZone } from '@/domain/enumerate/service-zone';
import { WasteType } from '@/domain/enumerate/waste-type';

/**
 * Command object for filtering containers
 */
export interface FilterContainersCommand {
    wasteType?: WasteType;
    serviceZone?: ServiceZone;
    minDemand?: number;
    maxDemand?: number;
}
