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

    @Bean
    public ExecuteAlgorithmService executeAlgorithmService(
            FacilityRepository facilityRepository,
            VehicleRepository vehicleRepository,
            ContainerRepository containerRepository) {
        return new ExecuteAlgorithmService(facilityRepository, vehicleRepository, containerRepository);
    }

    @Bean
    public RunAlgorithmService runAlgorithmService(AlgorithmRunner algorithmRunner) {
        return new RunAlgorithmService(algorithmRunner);
    }

    @Bean
    public PersistAlgorithmExecutionResultService persistAlgorithmExecutionResultService(
            InfrastructurePlanRepository infrastructurePlanRepository,
            ServiceAssignmentRepository serviceAssignmentRepository,
            DailyPlanRepository dailyPlanRepository) {
        return new PersistAlgorithmExecutionResultService(
                infrastructurePlanRepository,
                serviceAssignmentRepository,
                dailyPlanRepository);
    }

    @Bean
    public CreateContainerService createContainerService(ContainerRepository repository) {
        return new CreateContainerService(repository);
    }

    @Bean
    public ReadContainerService readContainerService(ContainerRepository repository) {
        return new ReadContainerService(repository);
    }

    @Bean
    public UpdateContainerService updateContainerService(ContainerRepository repository) {
        return new UpdateContainerService(repository);
    }

    @Bean
    public DeleteContainerService deleteContainerService(ContainerRepository repository) {
        return new DeleteContainerService(repository);
    }

    @Bean
    public CreateFacilityService createFacilityService(FacilityRepository repository) {
        return new CreateFacilityService(repository);
    }

    @Bean
    public ReadFacilityService readFacilityService(FacilityRepository repository) {
        return new ReadFacilityService(repository);
    }

    @Bean
    public UpdateFacilityService updateFacilityService(FacilityRepository repository) {
        return new UpdateFacilityService(repository);
    }

    @Bean
    public DeleteFacilityService deleteFacilityService(FacilityRepository repository) {
        return new DeleteFacilityService(repository);
    }

    @Bean
    public CreateVehicleService createVehicleService(VehicleRepository repository) {
        return new CreateVehicleService(repository);
    }

    @Bean
    public ReadVehicleService readVehicleService(VehicleRepository repository) {
        return new ReadVehicleService(repository);
    }

    @Bean
    public UpdateVehicleService updateVehicleService(VehicleRepository repository) {
        return new UpdateVehicleService(repository);
    }

    @Bean
    public DeleteVehicleService deleteVehicleService(VehicleRepository repository) {
        return new DeleteVehicleService(repository);
    }

    @Bean
    public ReadInfrastructurePlanService readInfrastructurePlanService(InfrastructurePlanRepository repository) {
        return new ReadInfrastructurePlanService(repository);
    }

    @Bean
    public DeleteInfrastructurePlanService deleteInfrastructurePlanService(
            InfrastructurePlanRepository repository,
            DailyPlanRepository dailyPlanRepository,
            ServiceAssignmentRepository serviceAssignmentRepository) {
        return new DeleteInfrastructurePlanService(repository, dailyPlanRepository, serviceAssignmentRepository);
    }

    @Bean
    public ReadDailyPlanService readDailyPlanService(DailyPlanRepository repository) {
        return new ReadDailyPlanService(repository);
    }
}
