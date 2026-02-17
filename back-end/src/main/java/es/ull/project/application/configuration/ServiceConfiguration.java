package es.ull.project.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.service.container.CreateContainerService;
import es.ull.project.application.service.container.DeleteContainerService;
import es.ull.project.application.service.container.ReadContainerService;
import es.ull.project.application.service.container.UpdateContainerService;
import es.ull.project.application.service.facility.CreateFacilityService;
import es.ull.project.application.service.facility.DeleteFacilityService;
import es.ull.project.application.service.facility.ReadFacilityService;
import es.ull.project.application.service.facility.UpdateFacilityService;
import es.ull.project.application.service.infrastructureplan.CreateInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.DeleteInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.ReadInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.UpdateInfrastructurePlanService;
import es.ull.project.application.service.serviceassignment.CreateServiceAssignmentService;
import es.ull.project.application.service.serviceassignment.DeleteServiceAssignmentService;
import es.ull.project.application.service.serviceassignment.ReadServiceAssignmentService;
import es.ull.project.application.service.serviceassignment.UpdateServiceAssignmentService;
import es.ull.project.application.service.vehicle.CreateVehicleService;
import es.ull.project.application.service.vehicle.DeleteVehicleService;
import es.ull.project.application.service.vehicle.ReadVehicleService;
import es.ull.project.application.service.vehicle.UpdateVehicleService;

/**
 * Configuration class for application services.
 * This class defines all service beans following the hexagonal architecture pattern.
 */
@Configuration
public class ServiceConfiguration {

    /**
     * Creates a bean for the CreateContainerService.
     * @param repository the container repository
     * @return a new CreateContainerService instance
     */
    @Bean
    public CreateContainerService createContainerService(ContainerRepository repository) {
        return new CreateContainerService(repository);
    }

    /**
     * Creates a bean for the ReadContainerService.
     * @param repository the container repository
     * @return a new ReadContainerService instance
     */
    @Bean
    public ReadContainerService readContainerService(ContainerRepository repository) {
        return new ReadContainerService(repository);
    }

    /**
     * Creates a bean for the UpdateContainerService.
     * @param repository the container repository
     * @return a new UpdateContainerService instance
     */
    @Bean
    public UpdateContainerService updateContainerService(ContainerRepository repository) {
        return new UpdateContainerService(repository);
    }

    /**
     * Creates a bean for the DeleteContainerService.
     * @param repository the container repository
     * @return a new DeleteContainerService instance
     */
    @Bean
    public DeleteContainerService deleteContainerService(ContainerRepository repository) {
        return new DeleteContainerService(repository);
    }

    /**
     * Creates a bean for the CreateFacilityService.
     * @param repository the facility repository
     * @return a new CreateFacilityService instance
     */
    @Bean
    public CreateFacilityService createFacilityService(FacilityRepository repository) {
        return new CreateFacilityService(repository);
    }

    /**
     * Creates a bean for the ReadFacilityService.
     * @param repository the facility repository
     * @return a new ReadFacilityService instance
     */
    @Bean
    public ReadFacilityService readFacilityService(FacilityRepository repository) {
        return new ReadFacilityService(repository);
    }

    /**
     * Creates a bean for the UpdateFacilityService.
     * @param repository the facility repository
     * @return a new UpdateFacilityService instance
     */
    @Bean
    public UpdateFacilityService updateFacilityService(FacilityRepository repository) {
        return new UpdateFacilityService(repository);
    }

    /**
     * Creates a bean for the DeleteFacilityService.
     * @param repository the facility repository
     * @return a new DeleteFacilityService instance
     */
    @Bean
    public DeleteFacilityService deleteFacilityService(FacilityRepository repository) {
        return new DeleteFacilityService(repository);
    }

    /**
     * Creates a bean for the CreateVehicleService.
     * @param repository the vehicle repository
     * @return a new CreateVehicleService instance
     */
    @Bean
    public CreateVehicleService createVehicleService(VehicleRepository repository) {
        return new CreateVehicleService(repository);
    }

