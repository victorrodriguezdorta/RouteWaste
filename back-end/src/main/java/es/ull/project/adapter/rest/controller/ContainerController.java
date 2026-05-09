package es.ull.project.adapter.rest.controller;

import es.ull.project.adapter.mongodb.mapper.ContainerFieldMapper;
import es.ull.project.adapter.mongodb.query.ContainerSearchCriteriaBuilder;
import es.ull.project.adapter.rest.mapper.ContainerResponseMapper;
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPutRequestBody;
import es.ull.project.adapter.rest.response.container.ContainerPageResponseBody;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.application.query.ContainerSearchCriteria;
import es.ull.project.application.usecase.container.CreateContainerUseCase;
import es.ull.project.application.usecase.container.DeleteContainerUseCase;
import es.ull.project.application.usecase.container.ReadContainerUseCase;
import es.ull.project.application.usecase.container.UpdateContainerUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.page.NumberOfElements;
import es.ull.project.domain.valueobject.page.PageFlag;
import es.ull.project.domain.valueobject.page.PageNumber;
import es.ull.project.domain.valueobject.page.PageSize;
import es.ull.project.domain.valueobject.page.TotalElements;
import es.ull.project.domain.valueobject.page.TotalPages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ContainerController
 * 
 * REST controller that handles HTTP requests for Container resources.
 * This controller exposes RESTful endpoints following REST architectural principles
 * to create, read, update, and delete containers in the system.
 * 
 * The controller uses Data Transfer Objects (DTOs) instead of domain entities
 * to avoid tight coupling and facilitate data validation.
 * 
 * Base path: defined in ApiRoutes.CONTAINERS
 */
@RestController
@RequestMapping(ApiRoutes.CONTAINERS)
public class ContainerController {

    private static final int ZERO = 0;

