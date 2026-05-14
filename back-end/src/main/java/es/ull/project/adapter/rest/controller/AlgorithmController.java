package es.ull.project.adapter.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.ull.project.adapter.rest.mapper.AlgorithmExecutionResponseMapper;
import es.ull.project.adapter.rest.request.algorithm.AlgorithmExecutionRequestBody;
import es.ull.project.adapter.rest.request.algorithm.FacilityVehiclesSelectionRequestBody;
import es.ull.project.adapter.rest.response.algorithm.AlgorithmExecutionResponseBody;
import es.ull.project.application.exception.AlgorithmExecutionException;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionSelection;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmUseCase;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
 * At this stage, the endpoint resolves all received identifiers and returns the
 * complete data loaded from the database.
 */
@RestController
@RequestMapping(ApiRoutes.ALGORITHMS)
public class AlgorithmController {

    private static final int ZERO = 0;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_PLAN_ID = "infrastructurePlanId";
    private static final String STATUS_ERROR = "error";
    private static final String STATUS_SUCCESS = "success";
    private static final String MSG_RUNNER_FAILED = "Failed to send algorithm to runner";
    private static final String MSG_PARSE_FAILED = "Failed to parse algorithm response";
    private static final String MSG_PERSIST_FAILED = "Failed to persist the algorithm response";
    private static final String MSG_PERSIST_SUCCESS = "Algorithm executed and persisted successfully";
    private static final String ERR_SERIALIZE = "Failed to serialize the processed algorithm payload";
    private static final String ERR_SERIALIZE_REQUEST = "Failed to serialize the algorithm execution request";
    private static final String ERR_PARSE = "Failed to parse the algorithm response";
    private static final String ERR_PERSIST = "Failed to persist the algorithm response";
    private static final String ERR_NO_FACILITIES = "facilitiesWithVehicles is required";
    private static final String ERR_NO_SELECTION = "Each facility selection must be defined";
    private static final String ERR_NO_IDS = "A required identifier list is missing";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_AVERAGE_PICKUP = "averagePickupTimeMinutes";
    private static final String FIELD_FACILITY_ID = "facilityId";
    private static final String FIELD_IDENTIFIER = "identifier";
    private static final String EMPTY_STRING = "";

    /**
     * Use case for executing the algorithm request flow.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ExecuteAlgorithmUseCase executeAlgorithmUseCase;

    /**
     * Use case for running the external algorithm process.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private RunAlgorithmUseCase runAlgorithmUseCase;

    /**
     * Use case for persisting the algorithm result into MongoDB.
     */
    @Autowired
    private PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultUseCase;

