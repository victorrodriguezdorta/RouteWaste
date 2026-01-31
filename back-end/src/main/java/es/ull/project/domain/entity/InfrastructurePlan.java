package es.ull.project.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * InfrastructurePlan
 * 
 * Represents a complete infrastructure planning decision for a given planning horizon.
 */
public class InfrastructurePlan {

    public static final String PLAN_ID_NOT_DEFINED = "Plan id is not defined";
    public static final String PERIOD_NOT_DEFINED = "Planning period is not defined";
    public static final String MAX_BUDGET_NOT_DEFINED = "Maximum budget is not defined";
    public static final String TOTAL_COST_EXCEEDED = "Total cost exceeds maximum budget";
    public static final String INVALID_ASSIGNMENT = "Container assignment is invalid";

    private final UUID id;
    private PlanningPeriod period;
    private List<Facility> selectedFacilities;
    private Map<Container, Facility> serviceAssignments;
    private ServicePolicies servicePolicies;
    private MaximumBudget maxBudget;
    private TotalCost estimatedTotalCost;

    /**
     * Creates a new InfrastructurePlan.
     *
     * @param id                Plan identifier
     * @param period            Planning period
     * @param maxBudget         Maximum budget allowed
     * @param servicePolicies   Service policies to comply with
     */
    public InfrastructurePlan(
                              PlanningPeriod period,
                              MaximumBudget maxBudget,
                              ServicePolicies servicePolicies) {
        validatePeriod(period);
        validateMaxBudget(maxBudget);
        this.id = UUID.randomUUID();
        this.period = period;
        this.maxBudget = maxBudget;
        this.servicePolicies = servicePolicies;
        this.selectedFacilities = new ArrayList<>();
        this.serviceAssignments = new HashMap<>();
        this.estimatedTotalCost = new TotalCost(0.0);
    }

    /**
     * Copy constructor.
     * Creates a new InfrastructurePlan as a copy of another InfrastructurePlan.
     *
     * @param otherObject the InfrastructurePlan to copy
     */
    public InfrastructurePlan(InfrastructurePlan otherObject) {
        this.id = otherObject.id;
        this.period = otherObject.period;
        this.maxBudget = otherObject.maxBudget;
        this.servicePolicies = otherObject.servicePolicies;
        this.selectedFacilities = new ArrayList<>(otherObject.selectedFacilities);
        this.serviceAssignments = new HashMap<>(otherObject.serviceAssignments);
        this.estimatedTotalCost = otherObject.estimatedTotalCost;
    }

    /**
     * Restore constructor.
     * Restores an InfrastructurePlan from persistence with all its attributes.
     *
     * @param id                  the plan identifier
     * @param period              the planning period
     * @param maxBudget           the maximum budget allowed
     * @param servicePolicies     the service policies
     * @param selectedFacilities  the list of selected facilities
     * @param serviceAssignments  the map of container to facility assignments
     * @param estimatedTotalCost  the estimated total cost
     */
    public InfrastructurePlan(UUID id,
                               PlanningPeriod period,
                               MaximumBudget maxBudget,
                               ServicePolicies servicePolicies,
                               List<Facility> selectedFacilities,
                               Map<Container, Facility> serviceAssignments,
                               TotalCost estimatedTotalCost) {
        validateId(id);
        validatePeriod(period);
        validateMaxBudget(maxBudget);
        this.id = id;
        this.period = period;
        this.maxBudget = maxBudget;
        this.servicePolicies = servicePolicies;
        this.selectedFacilities = selectedFacilities != null ? new ArrayList<>(selectedFacilities) : new ArrayList<>();
        this.serviceAssignments = serviceAssignments != null ? new HashMap<>(serviceAssignments) : new HashMap<>();
        this.estimatedTotalCost = estimatedTotalCost != null ? estimatedTotalCost : new TotalCost(0.0);
    }

    /**
     * Static factory method to restore an InfrastructurePlan from persistence.
     *
     * @param id                  the plan identifier
     * @param period              the planning period
     * @param maxBudget           the maximum budget allowed
     * @param servicePolicies     the service policies
     * @param selectedFacilities  the list of selected facilities
     * @param serviceAssignments  the map of container to facility assignments
     * @param estimatedTotalCost  the estimated total cost
     * @return the restored InfrastructurePlan instance
     */
    public static InfrastructurePlan restore(UUID id,
                                             PlanningPeriod period,
                                             MaximumBudget maxBudget,
                                             ServicePolicies servicePolicies,
                                             List<Facility> selectedFacilities,
                                             Map<Container, Facility> serviceAssignments,
                                             TotalCost estimatedTotalCost) {
        return new InfrastructurePlan(id, period, maxBudget, servicePolicies, selectedFacilities, serviceAssignments, estimatedTotalCost);
    }

