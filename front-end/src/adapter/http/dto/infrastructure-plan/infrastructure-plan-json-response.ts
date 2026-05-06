import type { ContainerJsonResponse } from '@/adapter/http/dto/container/container-json-response';
import type { FacilityJsonResponse } from '@/adapter/http/dto/facility/facility-json-response';
import type { VehicleJsonResponse } from '@/adapter/http/dto/vehicle/vehicle-json-response';

/**
 * Money-like value returned by the backend.
 */
export interface InfrastructurePlanMoneyJsonResponse {
  amount: number;
  currency?: string;
}

/**
 * Stop included in a daily plan.
 */
export interface InfrastructurePlanStopJsonResponse {
  sequence: number;
  container: ContainerJsonResponse;
  collectedKilograms: number;
  collectedLiters: number;
  distanceFromPreviousMeters: number;
  cumulativeDistanceMeters: number;
}

/**
 * Daily plan returned in the infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanDailyPlanJsonResponse {
  id?: string;
  infrastructurePlanId?: string;
  facility: FacilityJsonResponse;
  serviceDate: string;
  vehicle: VehicleJsonResponse;
  totalCollectedKilograms: number;
  totalCollectedLiters: number;
  totalDistanceMeters: number;
  stops: InfrastructurePlanStopJsonResponse[];
}

/**
 * Cluster returned in the infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanClusterJsonResponse {
  id?: string;
  infrastructurePlanId?: string;
  facility: FacilityJsonResponse;
  assignedContainers: ContainerJsonResponse[];
}

/**
 * Infrastructure plan detail payload returned by the backend.
 */
export interface InfrastructurePlanDetailJsonResponse {
  id?: string;
  executedAt: string;
  totalCollectedKilograms: number;
  totalCollectedLiters: number;
  maxBudget: InfrastructurePlanMoneyJsonResponse;
  totalDistanceMeters: number;
  clusters: InfrastructurePlanClusterJsonResponse[];
  status: string;
  dailyPlans: InfrastructurePlanDailyPlanJsonResponse[];
}

/**
 * Backwards-compatible alias used by the HTTP repository and application result types.
 */
export type InfrastructurePlanJsonResponse = InfrastructurePlanDetailJsonResponse;
