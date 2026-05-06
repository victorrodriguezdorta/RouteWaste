package es.ull.project.application.service.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;

class PersistAlgorithmExecutionResultServiceTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void persistAlgorithmResponse_createsPlanAssignmentsAndDailyPlans() throws Exception {
        InMemoryInfrastructurePlanRepository infrastructurePlanRepository = new InMemoryInfrastructurePlanRepository();
        InMemoryServiceAssignmentRepository serviceAssignmentRepository = new InMemoryServiceAssignmentRepository();
        InMemoryDailyPlanRepository dailyPlanRepository = new InMemoryDailyPlanRepository();

        PersistAlgorithmExecutionResultService service = new PersistAlgorithmExecutionResultService(
                infrastructurePlanRepository,
                serviceAssignmentRepository,
                dailyPlanRepository);

        JsonNode response = objectMapper.readTree(sampleAlgorithmJson());
        InfrastructurePlan plan = service.persist(response, 7, 15, null);

        assertNotNull(plan);
        assertEquals(1, plan.getSelectedFacilities().size());
        assertEquals(1, plan.getServiceAssignments().size());
        assertEquals(2, plan.getDailyPlanIds().size());
        assertEquals("2026", plan.getPeriod().getValue());
        assertEquals(Integer.valueOf(7), plan.getNumberOfDays());
        assertEquals(Integer.valueOf(15), plan.getAveragePickupTimeMinutes());
        assertEquals("2026-04-29T10:30:32.420542549Z", plan.getExecutedAt());
        assertEquals(1, infrastructurePlanRepository.saved.size());
        assertEquals(1, serviceAssignmentRepository.saved.size());
        assertEquals(2, dailyPlanRepository.saved.size());

        ServiceAssignment assignment = serviceAssignmentRepository.saved.values().iterator().next();
        assertEquals(2, assignment.getAssignedContainers().size());
        assertEquals(UUID.fromString("ce3d2863-eabe-4c6c-a31b-1c3b3ea72038"), assignment.getFacility().getId());

        DailyPlan firstDailyPlan = dailyPlanRepository.saved.values().iterator().next();
        assertEquals(2, firstDailyPlan.getStops().size());
        assertEquals(Integer.valueOf(1), firstDailyPlan.getPlanDay());
        assertEquals(UUID.fromString("ce3d2863-eabe-4c6c-a31b-1c3b3ea72038"), firstDailyPlan.getFacility().getId());
        assertEquals(UUID.fromString("2dd7627e-f357-42e1-b257-2cf1160440d3"), firstDailyPlan.getStops().get(0).getContainer().getId());
    }

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
                  ]
                }
                """;
    }

    private static final class InMemoryInfrastructurePlanRepository implements InfrastructurePlanRepository {
        private final Map<UUID, InfrastructurePlan> saved = new LinkedHashMap<>();

        @Override
        public void delete(InfrastructurePlan entity) {
            if (entity != null) {
                saved.remove(entity.getId());
            }
        }

        @Override
        public List<InfrastructurePlan> fetchAll() {
            return new ArrayList<>(saved.values());
        }

        @Override
        public List<InfrastructurePlan> findAll() {
            return fetchAll();
        }

        @Override
        public Page<InfrastructurePlan> findAll(Pageable pageable) {
          List<InfrastructurePlan> plans = new ArrayList<>(saved.values());
          int start = (int) pageable.getOffset();
          int end = Math.min(start + pageable.getPageSize(), plans.size());
          List<InfrastructurePlan> pageContent = start >= plans.size() ? List.of() : plans.subList(start, end);
          return new PageImpl<>(pageContent, pageable, plans.size());
        }

        @Override
        public InfrastructurePlan save(InfrastructurePlan entity) {
            saved.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<InfrastructurePlan> findById(UUID id) {
            return Optional.ofNullable(saved.get(id));
        }
    }

    private static final class InMemoryServiceAssignmentRepository implements ServiceAssignmentRepository {
        private final Map<UUID, ServiceAssignment> saved = new LinkedHashMap<>();

        @Override
        public void delete(ServiceAssignment entity) {
            if (entity != null) {
                saved.remove(entity.getId());
            }
        }

        @Override
        public List<ServiceAssignment> fetchAll() {
            return new ArrayList<>(saved.values());
        }

        @Override
        public List<ServiceAssignment> findAll() {
            return fetchAll();
        }

        @Override
        public ServiceAssignment save(ServiceAssignment entity) {
            saved.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<ServiceAssignment> findById(UUID id) {
            return Optional.ofNullable(saved.get(id));
        }
    }

    private static final class InMemoryDailyPlanRepository implements DailyPlanRepository {
        private final Map<UUID, DailyPlan> saved = new LinkedHashMap<>();

        @Override
        public DailyPlan save(DailyPlan entity) {
            saved.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public void delete(DailyPlan entity) {
            if (entity != null) {
                saved.remove(entity.getId());
            }
        }

        @Override
        public Optional<DailyPlan> findById(UUID id) {
            return Optional.ofNullable(saved.get(id));
        }

        @Override
        public List<DailyPlan> findByInfrastructurePlanId(UUID infrastructurePlanId) {
            List<DailyPlan> plans = new ArrayList<>();
            for (DailyPlan plan : saved.values()) {
                if (plan.getInfrastructurePlan().getId().equals(infrastructurePlanId)) {
                    plans.add(plan);
                }
            }
            return plans;
        }
    }
}