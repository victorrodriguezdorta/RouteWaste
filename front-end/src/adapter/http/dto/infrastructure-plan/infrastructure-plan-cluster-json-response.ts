import type { ContainerJsonResponse } from '@/adapter/http/dto/container/container-json-response';
import type { FacilityJsonResponse } from '@/adapter/http/dto/facility/facility-json-response';

/**
 * Cluster returned in the infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanClusterJsonResponse {
  id?: string;
  infrastructurePlanId?: string;
  facility: FacilityJsonResponse;
  assignedContainers: ContainerJsonResponse[];
}
