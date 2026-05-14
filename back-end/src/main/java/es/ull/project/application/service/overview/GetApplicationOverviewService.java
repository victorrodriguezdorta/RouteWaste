package es.ull.project.application.service.overview;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.infrastructureplan.ReadInfrastructurePlanUseCase;
import es.ull.project.application.usecase.overview.GetApplicationOverviewUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.readmodel.ApplicationOverview;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Builds an {@link ApplicationOverview} from repository totals and a short list of the latest infrastructure plans.
 */
public class GetApplicationOverviewService implements GetApplicationOverviewUseCase {

    private static final int RECENT_PLANS_PAGE_SIZE = 3;
    private static final String SORT_PROPERTY_EXECUTED_AT = "executedAt";

    private final ContainerRepository containerRepository;
    private final VehicleRepository vehicleRepository;
    private final FacilityRepository facilityRepository;
    private final ReadInfrastructurePlanUseCase readInfrastructurePlanUseCase;

    /**
     * @param containerRepository          container persistence
     * @param vehicleRepository            vehicle persistence
     * @param facilityRepository           facility persistence
     * @param readInfrastructurePlanUseCase  paginated plan reads (totals and recent slice)
     */
    public GetApplicationOverviewService(
            ContainerRepository containerRepository,
            VehicleRepository vehicleRepository,
            FacilityRepository facilityRepository,
            ReadInfrastructurePlanUseCase readInfrastructurePlanUseCase) {
        this.containerRepository = containerRepository;
        this.vehicleRepository = vehicleRepository;
        this.facilityRepository = facilityRepository;
        this.readInfrastructurePlanUseCase = readInfrastructurePlanUseCase;
    }

    @Override
    public ApplicationOverview fetch() {
        Pageable countProbe = PageRequest.of(0, 1);
        long containerCount = this.containerRepository.findAll(countProbe).getTotalElements();
        long vehicleCount = this.vehicleRepository.findAll(countProbe).getTotalElements();
        long facilityCount = this.facilityRepository.findAll(countProbe).getTotalElements();
        long infrastructurePlanCount = this.readInfrastructurePlanUseCase.fetchAll(countProbe).getTotalElements();

        Pageable recentPageable = PageRequest.of(
                0,
                RECENT_PLANS_PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, SORT_PROPERTY_EXECUTED_AT));
        Page<InfrastructurePlan> recentPage = this.readInfrastructurePlanUseCase.fetchAll(recentPageable);
        List<InfrastructurePlan> recentPlans = recentPage.getContent();

        return new ApplicationOverview(containerCount, vehicleCount, facilityCount, infrastructurePlanCount, recentPlans);
    }
}
