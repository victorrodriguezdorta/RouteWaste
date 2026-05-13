import { ContainerStatus } from '@/domain/enumerate/container-status';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Read-only monitoring row for a container.
 */
export class InfrastructurePlanContainerDailyStateDetail {
  constructor(
    public readonly id: UllUUID | null,
    public readonly containerId: UllUUID,
    public readonly planDay: number,
    public readonly dailyFillingLiters: number,
    public readonly containerCapacityLiters: ContainerCapacityLiters,
    public readonly dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay | null,
    public readonly status: ContainerStatus,
    public readonly containerName: Name | null = null,
  ) {}
}
