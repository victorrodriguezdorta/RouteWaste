package es.ull.project.adapter.rest.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ull.project.adapter.rest.mapper.ServiceAssignmentResponseMapper;
import es.ull.project.adapter.rest.request.serviceassignment.ServiceAssignmentPostRequestBody;
import es.ull.project.adapter.rest.request.serviceassignment.ServiceAssignmentPutRequestBody;
import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.application.usecase.serviceassignment.CreateServiceAssignmentUseCase;
import es.ull.project.application.usecase.serviceassignment.DeleteServiceAssignmentUseCase;
import es.ull.project.application.usecase.serviceassignment.ReadServiceAssignmentUseCase;
import es.ull.project.application.usecase.serviceassignment.UpdateServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * ServiceAssignmentController
 * 
 * REST controller that handles HTTP requests for ServiceAssignment resources.
 * This controller exposes RESTful endpoints following REST architectural principles
 * to create, read, update, and delete service assignments in the system.
 * 
 * The controller uses Data Transfer Objects (DTOs) instead of domain entities
 * to avoid tight coupling and facilitate data validation.
 * 
 * Base path: defined in ApiRoutes.SERVICE_ASSIGNMENTS
 */
@RestController
@RequestMapping(ApiRoutes.SERVICE_ASSIGNMENTS)
public class ServiceAssignmentController {

    /**
     * Use case for reading service assignment data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadServiceAssignmentUseCase readServiceAssignmentUseCase;

    /**
     * Use case for creating new service assignments.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateServiceAssignmentUseCase createServiceAssignmentUseCase;

    /**
     * Use case for updating existing service assignments.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateServiceAssignmentUseCase updateServiceAssignmentUseCase;

    /**
     * Use case for deleting service assignments.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteServiceAssignmentUseCase deleteServiceAssignmentUseCase;

    /**
     * GET /service-assignments/
     * 
     * Retrieves all service assignments in the system.
     * 
     * This endpoint returns a list of all available service assignments without pagination.
     * The assignments are returned as ServiceAssignmentResponseBody DTOs serialized to JSON.
     * 
     * @return ResponseEntity containing a list of all service assignments and HTTP 200 (OK) status
     */
    @Operation(summary = "Get all service assignments", description = "Retrieves a list of all service assignments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service assignments retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/")
    public ResponseEntity<List<ServiceAssignmentResponseBody>> getServiceAssignments() {
        List<ServiceAssignment> assignments = this.readServiceAssignmentUseCase.fetchAll();
        List<ServiceAssignmentResponseBody> responseBodies = assignments.stream()
                .map(ServiceAssignmentResponseMapper::toResponseBody)
                .toList();
        return new ResponseEntity<>(responseBodies, HttpStatus.OK);
    }

    /**
     * GET /service-assignments/{id}
     * 
     * Retrieves a specific service assignment by its unique identifier.
     * 
     * @param id the unique identifier of the service assignment (UUID format)
     * @return ResponseEntity containing the service assignment and HTTP 200 (OK) if found,
     *         or HTTP 404 (NOT_FOUND) if the assignment does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @Operation(summary = "Get service assignment by ID", description = "Retrieves a specific service assignment by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service assignment found"),
            @ApiResponse(responseCode = "404", description = "Service assignment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ServiceAssignmentResponseBody> getServiceAssignmentById(
            @Parameter(description = "Service assignment UUID") @PathVariable String id) {
        try {
            UUID assignmentId = UUID.fromString(id);
            ServiceAssignment assignment = this.readServiceAssignmentUseCase.fetch(assignmentId);
            ServiceAssignmentResponseBody responseBody = ServiceAssignmentResponseMapper.toResponseBody(assignment);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /service-assignments/
     * 
     * Creates a new service assignment with the data provided in the request body.
     * 
     * The request body must contain a ServiceAssignmentPostRequestBody DTO with all
     * required service assignment attributes. The DTO receives only the UUIDs of the
     * container and facility. The service layer will fetch the complete entities from
     * their repositories, validate their existence, and create the service assignment
     * with full entity references.
     * 
     * @param requestBody the service assignment data to create (ServiceAssignmentPostRequestBody DTO)
     * @return ResponseEntity containing the created service assignment and HTTP 201 (CREATED),
     *         or HTTP 400 (BAD_REQUEST) with error message if validation fails or entities are not found
     */
    @Operation(summary = "Create a service assignment", description = "Creates a new service assignment with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service assignment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/")
    public ResponseEntity<ServiceAssignmentResponseBody> createServiceAssignment(
            @Parameter(description = "Service assignment data") @RequestBody ServiceAssignmentPostRequestBody requestBody) {
        try {
            ServiceAssignment createdAssignment = this.createServiceAssignmentUseCase.create(
                    requestBody.containerId,
                    requestBody.facilityId,
                    requestBody.wasteDemand,
                    requestBody.distance,
                    requestBody.serviceTime,
                    requestBody.transportCost
            );
            ServiceAssignmentResponseBody responseBody = ServiceAssignmentResponseMapper.toResponseBody(createdAssignment);
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /service-assignments/{id}
     * 
     * Updates an existing service assignment with the data provided in the request body.
     * 
     * The request body must contain a ServiceAssignmentPutRequestBody DTO with all
     * service assignment attributes to update. All fields in the DTO will be applied
     * to the service assignment entity.
     * 
     * @param id the unique identifier of the service assignment to update (UUID format)
     * @param requestBody the new service assignment data (ServiceAssignmentPutRequestBody DTO)
     * @return ResponseEntity containing the updated service assignment and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the assignment does not exist,
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @Operation(summary = "Update a service assignment", description = "Updates an existing service assignment with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service assignment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service assignment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServiceAssignmentResponseBody> updateServiceAssignment(
            @Parameter(description = "Service assignment UUID") @PathVariable String id,
            @Parameter(description = "Updated service assignment data") @RequestBody ServiceAssignmentPutRequestBody requestBody) {
        try {
            UUID assignmentId = UUID.fromString(id);
            UUID containerId = requestBody.container != null ? requestBody.container.getId() : null;
            UUID facilityId = requestBody.facility != null ? requestBody.facility.getId() : null;
            ServiceAssignment updatedAssignment = this.updateServiceAssignmentUseCase.update(
                    assignmentId,
                    containerId,
                    facilityId,
                    requestBody.wasteDemand,
                    requestBody.distance,
                    requestBody.serviceTime,
                    requestBody.transportCost
            );
            ServiceAssignmentResponseBody responseBody = ServiceAssignmentResponseMapper.toResponseBody(updatedAssignment);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /service-assignments/{id}
     * 
     * Deletes a service assignment by its unique identifier.
     * 
     * Upon successful deletion, the deleted service assignment entity is returned
     * in the response body for confirmation purposes.
     * 
     * @param id the unique identifier of the service assignment to delete (UUID format)
     * @return ResponseEntity containing the deleted service assignment and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the assignment does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @Operation(summary = "Delete a service assignment", description = "Deletes a service assignment by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service assignment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service assignment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceAssignmentResponseBody> deleteServiceAssignment(
            @Parameter(description = "Service assignment UUID") @PathVariable String id) {
        try {
            UUID assignmentId = UUID.fromString(id);
            ServiceAssignment deletedAssignment = this.deleteServiceAssignmentUseCase.delete(assignmentId);
            ServiceAssignmentResponseBody responseBody = ServiceAssignmentResponseMapper.toResponseBody(deletedAssignment);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
