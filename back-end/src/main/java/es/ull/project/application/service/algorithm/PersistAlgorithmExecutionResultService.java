package es.ull.project.application.service.algorithm;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
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
import es.ull.project.domain.valueobject.route.RouteSequence;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * Service that transforms the raw algorithm response into the infrastructure
 * plan aggregate and persists the resulting documents.
 */
public class PersistAlgorithmExecutionResultService implements PersistAlgorithmExecutionResultUseCase {

	private static final String FIELD_EXECUTED_AT = "executedAt";
	private static final String FIELD_CLUSTERS = "clusters";
	private static final String FIELD_DAILY_PLANS = "dailyPlans";
	private static final String FIELD_FACILITY = "facility";
	private static final String FIELD_ASSIGNED_CONTAINERS = "assignedContainers";
	private static final String FIELD_STOPS = "stops";
	private static final String FIELD_FACILITY_ID = "facilityId";
	private static final String FIELD_PLAN_DAY = "planDay";
	private static final String FIELD_SERVICE_DATE = "serviceDate";
	private static final String FIELD_TOTAL_COLLECTED_KILOGRAMS = "totalCollectedKilograms";
	private static final String FIELD_TOTAL_COLLECTED_LITERS = "totalCollectedLiters";
	private static final String FIELD_TOTAL_DISTANCE_METERS = "totalDistanceMeters";
	private static final String FIELD_SEQUENCE = "sequence";
	private static final String FIELD_CONTAINER_ID = "containerId";
	private static final String FIELD_COLLECTED_KILOGRAMS = "collectedKilograms";
	private static final String FIELD_COLLECTED_LITERS = "collectedLiters";
	private static final String FIELD_DISTANCE_FROM_PREVIOUS_METERS = "distanceFromPreviousMeters";
	private static final String FIELD_CUMULATIVE_DISTANCE_METERS = "cumulativeDistanceMeters";
	private static final String FIELD_VEHICLE = "vehicle";
	private static final String FIELD_VEHICLE_TYPE = "vehicleType";
	private static final String FIELD_CAPACITY_KILOGRAMS = "capacityKilograms";
	private static final String FIELD_CAPACITY_LITERS = "capacityLiters";
	private static final String FIELD_COST_PER_KILOMETER = "costPerKilometer";
	private static final String DEFAULT_BUDGET = "1.7976931348623157E308";

	private final InfrastructurePlanRepository infrastructurePlanRepository;
	private final ServiceAssignmentRepository serviceAssignmentRepository;
	private final DailyPlanRepository dailyPlanRepository;

	public PersistAlgorithmExecutionResultService(
			InfrastructurePlanRepository infrastructurePlanRepository,
			ServiceAssignmentRepository serviceAssignmentRepository,
			DailyPlanRepository dailyPlanRepository) {
		this.infrastructurePlanRepository = infrastructurePlanRepository;
		this.serviceAssignmentRepository = serviceAssignmentRepository;
		this.dailyPlanRepository = dailyPlanRepository;
	}

