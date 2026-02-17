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

import es.ull.project.adapter.rest.mapper.VehicleResponseMapper;
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePutRequestBody;
import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import es.ull.project.application.service.vehicle.CreateVehicleService;
import es.ull.project.application.service.vehicle.DeleteVehicleService;
import es.ull.project.application.service.vehicle.ReadVehicleService;
import es.ull.project.application.service.vehicle.UpdateVehicleService;
import es.ull.project.domain.entity.Vehicle;

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
    private ReadVehicleService readVehicleService;

    /**
     * Use case for creating new vehicles.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateVehicleService createVehicleService;

    /**
     * Use case for updating existing vehicles.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateVehicleService updateVehicleService;

    /**
     * Use case for deleting vehicles.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteVehicleService deleteVehicleService;

    /**
     * GET /vehicles/
     * 
     * Retrieves all vehicles in the system.
     * 
     * This endpoint returns a list of all available vehicles without pagination.
     * The vehicles are returned as VehicleResponseBody DTOs serialized to JSON.
     * 
     * @return ResponseEntity containing a list of all vehicles and HTTP 200 (OK) status
     */
    @GetMapping("/")
    public ResponseEntity<List<VehicleResponseBody>> getVehicles() {
        List<Vehicle> vehicles = this.readVehicleService.fetchAll();
        List<VehicleResponseBody> responseBodies = vehicles.stream()
                .map(VehicleResponseMapper::toResponseBody)
                .toList();
        return new ResponseEntity<>(responseBodies, HttpStatus.OK);
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
            Vehicle vehicle = this.readVehicleService.fetch(vehicleId);
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
        try {
            Vehicle createdVehicle = this.createVehicleService.create(
                    requestBody.vehicleType,
                    requestBody.transportCapacity,
                    requestBody.costPerKilometer
            );
            VehicleResponseBody responseBody = VehicleResponseMapper.toResponseBody(createdVehicle);
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
            Vehicle updatedVehicle = this.updateVehicleService.update(
                    vehicleId,
                    requestBody.vehicleType,
                    requestBody.transportCapacity,
                    requestBody.costPerKilometer
            );
            VehicleResponseBody responseBody = VehicleResponseMapper.toResponseBody(updatedVehicle);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
            Vehicle deletedVehicle = this.deleteVehicleService.delete(vehicleId);
            VehicleResponseBody responseBody = VehicleResponseMapper.toResponseBody(deletedVehicle);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
