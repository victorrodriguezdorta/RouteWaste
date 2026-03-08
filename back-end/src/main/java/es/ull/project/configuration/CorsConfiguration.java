package es.ull.project.configuration;

import org.springframework.context.annotation.Bean;
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
public class CorsConfiguration {

    /**
     * Configures CORS mapping for all endpoints.
     * 
     * This configuration:
     * - Allows requests from http://localhost:5173 (Vite dev server)
     * - Permits all HTTP methods (GET, POST, PUT, DELETE, OPTIONS, etc.)
     * - Allows all headers in requests
     * - Allows credentials (cookies, authorization headers)
     * 
     * @return WebMvcConfigurer with CORS settings
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
