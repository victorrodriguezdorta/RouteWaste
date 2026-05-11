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

class ContainerDailyStateTests {

    private static UUID randomId() {
        return UUID.randomUUID();
    }

    private static ContainerDailyState randomState() {
        return new ContainerDailyState(
            randomId(),
            "container-" + UUID.randomUUID(),
            1 + (int) (Math.random() * 30),
            10.0 + Math.random() * 900.0,
            100.0 + Math.random() * 900.0,
            1.0 + Math.random() * 100.0,
            ContainerStatus.random()
        );
    }

    @Test
    void constructorRight() {
        UUID id = randomId();
        String containerId = "container-1";
        int planDay = 3;
        double dailyFillingLiters = 42.5;
        double containerCapacityLiters = 100.0;
        double dailyDemandLitersPerDay = 12.0;
        ContainerStatus status = ContainerStatus.OVERFLOWED;

        ContainerDailyState state = new ContainerDailyState(
            id,
            containerId,
            planDay,
            dailyFillingLiters,
            containerCapacityLiters,
            dailyDemandLitersPerDay,
            status
        );

        assertEquals(id, state.getId());
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
            "container-1",
            1,
            12.0,
            100.0,
            5.0,
            null
        );

        assertEquals(ContainerStatus.CORRECT, state.getStatus());
    }

    @Test
    void constructorRejectsNullId() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(null, "container-1", 1, 12.0, 100.0, 5.0, ContainerStatus.CORRECT)
        );

        assertEquals("id is required", exception.getMessage());
    }

    @Test
    void constructorRejectsBlankContainerId() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(randomId(), "   ", 1, 12.0, 100.0, 5.0, ContainerStatus.CORRECT)
        );

        assertEquals("containerId is required", exception.getMessage());
    }

    @Test
    void constructorRejectsInvalidPlanDay() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(randomId(), "container-1", 0, 12.0, 100.0, 5.0, ContainerStatus.CORRECT)
        );

        assertEquals("planDay must be >= 1", exception.getMessage());
    }

    @Test
    void constructorRejectsNegativeDailyFilling() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(randomId(), "container-1", 1, -1.0, 100.0, 5.0, ContainerStatus.CORRECT)
        );

        assertEquals("dailyFillingLiters must be >= 0", exception.getMessage());
    }

    @Test
    void constructorRejectsInvalidCapacity() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(randomId(), "container-1", 1, 1.0, 0.0, 5.0, ContainerStatus.CORRECT)
        );

        assertEquals("containerCapacityLiters must be > 0", exception.getMessage());
    }

    @Test
    void constructorRejectsNegativeDailyDemand() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ContainerDailyState(randomId(), "container-1", 1, 1.0, 100.0, -1.0, ContainerStatus.CORRECT)
        );

        assertEquals("dailyDemandLitersPerDay must be >= 0", exception.getMessage());
    }

    @Test
    void equalsAndHashCodeUseId() {
        UUID id = randomId();
        ContainerDailyState state1 = new ContainerDailyState(id, "container-1", 1, 12.0, 100.0, 5.0, ContainerStatus.CORRECT);
        ContainerDailyState state2 = new ContainerDailyState(id, "container-2", 8, 20.0, 150.0, 7.0, ContainerStatus.OVERFLOWED);
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
