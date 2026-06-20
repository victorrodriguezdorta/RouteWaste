package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.location.ServiceTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ServiceTimeTests {

    private static final String NEGATIVE_SERVICE_TIME_MESSAGE = "Service time cannot be negative";
    private static final String SERVICE_TIME_STRING_REPRESENTATION = "ServiceTime={value=10.0}";

    /**
     * Constructor - valid value
     */
    @Test
    void constructorValidValue() {
        ServiceTime st = new ServiceTime(30.0);
        assertEquals(30.0, st.getValue());
    }

    /**
     * Constructor - negative value
     */
    @Test
    void constructorNegativeValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceTime(-5.0)
        );
        assertEquals(NEGATIVE_SERVICE_TIME_MESSAGE, exception.getMessage());
    }

    /**
     * Getter
     */
    @Test
    void getterValue() {
        ServiceTime st = new ServiceTime(45.0);
        assertEquals(45.0, st.getValue());
    }

    /**
     * Setter (returns new object)
     */
    @Test
    void setterValue() {
        ServiceTime original = new ServiceTime(20.0);
        ServiceTime updated = original.setValue(40.0);
        assertNotSame(original, updated);
        assertEquals(20.0, original.getValue());
        assertEquals(40.0, updated.getValue());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> original.setValue(-1.0)
        );
        assertEquals(NEGATIVE_SERVICE_TIME_MESSAGE, exception.getMessage());
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        ServiceTime st1 = new ServiceTime(15.0);
        ServiceTime st2 = new ServiceTime(15.0);
        ServiceTime st3 = new ServiceTime(20.0);
        assertEquals(st1, st1);
        assertNotEquals(st1, null);
        assertNotEquals(st1, Integer.valueOf(0));
        assertEquals(st1, st2);
        assertNotEquals(st1, st3);
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
        assertEquals(SERVICE_TIME_STRING_REPRESENTATION, st.toString());
    }
}
