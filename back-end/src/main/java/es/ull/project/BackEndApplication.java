package es.ull.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * This is the entry point for the back-end service.
 */
@SpringBootApplication
public class BackEndApplication {

	/**
	 * Main method that starts the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackEndApplication.class, args);
	}
}
