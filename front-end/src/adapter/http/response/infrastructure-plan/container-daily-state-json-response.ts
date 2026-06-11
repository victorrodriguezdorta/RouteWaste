/**
 * Container daily state for monitoring purposes.
 */
export interface ContainerDailyStateJsonResponse {
  id?: string;
  containerId: string;
  containerName?: string;
  planDay: number;
  dailyFillingLiters: number;
  dailyFillingLitersBeforeCollection?: number;
  containerCapacityLiters: number;
  dailyDemandLitersPerDay?: number;
  status: 'CORRECT' | 'OVERFLOWED';
}
