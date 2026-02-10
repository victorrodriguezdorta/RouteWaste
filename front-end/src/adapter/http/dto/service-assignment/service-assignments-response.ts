import { ServiceAssignmentJsonResponse } from './service-assignment-json-response';

/**
 * Alias type for an array of `ServiceAssignmentJsonResponse` 
 * objects returned by the backend when listing service assignments.
 * Keeping this alias allows easy extension (e.g. adding pagination metadata) later without changing call sites.
*/
export type ServiceAssignmentsResponse = ServiceAssignmentJsonResponse[];
