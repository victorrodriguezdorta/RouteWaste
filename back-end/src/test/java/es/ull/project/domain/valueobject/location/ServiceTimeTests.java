package es.ull.project.domain.valueobject.location;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTimeTests {

    /**
     * Constructor - valid value
     */
    @Test
    void constructor_validValue() {
        ServiceTime st = new ServiceTime(30.0);
        assertEquals(30.0, st.getValue());
    }

    /**
     * Constructor - negative value
     */
    @Test
    void constructor_negativeValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceTime(-5.0)
        );
        assertEquals("Service time cannot be negative", exception.getMessage());
    }

    /**
     * Getter
     */
    @Test
    void getter_value() {
        ServiceTime st = new ServiceTime(45.0);
        assertEquals(45.0, st.getValue());
    }

    /**
     * Setter (returns new object)
     */
    @Test
    void setter_value() {
        ServiceTime original = new ServiceTime(20.0);
        ServiceTime updated = original.setValue(40.0);

        assertNotSame(original, updated);
        assertEquals(20.0, original.getValue());
        assertEquals(40.0, updated.getValue());

        // Setter validation: negative value
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> original.setValue(-1.0)
        );
        assertEquals("Service time cannot be negative", exception.getMessage());
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        ServiceTime st1 = new ServiceTime(15.0);
        ServiceTime st2 = new ServiceTime(15.0);
        ServiceTime st3 = new ServiceTime(20.0);

        assertEquals(st1, st1);           // same object
        assertNotEquals(st1, null);       // null
        assertNotEquals(st1, Integer.valueOf(0)); // different class
        assertEquals(st1, st2);           // same value
        assertNotEquals(st1, st3);        // different value
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        ServiceTime st1 = new ServiceTime(25.0);
        ServiceTime st2 = new ServiceTime(25.0);
        ServiceTime st3 = new ServiceTime(30.0);

        assertEquals(st1.hashCode(), st2.hashCode());
        assertNotEquals(st1.hashCode(), st3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        ServiceTime st = new ServiceTime(10.0);
        assertEquals("ServiceTime={value=10.0}", st.toString());
    }
}
