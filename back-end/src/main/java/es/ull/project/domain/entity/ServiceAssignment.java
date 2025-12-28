package es.ull.project.domain.entity;

import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.valueobject.identifiers.ContainerId;
import es.ull.project.domain.valueobject.identifiers.FacilityId;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
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
    private final ContainerId containerId;
    private final FacilityId facilityId;

    private final WasteDemand wasteDemand;
    private final Distance distance;
    private final ServiceTime serviceTime;
    private final TransportationVariableCost transportCost;

    /**
     * Creates a new service assignment.
     *
     * @param containerId      container identifier
     * @param facilityId       facility identifier
     * @param wasteDemand      waste demand of the container
     * @param distance         distance between container and facility
     * @param serviceTime      service time required
     * @param transportCost    transportation cost applied
     */
    public ServiceAssignment(ContainerId containerId,
                             FacilityId facilityId,
                             WasteDemand wasteDemand,
                             Distance distance,
                             ServiceTime serviceTime,
                             TransportationVariableCost transportCost) {

        validate(containerId, facilityId, wasteDemand, distance, serviceTime, transportCost);

        this.serviceAssignmentId = UUID.randomUUID();
        this.containerId = containerId;
        this.facilityId = facilityId;
        this.wasteDemand = wasteDemand;
        this.distance = distance;
        this.serviceTime = serviceTime;
        this.transportCost = transportCost;
    }

    private void validate(ContainerId containerId,
                          FacilityId facilityId,
                          WasteDemand wasteDemand,
                          Distance distance,
                          ServiceTime serviceTime,
                          TransportationVariableCost transportCost) {

        if (containerId == null) throw new IllegalArgumentException(CONTAINER_ID_NOT_DEFINED);
        if (facilityId == null) throw new IllegalArgumentException(FACILITY_ID_NOT_DEFINED);
        if (wasteDemand == null) throw new IllegalArgumentException(DEMAND_NOT_DEFINED);
        if (distance == null) throw new IllegalArgumentException(DISTANCE_NOT_DEFINED);
        if (serviceTime == null) throw new IllegalArgumentException(SERVICE_TIME_NOT_DEFINED);
        if (transportCost == null) throw new IllegalArgumentException(TRANSPORT_COST_NOT_DEFINED);
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
            return; // No policies to validate
        }
        
        // Delegate validation logic to ServicePolicies
        policies.validateServiceAssignment(
                this.distance.toMeters(),
                (int) this.serviceTime.getValue()
        ).ifPresent(errorMessage -> {
            throw new IllegalStateException(POLICY_VIOLATION + ": " + errorMessage);
        });
    }

    public UUID getServiceAssignmentId() {
        return serviceAssignmentId;
    }

    public ContainerId getContainerId() {
        return containerId;
    }

    public FacilityId getFacilityId() {
        return facilityId;
    }

    public WasteDemand getWasteDemand() {
        return wasteDemand;
    }

    public Distance getDistance() {
        return distance;
    }

    public ServiceTime getServiceTime() {
        return serviceTime;
    }

    public TransportationVariableCost getTransportCost() {
        return transportCost;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        ServiceAssignment other = (ServiceAssignment) otherObject;
        return Objects.equals(this.serviceAssignmentId, other.serviceAssignmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceAssignmentId);
    }

    @Override
    public String toString() {
        return String.format(
                "ServiceAssignment={id=%s, containerId=%s, facilityId=%s, demand=%s, distance=%s, serviceTime=%s, transportCost=%s}",
                serviceAssignmentId, containerId, facilityId, wasteDemand,
                distance, serviceTime, transportCost
        );
    }
}
