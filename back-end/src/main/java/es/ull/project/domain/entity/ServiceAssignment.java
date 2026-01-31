package es.ull.project.domain.entity;

import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.policy.ServicePolicies;

/**
 * ServiceAssignment
 *
 * Represents an assignment between a container and a facility,
 * including calculated values such as distance, service time and transportation cost.
 *
 * It is an immutable entity except for recalculations triggered by policy validation.
 */
public class ServiceAssignment {

    public static final String CONTAINER_ID_NOT_DEFINED = "Container id is not defined";
    public static final String FACILITY_ID_NOT_DEFINED = "Facility id is not defined";
    public static final String DEMAND_NOT_DEFINED = "Waste demand is not defined";
    public static final String DISTANCE_NOT_DEFINED = "Distance is not defined";
    public static final String SERVICE_TIME_NOT_DEFINED = "Service time is not defined";
    public static final String TRANSPORT_COST_NOT_DEFINED = "Transportation cost is not defined";
    public static final String POLICY_VIOLATION = "Service assignment violates service policies";

    private final UUID serviceAssignmentId;
    private final Container container;
    private final Facility facility;

    private final WasteDemand wasteDemand;
    private final Distance distance;
    private final ServiceTime serviceTime;
    private final TransportationVariableCost transportCost;

    /**
     * Creates a new service assignment.
     *
    * @param container        container entity
    * @param facility         facility entity
     * @param wasteDemand      waste demand of the container
     * @param distance         distance between container and facility
     * @param serviceTime      service time required
     * @param transportCost    transportation cost applied
     */
    public ServiceAssignment(Container container,
                             Facility facility,
                             WasteDemand wasteDemand,
                             Distance distance,
                             ServiceTime serviceTime,
                             TransportationVariableCost transportCost) {
        validate(container, facility, wasteDemand, distance, serviceTime, transportCost);
        this.serviceAssignmentId = UUID.randomUUID();
        this.container = container;
        this.facility = facility;
        this.wasteDemand = wasteDemand;
        this.distance = distance;
        this.serviceTime = serviceTime;
        this.transportCost = transportCost;
    }

    /**
     * Copy constructor.
     * Creates a new ServiceAssignment as a copy of another ServiceAssignment.
     *
     * @param otherObject the ServiceAssignment to copy
     */
    public ServiceAssignment(ServiceAssignment otherObject) {
        this.serviceAssignmentId = otherObject.serviceAssignmentId;
        this.container = otherObject.container;
        this.facility = otherObject.facility;
        this.wasteDemand = otherObject.wasteDemand;
        this.distance = otherObject.distance;
        this.serviceTime = otherObject.serviceTime;
        this.transportCost = otherObject.transportCost;
    }

    /**
     * Restore constructor.
     * Restores a ServiceAssignment from persistence with all its attributes.
     *
     * @param serviceAssignmentId the assignment identifier
     * @param container           the container entity
     * @param facility            the facility entity
     * @param wasteDemand         the waste demand
     * @param distance            the distance between container and facility
     * @param serviceTime         the service time required
     * @param transportCost       the transportation cost
     */
    public ServiceAssignment(UUID serviceAssignmentId,
                             Container container,
                             Facility facility,
                             WasteDemand wasteDemand,
                             Distance distance,
                             ServiceTime serviceTime,
                             TransportationVariableCost transportCost) {
        validate(container, facility, wasteDemand, distance, serviceTime, transportCost);
        this.serviceAssignmentId = serviceAssignmentId;
        this.container = container;
        this.facility = facility;
        this.wasteDemand = wasteDemand;
        this.distance = distance;
        this.serviceTime = serviceTime;
        this.transportCost = transportCost;
    }

    /**
     * Validates that all required parameters are not null.
     *
     * @param container     the container to validate
     * @param facility      the facility to validate
     * @param wasteDemand   the waste demand to validate
     * @param distance      the distance to validate
     * @param serviceTime   the service time to validate
     * @param transportCost the transport cost to validate
     * @throws IllegalArgumentException if any parameter is null
     */
    private void validate(Container container,
                          Facility facility,
                          WasteDemand wasteDemand,
                          Distance distance,
                          ServiceTime serviceTime,
                          TransportationVariableCost transportCost) {
        if (container == null) {
            throw new IllegalArgumentException(CONTAINER_ID_NOT_DEFINED);
        }
        if (facility == null) {
            throw new IllegalArgumentException(FACILITY_ID_NOT_DEFINED);
        }
        if (wasteDemand == null) {
            throw new IllegalArgumentException(DEMAND_NOT_DEFINED);
        }
        if (distance == null) {
            throw new IllegalArgumentException(DISTANCE_NOT_DEFINED);
        }
        if (serviceTime == null) {
            throw new IllegalArgumentException(SERVICE_TIME_NOT_DEFINED);
        }
        if (transportCost == null) {
            throw new IllegalArgumentException(TRANSPORT_COST_NOT_DEFINED);
        }
    }

    /**
     * Validates whether this assignment complies with service policies.
     *
     * This method is intended to be called from the InfrastructurePlan aggregate.
     *
     * @param policies service policies to validate
     * @throws IllegalStateException if assignment violates policies
     */
    public void validatePolicies(ServicePolicies policies) {
        if (policies == null) {
            return;
        }
        policies.validateServiceAssignment(
                this.distance.toMeters(),
                (int) this.serviceTime.getValue()
        ).ifPresent(errorMessage -> {
            throw new IllegalStateException(POLICY_VIOLATION + ": " + errorMessage);
        });
    }

    /**
     * Returns the unique identifier of this service assignment.
     *
     * @return the service assignment UUID
     */
    public UUID getServiceAssignmentId() {
        return serviceAssignmentId;
    }

    /**
     * Returns the container associated with this assignment.
     *
     * @return the container
     */
    public Container getContainer() {
        return container;
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
     * Returns the waste demand for this assignment.
     *
     * @return the waste demand
     */
    public WasteDemand getWasteDemand() {
        return wasteDemand;
    }

    /**
     * Returns the distance between container and facility.
     *
     * @return the distance
     */
    public Distance getDistance() {
        return distance;
    }

    /**
     * Returns the service time required for this assignment.
     *
     * @return the service time
     */
    public ServiceTime getServiceTime() {
        return serviceTime;
    }

    /**
     * Returns the transportation cost for this assignment.
     *
     * @return the transportation cost
     */
    public TransportationVariableCost getTransportCost() {
        return transportCost;
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
        return Objects.equals(this.serviceAssignmentId, other.serviceAssignmentId);
    }

    /**
     * Returns a hash code value for this service assignment.
     *
     * @return a hash code value based on the service assignment identifier
     */
    @Override
    public int hashCode() {
        return Objects.hash(serviceAssignmentId);
    }

    /**
     * Returns a string representation of this service assignment.
     *
     * @return a formatted string with all service assignment attributes
     */
    @Override
    public String toString() {
        return String.format(
                "ServiceAssignment={id=%s, containerId=%s, facilityId=%s, demand=%s, distance=%s, serviceTime=%s, transportCost=%s}",
                serviceAssignmentId, container.getId(), facility.getId(), wasteDemand,
                distance, serviceTime, transportCost
        );
    }
}
