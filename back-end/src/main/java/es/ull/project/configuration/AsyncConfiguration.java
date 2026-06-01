package es.ull.project.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables asynchronous execution for long-running algorithm jobs.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
}
