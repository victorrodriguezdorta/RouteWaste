import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { Container } from './container';
import { Facility } from './facility';
import { WasteDemand } from '../valueobject/demand/waste-demand';
import { Distance } from '../valueobject/location/distance';
import { ServiceTime } from '../valueobject/location/service-time';
import { TransportationVariableCost } from '../valueobject/cost/transportation-variable-cost';
import { ServicePolicies } from '../valueobject/policy/service-policies';

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
  readonly id: UllUUID;
  readonly container: Container;
  readonly facility: Facility;
  readonly wasteDemand: WasteDemand;
  readonly distance: Distance;
  readonly serviceTime: ServiceTime;
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

  /** Return the identifier for this service assignment. */
  getServiceAssignmentId(): UllUUID { return this.id; }

  /** Compare by identity. */
  equals(other: unknown): boolean { if (this === other) return true; if (!(other instanceof ServiceAssignment)) return false; return this.id.equals(other.id); }

  /** Human-readable representation useful for debugging. */
  toString(): string { return `ServiceAssignment={id=${this.id}, containerId=${this.container.getId()}, facilityId=${this.facility.getId()}, demand=${this.wasteDemand}, distance=${this.distance}, serviceTime=${this.serviceTime}, transportCost=${this.transportCost}}`; }
}
