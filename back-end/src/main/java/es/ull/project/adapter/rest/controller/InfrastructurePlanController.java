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

import es.ull.project.adapter.rest.mapper.InfrastructurePlanResponseMapper;
import es.ull.project.adapter.rest.request.infrastructureplan.InfrastructurePlanPostRequestBody;
import es.ull.project.adapter.rest.request.infrastructureplan.InfrastructurePlanPutRequestBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.application.service.infrastructureplan.CreateInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.DeleteInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.ReadInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.UpdateInfrastructurePlanService;
import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * InfrastructurePlanController
 * 
 * REST controller that handles HTTP requests for InfrastructurePlan resources.
 * This controller exposes RESTful endpoints following REST architectural principles
 * to create, read, update, and delete infrastructure plans in the system.
 * 
 * The controller uses Data Transfer Objects (DTOs) instead of domain entities
 * to avoid tight coupling and facilitate data validation.
 * 
 * Base path: defined in ApiRoutes.INFRASTRUCTURE_PLANS
 */
@RestController
@RequestMapping(ApiRoutes.INFRASTRUCTURE_PLANS)
public class InfrastructurePlanController {

    /**
     * Use case for reading infrastructure plan data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadInfrastructurePlanService readInfrastructurePlanService;

    /**
     * Use case for creating new infrastructure plans.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private CreateInfrastructurePlanService createInfrastructurePlanService;

    /**
     * Use case for updating existing infrastructure plans.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private UpdateInfrastructurePlanService updateInfrastructurePlanService;

    /**
     * Use case for deleting infrastructure plans.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteInfrastructurePlanService deleteInfrastructurePlanService;

    /**
     * GET /infrastructure-plans/
     * 
     * Retrieves all infrastructure plans in the system.
     * 
     * This endpoint returns a list of all available infrastructure plans without pagination.
     * The plans are returned as InfrastructurePlanResponseBody DTOs serialized to JSON.
     * 
     * @return ResponseEntity containing a list of all infrastructure plans and HTTP 200 (OK) status
     */
    @GetMapping("/")
    public ResponseEntity<List<InfrastructurePlanResponseBody>> getInfrastructurePlans() {
        List<InfrastructurePlan> plans = this.readInfrastructurePlanService.fetchAll();
        List<InfrastructurePlanResponseBody> responseBodies = plans.stream()
                .map(InfrastructurePlanResponseMapper::toResponseBody)
                .toList();
        return new ResponseEntity<>(responseBodies, HttpStatus.OK);
    }

    /**
     * GET /infrastructure-plans/{id}
     * 
     * Retrieves a specific infrastructure plan by its unique identifier.
     * 
     * @param id the unique identifier of the infrastructure plan (UUID format)
     * @return ResponseEntity containing the infrastructure plan and HTTP 200 (OK) if found,
     *         or HTTP 404 (NOT_FOUND) if the plan does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @GetMapping("/{id}")
    public ResponseEntity<InfrastructurePlanResponseBody> getInfrastructurePlanById(@PathVariable String id) {
        try {
            UUID planId = UUID.fromString(id);
            InfrastructurePlan plan = this.readInfrastructurePlanService.fetch(planId);
            InfrastructurePlanResponseBody responseBody = InfrastructurePlanResponseMapper.toResponseBody(plan);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /infrastructure-plans/
     * 
     * Creates a new infrastructure plan with the data provided in the request body.
     * 
     * The request body must contain an InfrastructurePlanPostRequestBody DTO with all
     * required infrastructure plan attributes. The DTO is used instead of the domain entity
     * to decouple the API contract from the domain model.
     * 
     * @param requestBody the infrastructure plan data to create (InfrastructurePlanPostRequestBody DTO)
     * @return ResponseEntity containing the created infrastructure plan and HTTP 201 (CREATED),
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @PostMapping("/")
    public ResponseEntity<InfrastructurePlanResponseBody> createInfrastructurePlan(@RequestBody InfrastructurePlanPostRequestBody requestBody) {
        try {
            InfrastructurePlan createdPlan = this.createInfrastructurePlanService.create(
                    requestBody.period,
                    requestBody.maxBudget,
                    requestBody.servicePolicies
            );
            InfrastructurePlanResponseBody responseBody = InfrastructurePlanResponseMapper.toResponseBody(createdPlan);
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /infrastructure-plans/{id}
     * 
     * Updates an existing infrastructure plan with the data provided in the request body.
     * 
     * The request body must contain an InfrastructurePlanPutRequestBody DTO with all
     * infrastructure plan attributes to update. All fields in the DTO will be applied
     * to the infrastructure plan entity.
     * 
     * @param id the unique identifier of the infrastructure plan to update (UUID format)
     * @param requestBody the new infrastructure plan data (InfrastructurePlanPutRequestBody DTO)
     * @return ResponseEntity containing the updated infrastructure plan and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the plan does not exist,
     *         or HTTP 400 (BAD_REQUEST) if validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<InfrastructurePlanResponseBody> updateInfrastructurePlan(
            @PathVariable String id,
            @RequestBody InfrastructurePlanPutRequestBody requestBody) {
        try {
            UUID planId = UUID.fromString(id);
            InfrastructurePlan updatedPlan = this.updateInfrastructurePlanService.update(
                    planId,
                    requestBody.period,
                    requestBody.maxBudget,
                    requestBody.servicePolicies
            );
            InfrastructurePlanResponseBody responseBody = InfrastructurePlanResponseMapper.toResponseBody(updatedPlan);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /infrastructure-plans/{id}
     * 
     * Deletes an infrastructure plan by its unique identifier.
     * 
     * Upon successful deletion, the deleted infrastructure plan entity is returned
     * in the response body for confirmation purposes.
     * 
     * @param id the unique identifier of the infrastructure plan to delete (UUID format)
     * @return ResponseEntity containing the deleted infrastructure plan and HTTP 200 (OK),
     *         or HTTP 404 (NOT_FOUND) if the plan does not exist,
     *         or HTTP 400 (BAD_REQUEST) if the ID format is invalid
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<InfrastructurePlanResponseBody> deleteInfrastructurePlan(@PathVariable String id) {
        try {
            UUID planId = UUID.fromString(id);
            InfrastructurePlan deletedPlan = this.deleteInfrastructurePlanService.delete(planId);
            InfrastructurePlanResponseBody responseBody = InfrastructurePlanResponseMapper.toResponseBody(deletedPlan);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
