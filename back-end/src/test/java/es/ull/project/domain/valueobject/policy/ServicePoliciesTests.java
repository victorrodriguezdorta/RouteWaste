package es.ull.project.domain.valueobject.policy;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ServicePoliciesTests {

    /**
     * Constructor - valid values
     */
    @Test
    void constructor_validValue() {
        ServicePolicies sp = new ServicePolicies(1000.0, 120, 5, 50.0);

        assertEquals(Optional.of(1000.0), sp.getMaxServiceDistance());
        assertEquals(Optional.of(120), sp.getMaxServiceTime());
        assertEquals(Optional.of(5), sp.getMaxInfrastructureCount());
        assertEquals(Optional.of(50.0), sp.getMaxEmissions());
    }

    /**
     * Constructor - null values (optional attributes)
     */
    @Test
    void constructor_optionalNullValues() {
        ServicePolicies sp = new ServicePolicies(null, null, null, null);

        assertEquals(Optional.empty(), sp.getMaxServiceDistance());
        assertEquals(Optional.empty(), sp.getMaxServiceTime());
        assertEquals(Optional.empty(), sp.getMaxInfrastructureCount());
        assertEquals(Optional.empty(), sp.getMaxEmissions());
    }

    /**
     * Constructor - negative values
     */
    @Test
    void constructor_negativeValue() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(-1.0, 10, 2, 5.0)
        );
        assertEquals("Policy values cannot be negative", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(1.0, -10, 2, 5.0)
        );
        assertEquals("Policy values cannot be negative", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(1.0, 10, -2, 5.0)
        );
        assertEquals("Policy values cannot be negative", ex3.getMessage());

        IllegalArgumentException ex4 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(1.0, 10, 2, -5.0)
        );
        assertEquals("Policy values cannot be negative", ex4.getMessage());
    }

    /**
     * hasAnyPolicy
     */
    @Test
    void hasAnyPolicyMethod() {
        ServicePolicies sp1 = new ServicePolicies(null, null, null, null);
        assertFalse(sp1.hasAnyPolicy());

        ServicePolicies sp2 = new ServicePolicies(1.0, null, null, null);
        assertTrue(sp2.hasAnyPolicy());
    }

    /**
     * validateServiceAssignment
     */
    @Test
    void validateServiceAssignmentMethod() {
        ServicePolicies sp = new ServicePolicies(1000.0, 60, null, null);

        Optional<String> ok = sp.validateServiceAssignment(500.0, 30);
        assertTrue(ok.isEmpty());

        Optional<String> distExceeded = sp.validateServiceAssignment(1500.0, 30);
        assertTrue(distExceeded.isPresent());
        assertTrue(distExceeded.get().contains("Service distance"));

        Optional<String> timeExceeded = sp.validateServiceAssignment(500.0, 90);
        assertTrue(timeExceeded.isPresent());
        assertTrue(timeExceeded.get().contains("Service time"));
    }

    /**
     * isCompliant
     */
    @Test
    void isCompliantMethod() {
        ServicePolicies sp = new ServicePolicies(1000.0, 60, 5, 10.0);

        assertTrue(sp.isCompliant(500.0, 30, 3, 5.0));
        assertFalse(sp.isCompliant(1500.0, 30, 3, 5.0)); // distance
        assertFalse(sp.isCompliant(500.0, 90, 3, 5.0));  // time
        assertFalse(sp.isCompliant(500.0, 30, 6, 5.0));  // infrastructure
        assertFalse(sp.isCompliant(500.0, 30, 3, 20.0)); // emissions
    }

    /**
     * equals
     */
    @Test
    void equalsMethod() {
        ServicePolicies sp1 = new ServicePolicies(1.0, 2, 3, 4.0);
        ServicePolicies sp2 = new ServicePolicies(1.0, 2, 3, 4.0);
        ServicePolicies sp3 = new ServicePolicies(10.0, 2, 3, 4.0);

        assertEquals(sp1, sp1);           // same object
        assertNotEquals(sp1, null);       // null
        assertNotEquals(sp1, Integer.valueOf(0)); // different class
        assertEquals(sp1, sp2);           // same values
        assertNotEquals(sp1, sp3);        // different values
    }

    /**
     * hashCode
     */
    @Test
    void hashCodeMethod() {
        ServicePolicies sp1 = new ServicePolicies(1.0, 2, 3, 4.0);
        ServicePolicies sp2 = new ServicePolicies(1.0, 2, 3, 4.0);
        ServicePolicies sp3 = new ServicePolicies(10.0, 2, 3, 4.0);

        assertEquals(sp1.hashCode(), sp2.hashCode());
        assertNotEquals(sp1.hashCode(), sp3.hashCode());
    }

    /**
     * toString
     */
    @Test
    void toStringMethod() {
        ServicePolicies sp = new ServicePolicies(1.0, 2, 3, 4.0);
        assertEquals(
                "ServicePolicies={maxServiceDistance=1.0, maxServiceTime=2, maxInfrastructureCount=3, maxEmissions=4.0}",
                sp.toString()
        );
    }
}
