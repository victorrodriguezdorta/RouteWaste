import type { FacilityAssignmentBarDatum } from './facility-assignment-bar-datum';
import type { PlanOverviewTotals } from './plan-overview-totals';
import type { TypeCountDatum } from './type-count-datum';

export interface PlanOverviewAnalytics {
  totals: PlanOverviewTotals;
  vehiclesByType: TypeCountDatum[];
  containersByType: TypeCountDatum[];
  facilityAssignmentBar: FacilityAssignmentBarDatum[];
  hasChartData: boolean;
}
