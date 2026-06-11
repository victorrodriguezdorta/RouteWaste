import { FacilityJsonResponse } from './facility-json-response';

/**
 * FacilitiesResponse
 *
 * Alias type for an array of `FacilityJsonResponse` objects returned by the
 * backend when listing facilities. Keeping this alias allows easy extension
 * (e.g. adding pagination metadata) later without changing call sites.
 */
export type FacilitiesResponse = FacilityJsonResponse[];
