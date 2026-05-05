package es.ull.project.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * InfrastructurePlan
 * 
 * Represents a complete infrastructure planning decision for a given planning
 * horizon.
 */
public class InfrastructurePlan {

    public static final String PERIOD_NOT_DEFINED = "Planning period is not defined";
    public static final String MAX_BUDGET_NOT_DEFINED = "Maximum budget is not defined";
    public static final String TOTAL_COST_EXCEEDED = "Total cost exceeds maximum budget";
    public static final String INVALID_ASSIGNMENT = "Service assignment is invalid";

    /**
     * Unique identifier for the infrastructure plan.
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * Planning period for which the infrastructure plan is designed.
     * It is a required attribute.
     */
    private PlanningPeriod period;

    /**
     * List of facilities selected to be part of the infrastructure plan.
     * It is a computed attribute.
     */
    private List<Facility> selectedFacilities;

    /**
     * List of service assignments linking containers to facilities.
     * It is a computed attribute.
     */
    private List<ServiceAssignment> serviceAssignments;

    /**
     * Identifiers of the daily plans generated for this infrastructure plan.
     * It is a computed attribute.
     */
    private List<DailyPlan> dailyPlans;

    /**
     * Service policies that must be complied with in the plan.
     * It is a required attribute.
     */
    private ServicePolicies servicePolicies;

    /**
     * Maximum budget allowed for the infrastructure plan.
     * It is a required attribute.
     */
    private MaximumBudget maxBudget;

    /**
     * Estimated total cost of implementing the infrastructure plan.
     * It is a computed attribute.
     */
    private TotalCost estimatedTotalCost;

    /**
     * Total weight collected as determined by the algorithm.
     */
    private CollectedWeightKilograms totalCollectedKilograms;

    /**
     * Total volume collected as determined by the algorithm.
     */
    private CollectedVolumeLiters totalCollectedLiters;

    /**
     * Total distance of all routes as determined by the algorithm.
     */
    private Distance totalDistanceMeters;

    /**
     * Number of days for the planning period.
     */
    private Integer numberOfDays;

    /**
     * Average pickup time in minutes.
     */
    private Integer averagePickupTimeMinutes;

    /**
     * Timestamp when the algorithm execution was performed (ISO 8601 format).
     */
    private String executedAt;

    /**
     * Creates a new InfrastructurePlan.
     *
     * @param id              Plan identifier
     * @param period          Planning period
     * @param maxBudget       Maximum budget allowed
     * @param servicePolicies Service policies to comply with
     */
    public InfrastructurePlan(
            PlanningPeriod period,
            MaximumBudget maxBudget,
            ServicePolicies servicePolicies,
            Integer numberOfDays,
            Integer averagePickupTimeMinutes,
            String executedAt) {
        validatePeriod(period);
        validateMaxBudget(maxBudget);
        this.id = UUID.randomUUID();
        this.period = period;
        this.maxBudget = maxBudget;
        this.servicePolicies = servicePolicies;
        this.numberOfDays = numberOfDays;
        this.averagePickupTimeMinutes = averagePickupTimeMinutes;
        this.executedAt = executedAt;
        this.selectedFacilities = new ArrayList<>();
        this.serviceAssignments = new ArrayList<>();
        this.dailyPlans = new ArrayList<>();
        this.estimatedTotalCost = new TotalCost(0.0);
        this.totalCollectedKilograms = CollectedWeightKilograms.fromKilograms(0.0);
        this.totalCollectedLiters = CollectedVolumeLiters.fromLiters(0.0);
        this.totalDistanceMeters = Distance.fromMeters(0.0);
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
        this.numberOfDays = otherObject.numberOfDays;
        this.averagePickupTimeMinutes = otherObject.averagePickupTimeMinutes;
        this.executedAt = otherObject.executedAt;
        this.selectedFacilities = new ArrayList<>(otherObject.selectedFacilities);
        this.serviceAssignments = new ArrayList<>(otherObject.serviceAssignments);
        this.dailyPlans = new ArrayList<>(otherObject.dailyPlans);
        this.estimatedTotalCost = otherObject.estimatedTotalCost;
        this.totalCollectedKilograms = otherObject.totalCollectedKilograms;
        this.totalCollectedLiters = otherObject.totalCollectedLiters;
        this.totalDistanceMeters = otherObject.totalDistanceMeters;
    }

