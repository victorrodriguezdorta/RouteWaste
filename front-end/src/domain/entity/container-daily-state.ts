import { ContainerStatus } from '@/domain/enumerate/container-status';
import { ContainerCapacityLiters } from '@/domain/valueobject/demand/container-capacity-liters';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * ContainerDailyState
 *
 * Domain entity representing a container daily state snapshot returned by the
 * infrastructure plan endpoint. Mirrors backend ContainerDailyState fields.
 */
export class ContainerDailyState {
  private readonly id: UllUUID;
  private readonly containerId: string;
  private readonly planDay: number;
  private readonly dailyFillingLiters: number;
  private readonly containerCapacityLiters: ContainerCapacityLiters;
  private readonly dailyDemandLitersPerDay?: DailyWasteDemandLitersPerDay;
  private readonly status: ContainerStatus;

  constructor(
    id: UllUUID | undefined,
    containerId: string,
    planDay: number,
    dailyFillingLiters: number,
    containerCapacityLiters: ContainerCapacityLiters,
    dailyDemandLitersPerDay: DailyWasteDemandLitersPerDay | undefined,
    status: ContainerStatus,
  ) {
    // id optional for results coming from algorithm; generate if absent
    if (!containerId || containerId.trim() === '') throw new Error('containerId is required');
    if (planDay < 1) throw new Error('planDay must be >= 1');
    if (dailyFillingLiters < 0) throw new Error('dailyFillingLiters must be >= 0');
    if (!containerCapacityLiters) throw new Error('containerCapacityLiters is required');
    if (!status) throw new Error('status is required');

    this.id = id ?? UllUUID.random();
    this.containerId = containerId;
    this.planDay = planDay;
    this.dailyFillingLiters = dailyFillingLiters;
    this.containerCapacityLiters = containerCapacityLiters;
    this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
    this.status = status;
  }

  getId(): UllUUID { return this.id; }
  getContainerId(): string { return this.containerId; }
  getPlanDay(): number { return this.planDay; }
  getDailyFillingLiters(): number { return this.dailyFillingLiters; }
  getContainerCapacityLiters(): ContainerCapacityLiters { return this.containerCapacityLiters; }
  getDailyDemandLitersPerDay(): DailyWasteDemandLitersPerDay | undefined { return this.dailyDemandLitersPerDay; }
  getStatus(): ContainerStatus { return this.status; }

  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ContainerDailyState)) return false;
    // equality based on id when available, else on containerId+planDay
    if (this.id && other.id) return this.id.equals(other.id);
    return this.containerId === other.containerId && this.planDay === other.planDay;
  }

  toString(): string {
    return `ContainerDailyState={id=${this.id},containerId=${this.containerId},planDay=${this.planDay},dailyFilling=${this.dailyFillingLiters},capacity=${this.containerCapacityLiters},dailyDemand=${this.dailyDemandLitersPerDay},status=${this.status}}`;
  }
}
