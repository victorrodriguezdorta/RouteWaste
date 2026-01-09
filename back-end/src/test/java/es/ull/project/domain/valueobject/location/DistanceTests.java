package es.ull.project.domain.valueobject.location;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DistanceTests {

    /**
     * Constructor (factory) - valid value in meters
     */
    @Test
    void constructor_validValue_fromMeters() {
        Distance distance = Distance.fromMeters(1500.0);

        assertEquals(1500.0, distance.toMeters());
        assertEquals(1.5, distance.toKilometers());
    }

    /**
     * Constructor (factory) - valid value in kilometers
     */
    @Test
    void constructor_validValue_fromKilometers() {
        Distance distance = Distance.fromKilometers(2.0);

        assertEquals(2000.0, distance.toMeters());
        assertEquals(2.0, distance.toKilometers());
    }

    /**
     * Constructor (factory) - valid value in miles
     */
    @Test
    void constructor_validValue_fromMiles() {
        Distance distance = Distance.fromMiles(1.0);

        assertEquals(1609.34, distance.toMeters(), 0.0001);
    }

    /**
     * Constructor - negative value in meters
     */
    @Test
    void constructor_negativeValue_fromMeters() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Distance.fromMeters(-1.0)
        );

        assertEquals(
                "Distance cannot be negative",
                exception.getMessage()
        );
    }

    /**
     * Constructor - negative value in kilometers
     */
    @Test
    void constructor_negativeValue_fromKilometers() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Distance.fromKilometers(-0.5)
        );

        assertEquals(
                "Distance cannot be negative",
                exception.getMessage()
        );
    }

    /**
     * Constructor - negative value in miles
     */
    @Test
    void constructor_negativeValue_fromMiles() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Distance.fromMiles(-2.0)
        );

        assertEquals(
                "Distance cannot be negative",
                exception.getMessage()
        );
    }

    /**
     * Getter methods
     */
    @Test
    void getters() {
        Distance distance = Distance.fromMeters(500.0);

        assertEquals(500.0, distance.getValue());
        assertEquals(0.5, distance.toKilometers());
    }

    /**
     * Setter - value (immutability)
     */
    @Test
    void setter_value() {
        Distance original = Distance.fromMeters(1000.0);
        Distance updated = original.setValue(2000.0);

        assertNotSame(original, updated);
        assertEquals(1000.0, original.getValue());
        assertEquals(2000.0, updated.getValue());
    }

    /**
     * equals()
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
     * hashCode()
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
     * toString()
     */
    @Test
    void toStringMethod() {
        Distance distance = Distance.fromMeters(1000.0);

        assertEquals(
                "Distance={meters=1000.00 m, kilometers=1.00 km}",
                distance.toString()
        );
    }
}
