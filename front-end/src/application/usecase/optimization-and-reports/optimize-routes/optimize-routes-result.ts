import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Distance } from '../../../../domain/valueobject/location/distance';
import { ServiceTime } from '../../../../domain/valueobject/location/service-time';


/**
 * Represents an optimized route segment
 */
export interface OptimizedRoute {
    vehicleId: UllUUID;
    orderedContainerIds: UllUUID[];
    targetFacilityId: UllUUID;
    totalDistance: Distance;
    estimatedTime: ServiceTime;
}

// Result type for the use case
export type OptimizeRoutesResult = OptimizedRoute[];

