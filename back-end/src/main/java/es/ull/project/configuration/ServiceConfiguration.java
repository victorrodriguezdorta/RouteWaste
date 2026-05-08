package es.ull.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.ull.project.application.port.algorithm.AlgorithmRunner;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.service.algorithm.ExecuteAlgorithmService;
import es.ull.project.application.service.algorithm.PersistAlgorithmExecutionResultService;
import es.ull.project.application.service.algorithm.RunAlgorithmService;
import es.ull.project.application.service.container.CreateContainerService;
import es.ull.project.application.service.container.DeleteContainerService;
import es.ull.project.application.service.container.ReadContainerService;
import es.ull.project.application.service.container.UpdateContainerService;
import es.ull.project.application.service.dailyplan.ReadDailyPlanService;
import es.ull.project.application.service.facility.CreateFacilityService;
import es.ull.project.application.service.facility.DeleteFacilityService;
import es.ull.project.application.service.facility.ReadFacilityService;
import es.ull.project.application.service.facility.UpdateFacilityService;
import es.ull.project.application.service.infrastructureplan.DeleteInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.ReadInfrastructurePlanService;
import es.ull.project.application.service.vehicle.CreateVehicleService;
import es.ull.project.application.service.vehicle.DeleteVehicleService;
import es.ull.project.application.service.vehicle.ReadVehicleService;
import es.ull.project.application.service.vehicle.UpdateVehicleService;

/**
 * Configuration class for application services.
 * This class defines all service beans following the hexagonal architecture
 * pattern.
 */
@Configuration
public class ServiceConfiguration {

    /**
     * Creates the service bean that collects input data for the algorithm.
     *
     * @param facilityRepository   repository for accessing facility data
     * @param vehicleRepository    repository for accessing vehicle data
     * @param containerRepository  repository for accessing container data
     * @return configured {@link ExecuteAlgorithmService} instance
     */
    @Bean
    public ExecuteAlgorithmService executeAlgorithmService(
            FacilityRepository facilityRepository,
            VehicleRepository vehicleRepository,
            ContainerRepository containerRepository) {
        return new ExecuteAlgorithmService(facilityRepository, vehicleRepository, containerRepository);
    }

    /**
     * Creates the service bean that delegates algorithm execution to the runner.
     *
     * @param algorithmRunner the port implementation that runs the optimisation algorithm
     * @return configured {@link RunAlgorithmService} instance
     */
    @Bean
    public RunAlgorithmService runAlgorithmService(AlgorithmRunner algorithmRunner) {
        return new RunAlgorithmService(algorithmRunner);
    }

    /**
     * Creates the service bean that persists the algorithm execution result.
     *
     * @param infrastructurePlanRepository  repository for infrastructure plan persistence
     * @param serviceAssignmentRepository   repository for service assignment persistence
     * @param dailyPlanRepository           repository for daily plan persistence
     * @return configured {@link PersistAlgorithmExecutionResultService} instance
     */
    @Bean
    public PersistAlgorithmExecutionResultService persistAlgorithmExecutionResultService(
            InfrastructurePlanRepository infrastructurePlanRepository,
            ServiceAssignmentRepository serviceAssignmentRepository,
            DailyPlanRepository dailyPlanRepository,
            FacilityRepository facilityRepository,
            ContainerRepository containerRepository,
            VehicleRepository vehicleRepository) {
        return new PersistAlgorithmExecutionResultService(
                infrastructurePlanRepository,
                serviceAssignmentRepository,
                dailyPlanRepository,
                facilityRepository,
                containerRepository,
                vehicleRepository);
    }

    /**
     * Creates the service bean responsible for creating containers.
     *
     * @param repository the container repository
     * @return configured {@link CreateContainerService} instance
     */
    @Bean
    public CreateContainerService createContainerService(ContainerRepository repository) {
        return new CreateContainerService(repository);
    }

    /**
     * Creates the service bean responsible for reading containers.
     *
     * @param repository the container repository
     * @return configured {@link ReadContainerService} instance
     */
    @Bean
    public ReadContainerService readContainerService(ContainerRepository repository) {
        return new ReadContainerService(repository);
    }

    /**
     * Creates the service bean responsible for updating containers.
     *
     * @param repository the container repository
     * @return configured {@link UpdateContainerService} instance
     */
    @Bean
    public UpdateContainerService updateContainerService(ContainerRepository repository) {
        return new UpdateContainerService(repository);
    }

