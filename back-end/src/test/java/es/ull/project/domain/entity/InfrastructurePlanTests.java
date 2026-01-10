package es.ull.project.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import es.ull.project.domain.valueobject.time.PlanningPeriod;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import java.util.concurrent.TimeUnit;

class InfrastructurePlanTests {

    // Helper methods
    private static PlanningPeriod randomPeriod() {
        return new PlanningPeriod("2026");
    }

    private static MaximumBudget randomMaxBudget() {
        return new MaximumBudget(1_000_000.0);
    }

    private static ServicePolicies randomServicePolicies() {
        return new ServicePolicies(5000.0, 60, 100, 1000.0);
    }

    private static Location randomLocation() {
        return new Location(28.47, -16.25, "Addr " + ((int)(Math.random()*1000)), "GIS-" + ((int)(Math.random()*1000)));
    }

    private static Capacity randomCapacity() {
        return new Capacity(100.0, new QuantityUnit("tons"), TimeUnit.DAYS);
    }

    private static Facility randomFacility() {
        return new Facility(
                FacilityType.random(),
                randomLocation(),
                randomCapacity(),
                new OpeningFixedCost(10000.0),
                FacilityStatus.PLANNED
        );
    }

    private static Container randomContainer() {
        return new Container(randomLocation(), WasteType.random(), new WasteDemand(10.0));
    }

    // ========== Constructors ==========

    @Test
    void constructor_1_right() {
        PlanningPeriod period = randomPeriod();
        MaximumBudget maxBudget = randomMaxBudget();
        ServicePolicies policies = randomServicePolicies();

        InfrastructurePlan plan = new InfrastructurePlan(period, maxBudget, policies);

        assertEquals(period, plan.getPeriod());
        assertEquals(maxBudget, plan.getMaxBudget());
        assertEquals(policies, plan.getServicePolicies());
        assertNotNull(plan.getId());
        assertEquals(0.0, plan.getEstimatedTotalCost().getAmount());
    }

    @Test
    void constructor_1_period_undefined() {
        PlanningPeriod period = null;
        MaximumBudget maxBudget = randomMaxBudget();
        ServicePolicies policies = randomServicePolicies();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(period, maxBudget, policies)
        );

        assertEquals(InfrastructurePlan.PERIOD_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_1_maxBudget_undefined() {
        PlanningPeriod period = randomPeriod();
        MaximumBudget maxBudget = null;
        ServicePolicies policies = randomServicePolicies();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlan(period, maxBudget, policies)
        );

        assertEquals(InfrastructurePlan.MAX_BUDGET_NOT_DEFINED, exception.getMessage());
    }

    // ========== equals & hashCode ==========

    @Test
    void equalsMethod() {
        InfrastructurePlan p1 = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        InfrastructurePlan p2 = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        InfrastructurePlan p3 = p1;

        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(Integer.valueOf(0)));
        assertNotEquals(p1, p2);
        assertEquals(p1, p3);
    }

    @Test
    void hashCodeMethod() {
        InfrastructurePlan p1 = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        InfrastructurePlan p2 = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());

        assertEquals(p1.hashCode(), p1.hashCode());
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    // ========== State modifiers / behaviors ==========

    @Test
    void assignContainerToFacility_valid() {
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), new MaximumBudget(1_000_000.0), randomServicePolicies());
        Facility facility = randomFacility();
        plan.addFacility(facility);
        Container container = randomContainer();

        plan.assignContainerToFacility(container, facility);

        assertTrue(plan.getServiceAssignments().containsKey(container));
        assertEquals(facility, plan.getServiceAssignments().get(container));
    }

    @Test
    void assignContainerToFacility_invalid_nulls() {
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        Container container = null;
        Facility facility = randomFacility();

        IllegalArgumentException exc1 = assertThrows(
                IllegalArgumentException.class,
                () -> plan.assignContainerToFacility(container, facility)
        );
        assertEquals(InfrastructurePlan.INVALID_ASSIGNMENT, exc1.getMessage());

        IllegalArgumentException exc2 = assertThrows(
                IllegalArgumentException.class,
                () -> plan.assignContainerToFacility(randomContainer(), null)
        );
        assertEquals(InfrastructurePlan.INVALID_ASSIGNMENT, exc2.getMessage());
    }

    @Test
    void recalculateTotalCost_exceeded() {
        MaximumBudget smallBudget = new MaximumBudget(1.0);
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), smallBudget, randomServicePolicies());
        // Facility with large opening cost
        Facility expensive = new Facility(FacilityType.random(), randomLocation(), randomCapacity(), new OpeningFixedCost(10000.0), FacilityStatus.PLANNED);
        plan.addFacility(expensive);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> plan.recalculateTotalCost()
        );
        assertEquals(InfrastructurePlan.TOTAL_COST_EXCEEDED, exception.getMessage());
    }

    @Test
    void isPlanValid_afterDiscardingFacility_returnsFalse() {
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        Facility facility = randomFacility();
        plan.addFacility(facility);
        Container container = randomContainer();
        plan.assignContainerToFacility(container, facility);

        // Mark facility as discarded
        facility.updateStatus(FacilityStatus.DISCARDED);

        assertFalse(plan.isPlanValid());
    }

    // ========== toString ==========

    @Test
    void toStringMethod() {
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        String expected = String.format("InfrastructurePlan={id=%s, period=%s, facilities=%s, assignments=%s, totalCost=%s}",
                plan.getId(), plan.getPeriod(), plan.getSelectedFacilities(), plan.getServiceAssignments().keySet(), plan.getEstimatedTotalCost());

        assertEquals(expected, plan.toString());
    }
}
