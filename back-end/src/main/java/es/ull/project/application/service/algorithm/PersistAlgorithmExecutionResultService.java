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
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.route.RouteSequence;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import es.ull.project.domain.valueobject.time.PlanDay;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * Service that transforms the raw algorithm response into the infrastructure
 * plan aggregate and persists the resulting documents.
 */
public class PersistAlgorithmExecutionResultService implements PersistAlgorithmExecutionResultUseCase {

	private static final Logger logger = LoggerFactory.getLogger(PersistAlgorithmExecutionResultService.class);

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
	private static final String FIELD_MAX_BUDGET = "maxBudget";
	private static final String FIELD_AMOUNT = "amount";
	private static final String FIELD_CURRENCY = "currency";
	private static final String FIELD_ID = "id";
	private static final String FIELD_LOCATION = "location";
	private static final String FIELD_FACILITY_TYPE = "facilityType";
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_WASTE_TYPE = "wasteType";
	private static final String FIELD_CAPACITY_LITERS_JSON = "capacityLiters";
	private static final String FIELD_DAILY_DEMAND_LITERS = "dailyDemandLitersPerDay";
	private static final String FIELD_SERVICE_ZONE = "serviceZone";
	private static final String FIELD_LATITUDE = "latitude";
	private static final String FIELD_LONGITUDE = "longitude";
	private static final String FIELD_POSTAL_ADDRESS = "postalAddress";
	private static final String FIELD_GIS_REFERENCE = "gisReference";
	private static final String ERR_ALGORITHM_RESPONSE = "Algorithm response is required";
	private static final String ERR_FACILITY_NOT_FOUND = "Facility not found in MongoDB: ";
	private static final String ERR_CONTAINER_NOT_FOUND = "Container not found in MongoDB: ";
	private static final String ERR_VEHICLE_NOT_FOUND = "Vehicle not found in MongoDB: ";

	private final InfrastructurePlanRepository infrastructurePlanRepository;
	private final ServiceAssignmentRepository serviceAssignmentRepository;
	private final DailyPlanRepository dailyPlanRepository;
	private final FacilityRepository facilityRepository;
	private final ContainerRepository containerRepository;
	private final VehicleRepository vehicleRepository;
	private final ObjectMapper objectMapper;

