package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.enumerate.VehicleType;
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

class InfrastructurePlanTests {

    // Helper methods
    private static PlanningPeriod randomPeriod() {
        return new PlanningPeriod("2026");
    }

    private static MaximumBudget randomMaxBudget() {
        return new MaximumBudget(1_000_000.0);
    }

    private static ServicePolicies randomServicePolicies() {
        return new ServicePolicies(5000.0, 60, 100, 1000.0);
    }

    private static Location randomLocation() {
        return new Location(28.47, -16.25, "Addr " + ((int)(Math.random()*1000)), "GIS-" + ((int)(Math.random()*1000)));
    }

    private static Facility randomFacility() {
        return new Facility(
                randomName("facility"),
                FacilityType.random(),
                randomLocation(),
                new StorageCapacityKilograms(1000.0),
                new ProcessingCapacityKilogramsPerDay(500.0),
                new UnloadingTime(60),
                new OpeningFixedCost(10000.0),
                FacilityStatus.PLANNED
        );
    }

    private static Container randomContainer() {
        return new Container(
            randomName("container"),
            randomLocation(),
            WasteType.random(),
            new ContainerCapacityLiters(100.0),
            new DailyWasteDemandLitersPerDay(10.0),
            ServiceZone.random()
        );
    }

    private static Name randomName(String prefix) {
        return new Name(prefix + "-" + ((int) (Math.random() * 10000)));
    }

    private static ServiceAssignment randomServiceAssignment(InfrastructurePlan plan, Facility facility) {
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        return new ServiceAssignment(plan, facility, containers);
    }

    private static Vehicle randomVehicle() {
        return new Vehicle(
                randomName("vehicle"),
                VehicleType.random(),
                new VehicleCapacityKilograms(5000.0),
                new VehicleCapacityLiters(8000.0),
                new TransportationVariableCost(0.75));
    }

    private static DailyPlan randomDailyPlan(InfrastructurePlan plan, Facility facility) {
        return new DailyPlan(
                plan,
                facility,
                LocalDate.of(2026, 4, 26),
                PlanDay.fromInteger(1),
                randomVehicle());
    }

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

    // ========== Constructors ==========

