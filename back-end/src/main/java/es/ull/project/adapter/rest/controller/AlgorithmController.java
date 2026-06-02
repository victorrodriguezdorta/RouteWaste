package es.ull.project.adapter.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.ull.project.adapter.rest.request.algorithm.AlgorithmExecutionRequestBody;
import es.ull.project.adapter.rest.request.algorithm.FacilityVehiclesSelectionRequestBody;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionJobCommand;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionSelection;
import es.ull.project.application.usecase.algorithm.CreatePendingInfrastructurePlanUseCase;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmExecutionJobAsyncUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AlgorithmController
 *
 * REST controller that handles HTTP requests related to algorithm execution.
 * Accepts execution requests, stores a pending infrastructure plan, and runs
 * the algorithm asynchronously.
 */
@Tag(name = "Algorithms")
@RestController
@RequestMapping(ApiRoutes.ALGORITHMS)
public class AlgorithmController {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_PLAN_ID = "infrastructurePlanId";
    private static final String KEY_EXECUTION_STATE = "executionState";
    private static final String STATUS_ERROR = "error";
    private static final String STATUS_ACCEPTED = "accepted";
    private static final String MSG_ACCEPTED = "Algorithm execution accepted and running asynchronously";
    private static final String ERR_SERIALIZE_REQUEST = "Failed to serialize the algorithm execution request";
    private static final String ERR_NO_FACILITIES = "facilitiesWithVehicles is required";
    private static final String ERR_NO_SELECTION = "Each facility selection must be defined";
    private static final String ERR_NO_IDS = "A required identifier list is missing";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_AVERAGE_PICKUP = "averagePickupTimeMinutes";
    private static final String FIELD_FACILITY_ID = "facilityId";
    private static final String DEFAULT_BUDGET = "1.7976931348623157E308";
    private static final String EMPTY_STRING = "";

    @Autowired
    private ExecuteAlgorithmUseCase executeAlgorithmUseCase;

    @Autowired
    private CreatePendingInfrastructurePlanUseCase createPendingInfrastructurePlanUseCase;

