/**
 * Container daily state for monitoring purposes.
 */
export interface ContainerDailyStateJsonResponse {
  id?: string;
  containerId: string;
  containerName?: string;
  planDay: number;
  /** Time of day ("HH:mm") represented by this snapshot, when available. */
  time?: string;
  dailyFillingLiters: number;
  dailyFillingLitersBeforeCollection?: number;
  containerCapacityLiters: number;
  dailyDemandLitersPerDay?: number;
  status: 'CORRECT' | 'OVERFLOWED';
}
