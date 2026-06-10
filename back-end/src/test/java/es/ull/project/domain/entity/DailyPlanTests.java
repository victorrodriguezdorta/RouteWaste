package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

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

class DailyPlanTests {

    // ========== HELPERS ==========

    private static PlanningPeriod randomPeriod() {
        return new PlanningPeriod("2026");
    }

    private static MaximumBudget randomMaxBudget() {
        return new MaximumBudget(1_000_000.0);
    }

    private static ServicePolicies randomServicePolicies() {
        return new ServicePolicies(5000.0, 60, 100, 1000.0);
    }

    private static InfrastructurePlan randomInfrastructurePlan() {
        return new InfrastructurePlan(
                randomPeriod(),
                randomMaxBudget(),
                randomServicePolicies(),
                null,
                null,
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED);
    }

    private static Location randomLocation() {
        return new Location(
                28.4682 + Math.random() * 0.1,
                -16.2546 + Math.random() * 0.1,
                "Test Address " + ((int) (Math.random() * 1000)),
                "GIS-REF-" + ((int) (Math.random() * 10000)));
    }

    private static Name randomName(String prefix) {
        return new Name(prefix + "-" + ((int) (Math.random() * 10000)));
    }

    private static Facility randomFacility() {
        return new Facility(
                randomName("facility"),
                FacilityType.random(),
                randomLocation(),
                new StorageCapacityKilograms(1000.0),
                new ProcessingCapacityKilogramsPerDay(500.0),
                new UnloadingTime(60),
                new OpeningFixedCost(10000.0),
                FacilityStatus.PLANNED);
    }

    private static Vehicle randomVehicle() {
        return new Vehicle(
                randomName("vehicle"),
                VehicleType.random(),
                new VehicleCapacityKilograms(5000.0),
                new VehicleCapacityLiters(8000.0),
                new TransportationVariableCost(0.75));
    }

    private static Container randomContainer() {
        return new Container(
                randomName("container"),
                randomLocation(),
                WasteType.random(),
                new ContainerCapacityLiters(100.0),
                new DailyWasteDemandLitersPerDay(10.0),
                ServiceZone.random());
    }

    private static CollectedWeightKilograms zeroKilograms() {
        return CollectedWeightKilograms.fromKilograms(0.0);
    }

    private static CollectedVolumeLiters zeroLiters() {
        return CollectedVolumeLiters.fromLiters(0.0);
    }

    private static Distance zeroDistance() {
        return Distance.fromMeters(0.0);
    }

    private static Stop randomStop() {
        return new Stop(
                RouteSequence.of(1),
                randomContainer(),
                CollectedWeightKilograms.fromKilograms(10.0),
                CollectedVolumeLiters.fromLiters(20.0),
                Distance.fromMeters(500.0),
                Distance.fromMeters(500.0));
    }

    private static DailyPlan randomDailyPlan() {
        return new DailyPlan(
                randomInfrastructurePlan(),
                randomFacility(),
                LocalDate.of(2026, 4, 26),
                PlanDay.fromInteger(1),
                randomVehicle());
    }

    // ========== MAIN CONSTRUCTOR ==========

