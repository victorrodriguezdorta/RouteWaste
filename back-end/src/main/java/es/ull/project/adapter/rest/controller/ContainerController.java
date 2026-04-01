package es.ull.project.adapter.rest.controller;

import es.ull.project.adapter.rest.mapper.ContainerResponseMapper;
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPutRequestBody;
import es.ull.project.adapter.rest.response.container.ContainerPageResponseBody;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.application.usecase.container.CreateContainerUseCase;
import es.ull.project.application.usecase.container.DeleteContainerUseCase;
import es.ull.project.application.usecase.container.ReadContainerUseCase;
import es.ull.project.application.usecase.container.UpdateContainerUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
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

    private static final String SORT_BY_WASTE_TYPE = "wasteType";
    private static final String FIELD_WASTE_TYPE = "wasteType";

    private static final String SORT_BY_LOCATION = "location";
    private static final String FIELD_LOCATION = "location.postalAddress";

    private static final String SORT_BY_DEMAND = "demand";
    private static final String FIELD_DEMAND = "wasteDemand.value";

    private static final String SORT_BY_SERVICE_ZONE = "serviceZone";
    private static final String FIELD_SERVICE_ZONE = "serviceZone";

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
     * Retrieves all containers in the system.
     * 
     * This endpoint returns a list of all available containers without pagination.
     * The containers are returned as ContainerResponseBody DTOs serialized to JSON.
     * 
     * @return ResponseEntity containing a list of all containers and HTTP 200 (OK) status
     */
    @GetMapping("/")
    public ResponseEntity<ContainerPageResponseBody> getContainers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String wasteType) {
        if (page < ZERO || size <= ZERO) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String mongoSortField = switch (sortBy != null ? sortBy : "") {
            case SORT_BY_WASTE_TYPE -> FIELD_WASTE_TYPE;
            case SORT_BY_LOCATION -> FIELD_LOCATION;
            case SORT_BY_DEMAND -> FIELD_DEMAND;
            case SORT_BY_SERVICE_ZONE -> FIELD_SERVICE_ZONE;
            default -> null;
        };
        Sort sort = Sort.unsorted();
        if (mongoSortField != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, mongoSortField);
        }
        WasteType wasteTypeFilter = null;
        if (wasteType != null && !wasteType.isBlank()) {
            try {
                wasteTypeFilter = WasteType.valueOf(wasteType);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Container> containerPage = this.readContainerUseCase.fetchAll(pageable, wasteTypeFilter);
        List<ContainerResponseBody> responseBodies = containerPage.getContent().stream()
                .map(ContainerResponseMapper::toResponseBody)
                .toList();
        ContainerPageResponseBody response = new ContainerPageResponseBody();
        response.content = responseBodies;
        response.totalElements = containerPage.getTotalElements();
        response.totalPages = containerPage.getTotalPages();
        response.page = containerPage.getNumber();
        response.size = containerPage.getSize();
        response.numberOfElements = containerPage.getNumberOfElements();
        response.first = containerPage.isFirst();
        response.last = containerPage.isLast();
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
    @GetMapping("/{id}")
    public ResponseEntity<ContainerResponseBody> getContainerById(@PathVariable String id) {
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
    @PostMapping("/")
    public ResponseEntity<ContainerResponseBody> createContainer(@RequestBody ContainerPostRequestBody requestBody) {
        try {
            Container createdContainer = this.createContainerUseCase.create(
                    requestBody.location,
                    requestBody.wasteType,
                    requestBody.wasteDemand,
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
    @PutMapping("/{id}")
    public ResponseEntity<ContainerResponseBody> updateContainer(
            @PathVariable String id,
            @RequestBody ContainerPutRequestBody requestBody) {
        try {
            UUID containerId = UUID.fromString(id);
            Container updatedContainer = this.updateContainerUseCase.update(
                    containerId,
                    requestBody.location,
                    requestBody.wasteType,
                    requestBody.wasteDemand,
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ContainerResponseBody> deleteContainer(@PathVariable String id) {
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
