package es.ull.project.domain.entity;
// TODO: this file needs to be checked for the logic

import java.util.*;

import es.ull.project.domain.valueobject.identifiers.PlanId;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;

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

    private final PlanId id;
    private PlanningPeriod period;
    private List<Facility> selectedFacilities;
    private Map<Container, Facility> serviceAssignments; // One per container
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
    public InfrastructurePlan(PlanId id,
                              PlanningPeriod period,
                              MaximumBudget maxBudget,
                              ServicePolicies servicePolicies) {
        validateId(id);
        validatePeriod(period);
        validateMaxBudget(maxBudget);

        this.id = id;
        this.period = period;
        this.maxBudget = maxBudget;
        this.servicePolicies = servicePolicies;
        this.selectedFacilities = new ArrayList<>();
        this.serviceAssignments = new HashMap<>();
        this.estimatedTotalCost = new TotalCost(0.0);
    }

    private void validateId(PlanId id) {
        if (id == null) {
            throw new IllegalArgumentException(PLAN_ID_NOT_DEFINED);
        }
    }

    private void validatePeriod(PlanningPeriod period) {
        if (period == null) {
            throw new IllegalArgumentException(PERIOD_NOT_DEFINED);
        }
    }

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

        // Check facility capacity and status
        facility.assignWasteDemand(container.getWasteDemand());

        serviceAssignments.put(container, facility);

        // Recalculate total cost
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
            total += entry.getValue().getOpeningFixedCost().getAmount(); // Optional: add transportation cost here
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
        // TODO: chek this logic, buceause idk if it's correct
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

    public PlanId getId() {
        return id;
    }

    public PlanningPeriod getPeriod() {
        return period;
    }

    public List<Facility> getSelectedFacilities() {
        return Collections.unmodifiableList(selectedFacilities);
    }

    public Map<Container, Facility> getServiceAssignments() {
        return Collections.unmodifiableMap(serviceAssignments);
    }

    public MaximumBudget getMaxBudget() {
        return maxBudget;
    }

    public TotalCost getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public ServicePolicies getServicePolicies() {
        return servicePolicies;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        InfrastructurePlan other = (InfrastructurePlan) otherObject;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

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