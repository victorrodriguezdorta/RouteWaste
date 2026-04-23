package es.ull.project.adapter.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ull.project.application.exception.AlgorithmExecutionException;
import es.ull.project.adapter.rest.mapper.AlgorithmExecutionResponseMapper;
import es.ull.project.adapter.rest.request.algorithm.AlgorithmExecutionRequestBody;
import es.ull.project.adapter.rest.request.algorithm.FacilityVehiclesSelectionRequestBody;
import es.ull.project.adapter.rest.response.algorithm.AlgorithmExecutionResponseBody;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionSelection;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
        List<UUID> selectedContainerIds = this.mapUuidList(requestBody.selectedContainerIds);

        AlgorithmExecutionResult result = this.executeAlgorithmUseCase.execute(
                facilitiesWithVehicles,
                selectedContainerIds,
                this.requirePositiveInteger(requestBody.numberOfDays, "numberOfDays"),
                this.requirePositiveInteger(requestBody.averagePickupTimeMinutes, "averagePickupTimeMinutes"));

        AlgorithmExecutionResponseBody processedResponseBody = AlgorithmExecutionResponseMapper.toResponseBody(result);
        String processedJson = this.serializeProcessedResponse(processedResponseBody);
        String algorithmJson = this.runAlgorithmUseCase.execute(processedJson);
        JsonNode responseBody = this.deserializeAlgorithmResponse(algorithmJson);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * Serializes the processed response body before sending it to the algorithm.
     *
     * @param processedResponseBody processed response body
     * @return serialized JSON string
     */
    private String serializeProcessedResponse(AlgorithmExecutionResponseBody processedResponseBody) {
        try {
            return this.objectMapper.writeValueAsString(processedResponseBody);
        } catch (JsonProcessingException e) {
            throw new AlgorithmExecutionException("Failed to serialize the processed algorithm payload", e);
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
            throw new AlgorithmExecutionException("Failed to parse the algorithm response", e);
        }
    }

    /**
     * Maps facility selections from the request body into application objects.
     *
     * @param requestSelections request selections to map
     * @return mapped immutable selection list
     */
    private List<AlgorithmExecutionSelection> mapFacilitySelections(
            List<FacilityVehiclesSelectionRequestBody> requestSelections) {
        if (requestSelections == null) {
            throw new IllegalArgumentException("facilitiesWithVehicles is required");
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
            throw new IllegalArgumentException("Each facility selection must be defined");
        }

        return new AlgorithmExecutionSelection(
                this.parseUuid(requestSelection.facilityId, "facilityId"),
                this.mapUuidList(requestSelection.selectedVehicleIds));
    }

    /**
     * Maps a list of string identifiers into UUID values.
     *
     * @param identifiers identifiers to map
     * @return mapped UUID list
     */
    private List<UUID> mapUuidList(List<String> identifiers) {
        if (identifiers == null) {
            throw new IllegalArgumentException("A required identifier list is missing");
        }

        return identifiers.stream()
                .map(identifier -> this.parseUuid(identifier, "identifier"))
                .toList();
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
    private int requirePositiveInteger(Integer value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
        return value;
    }
}
