package es.ull.project.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfiguration
 * 
 * Configuration class for Cross-Origin Resource Sharing (CORS).
 * This allows the front-end application running on a different origin
 * (e.g., http://localhost:5173) to make HTTP requests to this backend.
 * 
 * Without this configuration, browsers will block requests from the front-end
 * due to CORS policy restrictions.
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    private static final String ALL_PATHS = "/**";
    private static final String[] ALLOWED_ORIGINS = { "http://localhost:5173", "http://localhost:5174", "http://localhost:3000" };
    private static final String[] ALLOWED_METHODS = { "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH" };
    private static final String ALL_HEADERS = "*";

    /**
     * Applies CORS mappings to the registry.
     *
     * @param registry the CORS registry
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping(ALL_PATHS)
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods(ALLOWED_METHODS)
                .allowedHeaders(ALL_HEADERS)
                .allowCredentials(true)
                .maxAge(3600);
    }
}
