package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;



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
        return new Capacity(100.0, new QuantityUnit("tons"), TimeUnit.DAY);
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

    private static ServiceAssignment randomServiceAssignment(Container container, Facility facility) {
        return new ServiceAssignment(container, facility, container.getWasteDemand(), Distance.fromMeters(1000.0), new ServiceTime(10.0), new TransportationVariableCost(50.0));
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
    void addServiceAssignment_valid() {
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), new MaximumBudget(1_000_000.0), randomServicePolicies());
        Facility facility = randomFacility();
        plan.addFacility(facility);
        Container container = randomContainer();
        ServiceAssignment assignment = randomServiceAssignment(container, facility);

        plan.addServiceAssignment(assignment);

        assertTrue(plan.getServiceAssignments().contains(assignment));
        assertEquals(1, plan.getServiceAssignments().size());
    }

    @Test
    void addServiceAssignment_invalid_null() {
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        ServiceAssignment assignment = null;

        IllegalArgumentException exc = assertThrows(
                IllegalArgumentException.class,
                () -> plan.addServiceAssignment(assignment)
        );
        assertEquals(InfrastructurePlan.INVALID_ASSIGNMENT, exc.getMessage());
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
        ServiceAssignment assignment = randomServiceAssignment(container, facility);
        plan.addServiceAssignment(assignment);

        facility.updateStatus(FacilityStatus.DISCARDED);

        assertFalse(plan.isPlanValid());
    }

    // ========== toString ==========

    @Test
    void toStringMethod() {
        InfrastructurePlan plan = new InfrastructurePlan(randomPeriod(), randomMaxBudget(), randomServicePolicies());
        String expected = String.format("InfrastructurePlan={id=%s, period=%s, facilities=%s, assignments=%s, totalCost=%s}",
                plan.getId(), plan.getPeriod(), plan.getSelectedFacilities(), plan.getServiceAssignments(), plan.getEstimatedTotalCost());

        assertEquals(expected, plan.toString());
    }
}
