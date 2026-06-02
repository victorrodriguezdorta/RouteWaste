package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.time.PlanDay;

class ContainerDailyStateTests {

    private static UUID randomId() {
        return UUID.randomUUID();
    }

    private static InfrastructurePlan randomPlan() {
        return InfrastructurePlan.forIdReferenceOnly(randomId());
    }

    private static Container randomContainer() {
        return new Container(
                new Name("Container-" + randomId()),
                new Location(28.1, -16.2, "Test address", "GIS-1"),
                WasteType.ORGANIC,
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(10.0),
                ServiceZone.random());
    }

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

    @Test
    void constructorRight() {
        UUID id = randomId();
        InfrastructurePlan plan = InfrastructurePlan.forIdReferenceOnly(randomId());
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
                new ContainerCapacityLiters(containerCapacityLiters),
                new DailyWasteDemandLitersPerDay(dailyDemandLitersPerDay),
                status);

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

    @Test
    void constructorRejectsNegativeDailyFilling() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CollectedVolumeLiters.fromLiters(-1.0));
    }

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

    @Test
    void constructorRejectsNegativeDailyDemand() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DailyWasteDemandLitersPerDay(-1.0));
    }

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
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(5.0),
                ContainerStatus.CORRECT);
        ContainerDailyState state2 = new ContainerDailyState(
                id,
                plan,
                randomContainer(),
                new PlanDay(8),
                CollectedVolumeLiters.fromLiters(20.0),
                new ContainerCapacityLiters(150.0),
                new DailyWasteDemandLitersPerDay(7.0),
                ContainerStatus.OVERFLOWED);
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
