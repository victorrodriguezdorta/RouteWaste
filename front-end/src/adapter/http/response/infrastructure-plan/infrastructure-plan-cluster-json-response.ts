import type { ContainerJsonResponse } from '@/adapter/http/response/container/container-json-response';
import type { FacilityJsonResponse } from '@/adapter/http/response/facility/facility-json-response';

/**
 * Cluster returned in the infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanClusterJsonResponse {
  id?: string;
  infrastructurePlanId?: string;
  facility: FacilityJsonResponse;
  assignedContainers: ContainerJsonResponse[];
}
