/** Datum for ull-tfg-vue PieChart (`name` + `value`). */
export type PieChartDatum = Record<string, string | number> & {
  name: string;
  value: number;
};

/** Datum for ull-tfg-vue StackedBarPlot (first key = group, rest = series). */
export interface FacilityAssignmentBarDatum {
  group: string;
  vehicles: number;
  containers: number;
}

export interface PlanOverviewTotals {
  facilities: number;
  containers: number;
  vehicles: number;
}

/** Count grouped by enum key before i18n labels are applied. */
export interface TypeCountDatum {
  typeKey: string;
  value: number;
}

export interface PlanOverviewAnalytics {
  totals: PlanOverviewTotals;
  vehiclesByType: TypeCountDatum[];
  containersByType: TypeCountDatum[];
  facilityAssignmentBar: FacilityAssignmentBarDatum[];
  hasChartData: boolean;
}