    @Test
    void constructor_right_withPlanDay() {
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

    @Test
    void constructor_right_withoutPlanDay() {
        DailyPlan dailyPlan = new DailyPlan(
                randomInfrastructurePlan(),
                randomFacility(),
                LocalDate.of(2026, 5, 1),
                null,
                randomVehicle());

        assertFalse(dailyPlan.getPlanDay().isPresent());
        assertTrue(dailyPlan.getStops().isEmpty());
    }

    @Test
    void constructor_infrastructurePlan_undefined() {
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

    @Test
    void constructor_facility_undefined() {
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

    @Test
    void constructor_serviceDate_undefined() {
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

    @Test
    void constructor_vehicle_undefined() {
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

    // ========== COPY CONSTRUCTOR ==========

    @Test
    void copyConstructor_right() {
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

    @Test
    void copyConstructor_stopsAreIndependent() {
        DailyPlan original = randomDailyPlan();
        original.addStop(randomStop());

        DailyPlan copy = new DailyPlan(original);
        copy.addStop(randomStop());

        assertEquals(1, original.getStops().size());
        assertEquals(2, copy.getStops().size());
    }

    // ========== RESTORE CONSTRUCTOR ==========

    @Test
    void restoreConstructor_right() {
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

    @Test
    void restoreConstructor_nullStops_initializesEmptyList() {
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

    @Test
    void restoreConstructor_totalKilograms_undefined() {
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

    @Test
    void restoreConstructor_totalLiters_undefined() {
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

    @Test
    void restoreConstructor_totalDistance_undefined() {
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

    // ========== addStop & updateRouteMetrics ==========

    @Test
    void addStop_right() {
        DailyPlan dailyPlan = randomDailyPlan();
        Stop stop = randomStop();

        dailyPlan.addStop(stop);

        assertEquals(1, dailyPlan.getStops().size());
        assertEquals(stop, dailyPlan.getStops().get(0));
    }

    @Test
    void addStop_invalid() {
        DailyPlan dailyPlan = randomDailyPlan();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.addStop(null));

        assertEquals(DailyPlan.INVALID_STOP, exception.getMessage());
    }

    @Test
    void getStops_isUnmodifiable() {
        DailyPlan dailyPlan = randomDailyPlan();
        dailyPlan.addStop(randomStop());

        assertThrows(UnsupportedOperationException.class,
                () -> dailyPlan.getStops().add(randomStop()));
    }

    @Test
    void updateRouteMetrics_right() {
        DailyPlan dailyPlan = randomDailyPlan();
        CollectedWeightKilograms kilograms = CollectedWeightKilograms.fromKilograms(80.0);
        CollectedVolumeLiters liters = CollectedVolumeLiters.fromLiters(160.0);
        Distance distance = Distance.fromMeters(2500.0);

        dailyPlan.updateRouteMetrics(kilograms, liters, distance);

        assertEquals(kilograms, dailyPlan.getTotalCollectedKilograms());
        assertEquals(liters, dailyPlan.getTotalCollectedLiters());
        assertEquals(distance, dailyPlan.getTotalDistanceMeters());
    }

    @Test
    void updateRouteMetrics_totalKilograms_undefined() {
        DailyPlan dailyPlan = randomDailyPlan();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.updateRouteMetrics(null, zeroLiters(), zeroDistance()));

        assertEquals(DailyPlan.TOTAL_KILOGRAMS_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateRouteMetrics_totalLiters_undefined() {
        DailyPlan dailyPlan = randomDailyPlan();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.updateRouteMetrics(zeroKilograms(), null, zeroDistance()));

        assertEquals(DailyPlan.TOTAL_LITERS_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateRouteMetrics_totalDistance_undefined() {
        DailyPlan dailyPlan = randomDailyPlan();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dailyPlan.updateRouteMetrics(zeroKilograms(), zeroLiters(), null));

        assertEquals(DailyPlan.TOTAL_DISTANCE_NOT_DEFINED, exception.getMessage());
    }

    // ========== equals & hashCode ==========

    @Test
    void equalsMethod() {
        DailyPlan plan1 = randomDailyPlan();
        DailyPlan plan2 = randomDailyPlan();

        assertTrue(plan1.equals(plan1));
        assertFalse(plan1.equals(null));
        assertFalse(plan1.equals(Integer.valueOf(0)));
        assertNotEquals(plan1, plan2);
    }

    @Test
    void equalsMethod_sameId() {
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

    @Test
    void hashCodeMethod() {
        DailyPlan dailyPlan = randomDailyPlan();

        assertNotNull(dailyPlan.hashCode());
        assertEquals(dailyPlan.hashCode(), dailyPlan.hashCode());
    }

    // ========== toString ==========

    @Test
    void toStringMethod() {
        DailyPlan dailyPlan = randomDailyPlan();
        String result = dailyPlan.toString();

        assertNotNull(result);
        assertTrue(result.contains("DailyPlan="));
        assertTrue(result.contains(dailyPlan.getId().toString()));
        assertTrue(result.contains(dailyPlan.getInfrastructurePlan().getId().toString()));
        assertTrue(result.contains(dailyPlan.getFacility().getId().toString()));
        assertTrue(result.contains(dailyPlan.getVehicle().getId().toString()));
    }
}
