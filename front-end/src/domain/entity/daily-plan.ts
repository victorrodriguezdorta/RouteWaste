import { Facility } from '@/domain/entity/facility';
import { InfrastructurePlan } from '@/domain/entity/infrastructure-plan';
import { Stop } from '@/domain/entity/stop';
import { Vehicle } from '@/domain/entity/vehicle';
import { CollectedVolumeLiters } from '@/domain/valueobject/capacity/collected-volume-liters';
import { CollectedWeightKilograms } from '@/domain/valueobject/capacity/collected-weight-kilograms';
import { Distance } from '@/domain/valueobject/location/distance';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * DailyPlan
 *
 * Aggregate root representing the daily routing operation assigned to a vehicle
 * starting from a facility.
 *
 * Encapsulates the planned service date, vehicle, stops, and collected metrics
 * for a single day's waste collection operations.
 */
export class DailyPlan {
  /**
   * Unique identifier for the daily plan.
   */
  readonly id: UllUUID;

  /**
   * The parent infrastructure plan this daily plan belongs to.
   */
  private readonly infrastructurePlan: InfrastructurePlan;

  /**
   * The facility where the route starts and ends.
   */
  private readonly facility: Facility;

  /**
   * The service date (when the route is executed).
   */
  private readonly serviceDate: Date;

  /**
   * The planning day number within the execution horizon.
   */
  private readonly planDay: number;

  /**
   * The vehicle assigned to this route.
   */
  private readonly vehicle: Vehicle;

  /**
   * Total weight collected during the entire route.
   */
  private readonly totalCollectedKilograms: CollectedWeightKilograms;

  /**
   * Total volume collected during the entire route.
   */
  private readonly totalCollectedLiters: CollectedVolumeLiters;

  /**
   * Total distance traveled during the route.
   */
  private readonly totalDistanceMeters: Distance;

  /**
   * List of stops in the route.
   */
  private readonly stops: Stop[];

  /**
   * Create a new DailyPlan.
   * @param infrastructurePlan The parent infrastructure plan
   * @param facility The facility where the route starts/ends
   * @param serviceDate The date when the service is executed
   * @param planDay The planning day within the execution horizon
   * @param vehicle The vehicle assigned to the route
   * @param totalCollectedKilograms Total weight collected
   * @param totalCollectedLiters Total volume collected
   * @param totalDistanceMeters Total distance of the route
   * @param stops List of stops in the route
   * @param id Optional explicit id (generated when omitted)
   * @throws Error when required parameters are missing
   */
  constructor(
    infrastructurePlan: InfrastructurePlan,
    facility: Facility,
    serviceDate: Date,
    planDay: number,
    vehicle: Vehicle,
    totalCollectedKilograms: CollectedWeightKilograms,
    totalCollectedLiters: CollectedVolumeLiters,
    totalDistanceMeters: Distance,
    stops?: Stop[],
    id?: UllUUID
  ) {
    this.validate(infrastructurePlan, facility, serviceDate, vehicle, totalCollectedKilograms, totalCollectedLiters, totalDistanceMeters);
    this.id = id ?? UllUUID.random();
    this.infrastructurePlan = infrastructurePlan;
    this.facility = facility;
    this.serviceDate = serviceDate;
    this.planDay = planDay;
    this.vehicle = vehicle;
    this.totalCollectedKilograms = totalCollectedKilograms;
    this.totalCollectedLiters = totalCollectedLiters;
    this.totalDistanceMeters = totalDistanceMeters;
    this.stops = stops ? [...stops] : [];
  }

