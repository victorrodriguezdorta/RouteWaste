package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.location.Distance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class DistanceTests {

    private static final String DISTANCE_CANNOT_BE_NEGATIVE_MESSAGE = "Distance cannot be negative";
    private static final String DISTANCE_TO_STRING_TEMPLATE =
            "Distance={meters=%.2f m, kilometers=%.2f km}";

    /**
     * Should create a distance from a valid meter value.
     */
    @Test
    void constructorValidValueFromMeters() {
        Distance distance = Distance.fromMeters(1500.0);
        assertEquals(1500.0, distance.toMeters());
        assertEquals(1.5, distance.toKilometers());
    }

    /**
     * Should create a distance from a valid kilometer value.
     */
    @Test
    void constructorValidValueFromKilometers() {
        Distance distance = Distance.fromKilometers(2.0);
        assertEquals(2000.0, distance.toMeters());
        assertEquals(2.0, distance.toKilometers());
    }

    /**
     * Should create a distance from a valid mile value.
     */
    @Test
    void constructorValidValueFromMiles() {
        Distance distance = Distance.fromMiles(1.0);
        assertEquals(1609.34, distance.toMeters(), 0.0001);
    }

    /**
     * Should reject a negative distance in meters.
     */
    @Test
    void constructorNegativeValueFromMeters() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Distance.fromMeters(-1.0));
        assertEquals(DISTANCE_CANNOT_BE_NEGATIVE_MESSAGE, exception.getMessage());
    }

    /**
     * Should reject a negative distance in kilometers.
     */
    @Test
    void constructorNegativeValueFromKilometers() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Distance.fromKilometers(-0.5));
        assertEquals(DISTANCE_CANNOT_BE_NEGATIVE_MESSAGE, exception.getMessage());
    }

    /**
     * Should reject a negative distance in miles.
     */
    @Test
    void constructorNegativeValueFromMiles() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Distance.fromMiles(-2.0));
        assertEquals(DISTANCE_CANNOT_BE_NEGATIVE_MESSAGE, exception.getMessage());
    }

    /**
     * Should expose distance values through getter methods.
     */
    @Test
    void getters() {
        Distance distance = Distance.fromMeters(500.0);
        assertEquals(500.0, distance.getValue());
        assertEquals(0.5, distance.toKilometers());
    }

    /**
     * Should return a new instance when updating the distance value.
     */
    @Test
    void setterValue() {
        Distance original = Distance.fromMeters(1000.0);
        Distance updated = original.setValue(2000.0);
        assertNotSame(original, updated);
        assertEquals(1000.0, original.getValue());
        assertEquals(2000.0, updated.getValue());
    }

    /**
     * Should compare distances by their meter value.
     */
    @Test
    void equalsMethod() {
        Distance d1 = Distance.fromMeters(1000.0);
        Distance d2 = Distance.fromMeters(1000.0);
        Distance d3 = Distance.fromMeters(2000.0);
        assertEquals(d1, d1);
        assertNotEquals(d1, null);
        assertNotEquals(d1, Integer.valueOf(0));
        assertEquals(d1, d2);
        assertNotEquals(d1, d3);
    }

    /**
     * Should generate consistent hash codes for equal distances.
     */
    @Test
    void hashCodeMethod() {
        Distance d1 = Distance.fromMeters(1000.0);
        Distance d2 = Distance.fromMeters(1000.0);
        Distance d3 = Distance.fromMeters(1500.0);
        assertEquals(d1.hashCode(), d2.hashCode());
        assertNotEquals(d1.hashCode(), d3.hashCode());
    }

    /**
     * Should include meter and kilometer values in the string representation.
     */
    @Test
    void toStringMethod() {
        Distance distance = Distance.fromMeters(1000.0);
        assertEquals(
                String.format(DISTANCE_TO_STRING_TEMPLATE, 1000.0, 1.0),
                distance.toString());
    }
}
