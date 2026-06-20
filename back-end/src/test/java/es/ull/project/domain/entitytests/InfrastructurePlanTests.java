package es.ull.project.domain.entitytests;

import es.ull.project.domain.InfrastructurePlanReferences;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import es.ull.project.domain.valueobject.time.PlanDay;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InfrastructurePlanTests {

    private static final String FACILITY_NAME_PREFIX = "facility";
    private static final String CONTAINER_NAME_PREFIX = "container";
    private static final String VEHICLE_NAME_PREFIX = "vehicle";
    private static final String DOCKER_TIMEOUT_REASON = "Docker timeout";
    private static final String EXECUTION_REQUEST_JSON = "{\"containers\":[]}";
    private static final String BLANK_EXECUTION_REQUEST_JSON = "   ";

    /**
     * Creates a valid planning period for infrastructure plan tests.
     *
     * @return valid planning period.
     */
    private static PlanningPeriod randomPeriod() {
        return new PlanningPeriod("2026");
    }

    /**
     * Creates a valid maximum budget for infrastructure plan tests.
     *
     * @return valid maximum budget.
     */
    private static MaximumBudget randomMaxBudget() {
        return new MaximumBudget(1_000_000.0);
    }

    /**
     * Creates valid service policies for infrastructure plan tests.
     *
     * @return valid service policies.
     */
    private static ServicePolicies randomServicePolicies() {
        return new ServicePolicies(5000.0, 60, 100, 1000.0);
    }

    /**
     * Creates a valid location for related entity tests.
     *
     * @return valid location.
     */
    private static Location randomLocation() {
        return new Location(28.47, -16.25, "Addr " + ((int)(Math.random()*1000)), "GIS-" + ((int)(Math.random()*1000)));
    }

    /**
     * Creates a valid facility for infrastructure plan tests.
     *
     * @return valid facility.
     */
    private static Facility randomFacility() {
        return new Facility(
                randomName(FACILITY_NAME_PREFIX),
                FacilityType.random(),
                randomLocation(),
                new StorageCapacityKilograms(1000.0),
                new ProcessingCapacityKilogramsPerDay(500.0),
                new UnloadingTime(60),
                new OpeningFixedCost(10000.0),
                FacilityStatus.PLANNED
        );
    }

    /**
     * Creates a valid container for service assignment tests.
     *
     * @return valid container.
     */
    private static Container randomContainer() {
        return new Container(
            randomName(CONTAINER_NAME_PREFIX),
            randomLocation(),
            WasteType.random(),
            new ContainerCapacityLiters(100.0),
            new DailyWasteDemandLitersPerDay(10.0),
            ServiceZone.random()
        );
    }

    /**
     * Creates a random name with the provided prefix.
     *
     * @param prefix name prefix.
     * @return valid name.
     */
    private static Name randomName(String prefix) {
        return new Name(prefix + "-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Creates a valid service assignment for the given plan and facility.
     *
     * @param plan infrastructure plan.
     * @param facility assigned facility.
     * @return valid service assignment.
     */
    private static ServiceAssignment randomServiceAssignment(InfrastructurePlan plan, Facility facility) {
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        return new ServiceAssignment(plan, facility, containers);
    }

    /**
     * Creates a valid vehicle for daily plan tests.
     *
     * @return valid vehicle.
     */
    private static Vehicle randomVehicle() {
        return new Vehicle(
                randomName(VEHICLE_NAME_PREFIX),
                VehicleType.random(),
                new VehicleCapacityKilograms(5000.0),
                new VehicleCapacityLiters(8000.0),
                new TransportationVariableCost(0.75));
    }

    /**
     * Creates a valid daily plan for the given plan and facility.
     *
     * @param plan infrastructure plan.
     * @param facility assigned facility.
     * @return valid daily plan.
     */
    private static DailyPlan randomDailyPlan(InfrastructurePlan plan, Facility facility) {
        return new DailyPlan(
                plan,
                facility,
                LocalDate.of(2026, 4, 26),
                PlanDay.fromInteger(1),
                randomVehicle());
    }

    /**
     * Creates a valid container daily state for the given plan.
     *
     * @param plan infrastructure plan.
     * @return valid container daily state.
     */
    private static ContainerDailyState randomContainerDailyState(InfrastructurePlan plan) {
        return new ContainerDailyState(
                plan,
                randomContainer(),
                new PlanDay(1),
                CollectedVolumeLiters.fromLiters(10.0),
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(5.0),
                ContainerStatus.CORRECT);
    }

    /**
     * Tests that the main constructor creates an infrastructure plan with required values.
     */
    @Test
    void constructorOneRight() {
        PlanningPeriod period = randomPeriod();
        MaximumBudget maxBudget = randomMaxBudget();
        ServicePolicies policies = randomServicePolicies();
        InfrastructurePlan plan = new InfrastructurePlan(
                period, policies, maxBudget, null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        assertEquals(period, plan.getPeriod());
        assertEquals(maxBudget, plan.getMaxBudget());
        assertEquals(policies, plan.getServicePolicies());
        assertNotNull(plan.getId());
        assertEquals(0.0, plan.getEstimatedTotalCost().getAmount());
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, plan.getExecutionState());
        assertTrue(plan.getFailureReason().isEmpty());
    }

    /**
     * Tests infrastructure plan execution state transitions.
     */
    @Test
    void executionStateTransitions() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        plan.markExecutionRunning();
        assertEquals(InfrastructurePlanExecutionState.RUNNING, plan.getExecutionState());
        assertTrue(plan.isExecutionRunning());
        plan.markExecutionCompleted();
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, plan.getExecutionState());
        assertFalse(plan.isExecutionRunning());
        plan.markExecutionFailed(DOCKER_TIMEOUT_REASON);
        assertEquals(InfrastructurePlanExecutionState.FAILED, plan.getExecutionState());
        assertEquals(InfrastructurePlanValidityState.VALID, plan.getValidityState());
        assertFalse(plan.isExecutionRunning());
        assertEquals(DOCKER_TIMEOUT_REASON, plan.getFailureReason().orElseThrow());
    }

    /**
     * Tests that the constructor rejects an undefined planning period.
     */
    @Test
    void constructorOnePeriodUndefined() {
        PlanningPeriod period = null;
        MaximumBudget maxBudget = randomMaxBudget();
        ServicePolicies policies = randomServicePolicies();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        period, policies, maxBudget, null, null, null, InfrastructurePlanValidityState.VALID,
                        InfrastructurePlanExecutionState.COMPLETED)
        );
        assertEquals(InfrastructurePlan.PERIOD_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor stores optional algorithm fields.
     */
    @Test
    void constructorOneWithOptionalFields() {
        NumberOfDays days = new NumberOfDays(30);
        AveragePickupTimeMinutes pickupTime = new AveragePickupTimeMinutes(15);
        ExecutedAt executedAt = new ExecutedAt("2026-04-26T10:00:00Z");
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(),
                randomServicePolicies(),
                randomMaxBudget(),
                days,
                pickupTime,
                executedAt,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED);
        assertTrue(plan.getNumberOfDays().isPresent());
        assertEquals(days, plan.getNumberOfDays().get());
        assertTrue(plan.getAveragePickupTimeMinutes().isPresent());
        assertEquals(pickupTime, plan.getAveragePickupTimeMinutes().get());
        assertTrue(plan.getExecutedAt().isPresent());
        assertEquals(executedAt, plan.getExecutedAt().get());
    }

    /**
     * Tests that the constructor rejects an undefined validity state.
     */
    @Test
    void constructorOneValidityStateUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        randomPeriod(),
                        randomServicePolicies(),
                        randomMaxBudget(),
                        null,
                        null,
                        null,
                        null,
                        InfrastructurePlanExecutionState.COMPLETED));
        assertEquals(InfrastructurePlan.VALIDITY_STATE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an undefined execution state.
     */
    @Test
    void constructorOneExecutionStateUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        randomPeriod(),
                        randomServicePolicies(),
                        randomMaxBudget(),
                        null,
                        null,
                        null,
                        InfrastructurePlanValidityState.VALID,
                        null));
        assertEquals(InfrastructurePlan.EXECUTION_STATE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an undefined maximum budget.
     */
    @Test
    void constructorOneMaxBudgetUndefined() {
        PlanningPeriod period = randomPeriod();
        MaximumBudget maxBudget = null;
        ServicePolicies policies = randomServicePolicies();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        period, policies, maxBudget, null, null, null, InfrastructurePlanValidityState.VALID,
                        InfrastructurePlanExecutionState.COMPLETED)
        );
        assertEquals(InfrastructurePlan.MAX_BUDGET_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the copy constructor preserves persisted values and associations.
     */
    @Test
    void copyConstructorRight() {
        InfrastructurePlan original = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        original.addFacility(facility);
        original.addServiceAssignment(randomServiceAssignment(original, facility));
        InfrastructurePlan copy = new InfrastructurePlan(original);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getPeriod(), copy.getPeriod());
        assertEquals(original.getSelectedFacilities().size(), copy.getSelectedFacilities().size());
        assertEquals(original.getServiceAssignments().size(), copy.getServiceAssignments().size());
    }

    /**
     * Tests that an ID-only reference plan keeps the provided ID.
     */
    @Test
    void forIdReferenceOnlyCreatesPlanWithGivenId() {
        UUID planId = UUID.randomUUID();
        InfrastructurePlan plan = InfrastructurePlanReferences.forIdReferenceOnly(planId);
        assertEquals(planId, plan.getId());
        assertNotNull(plan.getPeriod());
        assertNotNull(plan.getMaxBudget());
    }

    /**
     * Tests that a list summary plan contains lightweight summary values.
     */
    @Test
    void forListSummaryCreatesLightweightPlan() {
        UUID planId = UUID.randomUUID();
        TotalCost cost = new TotalCost(15000.0);
        NumberOfDays days = new NumberOfDays(7);
        AveragePickupTimeMinutes pickup = new AveragePickupTimeMinutes(20);
        ExecutedAt executedAt = new ExecutedAt("2026-05-01T12:00:00Z");
        InfrastructurePlan plan = InfrastructurePlanReferences.forListSummary(
                planId, cost, days, pickup, executedAt,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED,
                null);
        assertEquals(planId, plan.getId());
        assertEquals(cost, plan.getEstimatedTotalCost());
        assertEquals(days, plan.getNumberOfDays().orElseThrow());
        assertTrue(plan.getFailureReason().isEmpty());
    }

    /**
     * Tests that the restore constructor rebuilds computed associations and metrics.
     */
    @Test
    void restoreConstructorRight() {
        UUID id = UUID.randomUUID();
        PlanningPeriod period = randomPeriod();
        Facility facility = randomFacility();
        InfrastructurePlan planRef = new InfrastructurePlan(
                period, randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ServiceAssignment assignment = randomServiceAssignment(planRef, facility);
        DailyPlan dailyPlan = randomDailyPlan(planRef, facility);
        ContainerDailyState dailyState = randomContainerDailyState(planRef);
        InfrastructurePlan restored = new InfrastructurePlan(
                id,
                period,
                randomServicePolicies(),
                randomMaxBudget(),
                new NumberOfDays(10),
                new AveragePickupTimeMinutes(12),
                new ExecutedAt("2026-04-01T08:00:00Z"),
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED,
                null,
                null);
        restored.restoreComputedAssociations(
                List.of(facility),
                List.of(assignment),
                List.of(dailyPlan),
                List.of(dailyState));
        restored.restoreComputedMetrics(
                new TotalCost(5000.0),
                CollectedWeightKilograms.fromKilograms(100.0),
                CollectedVolumeLiters.fromLiters(200.0),
                Distance.fromMeters(1500.0));
        assertEquals(id, restored.getId());
        assertEquals(1, restored.getSelectedFacilities().size());
        assertEquals(1, restored.getServiceAssignments().size());
        assertEquals(1, restored.getDailyPlans().size());
        assertEquals(1, restored.getContainerDailyStates().size());
    }

    /**
     * Tests equality behavior for infrastructure plans.
     */
    @Test
    void equalsMethod() {
        InfrastructurePlan p1 = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan p2 = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan p3 = p1;
        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(Integer.valueOf(0)));
        assertNotEquals(p1, p2);
        assertEquals(p1, p3);
    }

    /**
     * Tests hash code behavior for infrastructure plans.
     */
    @Test
    void hashCodeMethod() {
        InfrastructurePlan p1 = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan p2 = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        assertEquals(p1.hashCode(), p1.hashCode());
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    /**
     * Tests that a plan with healthy assignments is valid.
     */
    @Test
    void isPlanValidWhenAssignmentsAreHealthyReturnsTrue() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(facility);
        plan.addServiceAssignment(randomServiceAssignment(plan, facility));
        assertTrue(plan.isPlanValid());
    }

    /**
     * Tests that adding facilities ignores nulls and duplicates.
     */
    @Test
    void addFacilityIgnoresNullAndDuplicates() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(null);
        plan.addFacility(facility);
        plan.addFacility(facility);
        assertEquals(1, plan.getSelectedFacilities().size());
    }

    /**
     * Tests clearing selected facilities and service assignments.
     */
    @Test
    void clearFacilitiesAndAssignments() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(facility);
        plan.addServiceAssignment(randomServiceAssignment(plan, facility));
        plan.clearFacilities();
        plan.clearServiceAssignments();
        assertTrue(plan.getSelectedFacilities().isEmpty());
        assertTrue(plan.getServiceAssignments().isEmpty());
        assertEquals(0.0, plan.getEstimatedTotalCost().getAmount());
    }

    /**
     * Tests that adding daily plans ignores nulls and duplicates.
     */
    @Test
    void addDailyPlanIgnoresNullAndDuplicates() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        DailyPlan dailyPlan = randomDailyPlan(plan, facility);
        plan.addDailyPlan(null);
        plan.addDailyPlan(dailyPlan);
        plan.addDailyPlan(dailyPlan);
        assertEquals(1, plan.getDailyPlans().size());
        assertEquals(List.of(dailyPlan.getId()), plan.getDailyPlanIds());
    }

    /**
     * Tests clearing daily plans.
     */
    @Test
    void clearDailyPlans() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        plan.addDailyPlan(randomDailyPlan(plan, randomFacility()));
        plan.clearDailyPlans();
        assertTrue(plan.getDailyPlans().isEmpty());
    }

    /**
     * Tests that adding container daily states ignores nulls and duplicates.
     */
    @Test
    void addContainerDailyStateIgnoresNullAndDuplicates() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ContainerDailyState state = randomContainerDailyState(plan);
        plan.addContainerDailyState(null);
        plan.addContainerDailyState(state);
        plan.addContainerDailyState(state);
        assertEquals(1, plan.getContainerDailyStates().size());
        assertEquals(List.of(state.getId()), plan.getContainerDailyStateIds());
    }

    /**
     * Tests that only valid plans are marked obsolete.
     */
    @Test
    void markObsoleteIfStillValidOnlyWhenValid() {
        InfrastructurePlan validPlan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan runningPlan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.RUNNING, InfrastructurePlanExecutionState.RUNNING);
        validPlan.markObsoleteIfStillValid();
        runningPlan.markObsoleteIfStillValid();
        assertEquals(InfrastructurePlanValidityState.OBSOLETE, validPlan.getValidityState());
        assertEquals(InfrastructurePlanValidityState.RUNNING, runningPlan.getValidityState());
    }

    /**
     * Tests assigning and clearing the execution request snapshot.
     */
    @Test
    void assignExecutionRequestSnapshotAndClear() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        plan.assignExecutionRequestSnapshot(EXECUTION_REQUEST_JSON);
        assertTrue(plan.getExecutionRequestJson().isPresent());
        plan.assignExecutionRequestSnapshot(BLANK_EXECUTION_REQUEST_JSON);
        assertTrue(plan.getExecutionRequestJson().isEmpty());
        plan.assignExecutionRequestSnapshot(null);
        assertTrue(plan.getExecutionRequestJson().isEmpty());
    }

    /**
     * Tests assigning the execution timestamp.
     */
    @Test
    void assignExecutedAtUpdatesTimestamp() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ExecutedAt executedAt = new ExecutedAt("2026-06-01T09:30:00Z");
        plan.assignExecutedAt(executedAt);
        assertEquals(executedAt, plan.getExecutedAt().orElseThrow());
    }

    /**
     * Tests restoring computed associations and metrics.
     */
    @Test
    void restoreComputedAssociationsAndMetrics() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        ServiceAssignment assignment = randomServiceAssignment(plan, facility);
        DailyPlan dailyPlan = randomDailyPlan(plan, facility);
        ContainerDailyState dailyState = randomContainerDailyState(plan);
        plan.restoreComputedAssociations(List.of(facility), List.of(assignment), List.of(dailyPlan), List.of(dailyState));
        plan.restoreComputedMetrics(
                new TotalCost(9000.0),
                CollectedWeightKilograms.fromKilograms(50.0),
                CollectedVolumeLiters.fromLiters(120.0),
                Distance.fromMeters(800.0));
        assertEquals(1, plan.getSelectedFacilities().size());
        assertEquals(9000.0, plan.getEstimatedTotalCost().getAmount());
        assertEquals(50.0, plan.getTotalCollectedKilograms().getValue());
    }

    /**
     * Tests updating period, maximum budget, and service policies.
     */
    @Test
    void updatePeriodMaxBudgetAndServicePolicies() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        PlanningPeriod newPeriod = new PlanningPeriod("2027");
        MaximumBudget newBudget = new MaximumBudget(2_000_000.0);
        ServicePolicies newPolicies = new ServicePolicies(6000.0, 45, 80, 900.0);
        plan.updatePeriod(newPeriod);
        plan.updateMaxBudget(newBudget);
        plan.updateServicePolicies(newPolicies);
        assertEquals(newPeriod, plan.getPeriod());
        assertEquals(newBudget, plan.getMaxBudget());
        assertEquals(newPolicies, plan.getServicePolicies());
    }

    /**
     * Tests marking execution as failed with a null reason.
     */
    @Test
    void markExecutionFailedWithNullReason() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        plan.markExecutionFailed(null);
        assertEquals(InfrastructurePlanExecutionState.FAILED, plan.getExecutionState());
        assertTrue(plan.getFailureReason().isEmpty());
    }

    /**
     * Tests adding a valid service assignment.
     */
    @Test
    void addServiceAssignmentValid() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), new MaximumBudget(1_000_000.0), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(facility);
        ServiceAssignment assignment = randomServiceAssignment(plan, facility);
        plan.addServiceAssignment(assignment);
        assertTrue(plan.getServiceAssignments().contains(assignment));
        assertEquals(1, plan.getServiceAssignments().size());
    }

    /**
     * Tests that adding a null service assignment fails.
     */
    @Test
    void addServiceAssignmentInvalidNull() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ServiceAssignment assignment = null;
        IllegalArgumentException exc = assertThrows(
                IllegalArgumentException.class,
                () -> plan.addServiceAssignment(assignment)
        );
        assertEquals(InfrastructurePlan.INVALID_ASSIGNMENT, exc.getMessage());
    }

    /**
     * Tests that total cost recalculation can exceed the maximum budget.
     */
    @Test
    void recalculateTotalCostAllowsExceedingBudget() {
        MaximumBudget smallBudget = new MaximumBudget(1.0);
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), smallBudget, null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility expensive = new Facility(
            randomName(FACILITY_NAME_PREFIX),
            FacilityType.random(),
            randomLocation(),
            new StorageCapacityKilograms(1000.0),
            new ProcessingCapacityKilogramsPerDay(500.0),
            new UnloadingTime(60),
            new OpeningFixedCost(10000.0),
            FacilityStatus.PLANNED
        );
        plan.addFacility(expensive);
        plan.recalculateTotalCost();
        assertEquals(10000.0, plan.getEstimatedTotalCost().getAmount());
        assertTrue(plan.getEstimatedTotalCost().greaterThan(plan.getMaxBudget()));
    }

    /**
     * Tests that a plan becomes invalid after discarding an assigned facility.
     */
    @Test
    void isPlanValidAfterDiscardingFacilityReturnsFalse() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(facility);
        ServiceAssignment assignment = randomServiceAssignment(plan, facility);
        plan.addServiceAssignment(assignment);
        facility.updateStatus(FacilityStatus.DISCARDED);
        assertFalse(plan.isPlanValid());
    }

    /**
     * Tests updating algorithm metrics with valid values.
     */
    @Test
    void updateAlgorithmMetricsValid() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        CollectedWeightKilograms kg = CollectedWeightKilograms.fromKilograms(1000.0);
        CollectedVolumeLiters liters = CollectedVolumeLiters.fromLiters(5000.0);
        Distance distance = Distance.fromKilometers(150.0);
        plan.updateAlgorithmMetrics(kg, liters, distance);
        assertEquals(kg, plan.getTotalCollectedKilograms());
        assertEquals(liters, plan.getTotalCollectedLiters());
        assertEquals(distance, plan.getTotalDistanceMeters());
    }

    /**
     * Tests the infrastructure plan string representation.
     */
    @Test
    void toStringMethod() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomServicePolicies(), randomMaxBudget(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        String expected = String.format("InfrastructurePlan={id=%s, period=%s, facilities=%s, assignments=%s, totalCost=%s}",
                plan.getId(), plan.getPeriod(), plan.getSelectedFacilities(), plan.getServiceAssignments(), plan.getEstimatedTotalCost());
        assertEquals(expected, plan.toString());
    }
}