    /**
     * Validates that the plan identifier is not null.
     *
     * @param id the plan identifier to validate
     * @throws IllegalArgumentException if the id is null
     */
    private static void validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(PLAN_ID_NOT_DEFINED);
        }
    }

    /**
     * Validates that the planning period is not null.
     *
     * @param period the planning period to validate
     * @throws IllegalArgumentException if the period is null
     */
    private void validatePeriod(PlanningPeriod period) {
        if (period == null) {
            throw new IllegalArgumentException(PERIOD_NOT_DEFINED);
        }
    }

    /**
     * Validates that the maximum budget is not null.
     *
     * @param maxBudget the maximum budget to validate
     * @throws IllegalArgumentException if the maximum budget is null
     */
    private void validateMaxBudget(MaximumBudget maxBudget) {
        if (maxBudget == null) {
            throw new IllegalArgumentException(MAX_BUDGET_NOT_DEFINED);
        }
    }

    /**
     * Assigns a container to a facility.
     *
     * @param container Container to assign
     * @param facility Facility to assign to
     */
    public void assignContainerToFacility(Container container, Facility facility) {
        if (container == null || facility == null) {
            throw new IllegalArgumentException(INVALID_ASSIGNMENT);
        }
        facility.assignWasteDemand(container.getWasteDemand());
        serviceAssignments.put(container, facility);
        recalculateTotalCost();
    }

    /**
     * Adds a facility to the plan.
     *
     * @param facility Facility to add
     */
    public void addFacility(Facility facility) {
        if (facility != null && !selectedFacilities.contains(facility)) {
            selectedFacilities.add(facility);
        }
    }

    /**
     * Recalculates the total estimated cost of the plan.
     */
    public void recalculateTotalCost() {
        double total = 0.0;
        for (Facility facility : selectedFacilities) {
            total += facility.getOpeningFixedCost().getAmount();
        }
        for (Map.Entry<Container, Facility> entry : serviceAssignments.entrySet()) {
            total += entry.getValue().getOpeningFixedCost().getAmount();
        }
        TotalCost newCost = new TotalCost(total);
        if (newCost.greaterThan(this.maxBudget)) {
            throw new IllegalStateException(TOTAL_COST_EXCEEDED);
        }
        this.estimatedTotalCost = newCost;
    }

    /**
     * Checks if the plan is valid.
     *
     * @return true if the plan is valid, false otherwise
     */
    public boolean isPlanValid() {
        for (Map.Entry<Container, Facility> entry : serviceAssignments.entrySet()) {
            Facility facility = entry.getValue();
            if (facility.getStatus().isDiscarded()) {
                return false;
            }
            WasteDemand totalDemand = facility.getAssignedWasteDemand();
            if (totalDemand.greaterThan(facility.getCapacity())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the plan identifier.
     *
     * @return the unique identifier of the plan
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the planning period.
     *
     * @return the planning period of this plan
     */
    public PlanningPeriod getPeriod() {
        return period;
    }

    /**
     * Returns the list of selected facilities.
     *
     * @return an unmodifiable list of selected facilities
     */
    public List<Facility> getSelectedFacilities() {
        return Collections.unmodifiableList(selectedFacilities);
    }

    /**
     * Returns the service assignments map.
     *
     * @return an unmodifiable map of container to facility assignments
     */
    public Map<Container, Facility> getServiceAssignments() {
        return Collections.unmodifiableMap(serviceAssignments);
    }

    /**
     * Returns the maximum budget.
     *
     * @return the maximum budget allowed for this plan
     */
    public MaximumBudget getMaxBudget() {
        return maxBudget;
    }

    /**
     * Returns the estimated total cost.
     *
     * @return the estimated total cost of this plan
     */
    public TotalCost getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    /**
     * Returns the service policies.
     *
     * @return the service policies for this plan
     */
    public ServicePolicies getServicePolicies() {
        return servicePolicies;
    }

    /**
     * Updates the planning period.
     *
     * @param newPeriod the new planning period
     */
    public void updatePeriod(PlanningPeriod newPeriod) {
        validatePeriod(newPeriod);
        this.period = newPeriod;
    }

    /**
     * Updates the maximum budget.
     *
     * @param newMaxBudget the new maximum budget
     * @throws IllegalStateException if the estimated total cost exceeds the new budget
     */
    public void updateMaxBudget(MaximumBudget newMaxBudget) {
        validateMaxBudget(newMaxBudget);
        this.maxBudget = newMaxBudget;
        if (this.estimatedTotalCost.greaterThan(this.maxBudget)) {
            throw new IllegalStateException(TOTAL_COST_EXCEEDED);
        }
    }

    /**
     * Updates the service policies.
     *
     * @param newServicePolicies the new service policies
     */
    public void updateServicePolicies(ServicePolicies newServicePolicies) {
        this.servicePolicies = newServicePolicies;
    }

    /**
     * Compares this infrastructure plan with another object for equality.
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
        InfrastructurePlan other = (InfrastructurePlan) otherObject;
        return Objects.equals(this.id, other.id);
    }

    /**
     * Returns the hash code of this infrastructure plan.
     *
     * @return the hash code based on the plan id
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Returns a string representation of this infrastructure plan.
     *
     * @return a formatted string with plan details
     */
    @Override
    public String toString() {
        return String.format(
                "InfrastructurePlan={id=%s, period=%s, facilities=%s, assignments=%s, totalCost=%s}",
                id,
                period,
                selectedFacilities,
                serviceAssignments.keySet(),
                estimatedTotalCost
        );
    }
}