package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * InfrastructurePlan
 * 
 * Represents a complete infrastructure planning decision for a given planning
 * horizon.
 */
public class InfrastructurePlan {

    public static final String PERIOD_NOT_DEFINED = "Planning period is not defined";
    public static final String MAX_BUDGET_NOT_DEFINED = "Maximum budget is not defined";
    public static final String VALIDITY_STATE_NOT_DEFINED = "Infrastructure plan validity state is not defined";
    public static final String EXECUTION_STATE_NOT_DEFINED = "Infrastructure plan execution state is not defined";
    public static final String TOTAL_COST_EXCEEDED = "Total cost exceeds maximum budget";
    public static final String INVALID_ASSIGNMENT = "Service assignment is invalid";
    private static final int ZERO = 0;

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
     * It is a computed attribute.
     */
    private CollectedWeightKilograms totalCollectedKilograms;

    /**
     * Total volume collected as determined by the algorithm.
     * It is a computed attribute.
     */
    private CollectedVolumeLiters totalCollectedLiters;

    /**
     * Total distance of all routes as determined by the algorithm.
     * It is a computed attribute.
     */
    private Distance totalDistanceMeters;

    /**
     * Number of days for the planning period.
     * It is an optional attribute.
     */
    private NumberOfDays numberOfDays;

    /**
     * Average pickup time in minutes.
     * It is an optional attribute.
     */
    private AveragePickupTimeMinutes averagePickupTimeMinutes;

    /**
     * Timestamp when the algorithm execution was performed (ISO 8601 format).
     * It is an optional attribute.
     */
    private ExecutedAt executedAt;

    /**
     * Whether this plan is still aligned with master data for referenced entities.
     * It is a required attribute.
     */
    private InfrastructurePlanValidityState validityState;

    /**
     * Lifecycle state while the algorithm runs or after it finishes.
     * It is a required attribute.
     */
    private InfrastructurePlanExecutionState executionState;

    /**
     * Optional error description when {@link #executionState} is {@link InfrastructurePlanExecutionState#FAILED}.
     * It is an optional attribute.
     */
    private InfrastructurePlanFailureReason failureReason;

    /**
     * JSON snapshot of the client execution request used to run the algorithm (identifiers and parameters).
     * It is an optional attribute.
     */
    private AlgorithmJsonPayload executionRequestJson;

    /**
     * Container daily state snapshots associated with this plan (domain view).
     * It is a computed attribute.
     */
    private List<ContainerDailyState> containerDailyStates;

    /**
     * Creates a new InfrastructurePlan.
     *
     * @param period                   Planning period
     * @param maxBudget                Maximum budget allowed
     * @param servicePolicies          Service policies to comply with
     * @param numberOfDays             Number of days in the planning horizon
     * @param averagePickupTimeMinutes Average pickup time in minutes
     * @param executedAt               Timestamp of algorithm execution (ISO 8601)
     * @param validityState            Initial validity state
     * @param executionState           Initial algorithm execution lifecycle state
     */
    public InfrastructurePlan(
            PlanningPeriod period,
            MaximumBudget maxBudget,
            ServicePolicies servicePolicies,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            ExecutedAt executedAt,
            InfrastructurePlanValidityState validityState,
            InfrastructurePlanExecutionState executionState) {
        validatePeriod(period);
        validateMaxBudget(maxBudget);
        validateValidityState(validityState);
        validateExecutionState(executionState);
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
        this.containerDailyStates = new ArrayList<>();
        this.validityState = validityState;
        this.executionState = executionState;
        this.failureReason = null;
        this.executionRequestJson = null;
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
        this.containerDailyStates = new ArrayList<>(otherObject.containerDailyStates);
        this.validityState = otherObject.validityState;
        this.executionState = otherObject.executionState;
        this.failureReason = otherObject.failureReason;
        this.executionRequestJson = otherObject.executionRequestJson;
    }

