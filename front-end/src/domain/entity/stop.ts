import { Container } from '@/domain/entity/container';
import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { Distance } from '@/domain/valueobject/location/distance';
import { RouteSequence } from '@/domain/valueobject/location/route-sequence';

/**
 * Stop alert containing information about events during collection.
 */
export interface StopAlert {
  type: string;
  message: string;
  value?: number;
}

/**
 * Stop
 *
 * Represents a single point visited in a route (DailyPlan).
 * It acts as a value object within the DailyPlan aggregate.
 *
 * Encapsulates the sequence, container, collected quantities,
 * and distance metrics for a single stop in a delivery route.
 */
export class Stop {
  /**
   * The sequence number of this stop in the route.
   */
  private readonly sequence: RouteSequence;

  /**
   * The container visited at this stop.
   */
  private readonly container: Container;

  /**
   * The weight collected at this stop.
   */
  private readonly collectedKilograms: CollectedWeightKilograms;

  /**
   * The volume collected at this stop.
   */
  private readonly collectedLiters: CollectedVolumeLiters;

  /**
   * The distance traveled from the previous stop.
   */
  private readonly distanceFromPreviousMeters: Distance;

  /**
   * The cumulative distance traveled so far in the route.
   */
  private readonly cumulativeDistanceMeters: Distance;

  /**
   * The actual liters in the container before collection at this stop.
   */
  private readonly containerActualLiters: number;

  /**
   * List of alerts generated at this stop.
   */
  private readonly alerts: StopAlert[];

  /**
   * Create a new Stop.
   * @param sequence The sequence number in the route
   * @param container The container visited
   * @param collectedKilograms The weight of waste collected
   * @param collectedLiters The volume of waste collected
   * @param distanceFromPreviousMeters The distance traveled from the previous stop
   * @param cumulativeDistanceMeters The total distance traveled so far
   * @param containerActualLiters The actual liters before collection (optional)
   * @param alerts Array of alerts (optional)
   * @throws Error when required parameters are missing
   */
  constructor(
    sequence: RouteSequence,
    container: Container,
    collectedKilograms: CollectedWeightKilograms,
    collectedLiters: CollectedVolumeLiters,
    distanceFromPreviousMeters: Distance,
    cumulativeDistanceMeters: Distance,
    containerActualLiters?: number,
    alerts?: StopAlert[]
  ) {
    this.validate(sequence, container, collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters);
    this.sequence = sequence;
    this.container = container;
    this.collectedKilograms = collectedKilograms;
    this.collectedLiters = collectedLiters;
    this.distanceFromPreviousMeters = distanceFromPreviousMeters;
    this.cumulativeDistanceMeters = cumulativeDistanceMeters;
    this.containerActualLiters = containerActualLiters ?? 0;
    this.alerts = alerts ?? [];
  }

  /**
   * Validate that all required parameters are not null.
   * @throws Error if any required parameter is null or undefined
   */
  private validate(
    sequence: RouteSequence | undefined,
    container: Container | undefined,
    collectedKilograms: CollectedWeightKilograms | undefined,
    collectedLiters: CollectedVolumeLiters | undefined,
    distanceFromPreviousMeters: Distance | undefined,
    cumulativeDistanceMeters: Distance | undefined
  ): void {
    if (!sequence) throw new Error('Route sequence is not defined');
    if (!container) throw new Error('Container is not defined');
    if (!collectedKilograms) throw new Error('Collected kilograms is not defined');
    if (!collectedLiters) throw new Error('Collected liters is not defined');
    if (!distanceFromPreviousMeters) throw new Error('Distance from previous is not defined');
    if (!cumulativeDistanceMeters) throw new Error('Cumulative distance is not defined');
  }

  /**
   * Get the sequence number of this stop.
   * @returns The route sequence
   */
  getSequence(): RouteSequence {
    return this.sequence;
  }

  /**
   * Get the container visited at this stop.
   * @returns The container entity
   */
  getContainer(): Container {
    return this.container;
  }

  /**
   * Get the weight collected at this stop.
   * @returns The collected weight in kilograms
   */
  getCollectedKilograms(): CollectedWeightKilograms {
    return this.collectedKilograms;
  }

  /**
   * Get the volume collected at this stop.
   * @returns The collected volume in liters
   */
  getCollectedLiters(): CollectedVolumeLiters {
    return this.collectedLiters;
  }

  /**
   * Get the distance from the previous stop.
   * @returns The distance in meters
   */
  getDistanceFromPreviousMeters(): Distance {
    return this.distanceFromPreviousMeters;
  }

  /**
   * Get the cumulative distance up to this stop.
   * @returns The cumulative distance in meters
   */
  getCumulativeDistanceMeters(): Distance {
    return this.cumulativeDistanceMeters;
  }

  /**
   * Get the actual liters in the container before collection.
   * @returns The container actual liters
   */
  getContainerActualLiters(): number {
    return this.containerActualLiters;
  }

  /**
   * Get the alerts generated at this stop.
   * @returns The alerts as an array
   */
  getAlerts(): StopAlert[] {
    return this.alerts;
  }

  /**
   * Compare equality with another object.
   * Two stops are equal if they have the same sequence and container.
   * @param otherObject The object to compare with
   * @returns true if the objects are equal, false otherwise
   */
  equals(otherObject: unknown): boolean {
    if (this === otherObject) {
      return true;
    }
    if (!(otherObject instanceof Stop)) {
      return false;
    }
    return this.sequence.equals(otherObject.sequence) && this.container.equals(otherObject.container);
  }

  /**
   * Human-readable representation.
   * @returns A formatted string representation
   */
  toString(): string {
    return `Stop={sequence=${this.sequence}, containerId=${this.container.getId()}, collectedKg=${this.collectedKilograms}, collectedL=${this.collectedLiters}, distPrev=${this.distanceFromPreviousMeters}, distCum=${this.cumulativeDistanceMeters}, containerActualLiters=${this.containerActualLiters}, alerts=${this.alerts}}`;
  }
}
