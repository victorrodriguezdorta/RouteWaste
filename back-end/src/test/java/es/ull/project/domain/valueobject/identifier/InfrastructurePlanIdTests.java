package es.ull.project.domain.valueobject.identifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class InfrastructurePlanIdTests {

    @Test
    void constructorRight() {
        UUID value = UUID.randomUUID();

        InfrastructurePlanId id = new InfrastructurePlanId(value);

        assertEquals(value, id.getValue());
        assertEquals(value.toString(), id.toString());
    }

    @Test
    void constructorNull() {
        assertThrows(IllegalArgumentException.class, () -> new InfrastructurePlanId(null));
    }

    @Test
    void equalsAndHashCode() {
        UUID value = UUID.randomUUID();
        InfrastructurePlanId id = new InfrastructurePlanId(value);
        InfrastructurePlanId same = new InfrastructurePlanId(value);
        InfrastructurePlanId different = new InfrastructurePlanId(UUID.randomUUID());

        assertEquals(id, same);
        assertEquals(id.hashCode(), same.hashCode());
        assertNotEquals(id, different);
    }
}
