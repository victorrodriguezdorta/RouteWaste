import type { InfrastructurePlanDailyPlanJsonResponse } from './infrastructure-plan-daily-plan-json-response';
import type { ContainerJsonResponse } from '@/adapter/http/response/container/container-json-response';

/**
 * Facility returned by the newer infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanFacilityJsonResponse {
  id: string;
  name?: string;
  facilityType: string;
  status: string;
  location: {
    latitude: number;
    longitude: number;
    postalAddress: string;
    gisReference?: string;
  };
  capacities?: {
    storageCapacityKg?: number;
    processingCapacityKgPerDay?: number;
  };
  assignedContainers?: ContainerJsonResponse[];
  dailyPlans?: InfrastructurePlanDailyPlanJsonResponse[];
}
