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
  /** Unique identifier for this daily state row. */
  private readonly id: UllUUID;

  /** Identifier of the monitored container. */
  private readonly containerId: string;

  /** Planning day within the execution horizon. */
  private readonly planDay: number;

  /** Estimated filling level in liters for the plan day. */
  private readonly dailyFillingLiters: number;

  /** Container capacity in liters at the time of the snapshot. */
  private readonly containerCapacityLiters: ContainerCapacityLiters;

  /** Optional daily demand in liters per day. */
  private readonly dailyDemandLitersPerDay?: DailyWasteDemandLitersPerDay;

  /** Monitoring status for the container on the plan day. */
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

  /**
   * Return the daily state identifier.
   * @returns unique identifier for this daily state row
   */
  getId(): UllUUID { return this.id; }

  /**
   * Return the monitored container identifier.
   * @returns container identifier string
   */
  getContainerId(): string { return this.containerId; }

  /**
   * Return the planning day number.
   * @returns planning day within the execution horizon
   */
  getPlanDay(): number { return this.planDay; }

  /**
   * Return the estimated daily filling in liters.
   * @returns filling level in liters for the plan day
   */
  getDailyFillingLiters(): number { return this.dailyFillingLiters; }

  /**
   * Return the container capacity in liters.
   * @returns capacity value object in liters
   */
  getContainerCapacityLiters(): ContainerCapacityLiters { return this.containerCapacityLiters; }

  /**
   * Return the optional daily demand in liters per day.
   * @returns daily demand when defined
   */
  getDailyDemandLitersPerDay(): DailyWasteDemandLitersPerDay | undefined { return this.dailyDemandLitersPerDay; }

  /**
   * Return the monitoring status.
   * @returns container status for the plan day
   */
  getStatus(): ContainerStatus { return this.status; }

  /**
   * Compare equality with another object by id or container/day pair.
   * @param other value to compare with
   * @returns true when both instances represent the same daily state
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ContainerDailyState)) return false;
    // equality based on id when available, else on containerId+planDay
    if (this.id && other.id) return this.id.equals(other.id);
    return this.containerId === other.containerId && this.planDay === other.planDay;
  }

  /**
   * Human-readable representation for debugging.
   * @returns formatted debug string
   */
  toString(): string {
    return `ContainerDailyState={id=${this.id},containerId=${this.containerId},planDay=${this.planDay},dailyFilling=${this.dailyFillingLiters},capacity=${this.containerCapacityLiters},dailyDemand=${this.dailyDemandLitersPerDay},status=${this.status}}`;
  }
}
