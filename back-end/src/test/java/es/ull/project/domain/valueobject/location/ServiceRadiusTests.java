package es.ull.project.domain.valueobject.location;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceRadiusTests {

    /**
     * Constructor - valid value
     */
    @Test
    void constructor_validValue() {
        ServiceRadius radius = new ServiceRadius(500.0);
        assertEquals(500.0, radius.getValue());
    }

    /**
     * Constructor - negative value
     */
    @Test
    void constructor_negativeValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceRadius(-1.0)
        );
        assertEquals("Service radius cannot be negative", exception.getMessage());
    }

    /**
     * Getter
     */
    @Test
    void getter_value() {
        ServiceRadius radius = new ServiceRadius(200.0);
        assertEquals(200.0, radius.getValue());
    }

    /**
     * Setter
     */
    @Test
    void setter_value() {
        ServiceRadius original = new ServiceRadius(200.0);
        ServiceRadius updated = original.setValue(300.0);

        assertNotSame(original, updated);
        assertEquals(300.0, updated.getValue());

        // original remains unchanged
        assertEquals(200.0, original.getValue());
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        ServiceRadius r1 = new ServiceRadius(100.0);
        ServiceRadius r2 = new ServiceRadius(100.0);
        ServiceRadius r3 = new ServiceRadius(200.0);

        assertEquals(r1, r1);
        assertNotEquals(r1, null);
        assertNotEquals(r1, Integer.valueOf(0));
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        ServiceRadius r1 = new ServiceRadius(100.0);
        ServiceRadius r2 = new ServiceRadius(100.0);
        ServiceRadius r3 = new ServiceRadius(200.0);

        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        ServiceRadius radius = new ServiceRadius(150.0);
        assertEquals("ServiceRadius={value=150.0}", radius.toString());
    }
}
