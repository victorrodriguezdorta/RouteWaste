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

import es.ull.project.adapter.rest.mapper.ContainerResponseMapper;
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPutRequestBody;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.application.service.container.CreateContainerService;
import es.ull.project.application.service.container.DeleteContainerService;
import es.ull.project.application.service.container.ReadContainerService;
import es.ull.project.application.service.container.UpdateContainerService;
import es.ull.project.domain.entity.Container;

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

    /**
     * Use case for reading container data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadContainerService readContainerService;

    /**
     * Use case for creating new containers.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateContainerService createContainerService;

    /**
     * Use case for updating existing containers.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateContainerService updateContainerService;

    /**
     * Use case for deleting containers.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteContainerService deleteContainerService;

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
    public ResponseEntity<List<ContainerResponseBody>> getContainers() {
        List<Container> containers = this.readContainerService.fetchAll();
        List<ContainerResponseBody> responseBodies = containers.stream()
                .map(ContainerResponseMapper::toResponseBody)
                .toList();
        return new ResponseEntity<>(responseBodies, HttpStatus.OK);
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
            Container container = this.readContainerService.fetch(containerId);
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
            Container createdContainer = this.createContainerService.create(
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
            Container updatedContainer = this.updateContainerService.update(
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
            Container deletedContainer = this.deleteContainerService.delete(containerId);
            ContainerResponseBody responseBody = ContainerResponseMapper.toResponseBody(deletedContainer);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
