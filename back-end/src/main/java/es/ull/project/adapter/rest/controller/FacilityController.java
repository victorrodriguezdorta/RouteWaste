package es.ull.project.adapter.rest.controller;

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

import es.ull.project.adapter.rest.mapper.FacilityResponseMapper;
import es.ull.project.adapter.rest.request.facility.FacilityPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPutRequestBody;
import es.ull.project.adapter.rest.response.facility.FacilityPageResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.application.usecase.facility.CreateFacilityUseCase;
import es.ull.project.application.usecase.facility.DeleteFacilityUseCase;
import es.ull.project.application.usecase.facility.ReadFacilityUseCase;
import es.ull.project.application.usecase.facility.UpdateFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityType;

/**
 * FacilityController
 * 
 * REST controller that handles HTTP requests for Facility resources.
 * This controller exposes RESTful endpoints following REST architectural principles
 * to create, read, update, and delete facilities in the system.
 * 
 * The controller uses Data Transfer Objects (DTOs) instead of domain entities
 * to avoid tight coupling and facilitate data validation.
 * 
 * Base path: defined in ApiRoutes.FACILITIES
 */
@RestController
@RequestMapping(ApiRoutes.FACILITIES)
public class FacilityController {

    private static final int ZERO = 0;

    private static final String SORT_BY_TYPE = "type";
    private static final String FIELD_TYPE = "facilityType";

    private static final String SORT_BY_LOCATION = "location";
    private static final String FIELD_LOCATION = "location.postalAddress";

    private static final String SORT_BY_CAPACITY = "capacity";
    private static final String FIELD_CAPACITY = "capacity.value";

    private static final String SORT_BY_STATUS = "status";
    private static final String FIELD_STATUS = "status";

    /**
     * Use case for reading facility data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadFacilityUseCase readFacilityUseCase;

    /**
     * Use case for creating new facilities.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateFacilityUseCase createFacilityUseCase;

    /**
     * Use case for updating existing facilities.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateFacilityUseCase updateFacilityUseCase;

    /**
     * Use case for deleting facilities.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteFacilityUseCase deleteFacilityUseCase;

    /**
     * GET /facilities
     * 
     * Retrieves facilities in the system with pagination support.
     * 
     * This endpoint returns a paginated list of facilities with optional filtering
     * and sorting capabilities. Facilities are returned as FacilityResponseBody DTOs
     * serialized to JSON.
     * 
     * @param page zero-based page index (default: 0)
     * @param size page size (default: 10)
     * @param sortBy field to sort by: type, location, capacity, or status (optional)
     * @param sortOrder sort direction: asc or desc (default: asc)
     * @param facilityType optional facility type filter
     * @return ResponseEntity containing paginated facilities and HTTP 200 (OK) status
     */
    @GetMapping("/")
    public ResponseEntity<FacilityPageResponseBody> getFacilities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String facilityType) {
        if (page < ZERO || size <= ZERO) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        String mongoSortField = switch (sortBy != null ? sortBy : "") {
            case SORT_BY_TYPE -> FIELD_TYPE;
            case SORT_BY_LOCATION -> FIELD_LOCATION;
            case SORT_BY_CAPACITY -> FIELD_CAPACITY;
            case SORT_BY_STATUS -> FIELD_STATUS;
            default -> null;
        };
        
        Sort sort = Sort.unsorted();
        if (mongoSortField != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, mongoSortField);
        }
        
        FacilityType facilityTypeFilter = null;
        if (facilityType != null && !facilityType.isBlank()) {
            try {
                facilityTypeFilter = FacilityType.valueOf(facilityType);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Facility> facilityPage = this.readFacilityUseCase.fetchAll(pageable, facilityTypeFilter);
        List<FacilityResponseBody> responseBodies = facilityPage.getContent().stream()
                .map(FacilityResponseMapper::toResponseBody)
                .toList();
        
        FacilityPageResponseBody response = new FacilityPageResponseBody();
        response.content = responseBodies;
        response.totalElements = facilityPage.getTotalElements();
        response.totalPages = facilityPage.getTotalPages();
        response.page = facilityPage.getNumber();
        response.size = facilityPage.getSize();
        response.numberOfElements = facilityPage.getNumberOfElements();
        response.first = facilityPage.isFirst();
        response.last = facilityPage.isLast();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /facilities/{id}
     * 
     * Retrieves a specific facility by its unique identifier.
     * 
     * @param id the unique identifier of the facility (UUID format)
     * @return ResponseEntity containing the facility and HTTP 200 (OK) if found,
     *         or HTTP 404 (NOT_FOUND) if the facility does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @GetMapping("/{id}")
    public ResponseEntity<FacilityResponseBody> getFacilityById(@PathVariable String id) {
        try {
            UUID facilityId = UUID.fromString(id);
            Facility facility = this.readFacilityUseCase.fetch(facilityId);
            FacilityResponseBody responseBody = FacilityResponseMapper.toResponseBody(facility);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /facilities
     * 
     * Creates a new facility with the data provided in the request body.
     * 
     * The request body must contain a FacilityPostRequestBody DTO with all
     * required facility attributes. The DTO is used instead of the domain entity
     * to decouple the API contract from the domain model.
     * 
     * @param requestBody the facility data to create (FacilityPostRequestBody DTO)
     * @return ResponseEntity containing the created facility and HTTP 201 (CREATED),
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @PostMapping("/")
    public ResponseEntity<FacilityResponseBody> createFacility(@RequestBody FacilityPostRequestBody requestBody) {
        try {
            Facility createdFacility = this.createFacilityUseCase.create(
                    requestBody.facilityType,
                    requestBody.location,
                    requestBody.capacity,
                    requestBody.openingFixedCost,
                    requestBody.status
            );
            FacilityResponseBody responseBody = FacilityResponseMapper.toResponseBody(createdFacility);
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /facilities/{id}
     * 
     * Updates an existing facility with the data provided in the request body.
     * 
     * The request body must contain a FacilityPutRequestBody DTO with all
     * facility attributes to update. All fields in the DTO will be applied
     * to the facility entity.
     * 
     * @param id the unique identifier of the facility to update (UUID format)
     * @param requestBody the new facility data (FacilityPutRequestBody DTO)
     * @return ResponseEntity containing the updated facility and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the facility does not exist,
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<FacilityResponseBody> updateFacility(
            @PathVariable String id,
            @RequestBody FacilityPutRequestBody requestBody) {
        try {
            UUID facilityId = UUID.fromString(id);
            Facility updatedFacility = this.updateFacilityUseCase.update(
                    facilityId,
                    requestBody.facilityType,
                    requestBody.location,
                    requestBody.capacity,
                    requestBody.openingFixedCost,
                    requestBody.status
            );
            FacilityResponseBody responseBody = FacilityResponseMapper.toResponseBody(updatedFacility);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /facilities/{id}
     * 
     * Deletes a facility by its unique identifier.
     * 
     * Upon successful deletion, the deleted facility entity is returned
     * in the response body for confirmation purposes.
     * 
     * @param id the unique identifier of the facility to delete (UUID format)
     * @return ResponseEntity containing the deleted facility and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the facility does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<FacilityResponseBody> deleteFacility(@PathVariable String id) {
        try {
            UUID facilityId = UUID.fromString(id);
            Facility deletedFacility = this.deleteFacilityUseCase.delete(facilityId);
            FacilityResponseBody responseBody = FacilityResponseMapper.toResponseBody(deletedFacility);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
