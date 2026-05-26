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
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.time.PlanDay;

class ContainerDailyStateTests {

    private static UUID randomId() {
        return UUID.randomUUID();
    }

    private static ContainerDailyState randomState() {
        return new ContainerDailyState(
            randomId(),
            randomId(),
            new PlanDay(1 + (int) (Math.random() * 30)),
            CollectedVolumeLiters.fromLiters(10.0 + Math.random() * 900.0),
            new ContainerCapacityLiters(100.0 + Math.random() * 900.0),
            new DailyWasteDemandLitersPerDay(1.0 + Math.random() * 100.0),
            ContainerStatus.random()
        );
    }

    @Test
    void constructorRight() {
        UUID id = randomId();
        UUID planId = randomId();
        UUID containerId = randomId();
        int planDay = 3;
        double dailyFillingLiters = 42.5;
        double containerCapacityLiters = 100.0;
        double dailyDemandLitersPerDay = 12.0;
        ContainerStatus status = ContainerStatus.OVERFLOWED;

        ContainerDailyState state = new ContainerDailyState(
            id,
            planId,
            containerId,
            new PlanDay(planDay),
            CollectedVolumeLiters.fromLiters(dailyFillingLiters),
            new ContainerCapacityLiters(containerCapacityLiters),
            new DailyWasteDemandLitersPerDay(dailyDemandLitersPerDay),
            status
        );

        assertEquals(id, state.getId());
        assertEquals(planId, state.getInfrastructurePlanId().orElse(null));
        assertEquals(containerId, state.getContainerId());
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
            randomId(),
            randomId(),
            new PlanDay(1),
            CollectedVolumeLiters.fromLiters(12.0),
            new ContainerCapacityLiters(100.0),
            new DailyWasteDemandLitersPerDay(5.0),
            null
        );

        assertEquals(ContainerStatus.CORRECT, state.getStatus());
    }

    @Test
    void constructorRejectsNullContainerId() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(
                randomId(),
                null,
                new PlanDay(1),
                CollectedVolumeLiters.fromLiters(12.0),
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(5.0),
                ContainerStatus.CORRECT)
        );

        assertEquals("container id is required", exception.getMessage());
    }

    @Test
    void constructorRejectsNullPlanDay() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(
                randomId(),
                randomId(),
                null,
                CollectedVolumeLiters.fromLiters(12.0),
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(5.0),
                ContainerStatus.CORRECT)
        );

        assertEquals("plan day is required", exception.getMessage());
    }

    @Test
    void constructorRejectsInvalidPlanDay() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(
                randomId(),
                randomId(),
                new PlanDay(0),
                CollectedVolumeLiters.fromLiters(12.0),
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(5.0),
                ContainerStatus.CORRECT)
        );
    }

    @Test
    void constructorRejectsNegativeDailyFilling() {
        assertThrows(
            IllegalArgumentException.class,
            () -> CollectedVolumeLiters.fromLiters(-1.0)
        );
    }

    @Test
    void constructorRejectsInvalidCapacity() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(
                randomId(),
                randomId(),
                new PlanDay(1),
                CollectedVolumeLiters.fromLiters(1.0),
                new ContainerCapacityLiters(0.0),
                new DailyWasteDemandLitersPerDay(5.0),
                ContainerStatus.CORRECT)
        );

        assertEquals("containerCapacityLiters must be > 0", exception.getMessage());
    }

    @Test
    void constructorRejectsNegativeDailyDemand() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new DailyWasteDemandLitersPerDay(-1.0)
        );
    }

    @Test
    void equalsAndHashCodeUseId() {
        UUID id = randomId();
        UUID planId = randomId();
        ContainerDailyState state1 = new ContainerDailyState(
            id,
            planId,
            randomId(),
            new PlanDay(1),
            CollectedVolumeLiters.fromLiters(12.0),
            new ContainerCapacityLiters(100.0),
            new DailyWasteDemandLitersPerDay(5.0),
            ContainerStatus.CORRECT);
        ContainerDailyState state2 = new ContainerDailyState(
            id,
            planId,
            randomId(),
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
