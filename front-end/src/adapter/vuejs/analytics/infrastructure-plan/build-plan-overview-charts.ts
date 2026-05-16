import type { InfrastructurePlanDetail } from '@/domain/read-model/infrastructure-plan-detail';
import type { InfrastructurePlanFacilityDetail } from '@/domain/read-model/infrastructure-plan-detail/details/infrastructure-plan-facility-detail';
import type {
  FacilityAssignmentBarDatum,
  PlanOverviewAnalytics,
  TypeCountDatum,
} from './types';

function countUniqueVehiclesForFacility(facility: InfrastructurePlanFacilityDetail): number {
  const vehicleIds = new Set(
    facility.dailyPlans
      .map((dailyPlan) => dailyPlan.vehicleId.getValue())
      .filter((id) => id.length > 0),
  );
  return vehicleIds.size;
}

function groupTypeCounts(typeKeys: Iterable<string>): TypeCountDatum[] {
  const counts = new Map<string, number>();

  for (const typeKey of typeKeys) {
    const normalized = typeKey.trim();
    if (normalized.length === 0) {
      continue;
    }
    counts.set(normalized, (counts.get(normalized) ?? 0) + 1);
  }

  return Array.from(counts.entries())
    .map(([typeKey, value]) => ({ typeKey, value }))
    .sort((left, right) => right.value - left.value || left.typeKey.localeCompare(right.typeKey));
}

function buildFacilityAssignmentBar(
  facilities: InfrastructurePlanFacilityDetail[],
): FacilityAssignmentBarDatum[] {
  return facilities.map((facility, index) => {
    const facilityName = facility.name.getValue().trim();
    const group = facilityName.length > 0 ? facilityName : `Facility ${index + 1}`;

    return {
      group,
      vehicles: countUniqueVehiclesForFacility(facility),
      containers: facility.assignedContainers.length,
    };
  });
}

/**
 * Builds overview analytics for infrastructure plan charts from the existing detail read model.
 */
export function buildPlanOverviewAnalytics(
  plan: InfrastructurePlanDetail | undefined,
): PlanOverviewAnalytics | null {
  if (!plan) {
    return null;
  }

  const containerTypes: string[] = [];
  const containerIds = new Set<string>();

  for (const facility of plan.facilities) {
    for (const container of facility.assignedContainers) {
      const containerId = container.id.getValue();
      if (containerIds.has(containerId)) {
        continue;
      }
      containerIds.add(containerId);
      containerTypes.push(String(container.wasteType));
    }
  }

  const vehicleTypesById = new Map<string, string>();
  for (const dailyPlan of plan.getDailyPlans()) {
    const vehicleId = dailyPlan.vehicleId.getValue();
    if (vehicleId.length === 0 || vehicleTypesById.has(vehicleId)) {
      continue;
    }
    const vehicleType = dailyPlan.vehicle?.vehicleType;
    vehicleTypesById.set(vehicleId, vehicleType ? String(vehicleType) : 'UNKNOWN');
  }

  const vehiclesByType = groupTypeCounts(vehicleTypesById.values());
  const containersByType = groupTypeCounts(containerTypes);
  const facilityAssignmentBar = buildFacilityAssignmentBar(plan.facilities);

  const hasChartData =
    vehiclesByType.length > 0
    || containersByType.length > 0
    || facilityAssignmentBar.some((row) => row.vehicles > 0 || row.containers > 0);

  return {
    totals: {
      facilities: plan.facilities.length,
      containers: containerIds.size,
      vehicles: vehicleTypesById.size,
    },
    vehiclesByType,
    containersByType,
    facilityAssignmentBar,
    hasChartData,
  };
}