    /**
     * Restore constructor.
     * Restores an InfrastructurePlan from persistence with all its attributes.
     *
     * @param id                       the plan identifier
     * @param period                   the planning period
     * @param selectedFacilities       selected facilities restored for the plan
     * @param serviceAssignments       service assignments restored for the plan
     * @param dailyPlans               daily plans restored for the plan
     * @param servicePolicies          the service policies
     * @param maxBudget                the maximum budget allowed
     * @param estimatedTotalCost       estimated total cost restored for the plan
     * @param totalCollectedKilograms  total collected kilograms restored for the plan
     * @param totalCollectedLiters     total collected liters restored for the plan
     * @param totalDistanceMeters      total route distance restored for the plan
     * @param numberOfDays             the number of days for planning
     * @param averagePickupTimeMinutes the average pickup time in minutes
     * @param executedAt               the timestamp of algorithm execution
     * @param validityState            persisted validity state
     * @param executionState           persisted algorithm execution lifecycle state
     * @param failureReason            optional failure description when execution failed
     * @param executionRequestJson     persisted client request JSON snapshot
     * @param containerDailyStates     container daily states restored for the plan
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
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            ExecutedAt executedAt,
            InfrastructurePlanValidityState validityState,
            InfrastructurePlanExecutionState executionState,
            InfrastructurePlanFailureReason failureReason,
            AlgorithmJsonPayload executionRequestJson,
            List<ContainerDailyState> containerDailyStates) {
        validatePeriod(period);
        validateMaxBudget(maxBudget);
        validateValidityState(validityState);
        validateExecutionState(executionState);
        this.id = id;
        this.period = period;
        this.selectedFacilities = selectedFacilities != null ? new ArrayList<>(selectedFacilities) : new ArrayList<>();
        this.serviceAssignments = serviceAssignments != null ? new ArrayList<>(serviceAssignments) : new ArrayList<>();
        this.dailyPlans = dailyPlans != null ? new ArrayList<>(dailyPlans) : new ArrayList<>();
        this.containerDailyStates = containerDailyStates != null ? new ArrayList<>(containerDailyStates) : new ArrayList<>();
        this.servicePolicies = servicePolicies;
        this.maxBudget = maxBudget;
        this.estimatedTotalCost = estimatedTotalCost != null ? estimatedTotalCost : new TotalCost(0.0);
        this.totalCollectedKilograms = totalCollectedKilograms != null
                ? totalCollectedKilograms
                : CollectedWeightKilograms.fromKilograms(0.0);
        this.totalCollectedLiters = totalCollectedLiters != null
                ? totalCollectedLiters
                : CollectedVolumeLiters.fromLiters(0.0);
        this.totalDistanceMeters = totalDistanceMeters != null ? totalDistanceMeters : Distance.fromMeters(0.0);
        this.numberOfDays = numberOfDays;
        this.averagePickupTimeMinutes = averagePickupTimeMinutes;
        this.executedAt = executedAt;
        this.validityState = validityState;
        this.executionState = executionState;
        this.failureReason = failureReason;
        this.executionRequestJson = executionRequestJson;
    }

    /**
     * Restore constructor.
     * Restores an InfrastructurePlan reference with only required scalar values.
     *
     * @param id                       the plan identifier
     * @param period                   the planning period
     * @param servicePolicies          the service policies
     * @param maxBudget                the maximum budget allowed
     * @param numberOfDays             the number of days for planning
     * @param averagePickupTimeMinutes the average pickup time in minutes
     * @param executedAt               the timestamp of algorithm execution
     * @param validityState            persisted validity state
     * @param executionState           persisted algorithm execution lifecycle state
     * @param failureReason            optional failure description
     * @param executionRequestJson     persisted client request JSON snapshot
     */
    private InfrastructurePlan(UUID id,
            PlanningPeriod period,
            ServicePolicies servicePolicies,
            MaximumBudget maxBudget,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            ExecutedAt executedAt,
            InfrastructurePlanValidityState validityState,
            InfrastructurePlanExecutionState executionState,
            InfrastructurePlanFailureReason failureReason,
            AlgorithmJsonPayload executionRequestJson) {
        this(id, period, null, null, null, servicePolicies, maxBudget, null, null, null, null,
                numberOfDays, averagePickupTimeMinutes, executedAt, validityState, executionState, failureReason,
                executionRequestJson, null);
    }

