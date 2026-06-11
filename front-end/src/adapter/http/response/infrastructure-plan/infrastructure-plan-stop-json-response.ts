import type { StopAlertJsonResponse } from './stop-alert-json-response';
import type { ContainerJsonResponse } from '@/adapter/http/response/container/container-json-response';

/**
 * Stop included in a daily plan.
 */
export interface InfrastructurePlanStopJsonResponse {
  sequence: number | { value: number };
  container?: ContainerJsonResponse;
  containerId?: string;
  containerName?: string;
  type?: string;
  collectedKilograms: number | { value: number };
  collectedLiters: number | { value: number };
  distanceFromPreviousMeters: number | { value: number };
  cumulativeDistanceMeters: number | { value: number };
  containerActualLiters?: number;
  /** Time of day ("HH:mm") at which the vehicle performs this stop, when available. */
  collectedAt?: string;
  alerts?: StopAlertJsonResponse[];
}
