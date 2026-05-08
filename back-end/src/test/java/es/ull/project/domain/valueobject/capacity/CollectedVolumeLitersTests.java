package es.ull.project.domain.valueobject.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CollectedVolumeLitersTests {

    @Test
    void fromLiters_validValue() {
        CollectedVolumeLiters cvl = CollectedVolumeLiters.fromLiters(30.0);
        assertEquals(30.0, cvl.getValue());
    }

    @Test
    void fromLiters_zero() {
        CollectedVolumeLiters cvl = CollectedVolumeLiters.fromLiters(0.0);
        assertEquals(0.0, cvl.getValue());
    }

    @Test
    void fromLiters_negativeValue() {
        assertThrows(IllegalArgumentException.class, () -> CollectedVolumeLiters.fromLiters(-1.0));
    }

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

    @Test
    void hashCodeMethod() {
        CollectedVolumeLiters a = CollectedVolumeLiters.fromLiters(20.0);
        CollectedVolumeLiters b = CollectedVolumeLiters.fromLiters(20.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        CollectedVolumeLiters cvl = CollectedVolumeLiters.fromLiters(20.0);
        assertTrue(cvl.toString().contains("20"));
    }
}
