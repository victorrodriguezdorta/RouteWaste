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

import es.ull.project.adapter.rest.mapper.FacilityResponseMapper;
import es.ull.project.adapter.rest.request.facility.FacilityPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPutRequestBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.application.service.facility.CreateFacilityService;
import es.ull.project.application.service.facility.DeleteFacilityService;
import es.ull.project.application.service.facility.ReadFacilityService;
import es.ull.project.application.service.facility.UpdateFacilityService;
import es.ull.project.domain.entity.Facility;

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

    /**
     * Use case for reading facility data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadFacilityService readFacilityService;

    /**
     * Use case for creating new facilities.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateFacilityService createFacilityService;

    /**
     * Use case for updating existing facilities.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateFacilityService updateFacilityService;

    /**
     * Use case for deleting facilities.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteFacilityService deleteFacilityService;

    /**
     * GET /facilities/
     * 
     * Retrieves all facilities in the system.
     * 
     * This endpoint returns a list of all available facilities without pagination.
     * The facilities are returned as FacilityResponseBody DTOs serialized to JSON.
     * 
     * @return ResponseEntity containing a list of all facilities and HTTP 200 (OK) status
     */
    @GetMapping("/")
    public ResponseEntity<List<FacilityResponseBody>> getFacilities() {
        List<Facility> facilities = this.readFacilityService.fetchAll();
        List<FacilityResponseBody> responseBodies = facilities.stream()
                .map(FacilityResponseMapper::toResponseBody)
                .toList();
        return new ResponseEntity<>(responseBodies, HttpStatus.OK);
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
            Facility facility = this.readFacilityService.fetch(facilityId);
            FacilityResponseBody responseBody = FacilityResponseMapper.toResponseBody(facility);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /facilities/
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
            Facility createdFacility = this.createFacilityService.create(
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
            Facility updatedFacility = this.updateFacilityService.update(
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
            Facility deletedFacility = this.deleteFacilityService.delete(facilityId);
            FacilityResponseBody responseBody = FacilityResponseMapper.toResponseBody(deletedFacility);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
