export interface RouteProgressStop {
  sequence: number;
  collectedKilograms?: number;
  collectedLiters?: number;
  cumulativeKilograms?: number;
  cumulativeLiters?: number;
  cumulativeDistanceMeters?: number;
}