    /**
     * Creates a bean for the ReadVehicleService.
     * @param repository the vehicle repository
     * @return a new ReadVehicleService instance
     */
    @Bean
    public ReadVehicleService readVehicleService(VehicleRepository repository) {
        return new ReadVehicleService(repository);
    }

    /**
     * Creates a bean for the UpdateVehicleService.
     * @param repository the vehicle repository
     * @return a new UpdateVehicleService instance
     */
    @Bean
    public UpdateVehicleService updateVehicleService(VehicleRepository repository) {
        return new UpdateVehicleService(repository);
    }

    /**
     * Creates a bean for the DeleteVehicleService.
     * @param repository the vehicle repository
     * @return a new DeleteVehicleService instance
     */
    @Bean
    public DeleteVehicleService deleteVehicleService(VehicleRepository repository) {
        return new DeleteVehicleService(repository);
    }

    /**
     * Creates a bean for the CreateInfrastructurePlanService.
     * @param repository the infrastructure plan repository
     * @return a new CreateInfrastructurePlanService instance
     */
    @Bean
    public CreateInfrastructurePlanService createInfrastructurePlanService(InfrastructurePlanRepository repository) {
        return new CreateInfrastructurePlanService(repository);
    }

    /**
     * Creates a bean for the ReadInfrastructurePlanService.
     * @param repository the infrastructure plan repository
     * @return a new ReadInfrastructurePlanService instance
     */
    @Bean
    public ReadInfrastructurePlanService readInfrastructurePlanService(InfrastructurePlanRepository repository) {
        return new ReadInfrastructurePlanService(repository);
    }

    /**
     * Creates a bean for the UpdateInfrastructurePlanService.
     * @param repository the infrastructure plan repository
     * @return a new UpdateInfrastructurePlanService instance
     */
    @Bean
    public UpdateInfrastructurePlanService updateInfrastructurePlanService(InfrastructurePlanRepository repository) {
        return new UpdateInfrastructurePlanService(repository);
    }

    /**
     * Creates a bean for the DeleteInfrastructurePlanService.
     * @param repository the infrastructure plan repository
     * @return a new DeleteInfrastructurePlanService instance
     */
    @Bean
    public DeleteInfrastructurePlanService deleteInfrastructurePlanService(InfrastructurePlanRepository repository) {
        return new DeleteInfrastructurePlanService(repository);
    }

    /**
     * Creates a bean for the CreateServiceAssignmentService.
     * @param serviceAssignmentRepository the service assignment repository
     * @param containerRepository the container repository
     * @param facilityRepository the facility repository
     * @return a new CreateServiceAssignmentService instance
     */
    @Bean
    public CreateServiceAssignmentService createServiceAssignmentService(
            ServiceAssignmentRepository serviceAssignmentRepository,
            ContainerRepository containerRepository,
            FacilityRepository facilityRepository) {
        return new CreateServiceAssignmentService(serviceAssignmentRepository, containerRepository, facilityRepository);
    }

    /**
     * Creates a bean for the ReadServiceAssignmentService.
     * @param repository the service assignment repository
     * @return a new ReadServiceAssignmentService instance
     */
    @Bean
    public ReadServiceAssignmentService readServiceAssignmentService(ServiceAssignmentRepository repository) {
        return new ReadServiceAssignmentService(repository);
    }

    /**
     * Creates a bean for the UpdateServiceAssignmentService.
     * @param repository the service assignment repository
     * @param containerRepository the container repository
     * @param facilityRepository the facility repository
     * @return a new UpdateServiceAssignmentService instance
     */
    @Bean
    public UpdateServiceAssignmentService updateServiceAssignmentService(ServiceAssignmentRepository repository,
                                                                         ContainerRepository containerRepository,
                                                                         FacilityRepository facilityRepository) {
        return new UpdateServiceAssignmentService(repository, containerRepository, facilityRepository);
    }

    /**
     * Creates a bean for the DeleteServiceAssignmentService.
     * @param repository the service assignment repository
     * @return a new DeleteServiceAssignmentService instance
     */
    @Bean
    public DeleteServiceAssignmentService deleteServiceAssignmentService(ServiceAssignmentRepository repository) {
        return new DeleteServiceAssignmentService(repository);
    }
}