    /**
     * Creates the service bean responsible for deleting containers.
     *
     * @param repository the container repository
     * @return configured {@link DeleteContainerService} instance
     */
    @Bean
    public DeleteContainerService deleteContainerService(ContainerRepository repository) {
        return new DeleteContainerService(repository);
    }

    /**
     * Creates the service bean responsible for creating facilities.
     *
     * @param repository the facility repository
     * @return configured {@link CreateFacilityService} instance
     */
    @Bean
    public CreateFacilityService createFacilityService(FacilityRepository repository) {
        return new CreateFacilityService(repository);
    }

    /**
     * Creates the service bean responsible for reading facilities.
     *
     * @param repository the facility repository
     * @return configured {@link ReadFacilityService} instance
     */
    @Bean
    public ReadFacilityService readFacilityService(FacilityRepository repository) {
        return new ReadFacilityService(repository);
    }

    /**
     * Creates the service bean responsible for updating facilities.
     *
     * @param repository the facility repository
     * @return configured {@link UpdateFacilityService} instance
     */
    @Bean
    public UpdateFacilityService updateFacilityService(FacilityRepository repository) {
        return new UpdateFacilityService(repository);
    }

    /**
     * Creates the service bean responsible for deleting facilities.
     *
     * @param repository the facility repository
     * @return configured {@link DeleteFacilityService} instance
     */
    @Bean
    public DeleteFacilityService deleteFacilityService(FacilityRepository repository) {
        return new DeleteFacilityService(repository);
    }

    /**
     * Creates the service bean responsible for creating vehicles.
     *
     * @param repository the vehicle repository
     * @return configured {@link CreateVehicleService} instance
     */
    @Bean
    public CreateVehicleService createVehicleService(VehicleRepository repository) {
        return new CreateVehicleService(repository);
    }

    /**
     * Creates the service bean responsible for reading vehicles.
     *
     * @param repository the vehicle repository
     * @return configured {@link ReadVehicleService} instance
     */
    @Bean
    public ReadVehicleService readVehicleService(VehicleRepository repository) {
        return new ReadVehicleService(repository);
    }

    /**
     * Creates the service bean responsible for updating vehicles.
     *
     * @param repository the vehicle repository
     * @return configured {@link UpdateVehicleService} instance
     */
    @Bean
    public UpdateVehicleService updateVehicleService(VehicleRepository repository) {
        return new UpdateVehicleService(repository);
    }

    /**
     * Creates the service bean responsible for deleting vehicles.
     *
     * @param repository the vehicle repository
     * @return configured {@link DeleteVehicleService} instance
     */
    @Bean
    public DeleteVehicleService deleteVehicleService(VehicleRepository repository) {
        return new DeleteVehicleService(repository);
    }

    /**
     * Creates the service bean responsible for reading infrastructure plans.
     *
     * @param repository the infrastructure plan repository
     * @return configured {@link ReadInfrastructurePlanService} instance
     */
    @Bean
    public ReadInfrastructurePlanService readInfrastructurePlanService(InfrastructurePlanRepository repository, DailyPlanRepository dailyPlanRepository) {
        return new ReadInfrastructurePlanService(repository, dailyPlanRepository);
    }

    /**
     * Creates the service bean responsible for deleting infrastructure plans.
     *
     * @param repository                   the infrastructure plan repository
     * @param dailyPlanRepository          repository for associated daily plans
     * @param serviceAssignmentRepository  repository for associated service assignments
     * @return configured {@link DeleteInfrastructurePlanService} instance
     */
    @Bean
    public DeleteInfrastructurePlanService deleteInfrastructurePlanService(
            InfrastructurePlanRepository repository,
            DailyPlanRepository dailyPlanRepository,
            ServiceAssignmentRepository serviceAssignmentRepository) {
        return new DeleteInfrastructurePlanService(repository, dailyPlanRepository, serviceAssignmentRepository);
    }

    /**
     * Creates the service bean responsible for reading daily plans.
     *
     * @param repository the daily plan repository
     * @return configured {@link ReadDailyPlanService} instance
     */
    @Bean
    public ReadDailyPlanService readDailyPlanService(DailyPlanRepository repository) {
        return new ReadDailyPlanService(repository);
    }
}