	@Override
	public InfrastructurePlan persist(JsonNode algorithmResponse, Integer numberOfDays, Integer averagePickupTimeMinutes, MaximumBudget providedMaxBudget) {
		if (algorithmResponse == null || algorithmResponse.isNull()) {
			throw new IllegalArgumentException("Algorithm response is required");
		}

		String executedAt = algorithmResponse.path(FIELD_EXECUTED_AT).asText(null);

		// Resolve effective max budget: prefer algorithm response, then provided max budget, then default
		MaximumBudget effectiveMaxBudget = null;
		JsonNode maxBudgetNode = algorithmResponse.path("maxBudget");
		if (maxBudgetNode != null && maxBudgetNode.isObject() && maxBudgetNode.hasNonNull("amount")) {
			double amount = maxBudgetNode.path("amount").asDouble(Double.parseDouble(DEFAULT_BUDGET));
			String currency = maxBudgetNode.path("currency").asText(null);
			if (currency != null && !currency.isBlank()) {
				effectiveMaxBudget = new MaximumBudget(amount, currency);
			} else {
				effectiveMaxBudget = new MaximumBudget(amount);
			}
		} else if (providedMaxBudget != null) {
			effectiveMaxBudget = providedMaxBudget;
		} else {
			effectiveMaxBudget = new MaximumBudget(Double.parseDouble(DEFAULT_BUDGET));
		}

		InfrastructurePlan plan = new InfrastructurePlan(
				resolvePlanningPeriod(algorithmResponse),
				effectiveMaxBudget,
				null,
				numberOfDays,
				averagePickupTimeMinutes,
				executedAt);

		Map<UUID, Facility> facilitiesById = new LinkedHashMap<>();
		Map<UUID, Container> containersById = new LinkedHashMap<>();
		List<ServiceAssignment> serviceAssignments = new ArrayList<>();
		List<DailyPlan> dailyPlans = new ArrayList<>();

		JsonNode clustersNode = algorithmResponse.get(FIELD_CLUSTERS);
		if (clustersNode != null && clustersNode.isArray()) {
			for (JsonNode clusterNode : clustersNode) {
				Facility facility = readFacilitySnapshot(clusterNode.get(FIELD_FACILITY));
				plan.addFacility(facility);
				facilitiesById.put(facility.getId(), facility);

				List<Container> assignedContainers = readContainers(clusterNode.get(FIELD_ASSIGNED_CONTAINERS), containersById);
				ServiceAssignment serviceAssignment = new ServiceAssignment(plan, facility, assignedContainers);
				plan.addServiceAssignment(serviceAssignment);
				serviceAssignments.add(serviceAssignment);
			}
		}

		JsonNode dailyPlansNode = algorithmResponse.get(FIELD_DAILY_PLANS);
		if (dailyPlansNode != null && dailyPlansNode.isArray()) {
			for (JsonNode dailyPlanNode : dailyPlansNode) {
				UUID facilityId = parseUuid(dailyPlanNode.path(FIELD_FACILITY_ID).asText(), FIELD_FACILITY_ID);
				Facility facility = facilitiesById.get(facilityId);
				if (facility == null) {
					throw new NoSuchElementException("Facility not found in algorithm clusters: " + facilityId);
				}

				Vehicle vehicle = readVehicleSnapshot(dailyPlanNode.get(FIELD_VEHICLE));
				LocalDate serviceDate = LocalDate.parse(dailyPlanNode.path(FIELD_SERVICE_DATE).asText());
				Integer planDay = dailyPlanNode.hasNonNull(FIELD_PLAN_DAY) ? dailyPlanNode.get(FIELD_PLAN_DAY).asInt() : null;
				CollectedWeightKilograms totalCollectedKilograms = CollectedWeightKilograms.fromKilograms(
						dailyPlanNode.path(FIELD_TOTAL_COLLECTED_KILOGRAMS).asDouble(0.0));
				CollectedVolumeLiters totalCollectedLiters = CollectedVolumeLiters.fromLiters(
						dailyPlanNode.path(FIELD_TOTAL_COLLECTED_LITERS).asDouble(0.0));
				Distance totalDistanceMeters = Distance.fromMeters(
						dailyPlanNode.path(FIELD_TOTAL_DISTANCE_METERS).asDouble(0.0));
				List<Stop> stops = readStops(dailyPlanNode.get(FIELD_STOPS), containersById);

				DailyPlan dailyPlan = new DailyPlan(
						plan,
						facility,
						serviceDate,
						planDay,
						vehicle,
						totalCollectedKilograms,
						totalCollectedLiters,
						totalDistanceMeters,
						stops);
				plan.addDailyPlan(dailyPlan);
				dailyPlans.add(dailyPlan);
			}
		}

		plan.updateAlgorithmMetrics(
				CollectedWeightKilograms.fromKilograms(algorithmResponse.path(FIELD_TOTAL_COLLECTED_KILOGRAMS).asDouble(0.0)),
				CollectedVolumeLiters.fromLiters(algorithmResponse.path(FIELD_TOTAL_COLLECTED_LITERS).asDouble(0.0)),
				Distance.fromMeters(algorithmResponse.path(FIELD_TOTAL_DISTANCE_METERS).asDouble(0.0)));

		for (ServiceAssignment serviceAssignment : serviceAssignments) {
			this.serviceAssignmentRepository.save(serviceAssignment);
		}
		for (DailyPlan dailyPlan : dailyPlans) {
			this.dailyPlanRepository.save(dailyPlan);
		}

		return this.infrastructurePlanRepository.save(plan);
	}

	private PlanningPeriod resolvePlanningPeriod(JsonNode algorithmResponse) {
		String executedAt = algorithmResponse.path(FIELD_EXECUTED_AT).asText(null);
		if (executedAt != null) {
			try {
				int year = Instant.parse(executedAt).atZone(ZoneOffset.UTC).getYear();
				return new PlanningPeriod(String.valueOf(year));
			} catch (DateTimeParseException ignored) {
				// Fall back to the current year when the algorithm timestamp cannot be parsed.
			}
		}
		return new PlanningPeriod(String.valueOf(LocalDate.now(ZoneOffset.UTC).getYear()));
	}

	private List<Container> readContainers(JsonNode containersNode, Map<UUID, Container> containersById) {
		List<Container> containers = new ArrayList<>();
		if (containersNode == null || !containersNode.isArray()) {
			return containers;
		}
		for (JsonNode containerNode : containersNode) {
			Container container = readContainerSnapshot(containerNode);
			containers.add(container);
			containersById.put(container.getId(), container);
		}
		return containers;
	}

