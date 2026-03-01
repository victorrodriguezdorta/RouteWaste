package es.ull.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import es.ull.project.adapter.memory.InMemoryContainerRepository;
import es.ull.project.adapter.memory.InMemoryFacilityRepository;
import es.ull.project.adapter.memory.InMemoryInfrastructurePlanRepository;
import es.ull.project.adapter.memory.InMemoryServiceAssignmentRepository;
import es.ull.project.adapter.memory.InMemoryVehicleRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;

/**
 * Configuration class for in-memory repositories.
 * This class defines beans for repository implementations that store data in memory.
 */
@Configuration
@Profile("memory")
public class MemoryConfiguration {

    /**
     * Creates a bean for the ContainerRepository.
     * 
     * @return a new InMemoryContainerRepository instance
     */
    @Bean
    public ContainerRepository containerRepository() {
        return new InMemoryContainerRepository();
    }

    /**
     * Creates a bean for the FacilityRepository.
     * 
     * @return a new InMemoryFacilityRepository instance
     */
    @Bean
    public FacilityRepository facilityRepository() {
        return new InMemoryFacilityRepository();
    }

    /**
     * Creates a bean for the VehicleRepository.
     * 
     * @return a new InMemoryVehicleRepository instance
     */
    @Bean
    public VehicleRepository vehicleRepository() {
        return new InMemoryVehicleRepository();
    }

    /**
     * Creates a bean for the ServiceAssignmentRepository.
     * 
     * @return a new InMemoryServiceAssignmentRepository instance
     */
    @Bean
    public ServiceAssignmentRepository serviceAssignmentRepository() {
        return new InMemoryServiceAssignmentRepository();
    }

    /**
     * Creates a bean for the InfrastructurePlanRepository.
     * 
     * @return a new InMemoryInfrastructurePlanRepository instance
     */
    @Bean
    public InfrastructurePlanRepository infrastructurePlanRepository() {
        return new InMemoryInfrastructurePlanRepository();
    }
}
