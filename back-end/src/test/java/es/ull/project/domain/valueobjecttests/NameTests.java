package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.name.Name;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class NameTests {

    private static final String VALID_NAME_INPUT = "  Main facility  ";
    private static final String VALID_NAME_VALUE = "Main facility";
    private static final String EQUAL_NAME_VALUE = "Container A";
    private static final String DIFFERENT_NAME_VALUE = "Container B";
    private static final String BLANK_NAME_VALUE = "   ";

    /**
     * Verifies that a valid name is trimmed and stored correctly.
     */
    @Test
    void constructorRight() {
        Name name = new Name(VALID_NAME_INPUT);
        assertEquals(VALID_NAME_VALUE, name.getValue());
    }

    /**
     * Verifies that null and blank names are rejected.
     */
    @Test
    void constructorRejectsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Name(null));
        assertThrows(IllegalArgumentException.class, () -> new Name(BLANK_NAME_VALUE));
    }

    /**
     * Verifies equality and hash code behavior for names.
     */
    @Test
    void equalsAndHashCode() {
        Name name = new Name(EQUAL_NAME_VALUE);
        Name same = new Name(EQUAL_NAME_VALUE);
        Name different = new Name(DIFFERENT_NAME_VALUE);
        assertEquals(name, same);
        assertEquals(name.hashCode(), same.hashCode());
        assertNotEquals(name, different);
        assertNotEquals(name.hashCode(), different.hashCode());
    }
}