    /**
     * Minimal aggregate reference used when loading nested documents (for example a {@link DailyPlan})
     * where only the infrastructure plan identifier is required.
     *
     * @param infrastructurePlanId parent plan id
     * @return placeholder plan carrying only the id and default scalar placeholders
     */
    public static InfrastructurePlan forIdReferenceOnly(UUID infrastructurePlanId) {
        return new InfrastructurePlan(
                infrastructurePlanId,
                new PlanningPeriod(String.valueOf(LocalDate.now().getYear())),
                null,
                new MaximumBudget(1.0),
                null,
                null,
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED,
                null,
                null);
    }

    /**
     * Lightweight aggregate for paginated list reads without hydrating related entities.
     *
     * @param id                       plan identifier
     * @param estimatedTotalCost       persisted estimated total cost, may be null while running
     * @param numberOfDays             planning horizon length in days
     * @param averagePickupTimeMinutes average pickup time in minutes
     * @param executedAt               algorithm execution timestamp
     * @param validityState            persisted validity state
     * @param executionState           persisted execution lifecycle state
     * @param failureReason            optional failure description
     * @return infrastructure plan carrying only list-summary fields
     */
    public static InfrastructurePlan forListSummary(
            UUID id,
            TotalCost estimatedTotalCost,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            ExecutedAt executedAt,
            InfrastructurePlanValidityState validityState,
            InfrastructurePlanExecutionState executionState,
            String failureReason) {
        PlanningPeriod placeholderPeriod = new PlanningPeriod(String.valueOf(LocalDate.now().getYear()));
        MaximumBudget placeholderBudget = new MaximumBudget(0.0);
        return new InfrastructurePlan(
                id,
                placeholderPeriod,
                null,
                null,
                null,
                null,
                placeholderBudget,
                estimatedTotalCost,
                null,
                null,
                null,
                numberOfDays,
                averagePickupTimeMinutes,
                executedAt,
                validityState,
                executionState,
                InfrastructurePlanFailureReason.fromNullable(failureReason),
                null,
                null);
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
     * Validates that the infrastructure plan validity state is defined.
     *
     * @param validityState validity state to validate
     * @throws IllegalArgumentException if the validity state is null
     */
    private void validateValidityState(InfrastructurePlanValidityState validityState) {
        if (validityState == null) {
            throw new IllegalArgumentException(VALIDITY_STATE_NOT_DEFINED);
        }
    }

    /**
     * Validates that the infrastructure plan execution state is defined.
     *
     * @param executionState execution state to validate
     * @throws IllegalArgumentException if the execution state is null
     */
    private void validateExecutionState(InfrastructurePlanExecutionState executionState) {
        if (executionState == null) {
            throw new IllegalArgumentException(EXECUTION_STATE_NOT_DEFINED);
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
     * Adds a daily plan entity to this infrastructure plan.
     *
     * @param dailyPlan the daily plan to add; ignored if {@code null} or already present
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
     * Adds a container daily state snapshot to this plan if not already present.
     *
     * @param containerDailyState the snapshot to add
     */
    public void addContainerDailyState(ContainerDailyState containerDailyState) {
        if (containerDailyState != null) {
            boolean exists = false;
            for (ContainerDailyState cds : containerDailyStates) {
                if (cds.getId().equals(containerDailyState.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                containerDailyStates.add(containerDailyState);
            }
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
        this.estimatedTotalCost = new TotalCost(total);
    }

    /**
     * Checks if the plan is valid.
     *
     * @return true if the plan is valid, false otherwise
     */
    public boolean isPlanValid() {
        for (ServiceAssignment assignment : serviceAssignments) {
            Facility facility = assignment.getFacility();
            if (facility.getStatus().isDiscarded()) {
                return false;
            }
            DailyWasteDemandLitersPerDay currentFillingLevel = facility.getCurrentFillingLevel();
            if (currentFillingLevel.getLitersPerDay() < ZERO) {
                return false;
            }
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
     * This is a convenience derived from the stored {@link DailyPlan} entities.
     *
     * @return an unmodifiable list of daily plan UUIDs
     */
    public List<UUID> getDailyPlanIds() {
        List<UUID> ids = new ArrayList<>();
        for (DailyPlan dp : dailyPlans) {
            ids.add(dp.getId());
        }
        return Collections.unmodifiableList(ids);
    }

    /**
     * Returns the container daily states associated with this plan (domain objects).
     *
     * @return unmodifiable list of ContainerDailyState
     */
    public List<ContainerDailyState> getContainerDailyStates() {
        return Collections.unmodifiableList(containerDailyStates);
    }

    /**
     * Returns the identifiers of the ContainerDailyState snapshots associated with this plan.
     * Useful for persistence where only ids are stored in the InfrastructurePlan document.
     *
     * @return list of UUIDs
     */
    public List<UUID> getContainerDailyStateIds() {
        List<UUID> ids = new ArrayList<>();
        if (this.containerDailyStates != null) {
            for (ContainerDailyState cds : this.containerDailyStates) {
                ids.add(cds.getId());
            }
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

    /**
     * Returns the total collected weight determined by the algorithm.
     *
     * @return the total collected weight in kilograms
     */
    public CollectedWeightKilograms getTotalCollectedKilograms() {
        return totalCollectedKilograms;
    }

    /**
     * Returns the total collected volume determined by the algorithm.
     *
     * @return the total collected volume in liters
     */
    public CollectedVolumeLiters getTotalCollectedLiters() {
        return totalCollectedLiters;
    }

    /**
     * Returns the total route distance determined by the algorithm.
     *
     * @return the total distance in meters
     */
    public Distance getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    /**
     * Returns the number of days in the planning horizon.
     *
     * @return the number of days
     */
    public Optional<NumberOfDays> getNumberOfDays() {
        return Optional.ofNullable(numberOfDays);
    }

    /**
     * Returns the average pickup time in minutes.
     *
     * @return the average pickup time in minutes
     */
    public Optional<AveragePickupTimeMinutes> getAveragePickupTimeMinutes() {
        return Optional.ofNullable(averagePickupTimeMinutes);
    }

    /**
     * Returns the timestamp when the algorithm was executed.
     *
     * @return the execution timestamp in ISO 8601 format
     */
    public Optional<ExecutedAt> getExecutedAt() {
        return Optional.ofNullable(executedAt);
    }

    /**
     * Returns whether this plan is considered obsolete because underlying entities changed.
     *
     * @return current validity state (never null)
     */
    public InfrastructurePlanValidityState getValidityState() {
        return validityState != null ? validityState : InfrastructurePlanValidityState.VALID;
    }

    /**
     * Returns the algorithm execution lifecycle state for this plan.
     *
     * @return current execution state (never null)
     */
    public InfrastructurePlanExecutionState getExecutionState() {
        return executionState != null ? executionState : InfrastructurePlanExecutionState.COMPLETED;
    }

    /**
     * Returns the failure reason when execution ended with {@link InfrastructurePlanExecutionState#FAILED}.
     *
     * @return optional failure description
     */
    public Optional<String> getFailureReason() {
        return Optional.ofNullable(failureReason).map(InfrastructurePlanFailureReason::getValue);
    }

    /**
     * Returns whether the algorithm is still running for this plan.
     *
     * @return true when execution state is {@link InfrastructurePlanExecutionState#RUNNING}
     */
    public boolean isExecutionRunning() {
        return getExecutionState() == InfrastructurePlanExecutionState.RUNNING;
    }

    /**
     * Marks this plan as running while the algorithm executes asynchronously.
     */
    public void markExecutionRunning() {
        validateValidityState(InfrastructurePlanValidityState.RUNNING);
        validateExecutionState(InfrastructurePlanExecutionState.RUNNING);
        this.validityState = InfrastructurePlanValidityState.RUNNING;
        this.executionState = InfrastructurePlanExecutionState.RUNNING;
        this.failureReason = null;
    }

    /**
     * Marks this plan as successfully completed after algorithm persistence.
     */
    public void markExecutionCompleted() {
        validateValidityState(InfrastructurePlanValidityState.VALID);
        validateExecutionState(InfrastructurePlanExecutionState.COMPLETED);
        this.validityState = InfrastructurePlanValidityState.VALID;
        this.executionState = InfrastructurePlanExecutionState.COMPLETED;
        this.failureReason = null;
    }

    /**
     * Marks this plan as failed and stores an optional human-readable reason.
     *
     * @param reason failure description; may be null or blank
     */
    public void markExecutionFailed(String reason) {
        validateExecutionState(InfrastructurePlanExecutionState.FAILED);
        this.validityState = InfrastructurePlanValidityState.VALID;
        this.executionState = InfrastructurePlanExecutionState.FAILED;
        this.failureReason = InfrastructurePlanFailureReason.fromNullable(reason);
    }

    /**
     * Returns the client execution request JSON snapshot stored when the plan was produced.
     *
     * @return optional raw JSON text
     */
    public Optional<String> getExecutionRequestJson() {
        return Optional.ofNullable(executionRequestJson).map(AlgorithmJsonPayload::getJson);
    }

    /**
     * Attaches the client request JSON used to execute the algorithm (for later reference checks).
     *
     * @param json raw JSON text, or null to clear
     */
    public void assignExecutionRequestSnapshot(String json) {
        this.executionRequestJson = json != null && !json.isBlank() ? new AlgorithmJsonPayload(json) : null;
    }

    /**
     * Updates the execution timestamp (for example when the algorithm finishes).
     *
     * @param executedAt new execution timestamp
     */
    public void assignExecutedAt(ExecutedAt executedAt) {
        this.executedAt = executedAt;
    }

    /**
     * Restores computed associations loaded by persistence adapters.
     *
     * @param selectedFacilities selected facilities
     * @param serviceAssignments service assignments
     * @param dailyPlans daily plans
     * @param containerDailyStates container daily state snapshots
     */
    public void restoreComputedAssociations(
            List<Facility> selectedFacilities,
            List<ServiceAssignment> serviceAssignments,
            List<DailyPlan> dailyPlans,
            List<ContainerDailyState> containerDailyStates) {
        this.selectedFacilities = selectedFacilities != null ? new ArrayList<>(selectedFacilities) : new ArrayList<>();
        this.serviceAssignments = serviceAssignments != null ? new ArrayList<>(serviceAssignments) : new ArrayList<>();
        this.dailyPlans = dailyPlans != null ? new ArrayList<>(dailyPlans) : new ArrayList<>();
        this.containerDailyStates = containerDailyStates != null ? new ArrayList<>(containerDailyStates) : new ArrayList<>();
    }

    /**
     * Restores computed totals loaded by persistence adapters.
     *
     * @param estimatedTotalCost estimated total cost
     * @param totalCollectedKilograms total collected kilograms
     * @param totalCollectedLiters total collected liters
     * @param totalDistanceMeters total distance in meters
     */
    public void restoreComputedMetrics(
            TotalCost estimatedTotalCost,
            CollectedWeightKilograms totalCollectedKilograms,
            CollectedVolumeLiters totalCollectedLiters,
            Distance totalDistanceMeters) {
        this.estimatedTotalCost = estimatedTotalCost != null ? estimatedTotalCost : new TotalCost(0.0);
        this.totalCollectedKilograms = totalCollectedKilograms != null ? totalCollectedKilograms : CollectedWeightKilograms.fromKilograms(0.0);
        this.totalCollectedLiters = totalCollectedLiters != null ? totalCollectedLiters : CollectedVolumeLiters.fromLiters(0.0);
        this.totalDistanceMeters = totalDistanceMeters != null ? totalDistanceMeters : Distance.fromMeters(0.0);
    }

    /**
     * Marks this plan obsolete when it is still {@link InfrastructurePlanValidityState#VALID}.
     */
    public void markObsoleteIfStillValid() {
        if (getValidityState() == InfrastructurePlanValidityState.VALID) {
            this.validityState = InfrastructurePlanValidityState.OBSOLETE;
        }
    }

    /**
     * Updates the algorithm-computed totals for collected weight, volume, and distance.
     *
     * @param kg       the total collected weight in kilograms
     * @param liters   the total collected volume in liters
     * @param distance the total route distance in meters
     */
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
     */
    public void updateMaxBudget(MaximumBudget newMaxBudget) {
        validateMaxBudget(newMaxBudget);
        this.maxBudget = newMaxBudget;
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