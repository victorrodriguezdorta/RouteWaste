package es.ull.project.application.service.facility;

import es.ull.project.application.message.BulkCreateItemFailure;
import es.ull.project.application.message.BulkCreateOutcome;
import es.ull.project.application.usecase.facility.BulkCreateFacilitiesUseCase;
import es.ull.project.application.usecase.facility.CreateFacilityUseCase;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for bulk facility creation.
 */
public class BulkCreateFacilitiesService implements BulkCreateFacilitiesUseCase {

    private static final String MISMATCHED_BATCH_SIZE = "All attribute lists must have the same size";

    private final CreateFacilityUseCase createFacilityUseCase;

    /**
     * Constructs the service with the single-entity create use case.
     *
     * @param createFacilityUseCase single-entity create use case
     */
    public BulkCreateFacilitiesService(CreateFacilityUseCase createFacilityUseCase) {
        this.createFacilityUseCase = createFacilityUseCase;
    }

    /**
     * Creates facilities for each aligned index across the attribute lists.
     *
     * @param names                 facility names
     * @param facilityTypes         facility types
     * @param locations             facility locations
     * @param storageCapacities     storage capacity values
     * @param processingCapacities  processing capacity values
     * @param unloadingTimes        unloading time values
     * @param openingFixedCosts     opening fixed cost values
     * @param statuses              facility statuses
     * @return outcome with created and failed item counts
     */
    @Override
    public BulkCreateOutcome createAll(
            List<Name> names,
            List<FacilityType> facilityTypes,
            List<Location> locations,
            List<StorageCapacityKilograms> storageCapacities,
            List<ProcessingCapacityKilogramsPerDay> processingCapacities,
            List<UnloadingTime> unloadingTimes,
            List<OpeningFixedCost> openingFixedCosts,
            List<FacilityStatus> statuses) {
        int total = names.size();
        assertSameSize(total, facilityTypes, locations, storageCapacities, processingCapacities, unloadingTimes, openingFixedCosts, statuses);
        List<BulkCreateItemFailure> failures = new ArrayList<>();
        int createdCount = 0;
        for (int index = 0; index < total; index++) {
            try {
                this.createFacilityUseCase.create(
                        names.get(index),
                        facilityTypes.get(index),
                        locations.get(index),
                        storageCapacities.get(index),
                        processingCapacities.get(index),
                        unloadingTimes.get(index),
                        openingFixedCosts.get(index),
                        statuses.get(index));
                createdCount++;
            } catch (RuntimeException exception) {
                failures.add(new BulkCreateItemFailure(index, failureMessage(exception)));
            }
        }
        return new BulkCreateOutcome(total, createdCount, failures);
    }

    /**
     * Ensures every attribute list has the same length as the batch size.
     *
     * @param expected expected list size
     * @param lists    parallel attribute lists
     * @throws IllegalArgumentException when any list size differs
     */
    private static void assertSameSize(int expected, List<?>... lists) {
        for (List<?> list : lists) {
            if (list.size() != expected) {
                throw new IllegalArgumentException(MISMATCHED_BATCH_SIZE);
            }
        }
    }

    /**
     * Builds a human-readable failure message from a runtime exception.
     *
     * @param exception failure raised while creating one item
     * @return exception message or simple class name when message is blank
     */
    private static String failureMessage(RuntimeException exception) {
        String message = exception.getMessage();
        return message != null && !message.isBlank() ? message : exception.getClass().getSimpleName();
    }
}
