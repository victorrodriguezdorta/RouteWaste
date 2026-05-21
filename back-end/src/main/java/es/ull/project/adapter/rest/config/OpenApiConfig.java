package es.ull.project.adapter.rest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Global OpenAPI configuration for the REST API.
 * Defines API metadata and tag ordering displayed in Swagger UI.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Sensor App API",
                version = "v1",
                description = "REST API for waste collection infrastructure management: containers, vehicles, "
                        + "facilities, route algorithm execution, and infrastructure plans.",
                contact = @Contact(name = "ULL TFG Project"),
                license = @License(name = "Academic use")
        ),
        tags = {
                @Tag(name = "Application Overview", description = "Dashboard aggregate counts and recent plans"),
                @Tag(name = "Containers", description = "Waste container CRUD and listing with filters"),
                @Tag(name = "Vehicles", description = "Collection vehicle CRUD and listing"),
                @Tag(name = "Facilities", description = "Treatment and transfer facility CRUD and listing"),
                @Tag(name = "Infrastructure Plans", description = "Algorithm output plans and daily schedules"),
                @Tag(name = "Algorithms", description = "Route optimization algorithm execution")
        }
)
public class OpenApiConfig {
}
