import type { InfrastructurePlanSummaryJsonResponse } from './infrastructure-plan-summary-json-response';

/**
 * Paginated response returned by the backend when listing infrastructure plans.
 */
export interface InfrastructurePlanPageJsonResponse {
	content: InfrastructurePlanSummaryJsonResponse[];
	totalElements: number;
	totalPages: number;
	page: number;
	size: number;
	numberOfElements: number;
	first: boolean;
	last: boolean;
}
