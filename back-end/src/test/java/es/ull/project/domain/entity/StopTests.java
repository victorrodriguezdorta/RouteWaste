package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.StopType;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.route.RouteSequence;

class StopTests {

    // ========== Helpers ==========

    private static Location randomLocation() {
        return new Location(
            28.4682 + Math.random() * 0.1, // Latitude around Tenerife
            -16.2546 + Math.random() * 0.1, // Longitude around Tenerife
            "Test Address " + ((int)(Math.random() * 1000)),
            "GIS-REF-" + ((int)(Math.random() * 10000))
        );
    }

    private static ContainerCapacityLiters randomCapacityLiters() {
        return new ContainerCapacityLiters(100.0 + Math.random() * 900.0);
    }

    private static DailyWasteDemandLitersPerDay randomDailyDemandLitersPerDay() {
        return new DailyWasteDemandLitersPerDay(10.0 + Math.random() * 100.0);
    }

    private static RouteSequence randomSequence() {
        return RouteSequence.of(1);
    }

    private static Container randomContainer() {
        return new Container(
            randomName(),
            randomLocation(),
            WasteType.random(),
            randomCapacityLiters(),
            randomDailyDemandLitersPerDay(),
            ServiceZone.random()
        );
    }

    private static Name randomName() {
        return new Name("container-" + ((int) (Math.random() * 10000)));
    }

    private static CollectedWeightKilograms randomKilograms() {
        return CollectedWeightKilograms.fromKilograms(10.0);
    }

    private static CollectedVolumeLiters randomLiters() {
        return CollectedVolumeLiters.fromLiters(20.0);
    }

    private static Distance randomDistance() {
        return Distance.fromMeters(500.0);
    }

    private static Stop randomStop() {
        return new Stop(
                randomSequence(),
                randomContainer(),
                randomKilograms(),
                randomLiters(),
                randomDistance(),
                randomDistance());
    }

    private static Stop facilityStop() {
        return new Stop(
                randomSequence(),
                StopType.FACILITY,
                null,
                randomKilograms(),
                randomLiters(),
                randomDistance(),
                randomDistance());
    }

    // ========== Restore Constructor ==========

    @Test
    void restoreConstructor_right() {
        RouteSequence seq = randomSequence();
        Container container = randomContainer();
        CollectedWeightKilograms kg = randomKilograms();
        CollectedVolumeLiters liters = randomLiters();
        Distance distPrev = randomDistance();
        Distance distCum = randomDistance();

        Stop stop = new Stop(seq, container, kg, liters, distPrev, distCum);

        assertEquals(seq, stop.getSequence());
        assertEquals(container, stop.getContainer());
        assertEquals(kg, stop.getCollectedKilograms());
        assertEquals(liters, stop.getCollectedLiters());
        assertEquals(distPrev, stop.getDistanceFromPreviousMeters());
        assertEquals(distCum, stop.getCumulativeDistanceMeters());
        assertEquals(StopType.CONTAINER, stop.getType());
    }

    @Test
    void restoreConstructor_facilityStop_right() {
        Stop stop = facilityStop();

        assertEquals(StopType.FACILITY, stop.getType());
        assertEquals(null, stop.getContainer());
    }

    @Test
    void restoreConstructor_sequence_null() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(null, randomContainer(), randomKilograms(), randomLiters(),
                        randomDistance(), randomDistance()));
        assertEquals(Stop.SEQUENCE_NOT_DEFINED, ex.getMessage());
    }

    @Test
    void restoreConstructor_container_null() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), null, randomKilograms(), randomLiters(),
                        randomDistance(), randomDistance()));
        assertEquals(Stop.CONTAINER_NOT_DEFINED, ex.getMessage());
    }

    @Test
    void restoreConstructor_kilograms_null() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), null, randomLiters(),
                        randomDistance(), randomDistance()));
        assertEquals(Stop.COLLECTED_KILOGRAMS_NOT_DEFINED, ex.getMessage());
    }

    @Test
    void restoreConstructor_liters_null() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), randomKilograms(), null,
                        randomDistance(), randomDistance()));
        assertEquals(Stop.COLLECTED_LITERS_NOT_DEFINED, ex.getMessage());
    }

    @Test
    void restoreConstructor_distancePrevious_null() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), randomKilograms(),
                        randomLiters(), null, randomDistance()));
        assertEquals(Stop.DISTANCE_PREVIOUS_NOT_DEFINED, ex.getMessage());
    }

    @Test
    void restoreConstructor_cumulativeDistance_null() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), randomKilograms(),
                        randomLiters(), randomDistance(), null));
        assertEquals(Stop.CUMULATIVE_DISTANCE_NOT_DEFINED, ex.getMessage());
    }

    // ========== Copy Constructor ==========

    @Test
    void copyConstructor_right() {
        Stop original = randomStop();
        Stop copy = new Stop(original);

        assertEquals(original.getSequence(), copy.getSequence());
        assertEquals(original.getType(), copy.getType());
        assertEquals(original.getContainer(), copy.getContainer());
        assertEquals(original.getCollectedKilograms(), copy.getCollectedKilograms());
        assertEquals(original.getCollectedLiters(), copy.getCollectedLiters());
        assertEquals(original.getDistanceFromPreviousMeters(), copy.getDistanceFromPreviousMeters());
        assertEquals(original.getCumulativeDistanceMeters(), copy.getCumulativeDistanceMeters());
    }

    @Test
    void copyConstructor_null() {
        assertThrows(IllegalArgumentException.class, () -> new Stop(null));
    }

    // ========== equals & hashCode ==========

    @Test
    void equalsMethod() {
        Stop s1 = randomStop();
        Stop s2 = randomStop();

        assertTrue(s1.equals(s1));
        assertFalse(s1.equals(null));
        assertFalse(s1.equals(Integer.valueOf(0)));
        assertNotEquals(s1, s2);
    }

    @Test
    void hashCodeMethod() {
        Stop s1 = randomStop();

        assertNotNull(s1.hashCode());
        assertEquals(s1.hashCode(), s1.hashCode());
    }

    // ========== toString ==========

    @Test
    void toStringMethod() {
        Stop stop = randomStop();
        String result = stop.toString();

        assertNotNull(result);
        assertTrue(result.contains("Stop="));
        assertTrue(result.contains("type="));
    }
}
