package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.Capacity;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.policy.ServicePolicies;

class ServiceAssignmentTests {

    // Helpers
    private static WasteDemand randomWasteDemand() {
        return new WasteDemand(5.0);
    }

    private static ContainerCapacityLiters randomCapacityLiters() {
        return new ContainerCapacityLiters(100.0 + Math.random() * 900.0);
    }

    private static DailyWasteDemandLitersPerDay randomDailyDemandLitersPerDay() {
        return new DailyWasteDemandLitersPerDay(5.0 + Math.random() * 45.0);
    }

    private static Distance randomDistance() {
        return Distance.fromMeters(2000.0); // 2 km
    }

    private static ServiceTime randomServiceTime() {
        return new ServiceTime(15.0); // 15 minutes
    }

    private static TransportationVariableCost randomTransportCost() {
        return new TransportationVariableCost(2.5);
    }

    private static Location randomLocation() {
        return new Location(
            28.4682 + Math.random() * 0.1,
            -16.2546 + Math.random() * 0.1,
            "Address " + ((int)(Math.random() * 1000)),
            "GIS-REF-CNT-" + ((int)(Math.random() * 10000))
        );
    }

    private static Capacity randomCapacity() {
        return new Capacity(
            100.0 + Math.random() * 500.0,
            new QuantityUnit("tons"),
            TimeUnit.DAY
        );
    }

    private static OpeningFixedCost randomOpeningFixedCost() {
        return new OpeningFixedCost(10000.0 + Math.random() * 50000.0);
    }

    private static Container randomContainer() {
        return new Container(randomLocation(), WasteType.random(), randomCapacityLiters(), randomDailyDemandLitersPerDay(), ServiceZone.random());
    }

    private static Facility randomFacility() {
        return new Facility(FacilityType.random(), randomLocation(), randomCapacity(), randomOpeningFixedCost(), es.ull.project.domain.enumerate.FacilityStatus.random());
    }

    // ========== Constructors ==========

    @Test
    void constructor_1_right() {
        Container container = randomContainer();
        Facility facility = randomFacility();
        WasteDemand demand = randomWasteDemand();
        Distance distance = randomDistance();
        ServiceTime serviceTime = randomServiceTime();
        TransportationVariableCost cost = randomTransportCost();

        ServiceAssignment sa = new ServiceAssignment(container, facility, demand, distance, serviceTime, cost);

        assertEquals(container, sa.getContainer());
        assertEquals(facility, sa.getFacility());
        assertEquals(demand, sa.getWasteDemand());
        assertEquals(distance, sa.getDistance());
        assertEquals(serviceTime, sa.getServiceTime());
        assertEquals(cost, sa.getTransportCost());
        assertNotNull(sa.getId());
    }

    @Test
    void constructor_containerId_undefined() {
        Container container = null;
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(container, facility, randomWasteDemand(), randomDistance(), randomServiceTime(), randomTransportCost())
        );
        assertEquals(ServiceAssignment.CONTAINER_ID_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_facilityId_undefined() {
        Container container = randomContainer();
        Facility facility = null;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(container, facility, randomWasteDemand(), randomDistance(), randomServiceTime(), randomTransportCost())
        );
        assertEquals(ServiceAssignment.FACILITY_ID_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_demand_undefined() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(randomContainer(), randomFacility(), null, randomDistance(), randomServiceTime(), randomTransportCost())
        );
        assertEquals(ServiceAssignment.DEMAND_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_distance_undefined() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), null, randomServiceTime(), randomTransportCost())
        );
        assertEquals(ServiceAssignment.DISTANCE_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_serviceTime_undefined() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), randomDistance(), null, randomTransportCost())
        );
        assertEquals(ServiceAssignment.SERVICE_TIME_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_transportCost_undefined() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), randomDistance(), randomServiceTime(), null)
        );
        assertEquals(ServiceAssignment.TRANSPORT_COST_NOT_DEFINED, exception.getMessage());
    }

    // ========== equals & hashCode ==========

    @Test
    void equalsMethod() {
        ServiceAssignment sa1 = new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), randomDistance(), randomServiceTime(), randomTransportCost());
        ServiceAssignment sa2 = new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), randomDistance(), randomServiceTime(), randomTransportCost());

        assertTrue(sa1.equals(sa1));
        assertFalse(sa1.equals(null));
        assertFalse(sa1.equals(Integer.valueOf(0)));
        assertNotEquals(sa1, sa2);
    }

    @Test
    void hashCodeMethod() {
        ServiceAssignment sa1 = new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), randomDistance(), randomServiceTime(), randomTransportCost());
        ServiceAssignment sa2 = new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), randomDistance(), randomServiceTime(), randomTransportCost());

        assertEquals(sa1.hashCode(), sa1.hashCode());
        assertNotEquals(sa1.hashCode(), sa2.hashCode());
    }

    // ========== validatePolicies (state-related validation) ==========

    @Test
    void validatePolicies_valid() {
        ServiceAssignment sa = new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), Distance.fromMeters(100.0), new ServiceTime(5.0), randomTransportCost());
        ServicePolicies policies = new ServicePolicies(500.0, 60, 10, 1000.0);

        // Should not throw
        sa.validatePolicies(policies);
    }

    @Test
    void validatePolicies_violation_distance() {
        ServiceAssignment sa = new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), Distance.fromMeters(20000.0), new ServiceTime(5.0), randomTransportCost());
        // policy with small max distance to force violation (meters)
        ServicePolicies policies = new ServicePolicies(100.0, 60, 10, 1000.0);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> sa.validatePolicies(policies)
        );

        assertTrue(exception.getMessage().contains(ServiceAssignment.POLICY_VIOLATION));
    }

    // ========== toString ==========

    @Test
    void toStringMethod() {
        ServiceAssignment sa = new ServiceAssignment(randomContainer(), randomFacility(), randomWasteDemand(), randomDistance(), randomServiceTime(), randomTransportCost());
        String expected = String.format(
            "ServiceAssignment={id=%s, containerId=%s, facilityId=%s, demand=%s, distance=%s, serviceTime=%s, transportCost=%s}",
            sa.getId(), sa.getContainer().getId(), sa.getFacility().getId(), sa.getWasteDemand(), sa.getDistance(), sa.getServiceTime(), sa.getTransportCost()
        );

        assertEquals(expected, sa.toString());
    }
}