    @Test
    void constructor_1_right() {
        PlanningPeriod period = randomPeriod();
        MaximumBudget maxBudget = randomMaxBudget();
        ServicePolicies policies = randomServicePolicies();

        InfrastructurePlan plan = new InfrastructurePlan(
                period, maxBudget, policies, null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);

        assertEquals(period, plan.getPeriod());
        assertEquals(maxBudget, plan.getMaxBudget());
        assertEquals(policies, plan.getServicePolicies());
        assertNotNull(plan.getId());
        assertEquals(0.0, plan.getEstimatedTotalCost().getAmount());
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, plan.getExecutionState());
        assertTrue(plan.getFailureReason().isEmpty());
    }

    @Test
    void executionState_transitions() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);

        plan.markExecutionRunning();
        assertEquals(InfrastructurePlanExecutionState.RUNNING, plan.getExecutionState());
        assertTrue(plan.isExecutionRunning());

        plan.markExecutionCompleted();
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, plan.getExecutionState());
        assertFalse(plan.isExecutionRunning());

        plan.markExecutionFailed("Docker timeout");
        assertEquals(InfrastructurePlanExecutionState.FAILED, plan.getExecutionState());
        assertEquals("Docker timeout", plan.getFailureReason().orElseThrow());
    }

    @Test
    void constructor_1_period_undefined() {
        PlanningPeriod period = null;
        MaximumBudget maxBudget = randomMaxBudget();
        ServicePolicies policies = randomServicePolicies();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        period, maxBudget, policies, null, null, null, InfrastructurePlanValidityState.VALID,
                        InfrastructurePlanExecutionState.COMPLETED)
        );

        assertEquals(InfrastructurePlan.PERIOD_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_1_withOptionalFields() {
        NumberOfDays days = new NumberOfDays(30);
        AveragePickupTimeMinutes pickupTime = new AveragePickupTimeMinutes(15);
        ExecutedAt executedAt = new ExecutedAt("2026-04-26T10:00:00Z");

        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(),
                randomMaxBudget(),
                randomServicePolicies(),
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

    @Test
    void constructor_1_validityState_undefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        randomPeriod(),
                        randomMaxBudget(),
                        randomServicePolicies(),
                        null,
                        null,
                        null,
                        null,
                        InfrastructurePlanExecutionState.COMPLETED));

        assertEquals(InfrastructurePlan.VALIDITY_STATE_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_1_executionState_undefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        randomPeriod(),
                        randomMaxBudget(),
                        randomServicePolicies(),
                        null,
                        null,
                        null,
                        InfrastructurePlanValidityState.VALID,
                        null));

        assertEquals(InfrastructurePlan.EXECUTION_STATE_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_1_maxBudget_undefined() {
        PlanningPeriod period = randomPeriod();
        MaximumBudget maxBudget = null;
        ServicePolicies policies = randomServicePolicies();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(
                        period, maxBudget, policies, null, null, null, InfrastructurePlanValidityState.VALID,
                        InfrastructurePlanExecutionState.COMPLETED)
        );

        assertEquals(InfrastructurePlan.MAX_BUDGET_NOT_DEFINED, exception.getMessage());
    }

    // ========== COPY, RESTORE & FACTORY METHODS ==========

    @Test
    void copyConstructor_right() {
        InfrastructurePlan original = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
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

    @Test
    void forIdReferenceOnly_createsPlanWithGivenId() {
        UUID planId = UUID.randomUUID();

        InfrastructurePlan plan = InfrastructurePlan.forIdReferenceOnly(planId);

        assertEquals(planId, plan.getId());
        assertNotNull(plan.getPeriod());
        assertNotNull(plan.getMaxBudget());
    }

    @Test
    void forListSummary_createsLightweightPlan() {
        UUID planId = UUID.randomUUID();
        TotalCost cost = new TotalCost(15000.0);
        NumberOfDays days = new NumberOfDays(7);
        AveragePickupTimeMinutes pickup = new AveragePickupTimeMinutes(20);
        ExecutedAt executedAt = new ExecutedAt("2026-05-01T12:00:00Z");

        InfrastructurePlan plan = InfrastructurePlan.forListSummary(
                planId, cost, days, pickup, executedAt,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED,
                null);

        assertEquals(planId, plan.getId());
        assertEquals(cost, plan.getEstimatedTotalCost());
        assertEquals(days, plan.getNumberOfDays().orElseThrow());
        assertTrue(plan.getFailureReason().isEmpty());
    }

    @Test
    void restoreConstructor_right() {
        UUID id = UUID.randomUUID();
        PlanningPeriod period = randomPeriod();
        Facility facility = randomFacility();
        InfrastructurePlan planRef = new InfrastructurePlan(
                period, randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ServiceAssignment assignment = randomServiceAssignment(planRef, facility);
        DailyPlan dailyPlan = randomDailyPlan(planRef, facility);
        ContainerDailyState dailyState = randomContainerDailyState(planRef);

        InfrastructurePlan restored = new InfrastructurePlan(
                id,
                period,
                List.of(facility),
                List.of(assignment),
                List.of(dailyPlan),
                randomServicePolicies(),
                randomMaxBudget(),
                new TotalCost(5000.0),
                CollectedWeightKilograms.fromKilograms(100.0),
                CollectedVolumeLiters.fromLiters(200.0),
                Distance.fromMeters(1500.0),
                new NumberOfDays(10),
                new AveragePickupTimeMinutes(12),
                new ExecutedAt("2026-04-01T08:00:00Z"),
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED,
                null,
                null,
                List.of(dailyState));

        assertEquals(id, restored.getId());
        assertEquals(1, restored.getSelectedFacilities().size());
        assertEquals(1, restored.getServiceAssignments().size());
        assertEquals(1, restored.getDailyPlans().size());
        assertEquals(1, restored.getContainerDailyStates().size());
    }

    // ========== equals & hashCode ==========

    @Test
    void equalsMethod() {
        InfrastructurePlan p1 = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan p2 = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan p3 = p1;

        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(Integer.valueOf(0)));
        assertNotEquals(p1, p2);
        assertEquals(p1, p3);
    }

    @Test
    void hashCodeMethod() {
        InfrastructurePlan p1 = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan p2 = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);

        assertEquals(p1.hashCode(), p1.hashCode());
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    // ========== State modifiers / behaviors ==========

    @Test
    void isPlanValid_whenAssignmentsAreHealthy_returnsTrue() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(facility);
        plan.addServiceAssignment(randomServiceAssignment(plan, facility));

        assertTrue(plan.isPlanValid());
    }

    @Test
    void addFacility_ignoresNullAndDuplicates() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();

        plan.addFacility(null);
        plan.addFacility(facility);
        plan.addFacility(facility);

        assertEquals(1, plan.getSelectedFacilities().size());
    }

    @Test
    void clearFacilitiesAndAssignments() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
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

    @Test
    void addDailyPlan_ignoresNullAndDuplicates() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        DailyPlan dailyPlan = randomDailyPlan(plan, facility);

        plan.addDailyPlan(null);
        plan.addDailyPlan(dailyPlan);
        plan.addDailyPlan(dailyPlan);

        assertEquals(1, plan.getDailyPlans().size());
        assertEquals(List.of(dailyPlan.getId()), plan.getDailyPlanIds());
    }

    @Test
    void clearDailyPlans() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        plan.addDailyPlan(randomDailyPlan(plan, randomFacility()));

        plan.clearDailyPlans();

        assertTrue(plan.getDailyPlans().isEmpty());
    }

    @Test
    void addContainerDailyState_ignoresNullAndDuplicates() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ContainerDailyState state = randomContainerDailyState(plan);

        plan.addContainerDailyState(null);
        plan.addContainerDailyState(state);
        plan.addContainerDailyState(state);

        assertEquals(1, plan.getContainerDailyStates().size());
        assertEquals(List.of(state.getId()), plan.getContainerDailyStateIds());
    }

    @Test
    void markObsoleteIfStillValid_onlyWhenValid() {
        InfrastructurePlan validPlan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        InfrastructurePlan runningPlan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.RUNNING, InfrastructurePlanExecutionState.RUNNING);

        validPlan.markObsoleteIfStillValid();
        runningPlan.markObsoleteIfStillValid();

        assertEquals(InfrastructurePlanValidityState.OBSOLETE, validPlan.getValidityState());
        assertEquals(InfrastructurePlanValidityState.RUNNING, runningPlan.getValidityState());
    }

    @Test
    void assignExecutionRequestSnapshot_and_clear() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);

        plan.assignExecutionRequestSnapshot("{\"containers\":[]}");
        assertTrue(plan.getExecutionRequestJson().isPresent());

        plan.assignExecutionRequestSnapshot("   ");
        assertTrue(plan.getExecutionRequestJson().isEmpty());

        plan.assignExecutionRequestSnapshot(null);
        assertTrue(plan.getExecutionRequestJson().isEmpty());
    }

    @Test
    void assignExecutedAt_updatesTimestamp() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ExecutedAt executedAt = new ExecutedAt("2026-06-01T09:30:00Z");

        plan.assignExecutedAt(executedAt);

        assertEquals(executedAt, plan.getExecutedAt().orElseThrow());
    }

    @Test
    void restoreComputedAssociationsAndMetrics() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
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

    @Test
    void updatePeriodMaxBudgetAndServicePolicies() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
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

    @Test
    void markExecutionFailed_withNullReason() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);

        plan.markExecutionFailed(null);

        assertEquals(InfrastructurePlanExecutionState.FAILED, plan.getExecutionState());
        assertTrue(plan.getFailureReason().isEmpty());
    }

    @Test
    void addServiceAssignment_valid() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), new MaximumBudget(1_000_000.0), randomServicePolicies(), null, null, null,
                InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(facility);
        ServiceAssignment assignment = randomServiceAssignment(plan, facility);

        plan.addServiceAssignment(assignment);

        assertTrue(plan.getServiceAssignments().contains(assignment));
        assertEquals(1, plan.getServiceAssignments().size());
    }

    @Test
    void addServiceAssignment_invalid_null() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        ServiceAssignment assignment = null;

        IllegalArgumentException exc = assertThrows(
                IllegalArgumentException.class,
                () -> plan.addServiceAssignment(assignment)
        );
        assertEquals(InfrastructurePlan.INVALID_ASSIGNMENT, exc.getMessage());
    }

    @Test
    void recalculateTotalCost_allowsExceedingBudget() {
        MaximumBudget smallBudget = new MaximumBudget(1.0);
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), smallBudget, randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility expensive = new Facility(
            randomName("facility"),
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

    @Test
    void isPlanValid_afterDiscardingFacility_returnsFalse() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        Facility facility = randomFacility();
        plan.addFacility(facility);
        ServiceAssignment assignment = randomServiceAssignment(plan, facility);
        plan.addServiceAssignment(assignment);

        facility.updateStatus(FacilityStatus.DISCARDED);

        assertFalse(plan.isPlanValid());
    }
    
    @Test
    void updateAlgorithmMetrics_valid() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        CollectedWeightKilograms kg = CollectedWeightKilograms.fromKilograms(1000.0);
        CollectedVolumeLiters liters = CollectedVolumeLiters.fromLiters(5000.0);
        Distance distance = Distance.fromKilometers(150.0);
        
        plan.updateAlgorithmMetrics(kg, liters, distance);
        
        assertEquals(kg, plan.getTotalCollectedKilograms());
        assertEquals(liters, plan.getTotalCollectedLiters());
        assertEquals(distance, plan.getTotalDistanceMeters());
    }

    // ========== toString ==========

    @Test
    void toStringMethod() {
        InfrastructurePlan plan = new InfrastructurePlan(
                randomPeriod(), randomMaxBudget(), randomServicePolicies(), null, null, null, InfrastructurePlanValidityState.VALID, InfrastructurePlanExecutionState.COMPLETED);
        String expected = String.format("InfrastructurePlan={id=%s, period=%s, facilities=%s, assignments=%s, totalCost=%s}",
                plan.getId(), plan.getPeriod(), plan.getSelectedFacilities(), plan.getServiceAssignments(), plan.getEstimatedTotalCost());

        assertEquals(expected, plan.toString());
    }
}