    /**
     * Restore constructor.
     * Restores an InfrastructurePlan from persistence with all its attributes.
     *
     * @param id                 the plan identifier
     * @param period             the planning period
     * @param selectedFacilities the list of selected facilities
     * @param serviceAssignments the list of service assignments
     * @param dailyPlans         the list of daily plan entities
     * @param servicePolicies    the service policies
     * @param maxBudget          the maximum budget allowed
     * @param estimatedTotalCost the estimated total cost
     * @param numberOfDays       the number of days for planning
     * @param averagePickupTimeMinutes the average pickup time in minutes
     * @param executedAt         the timestamp of algorithm execution
     */
    public InfrastructurePlan(UUID id,
            PlanningPeriod period,
            List<Facility> selectedFacilities,
            List<ServiceAssignment> serviceAssignments,
            List<DailyPlan> dailyPlans,
            ServicePolicies servicePolicies,
            MaximumBudget maxBudget,
            TotalCost estimatedTotalCost,
            CollectedWeightKilograms totalCollectedKilograms,
            CollectedVolumeLiters totalCollectedLiters,
            Distance totalDistanceMeters,
            Integer numberOfDays,
            Integer averagePickupTimeMinutes,
            String executedAt) {
        validatePeriod(period);
        validateMaxBudget(maxBudget);
        this.id = id;
        this.period = period;
        this.selectedFacilities = selectedFacilities != null ? new ArrayList<>(selectedFacilities) : new ArrayList<>();
        this.serviceAssignments = serviceAssignments != null ? new ArrayList<>(serviceAssignments) : new ArrayList<>();
        this.dailyPlans = dailyPlans != null ? new ArrayList<>(dailyPlans) : new ArrayList<>();
        this.servicePolicies = servicePolicies;
        this.maxBudget = maxBudget;
        this.estimatedTotalCost = estimatedTotalCost != null ? estimatedTotalCost : new TotalCost(0.0);
        this.totalCollectedKilograms = totalCollectedKilograms != null ? totalCollectedKilograms : CollectedWeightKilograms.fromKilograms(0.0);
        this.totalCollectedLiters = totalCollectedLiters != null ? totalCollectedLiters : CollectedVolumeLiters.fromLiters(0.0);
        this.totalDistanceMeters = totalDistanceMeters != null ? totalDistanceMeters : Distance.fromMeters(0.0);
        this.numberOfDays = numberOfDays;
        this.averagePickupTimeMinutes = averagePickupTimeMinutes;
        this.executedAt = executedAt;
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
     * Adds a service assignment to the plan.
     *
     * @param assignment ServiceAssignment to add
     */
    public void addServiceAssignment(ServiceAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException(INVALID_ASSIGNMENT);
        }
        serviceAssignments.add(assignment);
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
     * Clears all facilities from the plan.
     */
    public void clearFacilities() {
        selectedFacilities.clear();
    }

    /**
     * Clears all service assignments from the plan.
     */
    public void clearServiceAssignments() {
        serviceAssignments.clear();
        recalculateTotalCost();
    }

    /**
     * Adds a daily plan identifier to the plan.
     *
     * @param dailyPlanId the daily plan identifier to add
     */
    public void addDailyPlan(DailyPlan dailyPlan) {
        if (dailyPlan != null) {
            boolean exists = false;
            for (DailyPlan dp : dailyPlans) {
                if (dp.getId().equals(dailyPlan.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                dailyPlans.add(dailyPlan);
            }
        }
    }

    /**
     * Clears all daily plans from the plan.
     */
    public void clearDailyPlans() {
        dailyPlans.clear();
    }

    /**
     * Recalculates the total estimated cost of the plan.
     */
    public void recalculateTotalCost() {
        double total = 0.0;
        for (Facility facility : selectedFacilities) {
            total += facility.getOpeningFixedCost().getAmount();
        }
        // Transport cost should be updated based on routing, but for now we just 
        // sum facility costs as routing cost is handled separately or needs to be derived from distance.
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
        for (ServiceAssignment assignment : serviceAssignments) {
            Facility facility = assignment.getFacility();
            
            // Check if facility is not discarded
            if (facility.getStatus().isDiscarded()) {
                return false;
            }
            
            // Check if current filling level is valid (not negative)
            DailyWasteDemandLitersPerDay currentFillingLevel = facility.getCurrentFillingLevel();
            if (currentFillingLevel.getLitersPerDay() < 0) {
                return false;
            }
            
            // Check if current filling level doesn't exceed a reasonable limit
            // relative to storage capacity (this is a business logic decision)
            // For now, we validate that storage capacity is sufficient
            if (facility.getStorageCapacity() == null) {
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
     * Returns the list of service assignments.
     *
     * @return an unmodifiable list of service assignments
     */
    public List<ServiceAssignment> getServiceAssignments() {
        return Collections.unmodifiableList(serviceAssignments);
    }

    /**
     * Returns the identifiers of the daily plans associated with this plan.
     *
     * @return the daily plan identifiers
     */
    /**
     * Returns the identifiers of the daily plans associated with this plan.
     * This is a convenience derived from the stored `DailyPlan` entities.
     *
     * @return the daily plan identifiers
     */
    public List<UUID> getDailyPlanIds() {
        List<UUID> ids = new ArrayList<>();
        for (DailyPlan dp : dailyPlans) {
            ids.add(dp.getId());
        }
        return Collections.unmodifiableList(ids);
    }

    /**
     * Returns the DailyPlan entities associated with this plan.
     *
     * @return an unmodifiable list of DailyPlan entities
     */
    public List<DailyPlan> getDailyPlans() {
        return Collections.unmodifiableList(dailyPlans);
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

    public CollectedWeightKilograms getTotalCollectedKilograms() {
        return totalCollectedKilograms;
    }

    public CollectedVolumeLiters getTotalCollectedLiters() {
        return totalCollectedLiters;
    }

    public Distance getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public Integer getAveragePickupTimeMinutes() {
        return averagePickupTimeMinutes;
    }

    public String getExecutedAt() {
        return executedAt;
    }

    public void updateAlgorithmMetrics(CollectedWeightKilograms kg, CollectedVolumeLiters liters, Distance distance) {
        this.totalCollectedKilograms = kg;
        this.totalCollectedLiters = liters;
        this.totalDistanceMeters = distance;
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
     * @throws IllegalStateException if the estimated total cost exceeds the new
     *                               budget
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
                serviceAssignments,
                estimatedTotalCost);
    }
}