package es.ull.project.domain.valueobject.infrastructureplan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlanReferencePresenceTests {

    @Test
    void constructorRight() {
        PlanReferencePresence presence = new PlanReferencePresence(true);

        assertTrue(presence.getValue());
    }

    @Test
    void equalsAndHashCode() {
        PlanReferencePresence presence = new PlanReferencePresence(true);
        PlanReferencePresence same = new PlanReferencePresence(true);
        PlanReferencePresence different = new PlanReferencePresence(false);

        assertEquals(presence, same);
        assertEquals(presence.hashCode(), same.hashCode());
        assertNotEquals(presence, different);
    }
}
