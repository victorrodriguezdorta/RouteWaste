package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CollectedVolumeLitersTests {

    private static final String EXPECTED_TO_STRING_VOLUME = "20";

    /**
     * Verifies that a valid collected volume in liters is stored correctly.
     */
    @Test
    void fromLitersValidValue() {
        CollectedVolumeLiters cvl = CollectedVolumeLiters.fromLiters(30.0);
        assertEquals(30.0, cvl.getValue());
    }

    /**
     * Verifies that zero liters is accepted as a collected volume.
     */
    @Test
    void fromLitersZero() {
        CollectedVolumeLiters cvl = CollectedVolumeLiters.fromLiters(0.0);
        assertEquals(0.0, cvl.getValue());
    }

    /**
     * Verifies that negative collected volume values are rejected.
     */
    @Test
    void fromLitersNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> CollectedVolumeLiters.fromLiters(-1.0));
    }

    /**
     * Verifies equality for matching and different collected volume values.
     */
    @Test
    void equalsMethod() {
        CollectedVolumeLiters a = CollectedVolumeLiters.fromLiters(20.0);
        CollectedVolumeLiters b = CollectedVolumeLiters.fromLiters(20.0);
        CollectedVolumeLiters c = CollectedVolumeLiters.fromLiters(40.0);
        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(Integer.valueOf(0)));
        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    /**
     * Verifies that equal collected volume values produce the same hash code.
     */
    @Test
    void hashCodeMethod() {
        CollectedVolumeLiters a = CollectedVolumeLiters.fromLiters(20.0);
        CollectedVolumeLiters b = CollectedVolumeLiters.fromLiters(20.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Verifies that the string representation includes the volume value.
     */
    @Test
    void toStringMethod() {
        CollectedVolumeLiters cvl = CollectedVolumeLiters.fromLiters(20.0);
        assertTrue(cvl.toString().contains(EXPECTED_TO_STRING_VOLUME));
    }
}
