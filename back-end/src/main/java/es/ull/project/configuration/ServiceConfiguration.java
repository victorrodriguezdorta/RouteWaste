package es.ull.project.configuration;

import es.ull.project.application.port.algorithm.AlgorithmExecutionPayloadSerializer;
import es.ull.project.application.port.algorithm.AlgorithmRunner;
import es.ull.project.application.port.infrastructureplan.InfrastructurePlanExecutionNotifier;
import es.ull.project.application.repository.ContainerDailyStateRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.service.algorithm.CreatePendingInfrastructurePlanService;
import es.ull.project.application.service.algorithm.ExecuteAlgorithmService;
import es.ull.project.application.service.algorithm.PersistAlgorithmExecutionResultService;
import es.ull.project.application.service.algorithm.RunAlgorithmExecutionJobService;
import es.ull.project.application.service.algorithm.RunAlgorithmService;
import es.ull.project.application.service.container.BulkCreateContainersService;
import es.ull.project.application.service.container.CreateContainerService;
import es.ull.project.application.service.container.DeleteContainerService;
import es.ull.project.application.service.container.ReadContainerService;
import es.ull.project.application.service.container.UpdateContainerService;
import es.ull.project.application.service.dailyplan.ReadDailyPlanService;
import es.ull.project.application.service.facility.BulkCreateFacilitiesService;
import es.ull.project.application.service.facility.CreateFacilityService;
import es.ull.project.application.service.facility.DeleteFacilityService;
import es.ull.project.application.service.facility.ReadFacilityService;
import es.ull.project.application.service.facility.UpdateFacilityService;
import es.ull.project.application.service.infrastructureplan.DeleteInfrastructurePlanService;
import es.ull.project.application.service.infrastructureplan.DeleteInfrastructurePlansReferencingEntityService;
import es.ull.project.application.service.infrastructureplan.InvalidateInfrastructurePlansOnEntityEditService;
import es.ull.project.application.service.infrastructureplan.ReadInfrastructurePlanService;
import es.ull.project.application.service.overview.GetApplicationOverviewService;
import es.ull.project.application.service.vehicle.BulkCreateVehiclesService;
import es.ull.project.application.service.vehicle.CreateVehicleService;
import es.ull.project.application.service.vehicle.DeleteVehicleService;
import es.ull.project.application.service.vehicle.ReadVehicleService;
import es.ull.project.application.service.vehicle.UpdateVehicleService;
import es.ull.project.application.usecase.algorithm.CreatePendingInfrastructurePlanUseCase;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.PersistAlgorithmExecutionResultUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmExecutionJobAsyncUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmExecutionJobUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmUseCase;
import es.ull.project.application.usecase.container.BulkCreateContainersUseCase;
import es.ull.project.application.usecase.container.CreateContainerUseCase;
import es.ull.project.application.usecase.container.DeleteContainerUseCase;
import es.ull.project.application.usecase.container.ReadContainerUseCase;
import es.ull.project.application.usecase.container.UpdateContainerUseCase;
import es.ull.project.application.usecase.dailyplan.ReadDailyPlanUseCase;
import es.ull.project.application.usecase.facility.BulkCreateFacilitiesUseCase;
import es.ull.project.application.usecase.facility.CreateFacilityUseCase;
import es.ull.project.application.usecase.facility.DeleteFacilityUseCase;
import es.ull.project.application.usecase.facility.ReadFacilityUseCase;
import es.ull.project.application.usecase.facility.UpdateFacilityUseCase;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlanUseCase;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlansReferencingEntityUseCase;
import es.ull.project.application.usecase.infrastructureplan.InvalidateInfrastructurePlansOnEntityEditUseCase;
import es.ull.project.application.usecase.infrastructureplan.ReadInfrastructurePlanUseCase;
import es.ull.project.application.usecase.overview.GetApplicationOverviewUseCase;
import es.ull.project.application.usecase.vehicle.BulkCreateVehiclesUseCase;
import es.ull.project.application.usecase.vehicle.CreateVehicleUseCase;
import es.ull.project.application.usecase.vehicle.DeleteVehicleUseCase;
import es.ull.project.application.usecase.vehicle.ReadVehicleUseCase;
import es.ull.project.application.usecase.vehicle.UpdateVehicleUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for application services.
 * This class defines all service beans following the hexagonal architecture
 * pattern.
 */
@Configuration
public class ServiceConfiguration {

