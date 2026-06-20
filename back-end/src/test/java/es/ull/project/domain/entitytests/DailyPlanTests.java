package es.ull.project.domain.entitytests;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.route.RouteSequence;
import es.ull.project.domain.valueobject.time.PlanDay;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class DailyPlanTests {

    private static final String FACILITY_NAME_PREFIX = "facility";
    private static final String VEHICLE_NAME_PREFIX = "vehicle";
    private static final String CONTAINER_NAME_PREFIX = "container";
    private static final String DAILY_PLAN_TO_STRING_PREFIX = "DailyPlan=";

    /**
     * Creates a valid planning period for daily plan tests.
     *
     * @return valid planning period.
     */
    private static PlanningPeriod randomPeriod() {
        return new PlanningPeriod("2026");
    }

    /**
     * Creates a valid maximum budget for infrastructure plan tests.
     *
     * @return valid maximum budget.
     */
    private static MaximumBudget randomMaxBudget() {
        return new MaximumBudget(1_000_000.0);
    }

    /**
     * Creates valid service policies for infrastructure plan tests.
     *
     * @return valid service policies.
     */
    private static ServicePolicies randomServicePolicies() {
        return new ServicePolicies(5000.0, 60, 100, 1000.0);
    }

    /**
     * Creates a valid infrastructure plan for daily plan tests.
     *
     * @return valid infrastructure plan.
     */
    private static InfrastructurePlan randomInfrastructurePlan() {
        return new InfrastructurePlan(
                randomPeriod(),
                randomServicePolicies(),
                randomMaxBudget(),
                null,
                null,
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED);
    }

    /**
     * Creates a valid random location for entity tests.
     *
     * @return valid location.
     */
    private static Location randomLocation() {
        return new Location(
                28.4682 + Math.random() * 0.1,
                -16.2546 + Math.random() * 0.1,
                "Test Address " + ((int) (Math.random() * 1000)),
                "GIS-REF-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Creates a random name using the provided prefix.
     *
     * @param prefix name prefix.
     * @return valid name.
     */
    private static Name randomName(String prefix) {
        return new Name(prefix + "-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Creates a valid facility for daily plan tests.
     *
     * @return valid facility.
     */
    private static Facility randomFacility() {
        return new Facility(
                randomName(FACILITY_NAME_PREFIX),
                FacilityType.random(),
                randomLocation(),
                new StorageCapacityKilograms(1000.0),
                new ProcessingCapacityKilogramsPerDay(500.0),
                new UnloadingTime(60),
                new OpeningFixedCost(10000.0),
                FacilityStatus.PLANNED);
    }

    /**
     * Creates a valid vehicle for daily plan tests.
     *
     * @return valid vehicle.
     */
    private static Vehicle randomVehicle() {
        return new Vehicle(
                randomName(VEHICLE_NAME_PREFIX),
                VehicleType.random(),
                new VehicleCapacityKilograms(5000.0),
                new VehicleCapacityLiters(8000.0),
                new TransportationVariableCost(0.75));
    }

    /**
     * Creates a valid container for stop tests.
     *
     * @return valid container.
     */
    private static Container randomContainer() {
        return new Container(
                randomName(CONTAINER_NAME_PREFIX),
                randomLocation(),
                WasteType.random(),
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(10.0),
                ServiceZone.random());
    }

    /**
     * Creates zero collected kilograms.
     *
     * @return zero collected kilograms.
     */
    private static CollectedWeightKilograms zeroKilograms() {
        return CollectedWeightKilograms.fromKilograms(0.0);
    }

    /**
     * Creates zero collected liters.
     *
     * @return zero collected liters.
     */
    private static CollectedVolumeLiters zeroLiters() {
        return CollectedVolumeLiters.fromLiters(0.0);
    }

    /**
     * Creates zero distance.
     *
     * @return zero distance.
     */
    private static Distance zeroDistance() {
        return Distance.fromMeters(0.0);
    }

    /**
     * Creates a valid stop for daily plan route tests.
     *
     * @return valid stop.
     */
    private static Stop randomStop() {
        return new Stop(
                RouteSequence.of(1),
                randomContainer(),
                CollectedWeightKilograms.fromKilograms(10.0),
                CollectedVolumeLiters.fromLiters(20.0),
                Distance.fromMeters(500.0),
                Distance.fromMeters(500.0));
    }

    /**
     * Creates a valid daily plan for entity tests.
     *
     * @return valid daily plan.
     */
    private static DailyPlan randomDailyPlan() {
        return new DailyPlan(
                randomInfrastructurePlan(),
                randomFacility(),
                LocalDate.of(2026, 4, 26),
                PlanDay.fromInteger(1),
                randomVehicle());
    }

    /**
     * Tests that the main constructor creates a daily plan with a plan day.
     */
    @Test
    void constructorRightWithPlanDay() {
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        LocalDate serviceDate = LocalDate.of(2026, 4, 26);
        PlanDay planDay = PlanDay.fromInteger(3);
        Vehicle vehicle = randomVehicle();
        DailyPlan dailyPlan = new DailyPlan(plan, facility, serviceDate, planDay, vehicle);
        assertNotNull(dailyPlan.getId());
        assertEquals(plan, dailyPlan.getInfrastructurePlan());
        assertEquals(facility, dailyPlan.getFacility());
        assertEquals(serviceDate, dailyPlan.getServiceDate());
        assertTrue(dailyPlan.getPlanDay().isPresent());
        assertEquals(planDay, dailyPlan.getPlanDay().get());
        assertEquals(vehicle, dailyPlan.getVehicle());
        assertEquals(zeroKilograms(), dailyPlan.getTotalCollectedKilograms());
        assertEquals(zeroLiters(), dailyPlan.getTotalCollectedLiters());
        assertEquals(zeroDistance(), dailyPlan.getTotalDistanceMeters());
        assertTrue(dailyPlan.getStops().isEmpty());
    }

    /**
     * Tests that the main constructor accepts a missing plan day.
     */
    @Test
    void constructorRightWithoutPlanDay() {
        DailyPlan dailyPlan = new DailyPlan(
                randomInfrastructurePlan(),
                randomFacility(),
                LocalDate.of(2026, 5, 1),
                null,
                randomVehicle());
        assertFalse(dailyPlan.getPlanDay().isPresent());
        assertTrue(dailyPlan.getStops().isEmpty());
    }

    /**
     * Tests that the constructor rejects an undefined infrastructure plan.
     */
    @Test
    void constructorInfrastructurePlanUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DailyPlan(
                        null,
                        randomFacility(),
                        LocalDate.of(2026, 4, 26),
                        PlanDay.fromInteger(1),
                        randomVehicle()));
        assertEquals(DailyPlan.INFRASTRUCTURE_PLAN_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an undefined facility.
     */
    @Test
    void constructorFacilityUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DailyPlan(
                        randomInfrastructurePlan(),
                        null,
                        LocalDate.of(2026, 4, 26),
                        PlanDay.fromInteger(1),
                        randomVehicle()));
        assertEquals(DailyPlan.FACILITY_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an undefined service date.
     */
    @Test
    void constructorServiceDateUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DailyPlan(
                        randomInfrastructurePlan(),
                        randomFacility(),
                        null,
                        PlanDay.fromInteger(1),
                        randomVehicle()));
        assertEquals(DailyPlan.SERVICE_DATE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an undefined vehicle.
     */
    @Test
    void constructorVehicleUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DailyPlan(
                        randomInfrastructurePlan(),
                        randomFacility(),
                        LocalDate.of(2026, 4, 26),
                        PlanDay.fromInteger(1),
                        null));
        assertEquals(DailyPlan.VEHICLE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the copy constructor preserves all daily plan values.
     */
    @Test
    void copyConstructorRight() {
        DailyPlan original = randomDailyPlan();
        original.addStop(randomStop());
        DailyPlan copy = new DailyPlan(original);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getInfrastructurePlan(), copy.getInfrastructurePlan());
        assertEquals(original.getFacility(), copy.getFacility());
        assertEquals(original.getServiceDate(), copy.getServiceDate());
        assertEquals(original.getPlanDay(), copy.getPlanDay());
        assertEquals(original.getVehicle(), copy.getVehicle());
        assertEquals(original.getTotalCollectedKilograms(), copy.getTotalCollectedKilograms());
        assertEquals(original.getTotalCollectedLiters(), copy.getTotalCollectedLiters());
        assertEquals(original.getTotalDistanceMeters(), copy.getTotalDistanceMeters());
        assertEquals(original.getStops().size(), copy.getStops().size());
    }

    /**
     * Tests that copied stops are stored independently from the original list.
     */
    @Test
    void copyConstructorStopsAreIndependent() {
        DailyPlan original = randomDailyPlan();
        original.addStop(randomStop());
        DailyPlan copy = new DailyPlan(original);
        copy.addStop(randomStop());
        assertEquals(1, original.getStops().size());
        assertEquals(2, copy.getStops().size());
    }

    /**
     * Tests that the restore constructor rebuilds a complete daily plan.
     */
    @Test
    void restoreConstructorRight() {
        UUID id = UUID.randomUUID();
        InfrastructurePlan plan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        LocalDate serviceDate = LocalDate.of(2026, 6, 15);
        PlanDay planDay = PlanDay.fromInteger(2);
        Vehicle vehicle = randomVehicle();
        CollectedWeightKilograms kilograms = CollectedWeightKilograms.fromKilograms(150.0);
        CollectedVolumeLiters liters = CollectedVolumeLiters.fromLiters(300.0);
        Distance distance = Distance.fromMeters(1200.0);
        List<Stop> stops = new ArrayList<>();
        stops.add(randomStop());
        DailyPlan dailyPlan = new DailyPlan(
                id, plan, facility, serviceDate, planDay, vehicle,
                kilograms, liters, distance, stops);
        assertEquals(id, dailyPlan.getId());
        assertEquals(plan, dailyPlan.getInfrastructurePlan());
        assertEquals(facility, dailyPlan.getFacility());
        assertEquals(serviceDate, dailyPlan.getServiceDate());
        assertEquals(planDay, dailyPlan.getPlanDay().orElseThrow());
        assertEquals(vehicle, dailyPlan.getVehicle());
        assertEquals(kilograms, dailyPlan.getTotalCollectedKilograms());
        assertEquals(liters, dailyPlan.getTotalCollectedLiters());
        assertEquals(distance, dailyPlan.getTotalDistanceMeters());
        assertEquals(1, dailyPlan.getStops().size());
    }

    /**
     * Tests that the restore constructor initializes null stops as an empty list.
     */
    @Test
    void restoreConstructorNullStopsInitializesEmptyList() {
        DailyPlan dailyPlan = new DailyPlan(
                UUID.randomUUID(),
                randomInfrastructurePlan(),
                randomFacility(),
                LocalDate.of(2026, 4, 26),
                null,
                randomVehicle(),
                zeroKilograms(),
                zeroLiters(),
                zeroDistance(),
                null);
        assertTrue(dailyPlan.getStops().isEmpty());
    }

    /**
     * Tests that the restore constructor rejects undefined collected kilograms.
     */
    @Test
    void restoreConstructorTotalKilogramsUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DailyPlan(
                        UUID.randomUUID(),
                        randomInfrastructurePlan(),
                        randomFacility(),
                        LocalDate.of(2026, 4, 26),
                        null,
                        randomVehicle(),
                        null,
                        zeroLiters(),
                        zeroDistance(),
                        List.of()));
        assertEquals(DailyPlan.TOTAL_KILOGRAMS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the restore constructor rejects undefined collected liters.
     */
    @Test
    void restoreConstructorTotalLitersUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DailyPlan(
                        UUID.randomUUID(),
                        randomInfrastructurePlan(),
                        randomFacility(),
                        LocalDate.of(2026, 4, 26),
                        null,
                        randomVehicle(),
                        zeroKilograms(),
                        null,
                        zeroDistance(),
                        List.of()));
        assertEquals(DailyPlan.TOTAL_LITERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the restore constructor rejects undefined total distance.
     */
    @Test
    void restoreConstructorTotalDistanceUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DailyPlan(
                        UUID.randomUUID(),
                        randomInfrastructurePlan(),
                        randomFacility(),
                        LocalDate.of(2026, 4, 26),
                        null,
                        randomVehicle(),
                        zeroKilograms(),
                        zeroLiters(),
                        null,
                        List.of()));
        assertEquals(DailyPlan.TOTAL_DISTANCE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that a valid stop can be added to a daily plan.
     */
    @Test
    void addStopRight() {
        DailyPlan dailyPlan = randomDailyPlan();
        Stop stop = randomStop();
        dailyPlan.addStop(stop);
        assertEquals(1, dailyPlan.getStops().size());
        assertEquals(stop, dailyPlan.getStops().get(0));
    }

    /**
     * Tests that adding an invalid stop fails.
     */
    @Test
    void addStopInvalid() {
        DailyPlan dailyPlan = randomDailyPlan();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.addStop(null));
        assertEquals(DailyPlan.INVALID_STOP, exception.getMessage());
    }

    /**
     * Tests that the stops collection returned by a daily plan is unmodifiable.
     */
    @Test
    void getStopsIsUnmodifiable() {
        DailyPlan dailyPlan = randomDailyPlan();
        dailyPlan.addStop(randomStop());
        assertThrows(UnsupportedOperationException.class,
                () -> dailyPlan.getStops().add(randomStop()));
    }

    /**
     * Tests that route metrics can be updated with valid values.
     */
    @Test
    void updateRouteMetricsRight() {
        DailyPlan dailyPlan = randomDailyPlan();
        CollectedWeightKilograms kilograms = CollectedWeightKilograms.fromKilograms(80.0);
        CollectedVolumeLiters liters = CollectedVolumeLiters.fromLiters(160.0);
        Distance distance = Distance.fromMeters(2500.0);
        dailyPlan.updateRouteMetrics(kilograms, liters, distance);
        assertEquals(kilograms, dailyPlan.getTotalCollectedKilograms());
        assertEquals(liters, dailyPlan.getTotalCollectedLiters());
        assertEquals(distance, dailyPlan.getTotalDistanceMeters());
    }

    /**
     * Tests that route metric updates reject undefined collected kilograms.
     */
    @Test
    void updateRouteMetricsTotalKilogramsUndefined() {
        DailyPlan dailyPlan = randomDailyPlan();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.updateRouteMetrics(null, zeroLiters(), zeroDistance()));
        assertEquals(DailyPlan.TOTAL_KILOGRAMS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that route metric updates reject undefined collected liters.
     */
    @Test
    void updateRouteMetricsTotalLitersUndefined() {
        DailyPlan dailyPlan = randomDailyPlan();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.updateRouteMetrics(zeroKilograms(), null, zeroDistance()));
        assertEquals(DailyPlan.TOTAL_LITERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that route metric updates reject undefined total distance.
     */
    @Test
    void updateRouteMetricsTotalDistanceUndefined() {
        DailyPlan dailyPlan = randomDailyPlan();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.updateRouteMetrics(zeroKilograms(), zeroLiters(), null));
        assertEquals(DailyPlan.TOTAL_DISTANCE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests daily plan equality for self, null, other type, and different IDs.
     */
    @Test
    void equalsMethod() {
        DailyPlan plan1 = randomDailyPlan();
        DailyPlan plan2 = randomDailyPlan();
        assertTrue(plan1.equals(plan1));
        assertFalse(plan1.equals(null));
        assertFalse(plan1.equals(Integer.valueOf(0)));
        assertNotEquals(plan1, plan2);
    }

    /**
     * Tests that daily plans with the same ID are equal.
     */
    @Test
    void equalsMethodSameId() {
        UUID id = UUID.randomUUID();
        InfrastructurePlan infrastructurePlan = randomInfrastructurePlan();
        Facility facility = randomFacility();
        LocalDate serviceDate = LocalDate.of(2026, 4, 26);
        Vehicle vehicle = randomVehicle();
        DailyPlan plan1 = new DailyPlan(
                id, infrastructurePlan, facility, serviceDate, null, vehicle,
                zeroKilograms(), zeroLiters(), zeroDistance(), List.of());
        DailyPlan plan2 = new DailyPlan(
                id, infrastructurePlan, facility, serviceDate, null, vehicle,
                CollectedWeightKilograms.fromKilograms(50.0),
                CollectedVolumeLiters.fromLiters(100.0),
                Distance.fromMeters(900.0),
                List.of(randomStop()));
        assertEquals(plan1, plan2);
        assertEquals(plan1.hashCode(), plan2.hashCode());
    }

    /**
     * Tests that the hash code is stable for the same daily plan.
     */
    @Test
    void hashCodeMethod() {
        DailyPlan dailyPlan = randomDailyPlan();
        assertNotNull(dailyPlan.hashCode());
        assertEquals(dailyPlan.hashCode(), dailyPlan.hashCode());
    }

    /**
     * Tests that the string representation includes key entity identifiers.
     */
    @Test
    void toStringMethod() {
        DailyPlan dailyPlan = randomDailyPlan();
        String result = dailyPlan.toString();
        assertNotNull(result);
        assertTrue(result.contains(DAILY_PLAN_TO_STRING_PREFIX));
        assertTrue(result.contains(dailyPlan.getId().toString()));
        assertTrue(result.contains(dailyPlan.getInfrastructurePlan().getId().toString()));
        assertTrue(result.contains(dailyPlan.getFacility().getId().toString()));
        assertTrue(result.contains(dailyPlan.getVehicle().getId().toString()));
    }
}
