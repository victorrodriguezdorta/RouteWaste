package es.ull.project.domain.valueobject.identifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ContainerIdTests {

    @Test
    void constructorRight() {
        UUID value = UUID.randomUUID();

        ContainerId id = new ContainerId(value);

        assertEquals(value, id.getValue());
        assertEquals(value.toString(), id.toString());
    }

    @Test
    void constructorNull() {
        assertThrows(IllegalArgumentException.class, () -> new ContainerId(null));
    }

    @Test
    void equalsAndHashCode() {
        UUID value = UUID.randomUUID();
        ContainerId id = new ContainerId(value);
        ContainerId same = new ContainerId(value);
        ContainerId different = new ContainerId(UUID.randomUUID());

        assertEquals(id, same);
        assertEquals(id.hashCode(), same.hashCode());
        assertNotEquals(id, different);
    }
}