    @Autowired
    private RunAlgorithmExecutionJobAsyncUseCase runAlgorithmExecutionJobAsyncUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * POST /algorithms/execute
     *
     * Validates the request, persists a RUNNING infrastructure plan placeholder, and
     * starts asynchronous algorithm execution.
     *
     * @param requestBody algorithm execution request
     * @return 202 Accepted with the pending plan identifier
     */
    @Operation(
            summary = "Execute the algorithm request flow",
            description = "Validates the request, creates a pending infrastructure plan, and runs the algorithm asynchronously")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Algorithm execution accepted and started asynchronously"),
            @ApiResponse(responseCode = "202", description = "Algorithm execution accepted and started asynchronously"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "One or more referenced resources were not found"),
            @ApiResponse(responseCode = "500", description = "The request could not be accepted")
    })
    @PostMapping("/execute")
    public ResponseEntity<JsonNode> executeAlgorithm(
            @Parameter(description = "Selected facilities, vehicles, containers, planning horizon, and optional budget")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Selected facilities, vehicles, containers, planning horizon, and optional budget",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AlgorithmExecutionRequestBody.class)))
            @RequestBody AlgorithmExecutionRequestBody requestBody) {
        List<AlgorithmExecutionSelection> facilitiesWithVehicles = this.mapFacilitySelections(
                requestBody.facilitiesWithVehicles);
        List<UUID> selectedContainerIds = requestBody.selectedContainerIds != null
                ? requestBody.selectedContainerIds
                : this.emptyOrThrow();
        NumberOfDays numberOfDaysVo = this.requireNonNull(requestBody.numberOfDays, FIELD_NUMBER_OF_DAYS);
        AveragePickupTimeMinutes averagePickupTimeMinutesVo =
                this.requireNonNull(requestBody.averagePickupTimeMinutes, FIELD_AVERAGE_PICKUP);
        MaximumBudget effectiveMaxBudget = requestBody.maxBudget != null
                ? requestBody.maxBudget
                : new MaximumBudget(Double.parseDouble(DEFAULT_BUDGET));
        this.executeAlgorithmUseCase.execute(
                facilitiesWithVehicles,
                selectedContainerIds,
                numberOfDaysVo,
                averagePickupTimeMinutesVo);
        AlgorithmJsonPayload executionRequestJson;
        try {
            executionRequestJson = new AlgorithmJsonPayload(this.objectMapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException e) {
            ObjectNode error = this.objectMapper.createObjectNode();
            error.put(KEY_STATUS, STATUS_ERROR);
            error.put(KEY_MESSAGE, ERR_SERIALIZE_REQUEST);
            error.put(KEY_DETAILS, e.getMessage() != null ? e.getMessage() : EMPTY_STRING);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        InfrastructurePlan pendingPlan = this.createPendingInfrastructurePlanUseCase.createPending(
                numberOfDaysVo,
                averagePickupTimeMinutesVo,
                effectiveMaxBudget,
                executionRequestJson);
        AlgorithmExecutionJobCommand jobCommand = new AlgorithmExecutionJobCommand(
                pendingPlan.getId(),
                facilitiesWithVehicles,
                selectedContainerIds,
                numberOfDaysVo,
                averagePickupTimeMinutesVo,
                effectiveMaxBudget,
                executionRequestJson);
        this.runAlgorithmExecutionJobAsyncUseCase.runAsync(jobCommand);
        ObjectNode accepted = this.objectMapper.createObjectNode();
        accepted.put(KEY_STATUS, STATUS_ACCEPTED);
        accepted.put(KEY_MESSAGE, MSG_ACCEPTED);
        accepted.put(KEY_PLAN_ID, pendingPlan.getId().toString());
        accepted.put(KEY_EXECUTION_STATE, InfrastructurePlanExecutionState.RUNNING.name());
        return new ResponseEntity<>(accepted, HttpStatus.ACCEPTED);
    }

    /**
     * Maps REST facility-vehicle selections to domain execution selections.
     *
     * @param requestSelections facility and vehicle selections from the request body
     * @return mapped selections for the use case layer
     */
    private List<AlgorithmExecutionSelection> mapFacilitySelections(
            List<FacilityVehiclesSelectionRequestBody> requestSelections) {
        if (requestSelections == null) {
            throw new IllegalArgumentException(ERR_NO_FACILITIES);
        }
        return requestSelections.stream()
                .map(this::mapFacilitySelection)
                .toList();
    }

    /**
     * Maps a single facility-vehicle selection to a domain execution selection.
     *
     * @param requestSelection one facility with its selected vehicles
     * @return mapped selection for the use case layer
     */
    private AlgorithmExecutionSelection mapFacilitySelection(FacilityVehiclesSelectionRequestBody requestSelection) {
        if (requestSelection == null) {
            throw new IllegalArgumentException(ERR_NO_SELECTION);
        }
        return new AlgorithmExecutionSelection(
                this.requireNonNull(requestSelection.facilityId, FIELD_FACILITY_ID),
                requestSelection.selectedVehicleIds != null ? requestSelection.selectedVehicleIds : this.emptyOrThrow());
    }

    /**
     * Returns the UUID when present; otherwise throws with the field name in the message.
     *
     * @param value     UUID to validate
     * @param fieldName field name used in the error message
     * @return the non-null UUID
     */
    private UUID requireNonNull(UUID value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }

    /**
     * Signals that a required identifier list was not provided in the request.
     *
     * @return never returns normally
     */
    private List<UUID> emptyOrThrow() {
        throw new IllegalArgumentException(ERR_NO_IDS);
    }

    /**
     * Returns the value when present; otherwise throws with the field name in the message.
     *
     * @param <T>       type of the validated value
     * @param value     value to validate
     * @param fieldName field name used in the error message
     * @return the non-null value
     */
    private <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }
}
