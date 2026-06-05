import type { StopType } from '@/domain/enumerate/stop-type';

export interface RouteProgressStop {
  sequence: number;
  type?: StopType;
  collectedKilograms?: number;
  collectedLiters?: number;
  cumulativeKilograms?: number;
  cumulativeLiters?: number;
  cumulativeDistanceMeters?: number;
}
