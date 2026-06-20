package es.ull.project.domain.entitytests;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
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
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ServiceAssignmentTests {

    private static final String CONTAINER_NAME_PREFIX = "container";
    private static final String FACILITY_NAME_PREFIX = "facility";

    /**
     * Creates a valid random container capacity in liters.
     *
     * @return valid container capacity.
     */
    private static ContainerCapacityLiters randomCapacityLiters() {
        return new ContainerCapacityLiters(100.0 + Math.random() * 900.0);
    }

    /**
     * Creates a valid random daily demand in liters per day.
     *
     * @return valid daily waste demand.
     */
    private static DailyWasteDemandLitersPerDay randomDailyDemandLitersPerDay() {
        return new DailyWasteDemandLitersPerDay(5.0 + Math.random() * 45.0);
    }

    /**
     * Creates a valid random location.
     *
     * @return valid location.
     */
    private static Location randomLocation() {
        return new Location(
                28.4682 + Math.random() * 0.1,
                -16.2546 + Math.random() * 0.1,
                "Address " + ((int) (Math.random() * 1000)),
                "GIS-REF-CNT-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Creates a valid random storage capacity.
     *
     * @return valid storage capacity.
     */
    private static StorageCapacityKilograms randomStorageCapacity() {
        return new StorageCapacityKilograms(1000.0 + Math.random() * 5000.0);
    }

    /**
     * Creates a valid random processing capacity.
     *
     * @return valid processing capacity.
     */
    private static ProcessingCapacityKilogramsPerDay randomProcessingCapacity() {
        return new ProcessingCapacityKilogramsPerDay(500.0 + Math.random() * 2000.0);
    }

    /**
     * Creates a valid random unloading time.
     *
     * @return valid unloading time.
     */
    private static UnloadingTime randomUnloadingTime() {
        return new UnloadingTime(30 + (int) (Math.random() * 120));
    }

    /**
     * Creates a valid random opening fixed cost.
     *
     * @return valid opening fixed cost.
     */
    private static OpeningFixedCost randomOpeningFixedCost() {
        return new OpeningFixedCost(10000.0 + Math.random() * 50000.0);
    }

    /**
     * Creates a valid container.
     *
     * @return valid container.
     */
    private static Container randomContainer() {
        return new Container(
                randomName(CONTAINER_NAME_PREFIX),
                randomLocation(),
                WasteType.random(),
                randomCapacityLiters(),
                randomDailyDemandLitersPerDay(),
                ServiceZone.random());
    }

    /**
     * Creates a valid facility.
     *
     * @return valid facility.
     */
    private static Facility randomFacility() {
        return new Facility(
                randomName(FACILITY_NAME_PREFIX),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.random());
    }

    /**
     * Creates a random name with the provided prefix.
     *
     * @param prefix name prefix.
     * @return valid name.
     */
    private static Name randomName(String prefix) {
        return new Name(prefix + "-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Creates a valid infrastructure plan.
     *
     * @return valid infrastructure plan.
     */
    private static InfrastructurePlan randomInfrastructurePlan() {
        return new InfrastructurePlan(
                new PlanningPeriod("2026"),
                new ServicePolicies(5000.0, 60, 100, 1000.0),
                new MaximumBudget(1_000_000.0),
                null,
                null,
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED);
    }

    /**
     * Tests that the constructor creates a valid service assignment.
     */
    @Test
    void constructorOneRight() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        ServiceAssignment serviceAssignment = new ServiceAssignment(plan, facility, containers);
        assertEquals(plan.getId(), serviceAssignment.getInfrastructurePlan().getId());
        assertEquals(facility, serviceAssignment.getFacility());
        assertEquals(containers, serviceAssignment.getAssignedContainers());
        assertNotNull(serviceAssignment.getId());
    }

    /**
     * Tests that the constructor rejects an undefined infrastructure plan.
     */
    @Test
    void constructorPlanUndefined() {
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceAssignment(null, facility, containers));
        assertEquals(ServiceAssignment.INFRASTRUCTURE_PLAN_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an undefined facility.
     */
    @Test
    void constructorFacilityUndefined() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceAssignment(plan, null, containers));
        assertEquals(ServiceAssignment.FACILITY_ID_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects undefined containers.
     */
    @Test
    void constructorContainersUndefined() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceAssignment(plan, facility, null));
        assertEquals(ServiceAssignment.CONTAINERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an empty container list.
     */
    @Test
    void constructorContainersEmpty() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceAssignment(plan, facility, containers));
        assertEquals(ServiceAssignment.CONTAINERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests service assignment equality semantics.
     */
    @Test
    void equalsMethod() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        ServiceAssignment firstAssignment = new ServiceAssignment(plan, facility, containers);
        ServiceAssignment secondAssignment = new ServiceAssignment(plan, facility, containers);
        assertTrue(firstAssignment.equals(firstAssignment));
        assertFalse(firstAssignment.equals(null));
        assertFalse(firstAssignment.equals(Integer.valueOf(0)));
        assertNotEquals(firstAssignment, secondAssignment);
    }

    /**
     * Tests service assignment hash code semantics.
     */
    @Test
    void hashCodeMethod() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        ServiceAssignment firstAssignment = new ServiceAssignment(plan, facility, containers);
        ServiceAssignment secondAssignment = new ServiceAssignment(plan, facility, containers);
        assertEquals(firstAssignment.hashCode(), firstAssignment.hashCode());
        assertNotEquals(firstAssignment.hashCode(), secondAssignment.hashCode());
    }

    /**
     * Tests the service assignment string representation.
     */
    @Test
    void toStringMethod() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        List<Container> containers = new ArrayList<>();
        containers.add(randomContainer());
        ServiceAssignment serviceAssignment = new ServiceAssignment(plan, facility, containers);
        String expected = String.format(
                "ServiceAssignment={id=%s, planId=%s, facilityId=%s, assignedContainers=[%s]}",
                serviceAssignment.getId(),
                serviceAssignment.getInfrastructurePlan().getId(),
                serviceAssignment.getFacility().getId(),
                serviceAssignment.getAssignedContainers().get(0).getId());
        assertEquals(expected, serviceAssignment.toString());
    }
}
