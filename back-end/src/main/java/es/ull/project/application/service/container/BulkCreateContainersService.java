package es.ull.project.application.service.container;

import es.ull.project.application.model.BulkCreateItemFailure;
import es.ull.project.application.model.BulkCreateOutcome;
import es.ull.project.application.usecase.container.BulkCreateContainersUseCase;
import es.ull.project.application.usecase.container.CreateContainerUseCase;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for bulk container creation.
 */
public class BulkCreateContainersService implements BulkCreateContainersUseCase {

    private static final String MISMATCHED_BATCH_SIZE = "All attribute lists must have the same size";

    private final CreateContainerUseCase createContainerUseCase;

    /**
     * Constructs the service with the single-entity create use case.
     *
     * @param createContainerUseCase single-entity create use case
     */
    public BulkCreateContainersService(CreateContainerUseCase createContainerUseCase) {
        this.createContainerUseCase = createContainerUseCase;
    }

    /**
     * Creates containers for each aligned index across the attribute lists.
     *
     * @param names                  container names
     * @param locations              container locations
     * @param wasteTypes             waste types
     * @param capacityLitersList     capacity values in liters
     * @param dailyDemandLitersPerDay daily demand values
     * @param serviceZones           service zones
     * @return outcome with created and failed item counts
     */
    @Override
    public BulkCreateOutcome createAll(
            List<Name> names,
            List<Location> locations,
            List<WasteType> wasteTypes,
            List<ContainerCapacityLiters> capacityLitersList,
            List<DailyWasteDemandLitersPerDay> dailyDemandLitersPerDay,
            List<ServiceZone> serviceZones) {
        int total = names.size();
        assertSameSize(total, locations, wasteTypes, capacityLitersList, dailyDemandLitersPerDay, serviceZones);
        List<BulkCreateItemFailure> failures = new ArrayList<>();
        int createdCount = 0;
        for (int index = 0; index < total; index++) {
            try {
                this.createContainerUseCase.create(
                        names.get(index),
                        locations.get(index),
                        wasteTypes.get(index),
                        capacityLitersList.get(index),
                        dailyDemandLitersPerDay.get(index),
                        serviceZones.get(index));
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
