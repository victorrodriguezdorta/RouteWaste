package es.ull.project.adapter.rest.controller;

import es.ull.project.adapter.mongodb.mapper.VehicleFieldMapper;
import es.ull.project.adapter.rest.mapper.VehicleResponseMapper;
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePutRequestBody;
import es.ull.project.adapter.rest.response.vehicle.VehiclePageResponseBody;
import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import es.ull.project.application.usecase.vehicle.CreateVehicleUseCase;
import es.ull.project.application.usecase.vehicle.DeleteVehicleUseCase;
import es.ull.project.application.usecase.vehicle.ReadVehicleUseCase;
import es.ull.project.application.usecase.vehicle.UpdateVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
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
 * VehicleController
 * 
 * REST controller that handles HTTP requests for Vehicle resources.
 * This controller exposes RESTful endpoints following REST architectural principles
 * to create, read, update, and delete vehicles in the system.
 * 
 * The controller uses Data Transfer Objects (DTOs) instead of domain entities
 * to avoid tight coupling and facilitate data validation.
 * 
 * Base path: defined in ApiRoutes.VEHICLES
 */
@RestController
@RequestMapping(ApiRoutes.VEHICLES)
public class VehicleController {

    private static final int ZERO = 0;

    /**
     * Use case for reading vehicle data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadVehicleUseCase readVehicleUseCase;

    /**
     * Use case for creating new vehicles.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateVehicleUseCase createVehicleUseCase;

    /**
     * Use case for updating existing vehicles.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateVehicleUseCase updateVehicleUseCase;

    /**
     * Use case for deleting vehicles.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteVehicleUseCase deleteVehicleUseCase;

    /**
     * GET /vehicles/
     *
     * Retrieves vehicles in the system with pagination, optional sort and optional type filter.
     *
     * @param page        page index (0-based, default 0)
     * @param size        page size (default 10)
     * @param sortBy      optional column to sort by: capacityKilograms, CapacityLiters, cost, type, etc.
     * @param sortOrder   sort direction: "asc" (default) or "desc"
     * @param vehicleType optional vehicle type filter (enum name, e.g. "COLLECTION_TRUCK")
     * @return paginated list of vehicles or 400 if parameters are invalid
     */
    @Operation(summary = "Get all vehicles", description = "Retrieves a paginated list of vehicles with optional sorting and filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping("/")
    public ResponseEntity<VehiclePageResponseBody> getVehicles(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of elements per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (e.g., vehicleType, capacityKilograms, CapacityLiters, cost, etc.)") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String sortOrder,
            @Parameter(description = "Filter by vehicle type") @RequestParam(required = false) String vehicleType) {
        if (page < ZERO || size <= ZERO) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (sortBy != null && !sortBy.isBlank() && !VehicleFieldMapper.isValidField(sortBy)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Sort sort = buildSort(sortBy, sortOrder);
        VehicleType vehicleTypeFilter = null;
        if (vehicleType != null && !vehicleType.isBlank()) {
            try {
                vehicleTypeFilter = VehicleType.valueOf(vehicleType);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Vehicle> vehiclePage = this.readVehicleUseCase.fetchAll(pageable, vehicleTypeFilter);
        return buildSuccessResponse(vehiclePage);
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
        String mongoField = VehicleFieldMapper.toMongoField(sortBy);
        if (mongoField == null) {
            return Sort.unsorted();
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        return Sort.by(direction, mongoField);
    }

    /**
     * Builds a successful response from a vehicle page.
     *
     * @param vehiclePage the page of vehicles
     * @return ResponseEntity with vehicle page response body and HTTP 200
     */
    private ResponseEntity<VehiclePageResponseBody> buildSuccessResponse(Page<Vehicle> vehiclePage) {
        List<VehicleResponseBody> responseBodies = vehiclePage.getContent().stream()
                .map(VehicleResponseMapper::toResponseBody)
                .toList();
        VehiclePageResponseBody response = new VehiclePageResponseBody();
        response.content = responseBodies;
        response.totalElements = new TotalElements(vehiclePage.getTotalElements());
        response.totalPages = new TotalPages(vehiclePage.getTotalPages());
        response.page = new PageNumber(vehiclePage.getNumber());
        response.size = new PageSize(vehiclePage.getSize());
        response.numberOfElements = new NumberOfElements(vehiclePage.getNumberOfElements());
        response.first = new PageFlag(vehiclePage.isFirst());
        response.last = new PageFlag(vehiclePage.isLast());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /vehicles/{id}
     * 
     * Retrieves a specific vehicle by its unique identifier.
     * 
     * @param id the unique identifier of the vehicle (UUID format)
     * @return ResponseEntity containing the vehicle and HTTP 200 (OK) if found,
     *         or HTTP 404 (NOT_FOUND) if the vehicle does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @Operation(summary = "Get vehicle by ID", description = "Retrieves a specific vehicle by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseBody> getVehicleById(
            @Parameter(description = "Vehicle UUID") @PathVariable String id) {
        try {
            UUID vehicleId = UUID.fromString(id);
            Vehicle vehicle = this.readVehicleUseCase.fetch(vehicleId);
            VehicleResponseBody responseBody = VehicleResponseMapper.toResponseBody(vehicle);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /vehicles/
     * 
     * Creates a new vehicle with the data provided in the request body.
     * 
     * The request body must contain a VehiclePostRequestBody DTO with all
     * required vehicle attributes. The DTO is used instead of the domain entity
     * to decouple the API contract from the domain model.
     * 
     * @param requestBody the vehicle data to create (VehiclePostRequestBody DTO)
     * @return ResponseEntity containing the created vehicle and HTTP 201 (CREATED),
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @Operation(summary = "Create a vehicle", description = "Creates a new vehicle with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/")
    public ResponseEntity<VehicleResponseBody> createVehicle(
            @Parameter(description = "Vehicle data") @RequestBody VehiclePostRequestBody requestBody) {
        Vehicle createdVehicle = this.createVehicleUseCase.create(
                requestBody.vehicleType,
                requestBody.capacityKilograms,
                requestBody.capacityLiters,
                requestBody.costPerKilometer
        );
        VehicleResponseBody responseBody = VehicleResponseMapper.toResponseBody(createdVehicle);
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    /**
     * PUT /vehicles/{id}
     * 
     * Updates an existing vehicle with the data provided in the request body.
     * 
     * The request body must contain a VehiclePutRequestBody DTO with all
     * vehicle attributes to update. All fields in the DTO will be applied
     * to the vehicle entity.
     * 
     * @param id the unique identifier of the vehicle to update (UUID format)
     * @param requestBody the new vehicle data (VehiclePutRequestBody DTO)
     * @return ResponseEntity containing the updated vehicle and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the vehicle does not exist,
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @Operation(summary = "Update a vehicle", description = "Updates an existing vehicle with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseBody> updateVehicle(
            @Parameter(description = "Vehicle UUID") @PathVariable String id,
            @Parameter(description = "Updated vehicle data") @RequestBody VehiclePutRequestBody requestBody) {
        try {
            UUID vehicleId = UUID.fromString(id);
            Vehicle updatedVehicle = this.updateVehicleUseCase.update(
                    vehicleId,
                    requestBody.vehicleType,
                    requestBody.capacityKilograms,
                    requestBody.capacityLiters,
                    requestBody.costPerKilometer
            );
            VehicleResponseBody responseBody = VehicleResponseMapper.toResponseBody(updatedVehicle);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /vehicles/{id}
     * 
     * Deletes a vehicle by its unique identifier.
     * 
     * Upon successful deletion, the deleted vehicle entity is returned
     * in the response body for confirmation purposes.
     * 
     * @param id the unique identifier of the vehicle to delete (UUID format)
     * @return ResponseEntity containing the deleted vehicle and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the vehicle does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @Operation(summary = "Delete a vehicle", description = "Deletes a vehicle by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<VehicleResponseBody> deleteVehicle(
            @Parameter(description = "Vehicle UUID") @PathVariable String id) {
        try {
            UUID vehicleId = UUID.fromString(id);
            Vehicle deletedVehicle = this.deleteVehicleUseCase.delete(vehicleId);
            VehicleResponseBody responseBody = VehicleResponseMapper.toResponseBody(deletedVehicle);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
