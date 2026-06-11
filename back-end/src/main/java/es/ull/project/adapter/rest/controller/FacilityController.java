package es.ull.project.adapter.rest.controller;

import es.ull.project.adapter.mongodb.mapper.FacilityFieldMapper;
import es.ull.project.adapter.mongodb.query.FacilitySearchCriteriaBuilder;
import es.ull.project.adapter.rest.mapper.BulkImportResponseMapper;
import es.ull.project.adapter.rest.mapper.FacilityResponseMapper;
import es.ull.project.adapter.rest.request.facility.FacilityBulkPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPutRequestBody;
import es.ull.project.adapter.rest.response.bulk.BulkImportResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityPageResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.adapter.rest.support.BulkImportMultipartSupport;
import es.ull.project.application.model.BulkCreateOutcome;
import es.ull.project.application.query.FacilitySearchCriteria;
import es.ull.project.application.usecase.facility.BulkCreateFacilitiesUseCase;
import es.ull.project.application.usecase.facility.CreateFacilityUseCase;
import es.ull.project.application.usecase.facility.DeleteFacilityUseCase;
import es.ull.project.application.usecase.facility.ReadFacilityUseCase;
import es.ull.project.application.usecase.facility.UpdateFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.page.NumberOfElements;
import es.ull.project.domain.valueobject.page.PageFlag;
import es.ull.project.domain.valueobject.page.PageNumber;
import es.ull.project.domain.valueobject.page.PageSize;
import es.ull.project.domain.valueobject.page.TotalElements;
import es.ull.project.domain.valueobject.page.TotalPages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@Tag(name = "Facilities")
@RestController
@RequestMapping(ApiRoutes.FACILITIES)
public class FacilityController {

    private static final int ZERO = 0;

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

    @Autowired
    private BulkCreateFacilitiesUseCase bulkCreateFacilitiesUseCase;