  /**
   * Validate that all required parameters are not null.
   * @throws Error if any required parameter is null or undefined
   */
  private validate(
    infrastructurePlan: InfrastructurePlan | undefined,
    facility: Facility | undefined,
    serviceDate: Date | undefined,
    vehicle: Vehicle | undefined,
    totalCollectedKilograms: CollectedWeightKilograms | undefined,
    totalCollectedLiters: CollectedVolumeLiters | undefined,
    totalDistanceMeters: Distance | undefined
  ): void {
    if (!infrastructurePlan) throw new Error('Infrastructure plan is not defined');
    if (!facility) throw new Error('Facility is not defined');
    if (!serviceDate) throw new Error('Service date is not defined');
    if (!vehicle) throw new Error('Vehicle is not defined');
    if (!totalCollectedKilograms) throw new Error('Total collected kilograms is not defined');
    if (!totalCollectedLiters) throw new Error('Total collected liters is not defined');
    if (!totalDistanceMeters) throw new Error('Total distance is not defined');
  }

  /**
   * Add a stop to the daily plan.
   * @param stop The stop to add
   * @throws Error when stop is null or undefined
   */
  addStop(stop: Stop | undefined): void {
    if (!stop) throw new Error('Stop is invalid');
    this.stops.push(stop);
  }

  /**
   * Get the daily plan identifier.
   * @returns The unique identifier of this daily plan
   */
  getId(): UllUUID {
    return this.id;
  }

  /**
   * Get the parent infrastructure plan.
   * @returns The infrastructure plan
   */
  getInfrastructurePlan(): InfrastructurePlan {
    return this.infrastructurePlan;
  }

  /**
   * Get the facility associated with this daily plan.
   * @returns The facility
   */
  getFacility(): Facility {
    return this.facility;
  }

  /**
   * Get the service date.
   * @returns The date when the service is executed
   */
  getServiceDate(): Date {
    return this.serviceDate;
  }

  /**
   * Get the planning day number.
   * @returns The planning day within the execution horizon
   */
  getPlanDay(): number {
    return this.planDay;
  }

  /**
   * Get the vehicle assigned to this daily plan.
   * @returns The vehicle
   */
  getVehicle(): Vehicle {
    return this.vehicle;
  }

  /**
   * Get the total collected kilograms.
   * @returns The total collected weight
   */
  getTotalCollectedKilograms(): CollectedWeightKilograms {
    return this.totalCollectedKilograms;
  }

  /**
   * Get the total collected liters.
   * @returns The total collected volume
   */
  getTotalCollectedLiters(): CollectedVolumeLiters {
    return this.totalCollectedLiters;
  }

  /**
   * Get the total distance traveled.
   * @returns The total distance in meters
   */
  getTotalDistanceMeters(): Distance {
    return this.totalDistanceMeters;
  }

  /**
   * Get an unmodifiable copy of the stops list.
   * @returns A readonly array of stops
   */
  getStops(): ReadonlyArray<Stop> {
    return this.stops.slice();
  }

  /**
   * Get the number of stops in this daily plan.
   * @returns The number of stops
   */
  getNumberOfStops(): number {
    return this.stops.length;
  }

  /**
   * Compare equality with another object.
   * Two daily plans are equal if they have the same identifier.
   * @param otherObject The object to compare with
   * @returns true if the objects are equal, false otherwise
   */
  equals(otherObject: unknown): boolean {
    if (this === otherObject) {
      return true;
    }
    if (!(otherObject instanceof DailyPlan)) {
      return false;
    }
    return this.id.equals(otherObject.id);
  }

  /**
   * Get the hash code for this object.
   * @returns A numeric hash code based on the UUID value
   */
  hashCode(): number {
    const idValue = this.id.getValue();
    let hash = 0;
    for (let i = 0; i < idValue.length; i++) {
      const char = idValue.charCodeAt(i);
      hash = (hash << 5) - hash + char;
      hash |= 0; // Convert to 32bit integer
    }
    return hash;
  }

  /**
   * Human-readable representation.
   * @returns A formatted string representation
   */
  toString(): string {
    return `DailyPlan={id=${this.id}, planId=${this.infrastructurePlan.getId()}, facilityId=${this.facility.getId()}, date=${this.serviceDate.toISOString()}, planDay=${this.planDay}, vehicle=${this.vehicle.getId()}, distance=${this.totalDistanceMeters}}`;
  }
}
