import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import { Distance } from '@/domain/valueobject/location/distance';
import { ServiceTime } from '@/domain/valueobject/location/service-time';
import { ServicePolicies } from '@/domain/valueobject/policy/service-policies';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Container } from './container';
import { Facility } from './facility';

/**
 * ServiceAssignment
 *
 * Represents an assignment between a `Container` and a `Facility`.
 *
 * Encapsulates the demand, distance, service time and transport cost for
 * a single service operation. Includes policy validation helpers used
 * by application logic.
 */
export class ServiceAssignment {
  /** Unique identifier for this service assignment */
  readonly id: UllUUID;

  /** Source container entity for this assignment */
  readonly container: Container;

  /** Target facility entity for this assignment */
  readonly facility: Facility;

  /** Expected waste demand value object for this assignment */
  readonly wasteDemand: WasteDemand;

  /** Distance value object representing meters between container and facility */
  readonly distance: Distance;

  /** Service time value object representing minutes for service operation */
  readonly serviceTime: ServiceTime;

  /** Transportation variable cost value object for this assignment */
  readonly transportCost: TransportationVariableCost;

  /**
   * Create a new `ServiceAssignment`.
   * @param container source container entity
   * @param facility target facility entity
   * @param wasteDemand expected waste demand value object
   * @param distance distance value object (meters)
   * @param serviceTime service time value object (minutes)
   * @param transportCost transportation variable cost value object
   * @param id optional explicit id (generated when omitted)
   * @throws Error when a required argument is missing
   */
  constructor(container: Container, facility: Facility, wasteDemand: WasteDemand, distance: Distance, serviceTime: ServiceTime, transportCost: TransportationVariableCost, id?: UllUUID) {
    if (!container) throw new Error('Container is not defined');
    if (!facility) throw new Error('Facility is not defined');
    if (!wasteDemand) throw new Error('Waste demand is not defined');
    if (!distance) throw new Error('Distance is not defined');
    if (!serviceTime) throw new Error('Service time is not defined');
    if (!transportCost) throw new Error('Transportation cost is not defined');
    this.id = id ?? UllUUID.random();
    this.container = container;
    this.facility = facility;
    this.wasteDemand = wasteDemand;
    this.distance = distance;
    this.serviceTime = serviceTime;
    this.transportCost = transportCost;
  }

  /**
   * Validate this assignment against optional `ServicePolicies`.
   * @param policies optional service policy configuration
   * @throws Error when a policy violation is detected
   */
  validatePolicies(policies?: ServicePolicies | null): void {
    if (!policies) return;
    const err = policies.validateServiceAssignment(this.distance.toMeters(), this.serviceTime.getValue());
    if (err) throw new Error('Service assignment violates service policies: ' + err);
  }

  /** Return the identifier for this service assignment.
   * @returns The unique identifier of this service assignment
   */
  getServiceAssignmentId(): UllUUID { return this.id; }

  /**
   * Compare by identity.
   * @param other Candidate object to compare with this service assignment
   * @returns True if both assignments have the same identifier, false otherwise
   */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof ServiceAssignment)) return false; return this.id.equals(other.id); }

  /**
   * Human-readable representation useful for debugging.
   * @returns A string representation of this service assignment
   */
  toString(): string { return `ServiceAssignment={id=${this.id}, containerId=${this.container.getId()}, facilityId=${this.facility.getId()}, demand=${this.wasteDemand}, distance=${this.distance}, serviceTime=${this.serviceTime}, transportCost=${this.transportCost}}`; }
}
