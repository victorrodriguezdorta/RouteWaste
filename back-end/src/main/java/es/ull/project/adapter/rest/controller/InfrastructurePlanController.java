package es.ull.project.adapter.rest.controller;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.adapter.mongodb.mapper.InfrastructurePlanFieldMapper;
import es.ull.project.adapter.rest.mapper.DailyPlanResponseMapper;
import es.ull.project.adapter.rest.mapper.InfrastructurePlanListResponseMapper;
import es.ull.project.adapter.rest.mapper.InfrastructurePlanResponseMapper;
import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanPageResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.adapter.rest.sse.InfrastructurePlanExecutionEventPublisher;
import es.ull.project.application.usecase.dailyplan.ReadDailyPlanUseCase;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlanUseCase;
import es.ull.project.application.usecase.infrastructureplan.ReadInfrastructurePlanUseCase;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.InfrastructurePlan;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * InfrastructurePlanController
 *
 * REST controller for InfrastructurePlan resources.
 */
@Tag(name = "Infrastructure Plans")
@RestController
@RequestMapping(ApiRoutes.INFRASTRUCTURE_PLANS)
public class InfrastructurePlanController {

    private static final int ZERO = 0;
    private static final long SSE_TIMEOUT_MS = 30L * 60L * 1000L;

    @Autowired
    private ReadInfrastructurePlanUseCase readInfrastructurePlanUseCase;

    @Autowired
    private ReadDailyPlanUseCase readDailyPlanUseCase;

    @Autowired
    private DeleteInfrastructurePlanUseCase deleteInfrastructurePlanUseCase;

    @Autowired
    private InfrastructurePlanExecutionEventPublisher executionEventPublisher;

    /**
     * GET /infrastructure-plans/execution-events
     *
     * @return SSE stream of infrastructure plan execution state updates
     */
    @Operation(
            summary = "Subscribe to infrastructure plan execution events",
            description = "Server-sent events stream notifying when plan execution state changes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SSE stream opened"),
            @ApiResponse(responseCode = "500", description = "The event stream could not be established")
    })
    @GetMapping(value = "/execution-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToExecutionEvents() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        this.executionEventPublisher.register(emitter);
        return emitter;
    }

    /**
     * GET /infrastructure-plans/
     *
     * @param page      zero-based page index
     * @param size      number of elements per page
     * @param sortBy    field to sort by (id, executedAt, estimatedTotalCost, numberOfDays, averagePickupTimeMinutes)
     * @param sortOrder sort direction: asc or desc
     * @return a paginated response containing lightweight infrastructure plan summaries,
     *         or a 400 Bad Request if the pagination or sort parameters are invalid
     */
    @Operation(summary = "Get all infrastructure plans", description = "Retrieves a paginated lightweight list of all infrastructure plans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Infrastructure plans retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InfrastructurePlanPageResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<InfrastructurePlanPageResponseBody> getInfrastructurePlans(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of elements per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (id, executedAt, estimatedTotalCost, numberOfDays, averagePickupTimeMinutes)")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String sortOrder) {
        if (page < ZERO || size <= ZERO) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (sortBy != null && !sortBy.isBlank() && !InfrastructurePlanFieldMapper.isValidField(sortBy)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortOrder));
        Page<InfrastructurePlan> infrastructurePlanPage = this.readInfrastructurePlanUseCase.fetchAll(pageable);
        return buildSuccessResponse(infrastructurePlanPage);
    }

    /**
     * Builds a {@link Sort} instance from the given API field name and sort direction.
     *
     * <p>Returns {@link Sort#unsorted()} when the field name is blank or cannot be mapped
     * to a MongoDB field path.
     *
     * @param sortBy    the public API field name to sort by
     * @param sortOrder the sort direction string; {@code "desc"} maps to descending, anything else to ascending
     * @return the resolved {@link Sort}, or {@link Sort#unsorted()} if the field is invalid
     */
    private Sort buildSort(String sortBy, String sortOrder) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.DESC, MongoFields.EXECUTED_AT);
        }
        String mongoField = InfrastructurePlanFieldMapper.toMongoField(sortBy);
        if (mongoField == null) {
            return Sort.unsorted();
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, mongoField);
    }

    /**
     * Wraps a {@link Page} of {@link InfrastructurePlan} entities into a paginated HTTP response.
     *
     * @param infrastructurePlanPage the page of infrastructure plans returned by the use case
     * @return a 200 OK {@link ResponseEntity} containing the populated {@link InfrastructurePlanPageResponseBody}
     */
    private ResponseEntity<InfrastructurePlanPageResponseBody> buildSuccessResponse(Page<InfrastructurePlan> infrastructurePlanPage) {
        List<InfrastructurePlanListResponseBody> responseBodies = infrastructurePlanPage.getContent().stream()
                .map(InfrastructurePlanListResponseMapper::toResponseBody)
                .toList();
        InfrastructurePlanPageResponseBody response = new InfrastructurePlanPageResponseBody();
        response.content = responseBodies;
        response.totalElements = new TotalElements(infrastructurePlanPage.getTotalElements());
        response.totalPages = new TotalPages(infrastructurePlanPage.getTotalPages());
        response.page = new PageNumber(infrastructurePlanPage.getNumber());
        response.size = new PageSize(infrastructurePlanPage.getSize());
        response.numberOfElements = new NumberOfElements(infrastructurePlanPage.getNumberOfElements());
        response.first = new PageFlag(infrastructurePlanPage.isFirst());
        response.last = new PageFlag(infrastructurePlanPage.isLast());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /infrastructure-plans/{id}
     *
     * @param id the string representation of the infrastructure plan UUID
     * @return a 200 OK response with the plan details, 404 if not found, or 400 if the ID format is invalid
     */
    @Operation(summary = "Get infrastructure plan by ID", description = "Retrieves a specific infrastructure plan by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Infrastructure plan found",
                    content = @Content(schema = @Schema(implementation = InfrastructurePlanResponseBody.class))),
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
     * @param id the string representation of the infrastructure plan UUID to delete
     * @return a 200 OK response with the deleted plan details, or 400 if the ID format is invalid
     */
    @Operation(summary = "Delete an infrastructure plan", description = "Deletes an infrastructure plan by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Infrastructure plan deleted successfully",
                    content = @Content(schema = @Schema(implementation = InfrastructurePlanResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Infrastructure plan not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<InfrastructurePlanResponseBody> deleteInfrastructurePlan(
            @Parameter(description = "Infrastructure plan UUID") @PathVariable String id) {
        UUID planId = UUID.fromString(id);
        InfrastructurePlan deletedPlan = this.deleteInfrastructurePlanUseCase.delete(planId);
        InfrastructurePlanResponseBody responseBody = InfrastructurePlanResponseMapper.toResponseBody(deletedPlan, List.of());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
