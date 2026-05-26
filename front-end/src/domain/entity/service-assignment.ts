import { Container } from '@/domain/entity/container';
import { Facility } from '@/domain/entity/facility';
import { InfrastructurePlan } from '@/domain/entity/infrastructure-plan';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * ServiceAssignmentCluster
 *
 * Represents an assignment between a cluster of containers and a facility.
 *
 * This is the backend concept where multiple containers are assigned to a single facility,
 * forming a service cluster. It is an immutable entity within the InfrastructurePlan aggregate.
 */
export class ServiceAssignment {
  /**
   * Unique identifier for the service assignment (cluster).
   */
  readonly id: UllUUID;

  /**
   * Parent infrastructure plan this assignment belongs to.
   */
  private readonly infrastructurePlan: InfrastructurePlan;

  /**
   * Facility providing the service in this assignment.
   */
  private readonly facility: Facility;

  /**
   * List of containers assigned to this facility (cluster).
   */
  private readonly assignedContainers: Container[];

  /**
   * Create a new service assignment (cluster).
   * @param infrastructurePlan the parent infrastructure plan
   * @param facility facility entity
   * @param assignedContainers list of container entities assigned to the facility
   * @param id optional explicit id (generated when omitted)
   * @throws Error when required parameters are missing
   */
  constructor(
    infrastructurePlan: InfrastructurePlan,
    facility: Facility,
    assignedContainers: Container[],
    id?: UllUUID
  ) {
    this.validate(infrastructurePlan, facility, assignedContainers);
    this.id = id ?? UllUUID.random();
    this.infrastructurePlan = infrastructurePlan;
    this.facility = facility;
    this.assignedContainers = [...assignedContainers];
  }

  /**
   * Validates that all required parameters are not null.
   *
   * @param infrastructurePlan Parent infrastructure plan
   * @param facility Facility providing the service
   * @param assignedContainers Containers assigned to the facility
   * @throws Error if any parameter is null, undefined, or invalid
   */
  private validate(
    infrastructurePlan: InfrastructurePlan | undefined,
    facility: Facility | undefined,
    assignedContainers: Container[] | undefined
  ): void {
    if (!infrastructurePlan) {
      throw new Error('Infrastructure plan is not defined');
    }
    if (!facility) {
      throw new Error('Facility is not defined');
    }
    if (!assignedContainers || assignedContainers.length === 0) {
      throw new Error('Assigned containers are not defined or empty');
    }
  }

  /**
   * Get the unique identifier of this service assignment.
   * @returns the service assignment UUID
   */
  getId(): UllUUID {
    return this.id;
  }

  /**
   * Get the parent infrastructure plan.
   * @returns the infrastructure plan
   */
  getInfrastructurePlan(): InfrastructurePlan {
    return this.infrastructurePlan;
  }

  /**
   * Get the facility associated with this assignment.
   * @returns the facility
   */
  getFacility(): Facility {
    return this.facility;
  }

  /**
   * Get the unmodifiable list of containers associated with this assignment.
   * @returns a readonly copy of the assigned containers
   */
  getAssignedContainers(): ReadonlyArray<Container> {
    return this.assignedContainers.slice();
  }

  /**
   * Get the number of containers in this cluster.
   * @returns the number of assigned containers
   */
  getNumberOfContainers(): number {
    return this.assignedContainers.length;
  }

  /**
   * Check if a container is part of this cluster.
   * @param container the container to check
   * @returns true if the container is assigned to this cluster, false otherwise
   */
  containsContainer(container: Container): boolean {
    return this.assignedContainers.some(c => c.equals(container));
  }

  /**
   * Compares this service assignment to another object for equality.
   * Two assignments are equal if they have the same identifier.
   * @param otherObject the object to compare with
   * @returns true if the objects are equal, false otherwise
   */
  equals(otherObject: unknown): boolean {
    if (this === otherObject) {
      return true;
    }
    if (!(otherObject instanceof ServiceAssignment)) {
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
   * Returns a string representation of this service assignment.
   * @returns a formatted string with service assignment cluster details
   */
  toString(): string {
    const containerIds = this.assignedContainers.map(c => c.getId());
    return `ServiceAssignmentCluster={id=${this.id}, planId=${this.infrastructurePlan.getId()}, facilityId=${this.facility.getId()}, assignedContainers=${containerIds}}`;
  }
}