    /**
     * Marks infrastructure plans obsolete when facilities, vehicles, or containers are edited.
     *
     * @param infrastructurePlanRepository plan persistence
     * @return invalidation helper
     */
    @Bean
    public InvalidateInfrastructurePlansOnEntityEditUseCase invalidateInfrastructurePlansOnEntityEditService(
            InfrastructurePlanRepository infrastructurePlanRepository) {
        return new InvalidateInfrastructurePlansOnEntityEditService(infrastructurePlanRepository);
    }

    /**
     * Creates the service bean that collects input data for the algorithm.
     *
     * @param facilityRepository   repository for accessing facility data
     * @param vehicleRepository    repository for accessing vehicle data
     * @param containerRepository  repository for accessing container data
     * @return configured {@link ExecuteAlgorithmService} instance
     */
    @Bean
    public ExecuteAlgorithmUseCase executeAlgorithmService(
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
    public RunAlgorithmUseCase runAlgorithmService(AlgorithmRunner algorithmRunner) {
        return new RunAlgorithmService(algorithmRunner);
    }

    /**
     * Creates the service bean that stores a RUNNING infrastructure plan placeholder before async execution.
     *
     * @param infrastructurePlanRepository repository for infrastructure plan persistence
     * @return configured {@link CreatePendingInfrastructurePlanService} instance
     */
    @Bean
    public CreatePendingInfrastructurePlanUseCase createPendingInfrastructurePlanService(
            InfrastructurePlanRepository infrastructurePlanRepository) {
        return new CreatePendingInfrastructurePlanService(infrastructurePlanRepository);
    }

    /**
     * Exposes asynchronous algorithm job execution to adapters.
     *
     * @param runAlgorithmExecutionJobUseCase synchronous job use case
     * @return async runner delegating to the job use case
     */
    @Bean
    public RunAlgorithmExecutionJobAsyncUseCase runAlgorithmExecutionJobAsyncUseCase(
            RunAlgorithmExecutionJobUseCase runAlgorithmExecutionJobUseCase) {
        return new AsyncRunAlgorithmExecutionJobRunner(runAlgorithmExecutionJobUseCase);
    }

    /**
     * Creates the service bean that runs the asynchronous algorithm execution pipeline.
     *
     * @param executeAlgorithmUseCase                resolves request identifiers
     * @param runAlgorithmUseCase                    invokes the external algorithm runner
     * @param persistAlgorithmExecutionResultUseCase completes or fails the pending plan
     * @param executionNotifier                      notifies clients of plan state changes
     * @param payloadSerializer                      builds JSON for the algorithm runner
     * @return configured {@link RunAlgorithmExecutionJobService} instance
     */
    @Bean
    public RunAlgorithmExecutionJobUseCase runAlgorithmExecutionJobService(
            ExecuteAlgorithmUseCase executeAlgorithmUseCase,
            RunAlgorithmUseCase runAlgorithmUseCase,
            PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultUseCase,
            InfrastructurePlanExecutionNotifier executionNotifier,
            AlgorithmExecutionPayloadSerializer payloadSerializer) {
        return new RunAlgorithmExecutionJobService(
                executeAlgorithmUseCase,
                runAlgorithmUseCase,
                persistAlgorithmExecutionResultUseCase,
                executionNotifier,
                payloadSerializer);
    }

    /**
     * Creates the service bean that persists the algorithm execution result.
     *
     * @param infrastructurePlanRepository  repository for infrastructure plan persistence
     * @param serviceAssignmentRepository   repository for service assignment persistence
     * @param dailyPlanRepository           repository for daily plan persistence
     * @param containerDailyStateRepository repository for container daily state persistence
     * @param facilityRepository            repository for facility persistence
     * @param containerRepository           repository for container persistence
     * @param vehicleRepository             repository for vehicle persistence
     * @return configured {@link PersistAlgorithmExecutionResultService} instance
     */
    @Bean
    public PersistAlgorithmExecutionResultUseCase persistAlgorithmExecutionResultService(
            InfrastructurePlanRepository infrastructurePlanRepository,
            ServiceAssignmentRepository serviceAssignmentRepository,
            DailyPlanRepository dailyPlanRepository,
            ContainerDailyStateRepository containerDailyStateRepository,
            FacilityRepository facilityRepository,
            ContainerRepository containerRepository,
            VehicleRepository vehicleRepository) {
        return new PersistAlgorithmExecutionResultService(
                infrastructurePlanRepository,
                serviceAssignmentRepository,
                dailyPlanRepository,
                containerDailyStateRepository,
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
    public CreateContainerUseCase createContainerService(ContainerRepository repository) {
        return new CreateContainerService(repository);
    }

    /**
     * Creates the service bean responsible for bulk container creation.
     *
     * @param createContainerUseCase single-entity create use case
     * @return configured {@link BulkCreateContainersService} instance
     */
    @Bean
    public BulkCreateContainersUseCase bulkCreateContainersService(CreateContainerUseCase createContainerUseCase) {
        return new BulkCreateContainersService(createContainerUseCase);
    }

    /**
     * Creates the service bean responsible for reading containers.
     *
     * @param repository the container repository
     * @return configured {@link ReadContainerService} instance
     */
    @Bean
    public ReadContainerUseCase readContainerService(ContainerRepository repository) {
        return new ReadContainerService(repository);
    }

    /**
     * Creates the service bean responsible for updating containers.
     *
     * @param repository                                      the container repository
     * @param invalidateInfrastructurePlansOnEntityEditService infrastructure plan invalidation helper
     * @return configured {@link UpdateContainerService} instance
     */
    @Bean
    public UpdateContainerUseCase updateContainerService(
            ContainerRepository repository,
            InvalidateInfrastructurePlansOnEntityEditUseCase invalidateInfrastructurePlansOnEntityEditService) {
        return new UpdateContainerService(repository, invalidateInfrastructurePlansOnEntityEditService);
    }

    /**
     * Creates the service bean responsible for deleting containers.
     *
     * @param repository                                      the container repository
     * @param deleteInfrastructurePlansReferencingEntityService infrastructure plan deletion helper
     * @return configured {@link DeleteContainerService} instance
     */
    @Bean
    public DeleteContainerUseCase deleteContainerService(
            ContainerRepository repository,
            DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityService) {
        return new DeleteContainerService(repository, deleteInfrastructurePlansReferencingEntityService);
    }

    /**
     * Creates the service bean responsible for creating facilities.
     *
     * @param repository the facility repository
     * @return configured {@link CreateFacilityService} instance
     */
    @Bean
    public CreateFacilityUseCase createFacilityService(FacilityRepository repository) {
        return new CreateFacilityService(repository);
    }

    /**
     * Creates the service bean responsible for bulk facility creation.
     *
     * @param createFacilityUseCase single-entity create use case
     * @return configured {@link BulkCreateFacilitiesService} instance
     */
    @Bean
    public BulkCreateFacilitiesUseCase bulkCreateFacilitiesService(CreateFacilityUseCase createFacilityUseCase) {
        return new BulkCreateFacilitiesService(createFacilityUseCase);
    }

    /**
     * Creates the service bean responsible for reading facilities.
     *
     * @param repository the facility repository
     * @return configured {@link ReadFacilityService} instance
     */
    @Bean
    public ReadFacilityUseCase readFacilityService(FacilityRepository repository) {
        return new ReadFacilityService(repository);
    }

    /**
     * Creates the service bean responsible for updating facilities.
     *
     * @param repository                                      the facility repository
     * @param invalidateInfrastructurePlansOnEntityEditService infrastructure plan invalidation helper
     * @return configured {@link UpdateFacilityService} instance
     */
    @Bean
    public UpdateFacilityUseCase updateFacilityService(
            FacilityRepository repository,
            InvalidateInfrastructurePlansOnEntityEditUseCase invalidateInfrastructurePlansOnEntityEditService) {
        return new UpdateFacilityService(repository, invalidateInfrastructurePlansOnEntityEditService);
    }

    /**
     * Creates the service bean responsible for deleting facilities.
     *
     * @param repository                                      the facility repository
     * @param deleteInfrastructurePlansReferencingEntityService infrastructure plan deletion helper
     * @return configured {@link DeleteFacilityService} instance
     */
    @Bean
    public DeleteFacilityUseCase deleteFacilityService(
            FacilityRepository repository,
            DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityService) {
        return new DeleteFacilityService(repository, deleteInfrastructurePlansReferencingEntityService);
    }

    /**
     * Creates the service bean responsible for creating vehicles.
     *
     * @param repository the vehicle repository
     * @return configured {@link CreateVehicleService} instance
     */
    @Bean
    public CreateVehicleUseCase createVehicleService(VehicleRepository repository) {
        return new CreateVehicleService(repository);
    }

    /**
     * Creates the service bean responsible for bulk vehicle creation.
     *
     * @param createVehicleUseCase single-entity create use case
     * @return configured {@link BulkCreateVehiclesService} instance
     */
    @Bean
    public BulkCreateVehiclesUseCase bulkCreateVehiclesService(CreateVehicleUseCase createVehicleUseCase) {
        return new BulkCreateVehiclesService(createVehicleUseCase);
    }

    /**
     * Creates the service bean responsible for reading vehicles.
     *
     * @param repository the vehicle repository
     * @return configured {@link ReadVehicleService} instance
     */
    @Bean
    public ReadVehicleUseCase readVehicleService(VehicleRepository repository) {
        return new ReadVehicleService(repository);
    }

    /**
     * Creates the service bean responsible for updating vehicles.
     *
     * @param repository                                      the vehicle repository
     * @param invalidateInfrastructurePlansOnEntityEditService infrastructure plan invalidation helper
     * @return configured {@link UpdateVehicleService} instance
     */
    @Bean
    public UpdateVehicleUseCase updateVehicleService(
            VehicleRepository repository,
            InvalidateInfrastructurePlansOnEntityEditUseCase invalidateInfrastructurePlansOnEntityEditService) {
        return new UpdateVehicleService(repository, invalidateInfrastructurePlansOnEntityEditService);
    }

    /**
     * Creates the service bean responsible for deleting vehicles.
     *
     * @param repository                                      the vehicle repository
     * @param deleteInfrastructurePlansReferencingEntityService infrastructure plan deletion helper
     * @return configured {@link DeleteVehicleService} instance
     */
    @Bean
    public DeleteVehicleUseCase deleteVehicleService(
            VehicleRepository repository,
            DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityService) {
        return new DeleteVehicleService(repository, deleteInfrastructurePlansReferencingEntityService);
    }

    /**
     * Creates the service bean responsible for reading infrastructure plans.
     *
     * @param repository          the infrastructure plan repository
     * @param dailyPlanRepository repository for associated daily plans
     * @return configured {@link ReadInfrastructurePlanService} instance
     */
    @Bean
    public ReadInfrastructurePlanUseCase readInfrastructurePlanService(InfrastructurePlanRepository repository, DailyPlanRepository dailyPlanRepository) {
        return new ReadInfrastructurePlanService(repository, dailyPlanRepository);
    }

    /**
     * Overview for home or dashboard: entity totals and latest infrastructure plans.
     *
     * @param containerRepository           container persistence
     * @param vehicleRepository             vehicle persistence
     * @param facilityRepository            facility persistence
     * @param readInfrastructurePlanUseCase paginated infrastructure plan reads
     * @return overview service bean
     */
    @Bean
    public GetApplicationOverviewUseCase getApplicationOverviewService(
            ContainerRepository containerRepository,
            VehicleRepository vehicleRepository,
            FacilityRepository facilityRepository,
            ReadInfrastructurePlanUseCase readInfrastructurePlanUseCase) {
        return new GetApplicationOverviewService(
                containerRepository, vehicleRepository, facilityRepository, readInfrastructurePlanUseCase);
    }

    /**
     * Creates the service bean responsible for deleting infrastructure plans.
     *
     * @param repository                     the infrastructure plan repository
     * @param dailyPlanRepository            repository for associated daily plans
     * @param serviceAssignmentRepository    repository for associated service assignments
     * @param containerDailyStateRepository  repository for container monitoring snapshots
     * @return configured {@link DeleteInfrastructurePlanService} instance
     */
    @Bean
    public DeleteInfrastructurePlanUseCase deleteInfrastructurePlanService(
            InfrastructurePlanRepository repository,
            DailyPlanRepository dailyPlanRepository,
            ServiceAssignmentRepository serviceAssignmentRepository,
            ContainerDailyStateRepository containerDailyStateRepository) {
        return new DeleteInfrastructurePlanService(
                repository, dailyPlanRepository, serviceAssignmentRepository, containerDailyStateRepository);
    }

    /**
     * Deletes infrastructure plans whose execution snapshot references a deleted master entity.
     *
     * @param infrastructurePlanRepository      plan persistence
     * @param deleteInfrastructurePlanUseCase   cascade delete of plans
     * @return helper invoked from delete-facility/container/vehicle services
     */
    @Bean
    public DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityService(
            InfrastructurePlanRepository infrastructurePlanRepository,
            DeleteInfrastructurePlanUseCase deleteInfrastructurePlanUseCase) {
        return new DeleteInfrastructurePlansReferencingEntityService(infrastructurePlanRepository, deleteInfrastructurePlanUseCase);
    }

    /**
     * Creates the service bean responsible for reading daily plans.
     *
     * @param repository the daily plan repository
     * @return configured {@link ReadDailyPlanService} instance
     */
    @Bean
    public ReadDailyPlanUseCase readDailyPlanService(DailyPlanRepository repository) {
        return new ReadDailyPlanService(repository);
    }
}
