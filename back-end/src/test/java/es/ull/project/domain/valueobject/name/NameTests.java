package es.ull.project.domain.valueobject.name;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NameTests {

    @Test
    void constructorRight() {
        Name name = new Name("  Main facility  ");

        assertEquals("Main facility", name.getValue());
    }

    @Test
    void constructorRejectsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Name(null));
        assertThrows(IllegalArgumentException.class, () -> new Name("   "));
    }

    @Test
    void equalsAndHashCode() {
        Name name = new Name("Container A");
        Name same = new Name("Container A");
        Name different = new Name("Container B");

        assertEquals(name, same);
        assertEquals(name.hashCode(), same.hashCode());
        assertNotEquals(name, different);
        assertNotEquals(name.hashCode(), different.hashCode());
    }
}