    /**
     * Shared object mapper used to serialize and deserialize JSON payloads.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * POST /algorithms/execute
     *
     * Resolves the incoming identifiers and returns the processed data with the
     * complete facility, vehicle, and container information.
     *
     * @param requestBody algorithm execution request
     * @return processed response with resolved entities
     */
    @Operation(
            summary = "Execute the algorithm request flow",
            description = "Processes the execution request and returns all entities resolved from the provided identifiers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algorithm request processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "One or more referenced resources were not found"),
            @ApiResponse(responseCode = "500", description = "The algorithm process could not be executed")
    })
    @PostMapping("/execute")
    public ResponseEntity<JsonNode> executeAlgorithm(
            @Parameter(description = "Algorithm execution request data") @RequestBody AlgorithmExecutionRequestBody requestBody) {
        List<AlgorithmExecutionSelection> facilitiesWithVehicles = this.mapFacilitySelections(
                requestBody.facilitiesWithVehicles);
        List<UUID> selectedContainerIds = requestBody.selectedContainerIds != null
                ? requestBody.selectedContainerIds
                : this.emptyOrThrow();
        NumberOfDays numberOfDaysVo = this.requireNonNull(requestBody.numberOfDays, FIELD_NUMBER_OF_DAYS);
        AveragePickupTimeMinutes averagePickupTimeMinutesVo =
                this.requireNonNull(requestBody.averagePickupTimeMinutes, FIELD_AVERAGE_PICKUP);
        MaximumBudget providedMaxBudget = null;
        if (requestBody.maxBudget != null) {
            providedMaxBudget = requestBody.maxBudget;
        }
        AlgorithmExecutionResult result = this.executeAlgorithmUseCase.execute(
                facilitiesWithVehicles,
                selectedContainerIds,
                numberOfDaysVo,
                averagePickupTimeMinutesVo);
        AlgorithmExecutionResponseBody processedResponseBody = AlgorithmExecutionResponseMapper.toResponseBody(result);
        if (providedMaxBudget != null) {
            processedResponseBody.maxBudget = providedMaxBudget;
        }
        String processedJson = this.serializeProcessedResponse(processedResponseBody);
        AlgorithmJsonPayload algorithmJsonPayload;
        try {
            algorithmJsonPayload = this.runAlgorithmUseCase.execute(new AlgorithmJsonPayload(processedJson));
        } catch (RuntimeException e) {
            ObjectNode error = this.objectMapper.createObjectNode();
            error.put(KEY_STATUS, STATUS_ERROR);
            error.put(KEY_MESSAGE, MSG_RUNNER_FAILED);
            error.put(KEY_DETAILS, e.getMessage() != null ? e.getMessage() : EMPTY_STRING);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JsonNode responseBody;
        try {
            responseBody = this.deserializeAlgorithmResponse(algorithmJsonPayload.getJson());
        } catch (AlgorithmExecutionException e) {
            ObjectNode error = this.objectMapper.createObjectNode();
            error.put(KEY_STATUS, STATUS_ERROR);
            error.put(KEY_MESSAGE, MSG_PARSE_FAILED);
            error.put(KEY_DETAILS, e.getMessage() != null ? e.getMessage() : EMPTY_STRING);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            String executionRequestJson;
            try {
                executionRequestJson = this.objectMapper.writeValueAsString(requestBody);
            } catch (JsonProcessingException e) {
                ObjectNode error = this.objectMapper.createObjectNode();
                error.put(KEY_STATUS, STATUS_ERROR);
                error.put(KEY_MESSAGE, ERR_SERIALIZE_REQUEST);
                error.put(KEY_DETAILS, e.getMessage() != null ? e.getMessage() : EMPTY_STRING);
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            es.ull.project.domain.entity.InfrastructurePlan persisted = this.persistAlgorithmExecutionResultUseCase.persist(
                    algorithmJsonPayload, numberOfDaysVo, averagePickupTimeMinutesVo, providedMaxBudget, executionRequestJson);
            ObjectNode success = this.objectMapper.createObjectNode();
            success.put(KEY_STATUS, STATUS_SUCCESS);
            success.put(KEY_MESSAGE, MSG_PERSIST_SUCCESS);
            if (persisted != null && persisted.getId() != null) {
                success.put(KEY_PLAN_ID, persisted.getId().toString());
            }
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (RuntimeException e) {
            ObjectNode error = this.objectMapper.createObjectNode();
            error.put(KEY_STATUS, STATUS_ERROR);
            error.put(KEY_MESSAGE, MSG_PERSIST_FAILED);
            error.put(KEY_DETAILS, e.getMessage() != null ? e.getMessage() : EMPTY_STRING);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Serializes the processed response body before sending it to the algorithm.
     *
     * @param processedResponseBody the response body to serialize
     * @return serialized JSON string
     */
    private String serializeProcessedResponse(AlgorithmExecutionResponseBody processedResponseBody) {
        try {
            return this.objectMapper.writeValueAsString(processedResponseBody);
        } catch (JsonProcessingException e) {
            throw new AlgorithmExecutionException(ERR_SERIALIZE, e);
        }
    }

    /**
     * Deserializes the JSON returned by the algorithm process.
     *
     * @param algorithmJson raw JSON returned by the algorithm
     * @return parsed JSON node
     */
    private JsonNode deserializeAlgorithmResponse(String algorithmJson) {
        try {
            return this.objectMapper.readTree(algorithmJson);
        } catch (JsonProcessingException e) {
            throw new AlgorithmExecutionException(ERR_PARSE, e);
        }
    }

    /**
     * Persists the algorithm JSON as an infrastructure plan aggregate.
     *
     * @param responseBody             raw algorithm response
     * @param numberOfDays             number of days for the planning period
     * @param averagePickupTimeMinutes average pickup time in minutes
     * @param providedMaxBudget        the optional maximum budget constraint
     */
    private void persistAlgorithmResult(AlgorithmJsonPayload responseBody, NumberOfDays numberOfDays, AveragePickupTimeMinutes averagePickupTimeMinutes, MaximumBudget providedMaxBudget) {
        try {
            this.persistAlgorithmExecutionResultUseCase.persist(responseBody, numberOfDays, averagePickupTimeMinutes, providedMaxBudget, null);
        } catch (RuntimeException e) {
            throw new AlgorithmExecutionException(ERR_PERSIST, e);
        }
    }
    /**
     * Maps a list of facility selection request bodies into domain selection objects.
     *
     * @param requestSelections list of facility selection request bodies
     * @return list of mapped algorithm execution selections
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
     * Maps one facility selection from the request body.
     *
     * @param requestSelection request selection to map
     * @return mapped selection
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
     * Requires that a UUID value is non-null.
     *
     * @param value     UUID value to validate
     * @param fieldName logical field name used for validation messages
     * @return the validated UUID
     */
    private UUID requireNonNull(UUID value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }

    /**
     * Returns an empty list or throws when identifiers are null.
     *
     * @return empty UUID list
     */
    private List<UUID> emptyOrThrow() {
        throw new IllegalArgumentException(ERR_NO_IDS);
    }

    /**
     * Parses a UUID value from a string identifier.
     *
     * @param identifier raw identifier
     * @param fieldName logical field name used for validation messages
     * @return parsed UUID value
     */
    private UUID parseUuid(String identifier, String fieldName) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return UUID.fromString(identifier);
    }

    /**
     * Ensures that an integer request field is defined and positive.
     *
     * @param value integer value to validate
     * @param fieldName logical field name used for validation messages
     * @return validated primitive integer
     */
    private <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }
}
