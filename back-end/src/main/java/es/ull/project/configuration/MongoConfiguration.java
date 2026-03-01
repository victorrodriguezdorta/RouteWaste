package es.ull.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import es.ull.project.adapter.mongo.MongoDbContainerRepository;
import es.ull.project.adapter.mongo.MongoDbInfrastructurePlanRepository;
import es.ull.project.adapter.mongo.MongoDbServiceAssignmentRepository;
import es.ull.project.adapter.mongo.spring.ContainerSpringRepository;
import es.ull.project.adapter.mongo.spring.FacilitySpringRepository;
import es.ull.project.adapter.mongo.spring.InfrastructurePlanSpringRepository;
import es.ull.project.adapter.mongo.spring.ServiceAssignmentSpringRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;

/**
 * Configuration class to register Mongo-backed repositories.
 *
 * Note: enable this configuration (and disable MemoryConfiguration) when
 * you want the application to use MongoDB persistence.
 */
@Configuration
@Profile("mongo")
public class MongoConfiguration {

    @Bean
    public ContainerRepository containerRepository(ContainerSpringRepository springRepo) {
        return new MongoDbContainerRepository(springRepo);
    }

    @Bean
    public es.ull.project.application.repository.VehicleRepository vehicleRepository(es.ull.project.adapter.mongo.spring.VehicleSpringRepository springRepo) {
        return new es.ull.project.adapter.mongo.MongoDbVehicleRepository(springRepo);
    }

    @Bean
    public FacilityRepository facilityRepository(FacilitySpringRepository springRepo) {
        return new es.ull.project.adapter.mongo.MongoDbFacilityRepository(springRepo);
    }

    /**
     * Provide in-memory implementations for repositories not yet migrated to Mongo.
     * This allows the application to start with profile 'mongo' while only some
     * aggregates use MongoDB.
     */
    

    @Bean
    public ServiceAssignmentRepository serviceAssignmentRepository(ServiceAssignmentSpringRepository springRepo,
            ContainerRepository containerRepository,
            FacilityRepository facilityRepository) {
        return new MongoDbServiceAssignmentRepository(springRepo, containerRepository, facilityRepository);
    }

    @Bean
    public InfrastructurePlanRepository infrastructurePlanRepository(InfrastructurePlanSpringRepository springRepo,
            FacilityRepository facilityRepository,
            ServiceAssignmentRepository serviceAssignmentRepository) {
        return new MongoDbInfrastructurePlanRepository(springRepo, facilityRepository, serviceAssignmentRepository);
    }
}
