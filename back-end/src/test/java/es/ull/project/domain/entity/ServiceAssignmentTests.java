package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

class ServiceAssignmentTests {

    // Helpers
    private static ContainerCapacityLiters randomCapacityLiters() {
        return new ContainerCapacityLiters(100.0 + Math.random() * 900.0);
    }

    private static DailyWasteDemandLitersPerDay randomDailyDemandLitersPerDay() {
        return new DailyWasteDemandLitersPerDay(5.0 + Math.random() * 45.0);
    }

    private static Location randomLocation() {
        return new Location(
            28.4682 + Math.random() * 0.1,
            -16.2546 + Math.random() * 0.1,
            "Address " + ((int)(Math.random() * 1000)),
            "GIS-REF-CNT-" + ((int)(Math.random() * 10000))
        );
    }

    private static StorageCapacityKilograms randomStorageCapacity() {
        return new StorageCapacityKilograms(1000.0 + Math.random() * 5000.0);
    }

    private static ProcessingCapacityKilogramsPerDay randomProcessingCapacity() {
        return new ProcessingCapacityKilogramsPerDay(500.0 + Math.random() * 2000.0);
    }

    private static UnloadingTime randomUnloadingTime() {
        return new UnloadingTime(30 + (int)(Math.random() * 120));
    }

    private static OpeningFixedCost randomOpeningFixedCost() {
        return new OpeningFixedCost(10000.0 + Math.random() * 50000.0);
    }

    private static Container randomContainer() {
        return new Container(randomLocation(), WasteType.random(), randomCapacityLiters(), randomDailyDemandLitersPerDay(), ServiceZone.random());
    }

    private static Facility randomFacility() {
        return new Facility(
            FacilityType.random(),
            randomLocation(),
            randomStorageCapacity(),
            randomProcessingCapacity(),
            randomUnloadingTime(),
            randomOpeningFixedCost(),
            FacilityStatus.random()
        );
    }
    
    private static InfrastructurePlan randomInfrastructurePlan() {
        return new InfrastructurePlan(new PlanningPeriod("2026"), new MaximumBudget(1_000_000.0), new ServicePolicies(5000.0, 60, 100, 1000.0), null, null, null);
    }

    // ========== Constructors ==========

    @Test
    void constructor_1_right() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());

        ServiceAssignment sa = new ServiceAssignment(plan, facility, containers);

        assertEquals(plan.getId(), sa.getInfrastructurePlan().getId());
        assertEquals(facility, sa.getFacility());
        assertEquals(containers, sa.getAssignedContainers());
        assertNotNull(sa.getId());
    }

    @Test
    void constructor_plan_undefined() {
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(null, facility, containers)
        );
        assertEquals(ServiceAssignment.INFRASTRUCTURE_PLAN_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_facility_undefined() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(plan, null, containers)
        );
        assertEquals(ServiceAssignment.FACILITY_ID_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void constructor_containers_undefined() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(plan, facility, null)
        );
        assertEquals(ServiceAssignment.CONTAINERS_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_containers_empty() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ServiceAssignment(plan, facility, containers)
        );
        assertEquals(ServiceAssignment.CONTAINERS_NOT_DEFINED, exception.getMessage());
    }

    // ========== equals & hashCode ==========

    @Test
    void equalsMethod() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        
        ServiceAssignment sa1 = new ServiceAssignment(plan, facility, containers);
        ServiceAssignment sa2 = new ServiceAssignment(plan, facility, containers);

        assertTrue(sa1.equals(sa1));
        assertFalse(sa1.equals(null));
        assertFalse(sa1.equals(Integer.valueOf(0)));
        assertNotEquals(sa1, sa2);
    }

    @Test
    void hashCodeMethod() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        
        ServiceAssignment sa1 = new ServiceAssignment(plan, facility, containers);
        ServiceAssignment sa2 = new ServiceAssignment(plan, facility, containers);

        assertEquals(sa1.hashCode(), sa1.hashCode());
        assertNotEquals(sa1.hashCode(), sa2.hashCode());
    }

    // ========== toString ==========

    @Test
    void toStringMethod() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        
        ServiceAssignment sa = new ServiceAssignment(plan, facility, containers);
        String expected = String.format(
            "ServiceAssignment={id=%s, planId=%s, facilityId=%s, assignedContainers=[%s]}",
            sa.getId(), sa.getInfrastructurePlan().getId(), sa.getFacility().getId(), sa.getAssignedContainers().get(0).getId()
        );

        assertEquals(expected, sa.toString());
    }
}