    @Autowired
    private BulkImportMultipartSupport bulkImportMultipartSupport;

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
     * Retrieves facilities in the system with advanced filtering and sorting options.
     * Supports pagination, sorting by any attribute, and filtering by multiple criteria.
     * 
     * @param page the zero-based page index (defaults to 0)
     * @param size the number of elements per page (defaults to 10)
     * @param sortBy the field to sort by (any facility attribute)
     * @param sortOrder the sort direction, "asc" or "desc" (defaults to "asc")
     * @param facilityType optional filter by facility type
     * @param status optional filter by facility status
     * @param location optional filter by location (postal address)
     * @param name optional filter by facility name
     * @return ResponseEntity containing a page of facilities with HTTP 200 (OK) status
     */
    @Operation(summary = "Get all facilities", description = "Retrieves a paginated list of facilities with optional sorting and advanced filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facilities retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FacilityPageResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping("/")
    public ResponseEntity<FacilityPageResponseBody> getFacilities(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of elements per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (e.g., type, location, storageCapacity, processingCapacity, unloadingTime, openingCost, status, etc.)") 
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String sortOrder,
            @Parameter(description = "Filter by facility type") @RequestParam(required = false) String facilityType,
            @Parameter(description = "Filter by facility status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by location (postal address)") @RequestParam(required = false) String location,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name) {
        if (page < ZERO || size <= ZERO) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Sort sort = buildSort(sortBy, sortOrder);
        if (sortBy != null && !sortBy.isBlank() && !FacilityFieldMapper.isValidField(sortBy)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FacilityType facilityTypeFilter = null;
        if (facilityType != null && !facilityType.isBlank()) {
            try {
                facilityTypeFilter = FacilityType.valueOf(facilityType);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        FacilityStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try {
                statusFilter = FacilityStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        FacilitySearchCriteria criteria = new FacilitySearchCriteriaBuilder()
                .withFacilityType(facilityTypeFilter)
                .withStatus(statusFilter)
                .withLocationPostalAddress(location)
                .withName(name)
                .build();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Facility> facilityPage = this.readFacilityUseCase.fetchAll(pageable, criteria);
        return buildSuccessResponse(facilityPage);
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
        String mongoField = FacilityFieldMapper.toMongoField(sortBy);
        if (mongoField == null) {
            return Sort.unsorted();
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        return Sort.by(direction, mongoField);
    }

    /**
     * Builds a successful response from a facility page.
     *
     * @param facilityPage the page of facilities
     * @return ResponseEntity with facility page response body and HTTP 200
     */
    private ResponseEntity<FacilityPageResponseBody> buildSuccessResponse(Page<Facility> facilityPage) {
        List<FacilityResponseBody> responseBodies = facilityPage.getContent().stream()
                .map(FacilityResponseMapper::toResponseBody)
                .toList();
        FacilityPageResponseBody response = new FacilityPageResponseBody();
        response.content = responseBodies;
        response.totalElements = new TotalElements(facilityPage.getTotalElements());
        response.totalPages = new TotalPages(facilityPage.getTotalPages());
        response.page = new PageNumber(facilityPage.getNumber());
        response.size = new PageSize(facilityPage.getSize());
        response.numberOfElements = new NumberOfElements(facilityPage.getNumberOfElements());
        response.first = new PageFlag(facilityPage.isFirst());
        response.last = new PageFlag(facilityPage.isLast());
        response.statistics = this.readFacilityUseCase.fetchStatistics();
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
    @Operation(summary = "Get facility by ID", description = "Retrieves a specific facility by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found",
                    content = @Content(schema = @Schema(implementation = FacilityResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Facility not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FacilityResponseBody> getFacilityById(
            @Parameter(description = "Facility UUID") @PathVariable String id) {
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
    @Operation(summary = "Create a facility", description = "Creates a new facility with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Facility created successfully",
                    content = @Content(schema = @Schema(implementation = FacilityResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/")
    public ResponseEntity<FacilityResponseBody> createFacility(
            @Parameter(description = "Facility attributes required to create a new record")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Facility attributes required to create a new record",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FacilityPostRequestBody.class)))
            @RequestBody FacilityPostRequestBody requestBody) {
        try {
            Facility createdFacility = this.createFacilityUseCase.create(
                    requestBody.name,
                    requestBody.facilityType,
                    requestBody.location,
                    requestBody.storageCapacity,
                    requestBody.processingCapacity,
                    requestBody.unloadingTime,
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
     * POST /facilities/bulk
     *
     * Creates multiple facilities from a JSON array in the request body.
     *
     * @param requestBody bulk payload with one or more facility records
     * @return ResponseEntity with import summary and HTTP 201 if all succeed,
     *         or HTTP 400 when one or more items fail validation or persistence
     */
    @Operation(summary = "Bulk create facilities", description = "Creates multiple facilities from a JSON array in the request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All facilities created successfully",
                    content = @Content(schema = @Schema(implementation = BulkImportResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or one or more items could not be created")
    })
    @PostMapping(ApiRoutes.BULK)
    public ResponseEntity<BulkImportResponseBody> bulkCreateFacilities(
            @Parameter(description = "JSON array of facilities or object {\"facilities\": [...]}")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON array of facilities or object {\"facilities\": [...]}",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FacilityBulkPostRequestBody.class)))
            @RequestBody FacilityBulkPostRequestBody requestBody) {
        return executeBulkCreate(requestBody);
    }

    /**
     * POST /facilities/bulk/import
     *
     * Uploads a JSON file containing many facilities to create in a single operation.
     *
     * @param file multipart JSON file (array or object with a facilities property)
     * @return ResponseEntity with import summary and HTTP 201 if all succeed,
     *         or HTTP 400 when the file is invalid or one or more items fail
     */
    @Operation(summary = "Import facilities from JSON file", description = "Uploads a JSON file containing many facilities to create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All facilities created successfully",
                    content = @Content(schema = @Schema(implementation = BulkImportResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or one or more items could not be created")
    })
    @PostMapping(value = ApiRoutes.BULK_IMPORT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkImportResponseBody> importFacilitiesFromFile(
            @Parameter(description = "JSON file with facilities (array or {\"facilities\": [...]})")
            @RequestPart("file") MultipartFile file) {
        FacilityBulkPostRequestBody requestBody = this.bulkImportMultipartSupport.parseJsonFile(
                file, FacilityBulkPostRequestBody.class);
        return executeBulkCreate(requestBody);
    }

    /**
     * Runs the bulk create use case and maps the outcome to an HTTP response.
     *
     * @param requestBody bulk payload with facility records to create
     * @return ResponseEntity with import summary and appropriate HTTP status
     */
    private ResponseEntity<BulkImportResponseBody> executeBulkCreate(FacilityBulkPostRequestBody requestBody) {
        List<FacilityPostRequestBody> items = requestBody.facilities;
        BulkCreateOutcome outcome = this.bulkCreateFacilitiesUseCase.createAll(
                items.stream().map(item -> item.name).toList(),
                items.stream().map(item -> item.facilityType).toList(),
                items.stream().map(item -> item.location).toList(),
                items.stream().map(item -> item.storageCapacity).toList(),
                items.stream().map(item -> item.processingCapacity).toList(),
                items.stream().map(item -> item.unloadingTime).toList(),
                items.stream().map(item -> item.openingFixedCost).toList(),
                items.stream().map(item -> item.status).toList());
        HttpStatus status = outcome.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(BulkImportResponseMapper.toResponseBody(outcome), status);
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
    @Operation(summary = "Update a facility", description = "Updates an existing facility with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility updated successfully",
                    content = @Content(schema = @Schema(implementation = FacilityResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Facility not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FacilityResponseBody> updateFacility(
            @Parameter(description = "Facility UUID") @PathVariable String id,
            @Parameter(description = "Full facility payload used to replace the existing record")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Full facility payload used to replace the existing record",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FacilityPutRequestBody.class)))
            @RequestBody FacilityPutRequestBody requestBody) {
        try {
            UUID facilityId = UUID.fromString(id);
            Facility updatedFacility = this.updateFacilityUseCase.update(
                    facilityId,
                    requestBody.name,
                    requestBody.facilityType,
                    requestBody.location,
                    requestBody.storageCapacity,
                    requestBody.processingCapacity,
                    requestBody.unloadingTime,
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
    @Operation(summary = "Delete a facility", description = "Deletes a facility by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility deleted successfully",
                    content = @Content(schema = @Schema(implementation = FacilityResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Facility not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<FacilityResponseBody> deleteFacility(
            @Parameter(description = "Facility UUID") @PathVariable String id) {
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
