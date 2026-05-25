import type { InfrastructurePlanStopJsonResponse } from './infrastructure-plan-stop-json-response';
import type { FacilityJsonResponse } from '@/adapter/http/dto/facility/facility-json-response';
import type { VehicleJsonResponse } from '@/adapter/http/dto/vehicle/vehicle-json-response';

/**
 * Daily plan returned in the infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanDailyPlanJsonResponse {
  id?: string;
  infrastructurePlanId?: string;
  facility?: FacilityJsonResponse;
  facilityId?: string;
  facilityName?: string;
  serviceDate: string | { date?: string; value?: string };
  planDay?: number;
  vehicle?: VehicleJsonResponse;
  /** @deprecated Prefer {@link vehicle}; kept for older API responses. */
  vehicleId?: string;
  totalCollectedKilograms: number | { value: number };
  totalCollectedLiters: number | { value: number };
  totalDistanceMeters: number | { value: number };
  stops: InfrastructurePlanStopJsonResponse[];
}
