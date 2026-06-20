package es.ull.project.application.algorithm;

import es.ull.project.adapter.memory.InMemoryContainerDailyStateRepository;
import es.ull.project.adapter.memory.InMemoryContainerRepository;
import es.ull.project.adapter.memory.InMemoryFacilityRepository;
import es.ull.project.adapter.memory.InMemoryInfrastructurePlanRepository;
import es.ull.project.adapter.memory.InMemoryServiceAssignmentRepository;
import es.ull.project.adapter.memory.InMemoryVehicleRepository;
import es.ull.project.application.service.algorithm.CreatePendingInfrastructurePlanService;
import es.ull.project.application.service.algorithm.PersistAlgorithmExecutionResultService;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.StopType;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
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
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.lang.reflect.Field;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class PersistAlgorithmExecutionResultTests {

    private static final String COLLECTION_START_TIME_VALUE = "08:00";
    private static final String CONTAINER_ONE_ADDRESS = "Calle random";
    private static final String CONTAINER_ONE_GIS_REFERENCE = "123414";
    private static final String CONTAINER_ONE_ID_VALUE = "2dd7627e-f357-42e1-b257-2cf1160440d3";
    private static final String CONTAINER_ONE_NAME = "container-seed-1";
    private static final String CONTAINER_TWO_ADDRESS = "adf";
    private static final String CONTAINER_TWO_GIS_REFERENCE = "afdsa";
    private static final String CONTAINER_TWO_ID_VALUE = "374ae62c-1e31-4210-88d3-dbefa4320a72";
    private static final String CONTAINER_TWO_NAME = "container-seed-2";
    private static final String ENTITY_ID_FIELD = "id";
    private static final String EXECUTED_AT_VALUE = "2026-04-29T10:30:32.420542549Z";
    private static final String FACILITY_ADDRESS = "Calle Principal 123, Las Palmas";
    private static final String FACILITY_GIS_REFERENCE = "GIS-REF-001";
    private static final String FACILITY_ID_VALUE = "ce3d2863-eabe-4c6c-a31b-1c3b3ea72038";
    private static final String FACILITY_NAME = "facility-seed";
    private static final String FAILURE_REASON_VALUE = "Docker timeout";
    private static final String PERIOD_VALUE = "2026";
    private static final String REQUEST_SNAPSHOT = "{\"facilityId\":\"ce3d2863-eabe-4c6c-a31b-1c3b3ea72038\"}";
    private static final String VEHICLE_ONE_ID_VALUE = "21703654-95aa-4620-9668-a429fa4b2cf8";
    private static final String VEHICLE_ONE_NAME = "vehicle-seed-1";
    private static final String VEHICLE_TWO_ID_VALUE = "882d413c-18c7-4a66-90ad-fa62f7600b02";
    private static final String VEHICLE_TWO_NAME = "vehicle-seed-2";
    private static final UUID CONTAINER_ONE_ID = UUID.fromString(CONTAINER_ONE_ID_VALUE);
    private static final UUID CONTAINER_TWO_ID = UUID.fromString(CONTAINER_TWO_ID_VALUE);
    private static final UUID FACILITY_ID = UUID.fromString(FACILITY_ID_VALUE);
    private static final UUID VEHICLE_ONE_ID = UUID.fromString(VEHICLE_ONE_ID_VALUE);
    private static final UUID VEHICLE_TWO_ID = UUID.fromString(VEHICLE_TWO_ID_VALUE);

    /**
     * Verifies that an algorithm response creates plan assignments and daily plans.
     *
     * @throws Exception when reflection-based fixture setup fails.
     */
    @Test
    void persistAlgorithmResponseCreatesPlanAssignmentsAndDailyPlans() throws Exception {
        InMemoryInfrastructurePlanRepository infrastructurePlanRepository = new InMemoryInfrastructurePlanRepository();
        InMemoryServiceAssignmentRepository serviceAssignmentRepository = new InMemoryServiceAssignmentRepository();
        TestInMemoryDailyPlanRepository dailyPlanRepository = new TestInMemoryDailyPlanRepository();
        InMemoryContainerDailyStateRepository containerDailyStateRepository = new InMemoryContainerDailyStateRepository();
        InMemoryFacilityRepository facilityRepository = new InMemoryFacilityRepository();
        InMemoryContainerRepository containerRepository = new InMemoryContainerRepository();
        InMemoryVehicleRepository vehicleRepository = new InMemoryVehicleRepository();
        seedInfrastructureEntities(facilityRepository, containerRepository, vehicleRepository);
        PersistAlgorithmExecutionResultService service = createPersistService(
                infrastructurePlanRepository,
                serviceAssignmentRepository,
                dailyPlanRepository,
                containerDailyStateRepository,
                facilityRepository,
                containerRepository,
                vehicleRepository);
        InfrastructurePlan plan = service.persist(
                new AlgorithmJsonPayload(sampleAlgorithmJson()),
                new NumberOfDays(7),
                new AveragePickupTimeMinutes(15),
                null,
                new AlgorithmJsonPayload(REQUEST_SNAPSHOT));
        assertNotNull(plan);
        assertEquals(REQUEST_SNAPSHOT, plan.getExecutionRequestJson().orElse(null));
        assertEquals(InfrastructurePlanValidityState.VALID, plan.getValidityState());
        assertEquals(1, plan.getSelectedFacilities().size());
        assertEquals(1, plan.getServiceAssignments().size());
        assertEquals(2, plan.getDailyPlanIds().size());
        assertEquals(PERIOD_VALUE, plan.getPeriod().getValue());
        assertEquals(new NumberOfDays(7), plan.getNumberOfDays().get());
        assertEquals(new AveragePickupTimeMinutes(15), plan.getAveragePickupTimeMinutes().get());
        assertEquals(new ExecutedAt(EXECUTED_AT_VALUE), plan.getExecutedAt().get());
        assertEquals(2, plan.getContainerDailyStates().size());
        for (ContainerDailyState state : plan.getContainerDailyStates()) {
            assertEquals(plan.getId(), state.getInfrastructurePlanId().orElse(null));
        }
        assertEquals(1, infrastructurePlanRepository.fetchAll().size());
        assertEquals(1, serviceAssignmentRepository.findAll().size());
        assertEquals(2, dailyPlanRepository.findAll().size());
        assertEquals(2, containerDailyStateRepository.findAll().size());
        ServiceAssignment assignment = serviceAssignmentRepository.findAll().iterator().next();
        assertEquals(2, assignment.getAssignedContainers().size());
        assertEquals(FACILITY_ID, assignment.getFacility().getId());
        DailyPlan firstDailyPlan = dailyPlanRepository.findAll().iterator().next();
        assertEquals(3, firstDailyPlan.getStops().size());
        assertEquals(new PlanDay(1), firstDailyPlan.getPlanDay().orElse(null));
        assertEquals(FACILITY_ID, firstDailyPlan.getFacility().getId());
        assertEquals(CONTAINER_ONE_ID, firstDailyPlan.getStops().get(0).getContainer().getId());
        assertEquals(StopType.FACILITY, firstDailyPlan.getStops().get(2).getType());
        assertNull(firstDailyPlan.getStops().get(2).getContainer());
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, plan.getExecutionState());
    }

    /**
     * Verifies that completion updates an existing running plan with the same identifier.
     *
     * @throws Exception when reflection-based fixture setup fails.
     */
    @Test
    void completeUpdatesExistingRunningPlanWithSameId() throws Exception {
        InMemoryInfrastructurePlanRepository infrastructurePlanRepository = new InMemoryInfrastructurePlanRepository();
        InMemoryServiceAssignmentRepository serviceAssignmentRepository = new InMemoryServiceAssignmentRepository();
        TestInMemoryDailyPlanRepository dailyPlanRepository = new TestInMemoryDailyPlanRepository();
        InMemoryContainerDailyStateRepository containerDailyStateRepository = new InMemoryContainerDailyStateRepository();
        InMemoryFacilityRepository facilityRepository = new InMemoryFacilityRepository();
        InMemoryContainerRepository containerRepository = new InMemoryContainerRepository();
        InMemoryVehicleRepository vehicleRepository = new InMemoryVehicleRepository();
        seedInfrastructureEntities(facilityRepository, containerRepository, vehicleRepository);
        CreatePendingInfrastructurePlanService pendingService =
                new CreatePendingInfrastructurePlanService(infrastructurePlanRepository);
        PersistAlgorithmExecutionResultService persistService = createPersistService(
                infrastructurePlanRepository,
                serviceAssignmentRepository,
                dailyPlanRepository,
                containerDailyStateRepository,
                facilityRepository,
                containerRepository,
                vehicleRepository);
        InfrastructurePlan pending = pendingService.createPending(
                new NumberOfDays(7),
                new AveragePickupTimeMinutes(15),
                CollectionStartTime.fromString(COLLECTION_START_TIME_VALUE),
                new AverageTransferTimeMinutes(10),
                GreedyWeights.defaultWeights(),
                new MaximumBudget(1_000_000.0),
                new AlgorithmJsonPayload(REQUEST_SNAPSHOT));
        UUID pendingId = pending.getId();
        InfrastructurePlan completed = persistService.complete(
                pendingId,
                new AlgorithmJsonPayload(sampleAlgorithmJson()),
                new NumberOfDays(7),
                new AveragePickupTimeMinutes(15),
                null,
                new AlgorithmJsonPayload(REQUEST_SNAPSHOT));
        assertEquals(pendingId, completed.getId());
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, completed.getExecutionState());
        assertEquals(1, completed.getSelectedFacilities().size());
        assertEquals(2, completed.getDailyPlanIds().size());
        assertEquals(1, infrastructurePlanRepository.fetchAll().size());
    }

    /**
     * Verifies that execution failure updates the running plan state.
     */
    @Test
    void markExecutionFailedUpdatesRunningPlan() {
        InMemoryInfrastructurePlanRepository infrastructurePlanRepository = new InMemoryInfrastructurePlanRepository();
        CreatePendingInfrastructurePlanService pendingService =
                new CreatePendingInfrastructurePlanService(infrastructurePlanRepository);
        PersistAlgorithmExecutionResultService persistService = createPersistService(
                infrastructurePlanRepository,
                new InMemoryServiceAssignmentRepository(),
                new TestInMemoryDailyPlanRepository(),
                new InMemoryContainerDailyStateRepository(),
                new InMemoryFacilityRepository(),
                new InMemoryContainerRepository(),
                new InMemoryVehicleRepository());
        InfrastructurePlan pending = pendingService.createPending(
                new NumberOfDays(2),
                new AveragePickupTimeMinutes(10),
                CollectionStartTime.fromString(COLLECTION_START_TIME_VALUE),
                new AverageTransferTimeMinutes(10),
                GreedyWeights.defaultWeights(),
                new MaximumBudget(1000.0),
                null);
        InfrastructurePlan failed = persistService.markExecutionFailed(
                pending.getId(),
                new InfrastructurePlanFailureReason(FAILURE_REASON_VALUE));
        assertEquals(InfrastructurePlanExecutionState.FAILED, failed.getExecutionState());
        assertEquals(InfrastructurePlanValidityState.VALID, failed.getValidityState());
        assertFalse(failed.isExecutionRunning());
        assertEquals(FAILURE_REASON_VALUE, failed.getFailureReason().orElseThrow());
    }

    /**
     * Creates the service under test with in-memory repositories.
     *
     * @param infrastructurePlanRepository infrastructure plan repository.
     * @param serviceAssignmentRepository service assignment repository.
     * @param dailyPlanRepository daily plan repository.
     * @param containerDailyStateRepository container daily state repository.
     * @param facilityRepository facility repository.
     * @param containerRepository container repository.
     * @param vehicleRepository vehicle repository.
     * @return configured persistence service.
     */
    private PersistAlgorithmExecutionResultService createPersistService(
            InMemoryInfrastructurePlanRepository infrastructurePlanRepository,
            InMemoryServiceAssignmentRepository serviceAssignmentRepository,
            TestInMemoryDailyPlanRepository dailyPlanRepository,
            InMemoryContainerDailyStateRepository containerDailyStateRepository,
            InMemoryFacilityRepository facilityRepository,
            InMemoryContainerRepository containerRepository,
            InMemoryVehicleRepository vehicleRepository) {
        return new PersistAlgorithmExecutionResultService(
                infrastructurePlanRepository,
                serviceAssignmentRepository,
                dailyPlanRepository,
                containerDailyStateRepository,
                facilityRepository,
                containerRepository,
                vehicleRepository);
    }

    /**
     * Seeds infrastructure repositories with deterministic entities.
     *
     * @param facilityRepository facility repository.
     * @param containerRepository container repository.
     * @param vehicleRepository vehicle repository.
     * @throws Exception when deterministic identifier injection fails.
     */
    private void seedInfrastructureEntities(
            InMemoryFacilityRepository facilityRepository,
            InMemoryContainerRepository containerRepository,
            InMemoryVehicleRepository vehicleRepository) throws Exception {
        Facility facility = new Facility(
                new Name(FACILITY_NAME),
                FacilityType.TRANSFER_STATION,
                new Location(28.47, -16.25, FACILITY_ADDRESS, FACILITY_GIS_REFERENCE),
                new StorageCapacityKilograms(1000.0),
                new ProcessingCapacityKilogramsPerDay(1000.0),
                new UnloadingTime(10),
                new OpeningFixedCost(100.0),
                FacilityStatus.OPEN);
        setEntityId(facility, FACILITY_ID);
        facilityRepository.save(facility);
        Container container1 = new Container(
                new Name(CONTAINER_ONE_NAME),
                new Location(28.465837, -16.263835, CONTAINER_ONE_ADDRESS, CONTAINER_ONE_GIS_REFERENCE),
                WasteType.ORGANIC,
                new ContainerCapacityLiters(1000.0),
                new DailyWasteDemandLitersPerDay(100.0),
                ServiceZone.DISTRICT);
        setEntityId(container1, CONTAINER_ONE_ID);
        containerRepository.save(container1);
        Container container2 = new Container(
                new Name(CONTAINER_TWO_NAME),
                new Location(28.462808, -16.264503, CONTAINER_TWO_ADDRESS, CONTAINER_TWO_GIS_REFERENCE),
                WasteType.ORGANIC,
                new ContainerCapacityLiters(1000.0),
                new DailyWasteDemandLitersPerDay(100.0),
                ServiceZone.DISTRICT);
        setEntityId(container2, CONTAINER_TWO_ID);
        containerRepository.save(container2);
        Vehicle vehicle1 = new Vehicle(
                new Name(VEHICLE_ONE_NAME),
                VehicleType.SUPPORT_VEHICLE,
                new VehicleCapacityKilograms(88),
                new VehicleCapacityLiters(88),
                new TransportationVariableCost(0.03));
        setEntityId(vehicle1, VEHICLE_ONE_ID);
        vehicleRepository.save(vehicle1);
        Vehicle vehicle2 = new Vehicle(
                new Name(VEHICLE_TWO_NAME),
                VehicleType.COLLECTION_TRUCK,
                new VehicleCapacityKilograms(88),
                new VehicleCapacityLiters(88),
                new TransportationVariableCost(0.03));
        setEntityId(vehicle2, VEHICLE_TWO_ID);
        vehicleRepository.save(vehicle2);
    }

    /**
     * Injects a deterministic entity identifier into domain fixtures.
     *
     * @param entity entity whose identifier is replaced.
     * @param id deterministic identifier.
     * @throws Exception when reflection fails.
     */
    private void setEntityId(Object entity, UUID id) throws Exception {
        Field idField = entity.getClass().getDeclaredField(ENTITY_ID_FIELD);
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    /**
     * Builds a representative algorithm response JSON.
     *
     * @return sample algorithm response.
     */
    private String sampleAlgorithmJson() {
        return """
                {
                  "executedAt": "2026-04-29T10:30:32.420542549Z",
                  "totalCollectedKilograms": 0,
                  "totalDistanceMeters": 3544.96,
                  "totalCollectedLiters": 0,
                  "clusters": [
                    {
                      "assignedContainers": [
                        {
                          "serviceZone": "DISTRICT",
                          "capacityLiters": 0,
                          "wasteType": "ORGANIC",
                          "dailyDemandLitersPerDay": 0,
                          "location": {
                            "postalAddress": "Calle random",
                            "latitude": 28.465837,
                            "gisReference": "123414",
                            "longitude": -16.263835
                          },
                          "id": "2dd7627e-f357-42e1-b257-2cf1160440d3"
                        },
                        {
                          "serviceZone": "DISTRICT",
                          "capacityLiters": 0,
                          "wasteType": "ORGANIC",
                          "dailyDemandLitersPerDay": 0,
                          "location": {
                            "postalAddress": "adf",
                            "latitude": 28.462808,
                            "gisReference": "afdsa",
                            "longitude": -16.264503
                          },
                          "id": "374ae62c-1e31-4210-88d3-dbefa4320a72"
                        }
                      ],
                      "facility": {
                        "facilityType": "TRANSFER_STATION",
                        "location": {
                          "postalAddress": "Calle Principal 123, Las Palmas",
                          "latitude": 28.47,
                          "gisReference": "GIS-REF-001",
                          "longitude": -16.25
                        },
                        "id": "ce3d2863-eabe-4c6c-a31b-1c3b3ea72038",
                        "status": "OPEN"
                      }
                    }
                  ],
                  "status": "SUBOPTIMAL",
                  "dailyPlans": [
                    {
                      "facilityId": "ce3d2863-eabe-4c6c-a31b-1c3b3ea72038",
                      "serviceDate": "2026-04-30",
                      "totalCollectedKilograms": 0,
                      "totalDistanceMeters": 1772.48,
                      "totalCollectedLiters": 0,
                      "stops": [
                        {
                          "sequence": 1,
                          "collectedKilograms": 0,
                          "containerId": "2dd7627e-f357-42e1-b257-2cf1160440d3",
                          "collectedLiters": 0,
                          "distanceFromPreviousMeters": 1429.4,
                          "cumulativeDistanceMeters": 1429.4
                        },
                        {
                          "sequence": 2,
                          "collectedKilograms": 0,
                          "containerId": "374ae62c-1e31-4210-88d3-dbefa4320a72",
                          "collectedLiters": 0,
                          "distanceFromPreviousMeters": 343.08,
                          "cumulativeDistanceMeters": 1772.48
                        },
                        {
                          "sequence": 3,
                          "collectedKilograms": 0,
                          "type": "FACILITY",
                          "collectedLiters": 0,
                          "containerId": null,
                          "distanceFromPreviousMeters": 0.0,
                          "cumulativeDistanceMeters": 1772.48,
                          "containerActualLiters": 0
                        }
                      ],
                      "planDay": 1,
                      "vehicle": {
                        "capacityKilograms": 88,
                        "capacityLiters": 88,
                        "costPerKilometer": 9.99999999E8,
                        "id": "21703654-95aa-4620-9668-a429fa4b2cf8",
                        "vehicleType": "SUPPORT_VEHICLE"
                      }
                    },
                    {
                      "facilityId": "ce3d2863-eabe-4c6c-a31b-1c3b3ea72038",
                      "serviceDate": "2026-04-30",
                      "totalCollectedKilograms": 0,
                      "totalDistanceMeters": 1772.48,
                      "totalCollectedLiters": 0,
                      "stops": [
                        {
                          "sequence": 1,
                          "collectedKilograms": 0,
                          "containerId": "2dd7627e-f357-42e1-b257-2cf1160440d3",
                          "collectedLiters": 0,
                          "distanceFromPreviousMeters": 1429.4,
                          "cumulativeDistanceMeters": 1429.4
                        },
                        {
                          "sequence": 2,
                          "collectedKilograms": 0,
                          "containerId": "374ae62c-1e31-4210-88d3-dbefa4320a72",
                          "collectedLiters": 0,
                          "distanceFromPreviousMeters": 343.08,
                          "cumulativeDistanceMeters": 1772.48
                        },
                        {
                          "sequence": 3,
                          "collectedKilograms": 0,
                          "type": "FACILITY",
                          "collectedLiters": 0,
                          "containerId": null,
                          "distanceFromPreviousMeters": 0.0,
                          "cumulativeDistanceMeters": 1772.48,
                          "containerActualLiters": 0
                        }
                      ],
                      "planDay": 1,
                      "vehicle": {
                        "capacityKilograms": 0,
                        "capacityLiters": 0,
                        "costPerKilometer": 0.03,
                        "id": "882d413c-18c7-4a66-90ad-fa62f7600b02",
                        "vehicleType": "COLLECTION_TRUCK"
                      }
                    }
                  ],
                  "containerStateMonitoring": [
                    {
                      "containerId": "2dd7627e-f357-42e1-b257-2cf1160440d3",
                      "planDay": 1,
                      "dailyFillingLiters": 100.0,
                      "containerCapacityLiters": 1000.0,
                      "dailyDemandLitersPerDay": 100.0,
                      "status": "CORRECT"
                    },
                    {
                      "containerId": "374ae62c-1e31-4210-88d3-dbefa4320a72",
                      "planDay": 2,
                      "dailyFillingLiters": 1100.0,
                      "containerCapacityLiters": 1000.0,
                      "dailyDemandLitersPerDay": 100.0,
                      "status": "OVERFLOWED"
                    }
                  ]
                }
                """;
    }
}
