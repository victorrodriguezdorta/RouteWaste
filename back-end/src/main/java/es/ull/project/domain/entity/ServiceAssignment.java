package es.ull.project.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ServiceAssignment
 *
 * Represents an assignment between a cluster of containers and a facility.
 *
 * It is an immutable entity.
 */
public class ServiceAssignment {

    public static final String INFRASTRUCTURE_PLAN_NOT_DEFINED = "Infrastructure plan is not defined";
    public static final String CONTAINERS_NOT_DEFINED = "Assigned containers are not defined or empty";
    public static final String FACILITY_ID_NOT_DEFINED = "Facility id is not defined";

    /**
     * Unique identifier for the service assignment (cluster).
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * Parent infrastructure plan this assignment belongs to.
     * It is a required attribute.
     */
    private final InfrastructurePlan infrastructurePlan;

    /**
     * Facility providing the service in this assignment.
     * It is a required attribute.
     */
    private final Facility facility;

    /**
     * List of containers assigned to this facility (cluster).
     * It is a required attribute.
     */
    private final List<Container> assignedContainers;

    /**
     * Creates a new service assignment (cluster).
     *
     * @param infrastructurePlan the parent infrastructure plan
     * @param facility           facility entity
     * @param assignedContainers list of container entities assigned to the facility
     */
    public ServiceAssignment(InfrastructurePlan infrastructurePlan, Facility facility, List<Container> assignedContainers) {
        validate(infrastructurePlan, facility, assignedContainers);
        this.id = UUID.randomUUID();
        this.infrastructurePlan = infrastructurePlan;
        this.facility = facility;
        this.assignedContainers = new ArrayList<>(assignedContainers);
    }

    /**
     * Copy constructor.
     * Creates a new ServiceAssignment as a copy of another ServiceAssignment.
     *
     * @param otherObject the ServiceAssignment to copy
     */
    public ServiceAssignment(ServiceAssignment otherObject) {
        this.id = otherObject.id;
        this.infrastructurePlan = otherObject.infrastructurePlan;
        this.facility = otherObject.facility;
        this.assignedContainers = new ArrayList<>(otherObject.assignedContainers);
    }

    /**
     * Restore constructor.
     * Restores a ServiceAssignment from persistence with all its attributes.
     *
     * @param id                 the assignment identifier
     * @param infrastructurePlan the parent infrastructure plan
     * @param facility           the facility entity
     * @param assignedContainers the list of assigned containers
     */
    public ServiceAssignment(
            UUID id,
            InfrastructurePlan infrastructurePlan,
            Facility facility,
            List<Container> assignedContainers) {
        validate(infrastructurePlan, facility, assignedContainers);
        this.id = id;
        this.infrastructurePlan = infrastructurePlan;
        this.facility = facility;
        this.assignedContainers = new ArrayList<>(assignedContainers);
    }

    /**
     * Validates that all required parameters are not null.
     *
     * @param infrastructurePlan the parent infrastructure plan
     * @param facility           the facility to validate
     * @param assignedContainers the list of containers to validate
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    private void validate(InfrastructurePlan infrastructurePlan, Facility facility, List<Container> assignedContainers) {
        if (infrastructurePlan == null) {
            throw new IllegalArgumentException(INFRASTRUCTURE_PLAN_NOT_DEFINED);
        }
        if (facility == null) {
            throw new IllegalArgumentException(FACILITY_ID_NOT_DEFINED);
        }
        if (assignedContainers == null || assignedContainers.isEmpty()) {
            throw new IllegalArgumentException(CONTAINERS_NOT_DEFINED);
        }
    }

    /**
     * Returns the unique identifier of this service assignment.
     *
     * @return the service assignment UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the parent infrastructure plan.
     *
     * @return the infrastructure plan
     */
    public InfrastructurePlan getInfrastructurePlan() {
        return infrastructurePlan;
    }

    /**
     * Returns the facility associated with this assignment.
     *
     * @return the facility
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Returns the unmodifiable list of containers associated with this assignment.
     *
     * @return the assigned containers
     */
    public List<Container> getAssignedContainers() {
        return Collections.unmodifiableList(assignedContainers);
    }

    /**
     * Compares this service assignment to another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ServiceAssignment other = (ServiceAssignment) otherObject;
        return Objects.equals(this.id, other.id);
    }

    /**
     * Returns a hash code value for this service assignment.
     *
     * @return a hash code value based on the service assignment identifier
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this service assignment.
     *
     * @return a formatted string with all service assignment attributes
     */
    @Override
    public String toString() {
        List<UUID> containerIds = assignedContainers.stream()
                .map(Container::getId)
                .collect(Collectors.toList());
        return String.format(
                "ServiceAssignment={id=%s, planId=%s, facilityId=%s, assignedContainers=%s}",
                id, infrastructurePlan.getId(), facility.getId(), containerIds);
    }
}
