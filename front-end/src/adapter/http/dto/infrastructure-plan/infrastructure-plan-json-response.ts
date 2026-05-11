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
 * Stop alert containing information about events during collection.
 */
export interface StopAlertJsonResponse {
  type: string;
  message: string;
  value?: number;
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
  containerActualLiters?: number;
  alerts?: StopAlertJsonResponse[];
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
 * Container daily state for monitoring purposes.
 */
export interface ContainerDailyStateJsonResponse {
  containerId: string;
  planDay: number;
  dailyFillingLiters: number;
  containerCapacityLiters: number;
  status: 'CORRECT' | 'OVERFLOWED';
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
  containerStateMonitoring?: ContainerDailyStateJsonResponse[];
}

/**
 * Backwards-compatible alias used by the HTTP repository and application result types.
 */
export type InfrastructurePlanJsonResponse = InfrastructurePlanDetailJsonResponse;