    /**
     * Use case for reading container data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadContainerUseCase readContainerUseCase;

    /**
     * Use case for creating new containers.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateContainerUseCase createContainerUseCase;

    /**
     * Use case for updating existing containers.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateContainerUseCase updateContainerUseCase;

    /**
     * Use case for deleting containers.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteContainerUseCase deleteContainerUseCase;

    /**
     * GET /containers/
     * 
     * Retrieves all containers with advanced filtering and sorting options.
     * Supports pagination, sorting by any attribute, and filtering by multiple criteria.
     * 
     * @param page the zero-based page index (defaults to 0)
     * @param size the number of elements per page (defaults to 10)
     * @param sortBy the field to sort by (any container attribute)
     * @param sortOrder the sort direction, "asc" or "desc" (defaults to "asc")
     * @param wasteType optional filter by waste type
     * @param serviceZone optional filter by service zone
     * @param minCapacity optional minimum capacity filter
     * @param maxCapacity optional maximum capacity filter
     * @param minDemand optional minimum daily demand filter
     * @param maxDemand optional maximum daily demand filter
     * @param location optional filter by location (postal address)
     * @return ResponseEntity containing a page of containers with HTTP 200 (OK) status
     */
    @Operation(summary = "Get all containers", description = "Retrieves a paginated list of containers with optional sorting and advanced filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Containers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping("/")
    public ResponseEntity<ContainerPageResponseBody> getContainers(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of elements per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (e.g., wasteType, location, capacity, demand, serviceZone, etc.)") 
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String sortOrder,
            @Parameter(description = "Filter by waste type") @RequestParam(required = false) String wasteType,
            @Parameter(description = "Filter by service zone") @RequestParam(required = false) String serviceZone,
            @Parameter(description = "Minimum capacity in liters") @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Maximum capacity in liters") @RequestParam(required = false) Integer maxCapacity,
            @Parameter(description = "Minimum daily demand") @RequestParam(required = false) Integer minDemand,
            @Parameter(description = "Maximum daily demand") @RequestParam(required = false) Integer maxDemand,
            @Parameter(description = "Filter by location (postal address)") @RequestParam(required = false) String location) {
        if (page < ZERO || size <= ZERO) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Sort sort = buildSort(sortBy, sortOrder);
        if (sortBy != null && !sortBy.isBlank() && !ContainerFieldMapper.isValidField(sortBy)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        WasteType wasteTypeFilter = null;
        if (wasteType != null && !wasteType.isBlank()) {
            try {
                wasteTypeFilter = WasteType.valueOf(wasteType);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        ContainerSearchCriteria criteria = new ContainerSearchCriteriaBuilder()
                .withWasteType(wasteTypeFilter)
                .withServiceZone(serviceZone)
                .withMinCapacityLiters(minCapacity)
                .withMaxCapacityLiters(maxCapacity)
                .withMinDailyDemand(minDemand)
                .withMaxDailyDemand(maxDemand)
                .withLocationPostalAddress(location)
                .build();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Container> containerPage = this.readContainerUseCase.fetchAll(pageable, criteria);
        return buildSuccessResponse(containerPage);
    }

    /**
     * Builds a Sort object from sortBy and sortOrder parameters.
     * Uses the field mapper to translate public field names to MongoDB paths.
     *
     * @param sortBy the field name to sort by
     * @param sortOrder the sort direction (asc/desc)
     * @return Sort object, unsorted if sortBy is null
     */
    private Sort buildSort(String sortBy, String sortOrder) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.unsorted();
        }
        String mongoField = ContainerFieldMapper.toMongoField(sortBy);
        if (mongoField == null) {
            return Sort.unsorted();
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, mongoField);
    }

    /**
     * Builds a successful response from a container page.
     *
     * @param containerPage the page of containers
     * @return ResponseEntity with container page response body and HTTP 200
     */
    private ResponseEntity<ContainerPageResponseBody> buildSuccessResponse(Page<Container> containerPage) {
        List<ContainerResponseBody> responseBodies = containerPage.getContent().stream()
                .map(ContainerResponseMapper::toResponseBody)
                .toList();
        ContainerPageResponseBody response = new ContainerPageResponseBody();
        response.content = responseBodies;
        response.totalElements = new TotalElements(containerPage.getTotalElements());
        response.totalPages = new TotalPages(containerPage.getTotalPages());
        response.page = new PageNumber(containerPage.getNumber());
        response.size = new PageSize(containerPage.getSize());
        response.numberOfElements = new NumberOfElements(containerPage.getNumberOfElements());
        response.first = new PageFlag(containerPage.isFirst());
        response.last = new PageFlag(containerPage.isLast());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /containers/{id}
     * 
     * Retrieves a specific container by its unique identifier.
     * 
     * @param id the unique identifier of the container (UUID format)
     * @return ResponseEntity containing the container and HTTP 200 (OK) if found,
     *         or HTTP 404 (NOT_FOUND) if the container does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @Operation(summary = "Get container by ID", description = "Retrieves a specific container by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Container found"),
            @ApiResponse(responseCode = "404", description = "Container not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContainerResponseBody> getContainerById(
            @Parameter(description = "Container UUID") @PathVariable String id) {
        try {
            UUID containerId = UUID.fromString(id);
            Container container = this.readContainerUseCase.fetch(containerId);
            ContainerResponseBody responseBody = ContainerResponseMapper.toResponseBody(container);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /containers/
     * 
     * Creates a new container with the data provided in the request body.
     * 
     * The request body must contain a ContainerPostRequestBody DTO with all
     * required container attributes. The DTO is used instead of the domain entity
     * to decouple the API contract from the domain model.
     * 
     * @param requestBody the container data to create (ContainerPostRequestBody DTO)
     * @return ResponseEntity containing the created container and HTTP 201 (CREATED),
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @Operation(summary = "Create a container", description = "Creates a new container with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Container created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/")
    public ResponseEntity<ContainerResponseBody> createContainer(
            @Parameter(description = "Container data") @RequestBody ContainerPostRequestBody requestBody) {
        try {
            Container createdContainer = this.createContainerUseCase.create(
                    requestBody.location,
                    requestBody.wasteType,
                    requestBody.capacityLiters,
                    requestBody.dailyDemandLitersPerDay,
                    requestBody.serviceZone
            );
            ContainerResponseBody responseBody = ContainerResponseMapper.toResponseBody(createdContainer);
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /containers/{id}
     * 
     * Updates an existing container with the data provided in the request body.
     * 
     * The request body must contain a ContainerPutRequestBody DTO with all
     * container attributes to update. All fields in the DTO will be applied
     * to the container entity.
     * 
     * @param id the unique identifier of the container to update (UUID format)
     * @param requestBody the new container data (ContainerPutRequestBody DTO)
     * @return ResponseEntity containing the updated container and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the container does not exist,
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @Operation(summary = "Update a container", description = "Updates an existing container with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Container updated successfully"),
            @ApiResponse(responseCode = "404", description = "Container not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContainerResponseBody> updateContainer(
            @Parameter(description = "Container UUID") @PathVariable String id,
            @Parameter(description = "Updated container data") @RequestBody ContainerPutRequestBody requestBody) {
        try {
            UUID containerId = UUID.fromString(id);
            Container updatedContainer = this.updateContainerUseCase.update(
                    containerId,
                    requestBody.location,
                    requestBody.wasteType,
                    requestBody.capacityLiters,
                    requestBody.dailyDemandLitersPerDay,
                    requestBody.serviceZone
            );
            ContainerResponseBody responseBody = ContainerResponseMapper.toResponseBody(updatedContainer);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /containers/{id}
     * 
     * Deletes a container by its unique identifier.
     * 
     * Upon successful deletion, the deleted container entity is returned
     * in the response body for confirmation purposes.
     * 
     * @param id the unique identifier of the container to delete (UUID format)
     * @return ResponseEntity containing the deleted container and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the container does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @Operation(summary = "Delete a container", description = "Deletes a container by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Container deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Container not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ContainerResponseBody> deleteContainer(
            @Parameter(description = "Container UUID") @PathVariable String id) {
        try {
            UUID containerId = UUID.fromString(id);
            Container deletedContainer = this.deleteContainerUseCase.delete(containerId);
            ContainerResponseBody responseBody = ContainerResponseMapper.toResponseBody(deletedContainer);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
