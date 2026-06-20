package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.infrastructureplan.PlanReferencePresence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PlanReferencePresenceTests {

    /**
     * Tests that the constructor stores the plan reference presence value.
     */
    @Test
    void constructorRight() {
        PlanReferencePresence presence = new PlanReferencePresence(true);
        assertTrue(presence.getValue());
    }

    /**
     * Tests equality and hash code behavior for plan reference presence values.
     */
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
