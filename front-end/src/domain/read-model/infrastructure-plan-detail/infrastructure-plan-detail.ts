import type { InfrastructurePlanContainerDailyStateDetail } from './details/infrastructure-plan-container-daily-state-detail';
import type { InfrastructurePlanDailyPlanDetail } from './details/infrastructure-plan-daily-plan-detail';
import type { InfrastructurePlanFacilityDetail } from './details/infrastructure-plan-facility-detail';
import type { InfrastructurePlanMetricsDetail } from './details/infrastructure-plan-metrics-detail';
import { FacilityStatus } from '@/domain/enumerate/facility-status';
import type { InfrastructurePlanValidityState } from '@/domain/enumerate/infrastructure-plan-validity-state';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Read-only infrastructure plan detail consumed by the views.
 */
export class InfrastructurePlanDetail {
  constructor(
    public readonly id: UllUUID | null,
    public readonly executedAt: string,
    public readonly period: number | null,
    public readonly numberOfDays: number | null,
    public readonly status: FacilityStatus | null,
    public readonly metrics: InfrastructurePlanMetricsDetail,
    public readonly facilities: InfrastructurePlanFacilityDetail[],
    public readonly containerStateMonitoring: InfrastructurePlanContainerDailyStateDetail[],
    public readonly validityState: InfrastructurePlanValidityState,
    public readonly executionRequestJson: string | null,
  ) {}

  /**
   * Flatten all daily plans from the facilities.
   *
   * @returns All daily plans across every facility
   */
  getDailyPlans(): InfrastructurePlanDailyPlanDetail[] {
    return this.facilities.flatMap((facility) => facility.dailyPlans);
  }

  /**
   * Return all daily plans that belong to a plan day.
   *
   * @param planDay The planning day within the execution horizon
   * @returns Daily plans scheduled for the given day
   */
  getDailyPlansForDay(planDay: number): InfrastructurePlanDailyPlanDetail[] {
    return this.getDailyPlans().filter((dailyPlan) => dailyPlan.planDay === planDay);
  }

  /**
   * Return the facilities that have daily plans on the provided day.
   *
   * @param planDay The planning day within the execution horizon
   * @returns Facilities with at least one daily plan on the given day
   */
  getFacilitiesForDay(planDay: number): InfrastructurePlanFacilityDetail[] {
    const dailyPlanFacilityIds = new Set(
      this.getDailyPlansForDay(planDay).map((dailyPlan) => dailyPlan.facilityId.getValue()),
    );

    return this.facilities.filter((facility) => dailyPlanFacilityIds.has(facility.id.getValue()));
  }

  /**
   * Return the facility that matches the provided identifier.
   *
   * @param facilityId Facility identifier as UUID value object or string
   * @returns Matching facility detail, or undefined when not found
   */
  getFacilityById(facilityId: UllUUID | string): InfrastructurePlanFacilityDetail | undefined {
    const targetId = typeof facilityId === 'string' ? facilityId : facilityId.getValue();
    return this.facilities.find((facility) => facility.id.getValue() === targetId);
  }

  /**
   * Return all monitoring rows that overflow.
   *
   * @returns Container daily state rows in OVERFLOWED status
   */
  getOverflowedContainerStates(): InfrastructurePlanContainerDailyStateDetail[] {
    return this.containerStateMonitoring.filter((state) => state.status === 'OVERFLOWED');
  }
}
