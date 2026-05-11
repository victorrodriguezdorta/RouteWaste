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
  sequence: number | { value: number };
  container?: ContainerJsonResponse;
  containerId?: string;
  collectedKilograms: number | { value: number };
  collectedLiters: number | { value: number };
  distanceFromPreviousMeters: number | { value: number };
  cumulativeDistanceMeters: number | { value: number };
  containerActualLiters?: number;
  alerts?: StopAlertJsonResponse[];
}

/**
 * Daily plan returned in the infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanDailyPlanJsonResponse {
  id?: string;
  infrastructurePlanId?: string;
  facility?: FacilityJsonResponse;
  facilityId?: string;
  serviceDate: string | { date?: string; value?: string };
  planDay?: number;
  vehicle?: VehicleJsonResponse;
  vehicleId?: string;
  totalCollectedKilograms: number | { value: number };
  totalCollectedLiters: number | { value: number };
  totalDistanceMeters: number | { value: number };
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
 * Facility returned by the newer infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanFacilityJsonResponse {
  id: string;
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

/**
 * Metrics block returned by the newer infrastructure plan detail endpoint.
 */
export interface InfrastructurePlanMetricsJsonResponse {
  totalCollectedKilograms?: number;
  totalCollectedLiters?: number;
  totalDistanceMeters?: number;
  averagePickupTimeMinutes?: number;
  estimatedTotalCost?: InfrastructurePlanMoneyJsonResponse;
  maxBudget?: InfrastructurePlanMoneyJsonResponse;
}

/**
 * Container daily state for monitoring purposes.
 */
export interface ContainerDailyStateJsonResponse {
  id?: string;
  containerId: string;
  planDay: number;
  dailyFillingLiters: number;
  containerCapacityLiters: number;
  dailyDemandLitersPerDay?: number;
  status: 'CORRECT' | 'OVERFLOWED';
}

/**
 * Infrastructure plan detail payload returned by the backend.
 */
export interface InfrastructurePlanDetailJsonResponse {
  id?: string;
  executedAt: string;
  period?: number;
  numberOfDays?: number;
  totalCollectedKilograms?: number;
  totalCollectedLiters?: number;
  maxBudget?: InfrastructurePlanMoneyJsonResponse;
  totalDistanceMeters?: number;
  averagePickupTimeMinutes?: number;
  estimatedTotalCost?: InfrastructurePlanMoneyJsonResponse;
  metrics?: InfrastructurePlanMetricsJsonResponse;
  facilities?: InfrastructurePlanFacilityJsonResponse[];
  clusters?: InfrastructurePlanClusterJsonResponse[];
  status?: string;
  dailyPlans?: InfrastructurePlanDailyPlanJsonResponse[];
  containerStateMonitoring?: ContainerDailyStateJsonResponse[];
}

/**
 * Backwards-compatible alias used by the HTTP repository and application result types.
 */
export type InfrastructurePlanJsonResponse = InfrastructurePlanDetailJsonResponse;
