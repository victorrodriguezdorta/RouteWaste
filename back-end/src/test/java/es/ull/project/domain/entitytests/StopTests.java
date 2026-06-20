package es.ull.project.domain.entitytests;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Stop;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class StopTests {

    private static final String STOP_TO_STRING_PREFIX = "Stop=";
    private static final String STOP_TYPE_TO_STRING_FIELD = "type=";

    /**
     * Creates a valid random location for stop tests.
     *
     * @return valid location.
     */
    private static Location randomLocation() {
        return new Location(
                28.4682 + Math.random() * 0.1,
                -16.2546 + Math.random() * 0.1,
                "Test Address " + ((int) (Math.random() * 1000)),
                "GIS-REF-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Creates a valid random container capacity in liters.
     *
     * @return valid container capacity.
     */
    private static ContainerCapacityLiters randomCapacityLiters() {
        return new ContainerCapacityLiters(100.0 + Math.random() * 900.0);
    }

    /**
     * Creates a valid random daily demand in liters per day.
     *
     * @return valid daily demand.
     */
    private static DailyWasteDemandLitersPerDay randomDailyDemandLitersPerDay() {
        return new DailyWasteDemandLitersPerDay(10.0 + Math.random() * 100.0);
    }

    /**
     * Creates a valid route sequence for stop tests.
     *
     * @return valid route sequence.
     */
    private static RouteSequence randomSequence() {
        return RouteSequence.of(1);
    }

    /**
     * Creates a valid container for stop tests.
     *
     * @return valid container.
     */
    private static Container randomContainer() {
        return new Container(
                randomName(),
                randomLocation(),
                WasteType.random(),
                randomCapacityLiters(),
                randomDailyDemandLitersPerDay(),
                ServiceZone.random());
    }

    /**
     * Creates a random container name.
     *
     * @return valid name.
     */
    private static Name randomName() {
        return new Name("container-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Creates valid collected kilograms.
     *
     * @return valid collected kilograms.
     */
    private static CollectedWeightKilograms randomKilograms() {
        return CollectedWeightKilograms.fromKilograms(10.0);
    }

    /**
     * Creates valid collected liters.
     *
     * @return valid collected liters.
     */
    private static CollectedVolumeLiters randomLiters() {
        return CollectedVolumeLiters.fromLiters(20.0);
    }

    /**
     * Creates a valid route distance.
     *
     * @return valid distance.
     */
    private static Distance randomDistance() {
        return Distance.fromMeters(500.0);
    }

    /**
     * Creates a valid container stop.
     *
     * @return valid stop.
     */
    private static Stop randomStop() {
        return new Stop(
                randomSequence(),
                randomContainer(),
                randomKilograms(),
                randomLiters(),
                randomDistance(),
                randomDistance());
    }

    /**
     * Creates a valid facility stop.
     *
     * @return valid facility stop.
     */
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

    /**
     * Tests that the restore constructor stores all container stop values.
     */
    @Test
    void restoreConstructorRight() {
        RouteSequence sequence = randomSequence();
        Container container = randomContainer();
        CollectedWeightKilograms kilograms = randomKilograms();
        CollectedVolumeLiters liters = randomLiters();
        Distance previousDistance = randomDistance();
        Distance cumulativeDistance = randomDistance();
        Stop stop = new Stop(sequence, container, kilograms, liters, previousDistance, cumulativeDistance);
        assertEquals(sequence, stop.getSequence());
        assertEquals(container, stop.getContainer());
        assertEquals(kilograms, stop.getCollectedKilograms());
        assertEquals(liters, stop.getCollectedLiters());
        assertEquals(previousDistance, stop.getDistanceFromPreviousMeters());
        assertEquals(cumulativeDistance, stop.getCumulativeDistanceMeters());
        assertEquals(StopType.CONTAINER, stop.getType());
    }

    /**
     * Tests that the restore constructor creates a valid facility stop.
     */
    @Test
    void restoreConstructorFacilityStopRight() {
        Stop stop = facilityStop();
        assertEquals(StopType.FACILITY, stop.getType());
        assertEquals(null, stop.getContainer());
    }

    /**
     * Tests that the restore constructor rejects an undefined sequence.
     */
    @Test
    void restoreConstructorSequenceNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(null, randomContainer(), randomKilograms(), randomLiters(),
                        randomDistance(), randomDistance()));
        assertEquals(Stop.SEQUENCE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the restore constructor rejects an undefined container.
     */
    @Test
    void restoreConstructorContainerNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), null, randomKilograms(), randomLiters(),
                        randomDistance(), randomDistance()));
        assertEquals(Stop.CONTAINER_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the restore constructor rejects undefined collected kilograms.
     */
    @Test
    void restoreConstructorKilogramsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), null, randomLiters(),
                        randomDistance(), randomDistance()));
        assertEquals(Stop.COLLECTED_KILOGRAMS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the restore constructor rejects undefined collected liters.
     */
    @Test
    void restoreConstructorLitersNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), randomKilograms(), null,
                        randomDistance(), randomDistance()));
        assertEquals(Stop.COLLECTED_LITERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the restore constructor rejects an undefined previous distance.
     */
    @Test
    void restoreConstructorDistancePreviousNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), randomKilograms(),
                        randomLiters(), null, randomDistance()));
        assertEquals(Stop.DISTANCE_PREVIOUS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the restore constructor rejects an undefined cumulative distance.
     */
    @Test
    void restoreConstructorCumulativeDistanceNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(randomSequence(), randomContainer(), randomKilograms(),
                        randomLiters(), randomDistance(), null));
        assertEquals(Stop.CUMULATIVE_DISTANCE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the copy constructor preserves stop values.
     */
    @Test
    void copyConstructorRight() {
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

    /**
     * Tests that the copy constructor rejects a null stop.
     */
    @Test
    void copyConstructorNull() {
        assertThrows(IllegalArgumentException.class, () -> new Stop(null));
    }

    /**
     * Tests stop equality for self, null, other type, and different stops.
     */
    @Test
    void equalsMethod() {
        Stop firstStop = randomStop();
        Stop secondStop = randomStop();
        assertTrue(firstStop.equals(firstStop));
        assertFalse(firstStop.equals(null));
        assertFalse(firstStop.equals(Integer.valueOf(0)));
        assertNotEquals(firstStop, secondStop);
    }

    /**
     * Tests that the hash code is stable for the same stop.
     */
    @Test
    void hashCodeMethod() {
        Stop stop = randomStop();
        assertNotNull(stop.hashCode());
        assertEquals(stop.hashCode(), stop.hashCode());
    }

    /**
     * Tests that the string representation includes the stop and type fields.
     */
    @Test
    void toStringMethod() {
        Stop stop = randomStop();
        String result = stop.toString();
        assertNotNull(result);
        assertTrue(result.contains(STOP_TO_STRING_PREFIX));
        assertTrue(result.contains(STOP_TYPE_TO_STRING_FIELD));
    }
}
