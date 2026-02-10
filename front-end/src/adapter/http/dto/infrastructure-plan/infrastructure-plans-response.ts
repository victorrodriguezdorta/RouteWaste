import { InfrastructurePlanJsonResponse } from './infrastructure-plan-json-response';

/**
 * InfrastructurePlansResponse
 * Alias type for an array of `InfrastructurePlanJsonResponse` objects returned by the backend when listing infrastructure plans. Keeping this alias allows
 * easy extension (e.g. adding pagination metadata) later without changing call
 * sites.
 */
export type InfrastructurePlansResponse = InfrastructurePlanJsonResponse[];
