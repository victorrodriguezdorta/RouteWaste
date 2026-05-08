package es.ull.project.domain.valueobject.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CollectedWeightKilogramsTests {

    @Test
    void fromKilograms_validValue() {
        CollectedWeightKilograms cwk = CollectedWeightKilograms.fromKilograms(25.5);
        assertEquals(25.5, cwk.getValue());
    }

    @Test
    void fromKilograms_zero() {
        CollectedWeightKilograms cwk = CollectedWeightKilograms.fromKilograms(0.0);
        assertEquals(0.0, cwk.getValue());
    }

    @Test
    void fromKilograms_negativeValue() {
        assertThrows(IllegalArgumentException.class,
                () -> CollectedWeightKilograms.fromKilograms(-1.0));
    }

    @Test
    void equalsMethod() {
        CollectedWeightKilograms a = CollectedWeightKilograms.fromKilograms(10.0);
        CollectedWeightKilograms b = CollectedWeightKilograms.fromKilograms(10.0);
        CollectedWeightKilograms c = CollectedWeightKilograms.fromKilograms(20.0);
        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(Integer.valueOf(0)));
        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void hashCodeMethod() {
        CollectedWeightKilograms a = CollectedWeightKilograms.fromKilograms(10.0);
        CollectedWeightKilograms b = CollectedWeightKilograms.fromKilograms(10.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        CollectedWeightKilograms cwk = CollectedWeightKilograms.fromKilograms(10.5);
        assertTrue(cwk.toString().contains("10"));
    }
}
