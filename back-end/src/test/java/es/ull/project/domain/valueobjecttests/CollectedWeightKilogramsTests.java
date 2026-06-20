package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CollectedWeightKilogramsTests {

    private static final String EXPECTED_TO_STRING_WEIGHT = "10";

    /**
     * Verifies that a valid collected weight in kilograms is stored correctly.
     */
    @Test
    void fromKilogramsValidValue() {
        CollectedWeightKilograms cwk = CollectedWeightKilograms.fromKilograms(25.5);
        assertEquals(25.5, cwk.getValue());
    }

    /**
     * Verifies that zero kilograms is accepted as a collected weight.
     */
    @Test
    void fromKilogramsZero() {
        CollectedWeightKilograms cwk = CollectedWeightKilograms.fromKilograms(0.0);
        assertEquals(0.0, cwk.getValue());
    }

    /**
     * Verifies that negative collected weight values are rejected.
     */
    @Test
    void fromKilogramsNegativeValue() {
        assertThrows(IllegalArgumentException.class,
                () -> CollectedWeightKilograms.fromKilograms(-1.0));
    }

    /**
     * Verifies equality for matching and different collected weight values.
     */
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

    /**
     * Verifies that equal collected weight values produce the same hash code.
     */
    @Test
    void hashCodeMethod() {
        CollectedWeightKilograms a = CollectedWeightKilograms.fromKilograms(10.0);
        CollectedWeightKilograms b = CollectedWeightKilograms.fromKilograms(10.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Verifies that the string representation includes the weight value.
     */
    @Test
    void toStringMethod() {
        CollectedWeightKilograms cwk = CollectedWeightKilograms.fromKilograms(10.5);
        assertTrue(cwk.toString().contains(EXPECTED_TO_STRING_WEIGHT));
    }
}
