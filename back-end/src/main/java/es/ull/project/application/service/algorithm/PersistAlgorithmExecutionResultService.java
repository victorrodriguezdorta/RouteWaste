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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ull.project.application.repository.ContainerDailyStateRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.enumerate.StopType;
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
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_CONTAINER_ID = "containerId";
	private static final String FIELD_COLLECTED_KILOGRAMS = "collectedKilograms";
	private static final String FIELD_COLLECTED_LITERS = "collectedLiters";
	private static final String FIELD_DISTANCE_FROM_PREVIOUS_METERS = "distanceFromPreviousMeters";
	private static final String FIELD_CUMULATIVE_DISTANCE_METERS = "cumulativeDistanceMeters";
	private static final String FIELD_VEHICLE = "vehicle";
	private static final String FIELD_CONTAINER_STATE_MONITORING = "containerStateMonitoring";
	private static final String FIELD_DAILY_FILLING_LITERS = "dailyFillingLiters";
	private static final String FIELD_CONTAINER_CAPACITY_LITERS = "containerCapacityLiters";
	private static final String FIELD_DAILY_DEMAND_LITERS_PER_DAY = "dailyDemandLitersPerDay";
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_MAX_BUDGET = "maxBudget";
	private static final String FIELD_AMOUNT = "amount";
	private static final String FIELD_CURRENCY = "currency";
	private static final String FIELD_ID = "id";
	private static final String DEFAULT_BUDGET = "1.7976931348623157E308";
	private static final String ERR_ALGORITHM_RESPONSE = "Algorithm response is required";
	private static final String ERR_FACILITY_NOT_FOUND = "Facility not found in MongoDB: ";
	private static final String ERR_VEHICLE_NOT_FOUND = "Vehicle not found in MongoDB: ";
	private static final String ERR_FACILITY_NODE_REQUIRED = "Facility node is required";
	private static final String ERR_FACILITY_INVALID_FORMAT = "Facility must be either a UUID string or an object with 'id' field";
	private static final String ERR_VEHICLE_NODE_REQUIRED = "Vehicle node is required";
	private static final String ERR_VEHICLE_INVALID_FORMAT = "Vehicle must be either a UUID string or an object with 'id' field";

	private final InfrastructurePlanRepository infrastructurePlanRepository;
	private final ServiceAssignmentRepository serviceAssignmentRepository;
	private final DailyPlanRepository dailyPlanRepository;
	private final ContainerDailyStateRepository containerDailyStateRepository;
	private final FacilityRepository facilityRepository;
	private final ContainerRepository containerRepository;
	private final VehicleRepository vehicleRepository;

	/**
	 * Constructs a new PersistAlgorithmExecutionResultService.
	 *
	 * @param infrastructurePlanRepository repository for persisting infrastructure plans
	 * @param serviceAssignmentRepository  repository for persisting service assignments
	 * @param dailyPlanRepository          repository for persisting daily plans
	 * @param facilityRepository           repository for accessing facilities
	 * @param containerRepository          repository for accessing containers
	 * @param vehicleRepository            repository for accessing vehicles
	 */
	public PersistAlgorithmExecutionResultService(
			InfrastructurePlanRepository infrastructurePlanRepository,
			ServiceAssignmentRepository serviceAssignmentRepository,
			DailyPlanRepository dailyPlanRepository,
			ContainerDailyStateRepository containerDailyStateRepository,
			FacilityRepository facilityRepository,
			ContainerRepository containerRepository,
			VehicleRepository vehicleRepository) {
		this.infrastructurePlanRepository = infrastructurePlanRepository;
		this.serviceAssignmentRepository = serviceAssignmentRepository;
		this.dailyPlanRepository = dailyPlanRepository;
		this.containerDailyStateRepository = containerDailyStateRepository;
		this.facilityRepository = facilityRepository;
		this.containerRepository = containerRepository;
		this.vehicleRepository = vehicleRepository;
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
	public InfrastructurePlan persist(AlgorithmJsonPayload algorithmResponse, NumberOfDays numberOfDays, AveragePickupTimeMinutes averagePickupTimeMinutes, MaximumBudget providedMaxBudget, String executionRequestJson) {
		logger.info("=== PERSIST START ===");
		if (algorithmResponse == null) {
			throw new IllegalArgumentException(ERR_ALGORITHM_RESPONSE);
		}
		String jsonStr = algorithmResponse.getJson();
		logger.info("Algorithm response JSON size: {} bytes", jsonStr.length());
		logger.debug("Algorithm response JSON:\n{}", jsonStr);
		JSONObject root;
		try {
			root = new JSONObject(jsonStr);
		} catch (JSONException e) {
			logger.error("Failed to parse algorithm JSON response", e);
			throw new IllegalArgumentException(ERR_ALGORITHM_RESPONSE, e);
		}
		logger.info("Successfully parsed JSON. Starting persistence...");
		return this.persistFromNode(root, numberOfDays, averagePickupTimeMinutes, providedMaxBudget, executionRequestJson);
	}

	/**
	 * Core persistence logic operating on a pre-parsed JSONObject.
	 *
	 * @param algorithmResponse          the pre-parsed JSON response from the algorithm
	 * @param numberOfDays               the number of days in the planning period
	 * @param averagePickupTimeMinutes   average pickup time per stop in minutes
	 * @param providedMaxBudget          optional maximum budget override
	 * @param executionRequestJson       client request JSON snapshot (optional)
	 * @return the persisted InfrastructurePlan
	 */
	private InfrastructurePlan persistFromNode(JSONObject algorithmResponse, NumberOfDays numberOfDays, AveragePickupTimeMinutes averagePickupTimeMinutes, MaximumBudget providedMaxBudget, String executionRequestJson) {
		if (algorithmResponse == null) {
			throw new IllegalArgumentException(ERR_ALGORITHM_RESPONSE);
		}
		logger.info("Parsing algorithm clusters and daily plans...");
		String executedAt = algorithmResponse.has(FIELD_EXECUTED_AT) && !algorithmResponse.isNull(FIELD_EXECUTED_AT) ? algorithmResponse.getString(FIELD_EXECUTED_AT) : null;
		MaximumBudget effectiveMaxBudget = null;
		JSONObject maxBudgetNode = algorithmResponse.optJSONObject(FIELD_MAX_BUDGET);
		if (maxBudgetNode != null && maxBudgetNode.has(FIELD_AMOUNT) && !maxBudgetNode.isNull(FIELD_AMOUNT)) {
			double amount = maxBudgetNode.optDouble(FIELD_AMOUNT, Double.parseDouble(DEFAULT_BUDGET));
			String currency = maxBudgetNode.has(FIELD_CURRENCY) && !maxBudgetNode.isNull(FIELD_CURRENCY) ? maxBudgetNode.getString(FIELD_CURRENCY) : null;
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
		JSONArray clustersNode = algorithmResponse.optJSONArray(FIELD_CLUSTERS);
		if (clustersNode != null) {
			logger.info("Processing {} clusters...", clustersNode.length());
			for (int i = 0; i < clustersNode.length(); i++) {
				JSONObject clusterNode = clustersNode.getJSONObject(i);
				Facility facility = resolveFacility(clusterNode.opt(FIELD_FACILITY));
				logger.debug("Processing cluster for facility: {}", facility.getId());
				plan.addFacility(facility);
				facilitiesById.put(facility.getId(), facility);
				List<Container> assignedContainers = resolveContainers(clusterNode.optJSONArray(FIELD_ASSIGNED_CONTAINERS), containersById);
				if (assignedContainers == null || assignedContainers.isEmpty()) {
					logger.info("Cluster for facility {} has no assigned containers. Skipping ServiceAssignment.", facility.getId());
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
		JSONArray dailyPlansNode = algorithmResponse.optJSONArray(FIELD_DAILY_PLANS);
		if (dailyPlansNode != null) {
			logger.info("Processing {} daily plans...", dailyPlansNode.length());
			for (int i = 0; i < dailyPlansNode.length(); i++) {
				JSONObject dailyPlanNode = dailyPlansNode.getJSONObject(i);
				String facilityIdRaw = dailyPlanNode.has(FIELD_FACILITY_ID) && !dailyPlanNode.isNull(FIELD_FACILITY_ID) ? dailyPlanNode.getString(FIELD_FACILITY_ID) : null;
				UUID facilityId = parseUuid(facilityIdRaw, FIELD_FACILITY_ID);
				Facility facility = facilitiesById.get(facilityId);
				if (facility == null) {
					logger.error("Facility not found in algorithm clusters for daily plan: {}", facilityId);
					throw new NoSuchElementException("Facility not found in algorithm clusters: " + facilityId);
				}
				Vehicle vehicle = resolveVehicle(dailyPlanNode.opt(FIELD_VEHICLE));
				LocalDate serviceDate = LocalDate.parse(dailyPlanNode.getString(FIELD_SERVICE_DATE));
				PlanDay planDay = dailyPlanNode.has(FIELD_PLAN_DAY) && !dailyPlanNode.isNull(FIELD_PLAN_DAY) ? new PlanDay(dailyPlanNode.getInt(FIELD_PLAN_DAY)) : null;
				CollectedWeightKilograms totalCollectedKilograms = CollectedWeightKilograms.fromKilograms(
						dailyPlanNode.optDouble(FIELD_TOTAL_COLLECTED_KILOGRAMS, 0.0));
				CollectedVolumeLiters totalCollectedLiters = CollectedVolumeLiters.fromLiters(
						dailyPlanNode.optDouble(FIELD_TOTAL_COLLECTED_LITERS, 0.0));
				Distance totalDistanceMeters = Distance.fromMeters(
						dailyPlanNode.optDouble(FIELD_TOTAL_DISTANCE_METERS, 0.0));
				List<Stop> stops = readStops(dailyPlanNode.optJSONArray(FIELD_STOPS), containersById);
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
		JSONArray containerStateMonitoringNode = algorithmResponse.optJSONArray(FIELD_CONTAINER_STATE_MONITORING);
		List<ContainerDailyState> containerDailyStates = readContainerDailyStates(containerStateMonitoringNode);
		for (ContainerDailyState containerDailyState : containerDailyStates) {
			plan.addContainerDailyState(containerDailyState);
			if (this.containerDailyStateRepository != null) {
				this.containerDailyStateRepository.save(containerDailyState);
			}
		}
		plan.updateAlgorithmMetrics(
				CollectedWeightKilograms.fromKilograms(algorithmResponse.optDouble(FIELD_TOTAL_COLLECTED_KILOGRAMS, 0.0)),
				CollectedVolumeLiters.fromLiters(algorithmResponse.optDouble(FIELD_TOTAL_COLLECTED_LITERS, 0.0)),
				Distance.fromMeters(algorithmResponse.optDouble(FIELD_TOTAL_DISTANCE_METERS, 0.0)));
		if (executionRequestJson != null && !executionRequestJson.isBlank()) {
			plan.assignExecutionRequestSnapshot(executionRequestJson);
		}
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
		logger.info("Saved {} container daily states to MongoDB", containerDailyStates.size());
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
	private PlanningPeriod resolvePlanningPeriod(JSONObject algorithmResponse) {
		String executedAt = algorithmResponse.has(FIELD_EXECUTED_AT) && !algorithmResponse.isNull(FIELD_EXECUTED_AT) ? algorithmResponse.getString(FIELD_EXECUTED_AT) : null;
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
	private List<Container> resolveContainers(JSONArray containersNode, Map<UUID, Container> containersById) {
		List<Container> containers = new ArrayList<>();
		if (containersNode == null) {
			return containers;
		}
		for (int i = 0; i < containersNode.length(); i++) {
			Object containerNode = containersNode.get(i);
			UUID containerId = null;
			if (containerNode instanceof String) {
				String idStr = (String) containerNode;
				try {
					containerId = UUID.fromString(idStr);
				} catch (IllegalArgumentException ex) {
					continue;
				}
			} else if (containerNode instanceof JSONObject) {
				JSONObject obj = (JSONObject) containerNode;
				String idStr = obj.has(FIELD_ID) && !obj.isNull(FIELD_ID) ? obj.getString(FIELD_ID) : null;
				if (idStr == null || idStr.isBlank()) {
					continue;
				}
				try {
					containerId = UUID.fromString(idStr);
				} catch (IllegalArgumentException ex) {
					continue;
				}
			} else {
				continue;
			}
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
	private List<Stop> readStops(JSONArray stopsNode, Map<UUID, Container> containersById) {
		List<Stop> stops = new ArrayList<>();
		if (stopsNode == null) {
			return stops;
		}
		for (int i = 0; i < stopsNode.length(); i++) {
			JSONObject stopNode = stopsNode.getJSONObject(i);
			int sequence = stopNode.getInt(FIELD_SEQUENCE);
			StopType stopType = stopNode.has(FIELD_TYPE) && !stopNode.isNull(FIELD_TYPE)
					? StopType.fromString(stopNode.getString(FIELD_TYPE))
					: StopType.CONTAINER;
			String containerIdRaw = stopNode.has(FIELD_CONTAINER_ID) && !stopNode.isNull(FIELD_CONTAINER_ID) ? stopNode.getString(FIELD_CONTAINER_ID) : null;
			Container container = null;
			if (stopType == StopType.CONTAINER) {
				if (containerIdRaw == null || containerIdRaw.isBlank()) {
					continue;
				}
				UUID containerId;
				try {
					containerId = UUID.fromString(containerIdRaw);
				} catch (IllegalArgumentException ex) {
					continue;
				}
				container = containersById.get(containerId);
				if (container == null) {
					container = this.containerRepository.findById(containerId).orElse(null);
					if (container != null) {
						containersById.put(container.getId(), container);
					} else {
						continue;
					}
				}
			}
			Stop stop = new Stop(
					RouteSequence.of(sequence),
					stopType,
					container,
					CollectedWeightKilograms.fromKilograms(stopNode.optDouble(FIELD_COLLECTED_KILOGRAMS, 0.0)),
					CollectedVolumeLiters.fromLiters(stopNode.optDouble(FIELD_COLLECTED_LITERS, 0.0)),
					Distance.fromMeters(stopNode.optDouble(FIELD_DISTANCE_FROM_PREVIOUS_METERS, 0.0)),
					Distance.fromMeters(stopNode.optDouble(FIELD_CUMULATIVE_DISTANCE_METERS, 0.0)));
			stops.add(stop);
		}
		return stops;
	}

	/**
	 * Reads container daily state monitoring entries from the algorithm response.
	 *
	 * @param containerStateMonitoringNode JSON array with container daily states
	 * @return list of parsed ContainerDailyState entities
	 */
	private List<ContainerDailyState> readContainerDailyStates(JSONArray containerStateMonitoringNode) {
		List<ContainerDailyState> containerDailyStates = new ArrayList<>();
		if (containerStateMonitoringNode == null) {
			return containerDailyStates;
		}
		for (int i = 0; i < containerStateMonitoringNode.length(); i++) {
			Object node = containerStateMonitoringNode.get(i);
			if (!(node instanceof JSONObject)) {
				continue;
			}
			JSONObject stateNode = (JSONObject) node;
			String containerId = stateNode.has(FIELD_CONTAINER_ID) && !stateNode.isNull(FIELD_CONTAINER_ID)
					? stateNode.getString(FIELD_CONTAINER_ID)
					: null;
			if (containerId == null || containerId.isBlank()) {
				continue;
			}
			int planDay = stateNode.has(FIELD_PLAN_DAY) && !stateNode.isNull(FIELD_PLAN_DAY)
					? stateNode.getInt(FIELD_PLAN_DAY)
					: 1;
			double dailyFillingLiters = stateNode.optDouble(FIELD_DAILY_FILLING_LITERS, 0.0);
			double containerCapacityLiters = stateNode.optDouble(FIELD_CONTAINER_CAPACITY_LITERS, 0.0);
			double dailyDemandLitersPerDay = stateNode.optDouble(FIELD_DAILY_DEMAND_LITERS_PER_DAY, 0.0);
			String statusRaw = stateNode.has(FIELD_STATUS) && !stateNode.isNull(FIELD_STATUS)
					? stateNode.getString(FIELD_STATUS)
					: null;
			ContainerStatus status = ContainerStatus.fromString(statusRaw);
			try {
				ContainerDailyState containerDailyState = new ContainerDailyState(
						UUID.randomUUID(),
						containerId,
						planDay,
						dailyFillingLiters,
						containerCapacityLiters,
						dailyDemandLitersPerDay,
						status);
				containerDailyStates.add(containerDailyState);
			} catch (IllegalArgumentException ex) {
				logger.debug("Skipping invalid container daily state node: {}", ex.getMessage());
			}
		}
		return containerDailyStates;
	}

	/**
	 * Resolves a Facility from a JSON node.
	 *
	 * @param facilityNode the JSON node representing the facility
	 * @return the resolved Facility
	 * @throws IllegalArgumentException if the node is missing or malformed
	 * @throws NoSuchElementException if the facility is not found
	 */
	private Facility resolveFacility(Object facilityNode) {
		UUID id;
		if (facilityNode == null || facilityNode == JSONObject.NULL) {
			throw new IllegalArgumentException(ERR_FACILITY_NODE_REQUIRED);
		}
		if (facilityNode instanceof String) {
			id = parseUuid((String) facilityNode, FIELD_FACILITY);
		} else if (facilityNode instanceof JSONObject) {
			JSONObject obj = (JSONObject) facilityNode;
			id = parseUuid(obj.has(FIELD_ID) && !obj.isNull(FIELD_ID) ? obj.getString(FIELD_ID) : null, FIELD_ID);
		} else {
			throw new IllegalArgumentException(ERR_FACILITY_INVALID_FORMAT);
		}
		return this.facilityRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(ERR_FACILITY_NOT_FOUND + id));
	}

	/**
	 * Resolves a Vehicle from a JSON node.
	 *
	 * @param vehicleNode the JSON node representing the vehicle
	 * @return the resolved Vehicle
	 * @throws IllegalArgumentException if the node is missing or malformed
	 * @throws NoSuchElementException if the vehicle is not found
	 */
	private Vehicle resolveVehicle(Object vehicleNode) {
		UUID id;
		if (vehicleNode == null || vehicleNode == JSONObject.NULL) {
			throw new IllegalArgumentException(ERR_VEHICLE_NODE_REQUIRED);
		}
		if (vehicleNode instanceof String) {
			id = parseUuid((String) vehicleNode, FIELD_VEHICLE);
		} else if (vehicleNode instanceof JSONObject) {
			JSONObject obj = (JSONObject) vehicleNode;
			id = parseUuid(obj.has(FIELD_ID) && !obj.isNull(FIELD_ID) ? obj.getString(FIELD_ID) : null, FIELD_ID);
		} else {
			throw new IllegalArgumentException(ERR_VEHICLE_INVALID_FORMAT);
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