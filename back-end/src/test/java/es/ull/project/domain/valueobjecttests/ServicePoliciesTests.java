package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.policy.ServicePolicies;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ServicePoliciesTests {

    private static final String NEGATIVE_POLICY_VALUE_MESSAGE = "Policy values cannot be negative";
    private static final String SERVICE_DISTANCE_MESSAGE_PREFIX = "Service distance";
    private static final String SERVICE_TIME_MESSAGE_PREFIX = "Service time";
    private static final String EXPECTED_TO_STRING = "ServicePolicies={maxServiceDistance=1.0, maxServiceTime=2, "
            + "maxInfrastructureCount=3, maxEmissions=4.0}";

    /**
     * Constructor - valid values
     */
    @Test
    void constructorValidValue() {
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
    void constructorOptionalNullValues() {
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
    void constructorNegativeValue() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(-1.0, 10, 2, 5.0)
        );
        assertEquals(NEGATIVE_POLICY_VALUE_MESSAGE, ex1.getMessage());
        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(1.0, -10, 2, 5.0)
        );
        assertEquals(NEGATIVE_POLICY_VALUE_MESSAGE, ex2.getMessage());
        IllegalArgumentException ex3 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(1.0, 10, -2, 5.0)
        );
        assertEquals(NEGATIVE_POLICY_VALUE_MESSAGE, ex3.getMessage());
        IllegalArgumentException ex4 = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicePolicies(1.0, 10, 2, -5.0)
        );
        assertEquals(NEGATIVE_POLICY_VALUE_MESSAGE, ex4.getMessage());
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
        assertTrue(distExceeded.get().contains(SERVICE_DISTANCE_MESSAGE_PREFIX));
        Optional<String> timeExceeded = sp.validateServiceAssignment(500.0, 90);
        assertTrue(timeExceeded.isPresent());
        assertTrue(timeExceeded.get().contains(SERVICE_TIME_MESSAGE_PREFIX));
    }

    /**
     * isCompliant
     */
    @Test
    void isCompliantMethod() {
        ServicePolicies sp = new ServicePolicies(1000.0, 60, 5, 10.0);
        assertTrue(sp.isCompliant(500.0, 30, 3, 5.0));
        assertFalse(sp.isCompliant(1500.0, 30, 3, 5.0));
        assertFalse(sp.isCompliant(500.0, 90, 3, 5.0));
        assertFalse(sp.isCompliant(500.0, 30, 6, 5.0));
        assertFalse(sp.isCompliant(500.0, 30, 3, 20.0));
    }

    /**
     * equals
     */
    @Test
    void equalsMethod() {
        ServicePolicies sp1 = new ServicePolicies(1.0, 2, 3, 4.0);
        ServicePolicies sp2 = new ServicePolicies(1.0, 2, 3, 4.0);
        ServicePolicies sp3 = new ServicePolicies(10.0, 2, 3, 4.0);
        assertEquals(sp1, sp1);
        assertNotEquals(sp1, null);
        assertNotEquals(sp1, Integer.valueOf(0));
        assertEquals(sp1, sp2);
        assertNotEquals(sp1, sp3);
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
                EXPECTED_TO_STRING,
                sp.toString()
        );
    }
}