	/**
	 * Constructs a new PersistAlgorithmExecutionResultService.
	 *
	 * @param infrastructurePlanRepository repository for persisting infrastructure plans
	 * @param serviceAssignmentRepository  repository for persisting service assignments
	 * @param dailyPlanRepository          repository for persisting daily plans
	 */
	public PersistAlgorithmExecutionResultService(
			InfrastructurePlanRepository infrastructurePlanRepository,
			ServiceAssignmentRepository serviceAssignmentRepository,
			DailyPlanRepository dailyPlanRepository,
			FacilityRepository facilityRepository,
			ContainerRepository containerRepository,
			VehicleRepository vehicleRepository) {
		this.infrastructurePlanRepository = infrastructurePlanRepository;
		this.serviceAssignmentRepository = serviceAssignmentRepository;
		this.dailyPlanRepository = dailyPlanRepository;
		this.facilityRepository = facilityRepository;
		this.containerRepository = containerRepository;
		this.vehicleRepository = vehicleRepository;
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Persists the result of an algorithm execution into the repository.
	 *
	 * @param algorithmResponse          the raw JSON response from the algorithm
	 * @param numberOfDays               the number of days in the planning period
	 * @param averagePickupTimeMinutes   average pickup time per stop in minutes
	 * @param providedMaxBudget          optional maximum budget override
	 * @return the persisted InfrastructurePlan
	 */
	@Override
	public InfrastructurePlan persist(AlgorithmJsonPayload algorithmResponse, NumberOfDays numberOfDays, AveragePickupTimeMinutes averagePickupTimeMinutes, MaximumBudget providedMaxBudget) {
		logger.info("=== PERSIST START ===");
		if (algorithmResponse == null) {
			throw new IllegalArgumentException(ERR_ALGORITHM_RESPONSE);
		}
		String jsonStr = algorithmResponse.getJson();
		logger.info("Algorithm response JSON size: {} bytes", jsonStr.length());
		logger.debug("Algorithm response JSON:\n{}", jsonStr);

		JsonNode root;
		try {
			root = this.objectMapper.readTree(algorithmResponse.getJson());
		} catch (JsonProcessingException e) {
			logger.error("Failed to parse algorithm JSON response", e);
			throw new IllegalArgumentException(ERR_ALGORITHM_RESPONSE, e);
		}
		logger.info("Successfully parsed JSON. Starting persistence...");
		return this.persistFromNode(root, numberOfDays, averagePickupTimeMinutes, providedMaxBudget);
	}

	/**
	 * Core persistence logic operating on a pre-parsed JsonNode.
	 *
	 * @param algorithmResponse          the pre-parsed JSON response from the algorithm
	 * @param numberOfDays               the number of days in the planning period
	 * @param averagePickupTimeMinutes   average pickup time per stop in minutes
	 * @param providedMaxBudget          optional maximum budget override
	 * @return the persisted InfrastructurePlan
	 */
	private InfrastructurePlan persistFromNode(JsonNode algorithmResponse, NumberOfDays numberOfDays, AveragePickupTimeMinutes averagePickupTimeMinutes, MaximumBudget providedMaxBudget) {
		if (algorithmResponse == null || algorithmResponse.isNull()) {
			throw new IllegalArgumentException(ERR_ALGORITHM_RESPONSE);
		}
		logger.info("Parsing algorithm clusters and daily plans...");

		String executedAt = algorithmResponse.path(FIELD_EXECUTED_AT).asText(null);
		MaximumBudget effectiveMaxBudget = null;
		JsonNode maxBudgetNode = algorithmResponse.path(FIELD_MAX_BUDGET);
		if (maxBudgetNode != null && maxBudgetNode.isObject() && maxBudgetNode.hasNonNull(FIELD_AMOUNT)) {
			double amount = maxBudgetNode.path(FIELD_AMOUNT).asDouble(Double.parseDouble(DEFAULT_BUDGET));
			String currency = maxBudgetNode.path(FIELD_CURRENCY).asText(null);
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
				executedAt != null ? new ExecutedAt(executedAt) : null);
		Map<UUID, Facility> facilitiesById = new LinkedHashMap<>();
		Map<UUID, Container> containersById = new LinkedHashMap<>();
		List<ServiceAssignment> serviceAssignments = new ArrayList<>();
		List<DailyPlan> dailyPlans = new ArrayList<>();
		JsonNode clustersNode = algorithmResponse.get(FIELD_CLUSTERS);
		if (clustersNode != null && clustersNode.isArray()) {
			logger.info("Processing {} clusters...", clustersNode.size());
			for (JsonNode clusterNode : clustersNode) {
				Facility facility = resolveFacility(clusterNode.get(FIELD_FACILITY));
				logger.debug("Processing cluster for facility: {}", facility.getId());
				plan.addFacility(facility);
				facilitiesById.put(facility.getId(), facility);
				List<Container> assignedContainers = resolveContainers(clusterNode.get(FIELD_ASSIGNED_CONTAINERS), containersById);
				if (assignedContainers == null || assignedContainers.isEmpty()) {
					logger.info("Cluster for facility {} has no assigned containers. Skipping ServiceAssignment.", facility.getId());
					// Do not create a ServiceAssignment for empty clusters; they represent facilities
					// that were returned by the algorithm but have no containers assigned.
					continue;
				}
				ServiceAssignment serviceAssignment = new ServiceAssignment(plan, facility, assignedContainers);
				plan.addServiceAssignment(serviceAssignment);
				serviceAssignments.add(serviceAssignment);
			}
			logger.info("Successfully created {} service assignments", serviceAssignments.size());
		} else {
			logger.warn("No clusters found in algorithm response");
		}
		JsonNode dailyPlansNode = algorithmResponse.get(FIELD_DAILY_PLANS);
		if (dailyPlansNode != null && dailyPlansNode.isArray()) {
			logger.info("Processing {} daily plans...", dailyPlansNode.size());
			for (JsonNode dailyPlanNode : dailyPlansNode) {
				UUID facilityId = parseUuid(dailyPlanNode.path(FIELD_FACILITY_ID).asText(), FIELD_FACILITY_ID);
				Facility facility = facilitiesById.get(facilityId);
				if (facility == null) {
					logger.error("Facility not found in algorithm clusters for daily plan: {}", facilityId);
					throw new NoSuchElementException("Facility not found in algorithm clusters: " + facilityId);
				}
				Vehicle vehicle = resolveVehicle(dailyPlanNode.get(FIELD_VEHICLE));
				LocalDate serviceDate = LocalDate.parse(dailyPlanNode.path(FIELD_SERVICE_DATE).asText());
				PlanDay planDay = dailyPlanNode.hasNonNull(FIELD_PLAN_DAY) ? new PlanDay(dailyPlanNode.get(FIELD_PLAN_DAY).asInt()) : null;
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
			logger.info("Successfully created {} daily plans", dailyPlans.size());
		} else {
			logger.warn("No daily plans found in algorithm response");
		}
		plan.updateAlgorithmMetrics(
				CollectedWeightKilograms.fromKilograms(algorithmResponse.path(FIELD_TOTAL_COLLECTED_KILOGRAMS).asDouble(0.0)),
				CollectedVolumeLiters.fromLiters(algorithmResponse.path(FIELD_TOTAL_COLLECTED_LITERS).asDouble(0.0)),
				Distance.fromMeters(algorithmResponse.path(FIELD_TOTAL_DISTANCE_METERS).asDouble(0.0)));
		for (ServiceAssignment serviceAssignment : serviceAssignments) {
			logger.debug("Saving ServiceAssignment for facility: {}", serviceAssignment.getFacility().getId());
			this.serviceAssignmentRepository.save(serviceAssignment);
		}
		logger.info("Saved {} service assignments to MongoDB", serviceAssignments.size());

		for (DailyPlan dailyPlan : dailyPlans) {
			logger.debug("Saving DailyPlan for facility: {} on date: {}", dailyPlan.getFacility().getId(), dailyPlan.getServiceDate());
			this.dailyPlanRepository.save(dailyPlan);
		}
		logger.info("Saved {} daily plans to MongoDB", dailyPlans.size());

		logger.info("Saving InfrastructurePlan...");
		InfrastructurePlan savedPlan = this.infrastructurePlanRepository.save(plan);
		logger.info("=== PERSIST END === InfrastructurePlan saved with ID: {}", savedPlan.getId());
		return savedPlan;
	}

	/**
	 * Resolves the planning period from the algorithm response execution timestamp.
	 *
	 * @param algorithmResponse the raw JSON response from the algorithm
	 * @return the resolved PlanningPeriod
	 */
	private PlanningPeriod resolvePlanningPeriod(JsonNode algorithmResponse) {
		String executedAt = algorithmResponse.path(FIELD_EXECUTED_AT).asText(null);
		if (executedAt != null) {
			try {
				int year = Instant.parse(executedAt).atZone(ZoneOffset.UTC).getYear();
				return new PlanningPeriod(String.valueOf(year));
			} catch (DateTimeParseException ignored) {
				ignored.getMessage();
			}
		}
		return new PlanningPeriod(String.valueOf(LocalDate.now(ZoneOffset.UTC).getYear()));
	}

	/**
	 * Reads a list of Container snapshots from a JSON array node and registers them by ID.
	 *
	 * @param containersNode the JSON array node containing container data
	 * @param containersById map to register each parsed container by its UUID
	 * @return list of parsed Container entities
	 */
	private List<Container> resolveContainers(JsonNode containersNode, Map<UUID, Container> containersById) {
		List<Container> containers = new ArrayList<>();
		if (containersNode == null || !containersNode.isArray()) {
			return containers;
		}
		for (JsonNode containerNode : containersNode) {
			UUID containerId;
			// Handle both textual (UUID string) and object (with "id" field) formats
			if (containerNode.isTextual()) {
				// If the element is a direct UUID string, use it
				String idStr = containerNode.asText();
				try {
					containerId = UUID.fromString(idStr);
				} catch (IllegalArgumentException ex) {
					// malformed UUID: skip this container
					continue;
				}
			} else if (containerNode.isObject()) {
				// If it's an object, extract the "id" field
				String idStr = containerNode.path(FIELD_ID).asText(null);
				if (idStr == null || idStr.isBlank()) {
					// skip containers without an id
					continue;
				}
				try {
					containerId = UUID.fromString(idStr);
				} catch (IllegalArgumentException ex) {
					// malformed UUID: skip this container
					continue;
				}
			} else {
				// skip unrecognized container node type
				continue;
			}
			// If the container does not exist in the DB, skip it instead of failing the whole persistence flow.
			this.containerRepository.findById(containerId).ifPresent(container -> {
				containers.add(container);
				containersById.put(container.getId(), container);
			});
		}
		return containers;
	}

	/**
	 * Reads a list of Stop entities from a JSON array node, resolving each container reference.
	 *
	 * @param stopsNode      the JSON array node containing stop data
	 * @param containersById map of already-parsed containers keyed by UUID
	 * @return list of parsed Stop entities
	 */
	private List<Stop> readStops(JsonNode stopsNode, Map<UUID, Container> containersById) {
		List<Stop> stops = new ArrayList<>();
		if (stopsNode == null || !stopsNode.isArray()) {
			return stops;
		}
		for (JsonNode stopNode : stopsNode) {
			int sequence = stopNode.path(FIELD_SEQUENCE).asInt();
			String containerIdRaw = stopNode.path(FIELD_CONTAINER_ID).asText(null);
			if (containerIdRaw == null || containerIdRaw.isBlank()) {
				// skip stops without a container identifier
				continue;
			}
			UUID containerId;
			try {
				containerId = UUID.fromString(containerIdRaw);
			} catch (IllegalArgumentException ex) {
				// malformed UUID: skip this stop
				continue;
			}
			Container container = containersById.get(containerId);
			if (container == null) {
				Optional<Container> opt = this.containerRepository.findById(containerId);
				if (opt.isPresent()) {
					container = opt.get();
					containersById.put(container.getId(), container);
				} else {
					// container not found in DB: skip this stop rather than failing
					continue;
				}
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

	private Facility resolveFacility(JsonNode facilityNode) {
		// Handle both textual (UUID string) and object (with "id" field) formats
		UUID id;
		if (facilityNode == null) {
			throw new IllegalArgumentException("Facility node is required");
		}
		if (facilityNode.isTextual()) {
			// If the node is a direct UUID string, use it
			id = parseUuid(facilityNode.asText(), FIELD_FACILITY);
		} else if (facilityNode.isObject()) {
			// If it's an object, extract the "id" field
			id = parseUuid(facilityNode.path(FIELD_ID).asText(), FIELD_ID);
		} else {
			throw new IllegalArgumentException("Facility must be either a UUID string or an object with 'id' field");
		}
		return this.facilityRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(ERR_FACILITY_NOT_FOUND + id));
	}

	private Vehicle resolveVehicle(JsonNode vehicleNode) {
		// Handle both textual (UUID string) and object (with "id" field) formats
		UUID id;
		if (vehicleNode == null) {
			throw new IllegalArgumentException("Vehicle node is required");
		}
		if (vehicleNode.isTextual()) {
			// If the node is a direct UUID string, use it
			id = parseUuid(vehicleNode.asText(), FIELD_VEHICLE);
		} else if (vehicleNode.isObject()) {
			// If it's an object, extract the "id" field
			id = parseUuid(vehicleNode.path(FIELD_ID).asText(), FIELD_ID);
		} else {
			throw new IllegalArgumentException("Vehicle must be either a UUID string or an object with 'id' field");
		}
		return this.vehicleRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(ERR_VEHICLE_NOT_FOUND + id));
	}

	/**
	 * Parses a UUID string value, throwing an exception if the value is blank.
	 *
	 * @param value     the UUID string to parse
	 * @param fieldName the field name used in the error message if the value is blank
	 * @return the parsed UUID
	 */
	private UUID parseUuid(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}
		return UUID.fromString(value);
	}
}