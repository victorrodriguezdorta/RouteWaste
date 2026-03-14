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
     * @param sortBy      optional column to sort by: "capacity", "cost" or "type"
     * @param sortOrder   sort direction: "asc" (default) or "desc"
     * @param vehicleType optional vehicle type filter (enum name, e.g. "COLLECTION_TRUCK")
     * @return paginated list of vehicles or 400 if parameters are invalid
     */
    @GetMapping("/")
    public ResponseEntity<VehiclePageResponseBody> getVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String vehicleType) {
        if (page < 0 || size <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Map frontend column key to the actual MongoDB / domain field name
        String mongoSortField = switch (sortBy != null ? sortBy : "") {
            case "capacity" -> "transportCapacity.value";
            case "cost"     -> "costPerKilometer.amount";
            case "type"     -> "vehicleType";
            default         -> null;
        };

        Sort sort = Sort.unsorted();
        if (mongoSortField != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, mongoSortField);
        }

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
        List<VehicleResponseBody> responseBodies = vehiclePage.getContent().stream()
                .map(VehicleResponseMapper::toResponseBody)
                .toList();

        VehiclePageResponseBody response = new VehiclePageResponseBody();
        response.content = responseBodies;
        response.totalElements = vehiclePage.getTotalElements();
        response.totalPages = vehiclePage.getTotalPages();
        response.page = vehiclePage.getNumber();
        response.size = vehiclePage.getSize();
        response.numberOfElements = vehiclePage.getNumberOfElements();
        response.first = vehiclePage.isFirst();
        response.last = vehiclePage.isLast();

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
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseBody> getVehicleById(@PathVariable String id) {
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
    @PostMapping("/")
    public ResponseEntity<VehicleResponseBody> createVehicle(@RequestBody VehiclePostRequestBody requestBody) {
        Vehicle createdVehicle = this.createVehicleUseCase.create(
                requestBody.vehicleType,
                requestBody.transportCapacity,
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
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseBody> updateVehicle(
            @PathVariable String id,
            @RequestBody VehiclePutRequestBody requestBody) {
        try {
            UUID vehicleId = UUID.fromString(id);
            Vehicle updatedVehicle = this.updateVehicleUseCase.update(
                    vehicleId,
                    requestBody.vehicleType,
                    requestBody.transportCapacity,
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
    @DeleteMapping("/{id}")
    public ResponseEntity<VehicleResponseBody> deleteVehicle(@PathVariable String id) {
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
