package es.ull.project.domain.entitytests;

import es.ull.project.domain.InfrastructurePlanReferences;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ContainerDailyStateTests {

    /**
     * Creates a random identifier for container daily state tests.
     *
     * @return random UUID.
     */
    private static UUID randomId() {
        return UUID.randomUUID();
    }

    /**
     * Creates a plan reference for container daily state tests.
     *
     * @return infrastructure plan reference.
     */
    private static InfrastructurePlan randomPlan() {
        return InfrastructurePlanReferences.forIdReferenceOnly(randomId());
    }

    /**
     * Creates a valid container for container daily state tests.
     *
     * @return valid container.
     */
    private static Container randomContainer() {
        return new Container(
                new Name("Container-" + randomId()),
                new Location(28.1, -16.2, "Test address", "GIS-1"),
                WasteType.ORGANIC,
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(10.0),
                ServiceZone.random());
    }

    /**
     * Creates a random container daily state for equality tests.
     *
     * @return valid container daily state.
     */
    private static ContainerDailyState randomState() {
        return new ContainerDailyState(
                randomPlan(),
                randomContainer(),
                new PlanDay(1 + (int) (Math.random() * 30)),
                CollectedVolumeLiters.fromLiters(10.0 + Math.random() * 900.0),
                new ContainerCapacityLiters(100.0 + Math.random() * 900.0),
                new DailyWasteDemandLitersPerDay(1.0 + Math.random() * 100.0),
                ContainerStatus.random());
    }

    /**
     * Verifies that the constructor stores all provided values correctly.
     */
    @Test
    void constructorRight() {
        UUID id = randomId();
        InfrastructurePlan plan = InfrastructurePlanReferences.forIdReferenceOnly(randomId());
        Container container = randomContainer();
        int planDay = 3;
        double dailyFillingLiters = 42.5;
        double containerCapacityLiters = 100.0;
        double dailyDemandLitersPerDay = 12.0;
        ContainerStatus status = ContainerStatus.OVERFLOWED;
        ContainerDailyState state = new ContainerDailyState(
                id,
                plan,
                container,
                new PlanDay(planDay),
                CollectedVolumeLiters.fromLiters(dailyFillingLiters),
                null,
                new ContainerCapacityLiters(containerCapacityLiters),
                new DailyWasteDemandLitersPerDay(dailyDemandLitersPerDay),
                status,
                null);
        assertEquals(id, state.getId());
        assertEquals(plan.getId(), state.getInfrastructurePlanId().orElse(null));
        assertEquals(container.getId(), state.getContainerId());
        assertEquals(planDay, state.getPlanDay());
        assertEquals(dailyFillingLiters, state.getDailyFillingLiters());
        assertEquals(containerCapacityLiters, state.getContainerCapacityLiters());
        assertEquals(dailyDemandLitersPerDay, state.getDailyDemandLitersPerDay());
        assertEquals(status, state.getStatus());
        assertNotNull(state.toString());
    }

    /**
     * Verifies that a null status defaults to correct.
     */
    @Test
    void constructorDefaultsStatusWhenNull() {
        ContainerDailyState state = new ContainerDailyState(
                randomPlan(),
                randomContainer(),
                new PlanDay(1),
                CollectedVolumeLiters.fromLiters(12.0),
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(5.0),
                null);
        assertEquals(ContainerStatus.CORRECT, state.getStatus());
    }

    /**
     * Verifies that a null container is rejected.
     */
    @Test
    void constructorRejectsNullContainer() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ContainerDailyState(
                        randomPlan(),
                        null,
                        new PlanDay(1),
                        CollectedVolumeLiters.fromLiters(12.0),
                        new ContainerCapacityLiters(100.0),
                        new DailyWasteDemandLitersPerDay(5.0),
                        ContainerStatus.CORRECT));
        assertEquals(ContainerDailyState.CONTAINER_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that a null plan day is rejected.
     */
    @Test
    void constructorRejectsNullPlanDay() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ContainerDailyState(
                        randomPlan(),
                        randomContainer(),
                        null,
                        CollectedVolumeLiters.fromLiters(12.0),
                        new ContainerCapacityLiters(100.0),
                        new DailyWasteDemandLitersPerDay(5.0),
                        ContainerStatus.CORRECT));
        assertEquals(ContainerDailyState.PLAN_DAY_REQUIRED, exception.getMessage());
    }

    /**
     * Verifies that an invalid plan day is rejected.
     */
    @Test
    void constructorRejectsInvalidPlanDay() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ContainerDailyState(
                        randomPlan(),
                        randomContainer(),
                        new PlanDay(0),
                        CollectedVolumeLiters.fromLiters(12.0),
                        new ContainerCapacityLiters(100.0),
                        new DailyWasteDemandLitersPerDay(5.0),
                        ContainerStatus.CORRECT));
    }

    /**
     * Verifies that negative daily filling values are rejected.
     */
    @Test
    void constructorRejectsNegativeDailyFilling() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CollectedVolumeLiters.fromLiters(-1.0));
    }

    /**
     * Verifies that non-positive container capacity values are rejected.
     */
    @Test
    void constructorRejectsInvalidCapacity() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ContainerDailyState(
                        randomPlan(),
                        randomContainer(),
                        new PlanDay(1),
                        CollectedVolumeLiters.fromLiters(1.0),
                        new ContainerCapacityLiters(0.0),
                        new DailyWasteDemandLitersPerDay(5.0),
                        ContainerStatus.CORRECT));
        assertEquals(ContainerDailyState.CONTAINER_CAPACITY_NOT_POSITIVE, exception.getMessage());
    }

    /**
     * Verifies that negative daily demand values are rejected.
     */
    @Test
    void constructorRejectsNegativeDailyDemand() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DailyWasteDemandLitersPerDay(-1.0));
    }

    /**
     * Verifies that equality and hash code depend only on the identifier.
     */
    @Test
    void equalsAndHashCodeUseId() {
        UUID id = randomId();
        InfrastructurePlan plan = randomPlan();
        ContainerDailyState state1 = new ContainerDailyState(
                id,
                plan,
                randomContainer(),
                new PlanDay(1),
                CollectedVolumeLiters.fromLiters(12.0),
                null,
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(5.0),
                ContainerStatus.CORRECT,
                null);
        ContainerDailyState state2 = new ContainerDailyState(
                id,
                plan,
                randomContainer(),
                new PlanDay(8),
                CollectedVolumeLiters.fromLiters(20.0),
                null,
                new ContainerCapacityLiters(150.0),
                new DailyWasteDemandLitersPerDay(7.0),
                ContainerStatus.OVERFLOWED,
                null);
        ContainerDailyState state3 = randomState();
        assertTrue(state1.equals(state1));
        assertFalse(state1.equals(null));
        assertFalse(state1.equals(new Object()));
        assertEquals(state1, state2);
        assertEquals(state1.hashCode(), state2.hashCode());
        assertNotEquals(state1, state3);
        assertNotEquals(state1.hashCode(), state3.hashCode());
    }
}
