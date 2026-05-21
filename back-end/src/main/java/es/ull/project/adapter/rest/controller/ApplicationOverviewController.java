package es.ull.project.adapter.rest.controller;

import es.ull.project.adapter.rest.mapper.ApplicationOverviewResponseMapper;
import es.ull.project.adapter.rest.response.overview.ApplicationOverviewResponseBody;
import es.ull.project.application.usecase.overview.GetApplicationOverviewUseCase;
import es.ull.project.domain.readmodel.ApplicationOverview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint exposing aggregate counts and recent infrastructure plans for dashboard views.
 */
@Tag(name = "Application Overview")
@RestController
@RequestMapping(ApiRoutes.APPLICATION_OVERVIEW)
public class ApplicationOverviewController {

    @Autowired
    private GetApplicationOverviewUseCase getApplicationOverviewUseCase;

    /**
     * GET /api/v1/application-overview
     *
     * @return entity totals and up to three most recently executed infrastructure plans
     */
    @Operation(
            summary = "Application overview",
            description = "Returns counts of containers, vehicles, facilities, infrastructure plans, and the latest executed plans.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overview returned",
                    content = @Content(schema = @Schema(implementation = ApplicationOverviewResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<ApplicationOverviewResponseBody> getApplicationOverview() {
        ApplicationOverview overview = this.getApplicationOverviewUseCase.fetch();
        ApplicationOverviewResponseBody body = ApplicationOverviewResponseMapper.toResponseBody(overview);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