	private List<Stop> readStops(JsonNode stopsNode, Map<UUID, Container> containersById) {
		List<Stop> stops = new ArrayList<>();
		if (stopsNode == null || !stopsNode.isArray()) {
			return stops;
		}
		for (JsonNode stopNode : stopsNode) {
			int sequence = stopNode.path(FIELD_SEQUENCE).asInt();
			UUID containerId = parseUuid(stopNode.path(FIELD_CONTAINER_ID).asText(), FIELD_CONTAINER_ID);
			Container container = containersById.get(containerId);
			if (container == null) {
				throw new NoSuchElementException("Container not found in algorithm clusters: " + containerId);
			}
			Stop stop = new Stop(
					RouteSequence.of(sequence),
					container,
					CollectedWeightKilograms.fromKilograms(stopNode.path(FIELD_COLLECTED_KILOGRAMS).asDouble(0.0)),
					CollectedVolumeLiters.fromLiters(stopNode.path(FIELD_COLLECTED_LITERS).asDouble(0.0)),
					Distance.fromMeters(stopNode.path(FIELD_DISTANCE_FROM_PREVIOUS_METERS).asDouble(0.0)),
					Distance.fromMeters(stopNode.path(FIELD_CUMULATIVE_DISTANCE_METERS).asDouble(0.0)));
			stops.add(stop);
		}
		return stops;
	}

	private Facility readFacilitySnapshot(JsonNode facilityNode) {
		if (facilityNode == null || facilityNode.isNull()) {
			throw new IllegalArgumentException("Facility snapshot is required");
		}

		UUID id = parseUuid(facilityNode.path("id").asText(), "id");
		Location location = readLocation(facilityNode.path("location"));
		FacilityType facilityType = FacilityType.fromString(facilityNode.path("facilityType").asText());
		FacilityStatus status = facilityNode.hasNonNull("status")
				? FacilityStatus.fromString(facilityNode.path("status").asText())
				: FacilityStatus.OPEN;

		return new Facility(
				id,
				facilityType,
				location,
				new StorageCapacityKilograms(0.0),
				new ProcessingCapacityKilogramsPerDay(0.0),
				new UnloadingTime(1),
				new OpeningFixedCost(0.01),
				status,
				new DailyWasteDemandLitersPerDay(0.0));
	}

	private Container readContainerSnapshot(JsonNode containerNode) {
		if (containerNode == null || containerNode.isNull()) {
			throw new IllegalArgumentException("Container snapshot is required");
		}

		UUID id = parseUuid(containerNode.path("id").asText(), "id");
		Location location = readLocation(containerNode.path("location"));
		WasteType wasteType = WasteType.fromString(containerNode.path("wasteType").asText());
		double capacityLiters = containerNode.path("capacityLiters").asDouble(0.0);
		double dailyDemandLitersPerDay = containerNode.path("dailyDemandLitersPerDay").asDouble(0.0);
		ServiceZone serviceZone = containerNode.hasNonNull("serviceZone")
				? ServiceZone.fromString(containerNode.path("serviceZone").asText())
				: null;

		return new Container(
				id,
				location,
				wasteType,
				new ContainerCapacityLiters(capacityLiters),
				new DailyWasteDemandLitersPerDay(dailyDemandLitersPerDay),
				serviceZone);
	}

	private Vehicle readVehicleSnapshot(JsonNode vehicleNode) {
		if (vehicleNode == null || vehicleNode.isNull()) {
			throw new IllegalArgumentException("Vehicle snapshot is required");
		}

		UUID id = parseUuid(vehicleNode.path("id").asText(), "id");
		VehicleType vehicleType = VehicleType.fromString(vehicleNode.path(FIELD_VEHICLE_TYPE).asText());
		double capacityKilograms = vehicleNode.path(FIELD_CAPACITY_KILOGRAMS).asDouble(0.0);
		double capacityLiters = vehicleNode.path(FIELD_CAPACITY_LITERS).asDouble(0.0);
		double costPerKilometer = vehicleNode.path(FIELD_COST_PER_KILOMETER).asDouble(0.0);

		return new Vehicle(
				id,
				vehicleType,
				new VehicleCapacityKilograms(capacityKilograms),
				new VehicleCapacityLiters(capacityLiters),
				new TransportationVariableCost(costPerKilometer));
	}

	private Location readLocation(JsonNode locationNode) {
		if (locationNode == null || locationNode.isNull()) {
			throw new IllegalArgumentException("Location snapshot is required");
		}

		return new Location(
				locationNode.path("latitude").asDouble(),
				locationNode.path("longitude").asDouble(),
				locationNode.path("postalAddress").asText(null),
				locationNode.path("gisReference").asText(null));
	}

	private UUID parseUuid(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}
		return UUID.fromString(value);
	}
}