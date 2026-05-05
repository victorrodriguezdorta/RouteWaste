package es.ull.project.adapter.rest.controller;

import es.ull.project.adapter.rest.mapper.DailyPlanResponseMapper;
import es.ull.project.adapter.rest.mapper.InfrastructurePlanResponseMapper;
import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.application.usecase.dailyplan.ReadDailyPlanUseCase;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlanUseCase;
import es.ull.project.application.usecase.infrastructureplan.ReadInfrastructurePlanUseCase;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.InfrastructurePlan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ReadInfrastructurePlanUseCase readInfrastructurePlanUseCase;

    /**
     * Use case for reading daily plan data.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private ReadDailyPlanUseCase readDailyPlanUseCase;

    /**
     * Use case for deleting infrastructure plans.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private DeleteInfrastructurePlanUseCase deleteInfrastructurePlanUseCase;

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
    @Operation(summary = "Get all infrastructure plans", description = "Retrieves a list of all infrastructure plans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Infrastructure plans retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/")
    public ResponseEntity<List<InfrastructurePlanResponseBody>> getInfrastructurePlans() {
        List<InfrastructurePlan> plans = this.readInfrastructurePlanUseCase.fetchAll();
        List<InfrastructurePlanResponseBody> responseBodies = plans.stream()
                .map(plan -> {
                    List<DailyPlan> dailyPlans = readDailyPlanUseCase.findByInfrastructurePlanId(plan.getId());
                    List<DailyPlanResponseBody> dailyPlanBodies = dailyPlans.stream()
                            .map(DailyPlanResponseMapper::toResponseBody)
                            .toList();
                    return InfrastructurePlanResponseMapper.toResponseBody(plan, dailyPlanBodies);
                })
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
    @Operation(summary = "Get infrastructure plan by ID", description = "Retrieves a specific infrastructure plan by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Infrastructure plan found"),
            @ApiResponse(responseCode = "404", description = "Infrastructure plan not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InfrastructurePlanResponseBody> getInfrastructurePlanById(
            @Parameter(description = "Infrastructure plan UUID") @PathVariable String id) {
        try {
            UUID planId = UUID.fromString(id);
            InfrastructurePlan plan = this.readInfrastructurePlanUseCase.fetch(planId);
            List<DailyPlan> dailyPlans = readDailyPlanUseCase.findByInfrastructurePlanId(planId);
            List<DailyPlanResponseBody> dailyPlanBodies = dailyPlans.stream()
                    .map(DailyPlanResponseMapper::toResponseBody)
                    .toList();
            InfrastructurePlanResponseBody responseBody = InfrastructurePlanResponseMapper.toResponseBody(plan, dailyPlanBodies);
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
    @Operation(summary = "Delete an infrastructure plan", description = "Deletes an infrastructure plan by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Infrastructure plan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Infrastructure plan not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<InfrastructurePlanResponseBody> deleteInfrastructurePlan(
            @Parameter(description = "Infrastructure plan UUID") @PathVariable String id) {
        UUID planId = UUID.fromString(id);
        InfrastructurePlan deletedPlan = this.deleteInfrastructurePlanUseCase.delete(planId);
        
        // Return the plan with an empty list of daily plans since they were cascade deleted
        InfrastructurePlanResponseBody responseBody = InfrastructurePlanResponseMapper.toResponseBody(deletedPlan, List.of());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
