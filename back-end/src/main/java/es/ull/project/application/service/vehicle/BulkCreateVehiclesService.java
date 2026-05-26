package es.ull.project.application.service.vehicle;

import es.ull.project.application.model.BulkCreateItemFailure;
import es.ull.project.application.model.BulkCreateOutcome;
import es.ull.project.application.usecase.vehicle.BulkCreateVehiclesUseCase;
import es.ull.project.application.usecase.vehicle.CreateVehicleUseCase;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.name.Name;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for bulk vehicle creation.
 */
public class BulkCreateVehiclesService implements BulkCreateVehiclesUseCase {

    private static final String MISMATCHED_BATCH_SIZE = "All attribute lists must have the same size";

    private final CreateVehicleUseCase createVehicleUseCase;

    /**
     * Constructs the service with the single-entity create use case.
     *
     * @param createVehicleUseCase single-entity create use case
     */
    public BulkCreateVehiclesService(CreateVehicleUseCase createVehicleUseCase) {
        this.createVehicleUseCase = createVehicleUseCase;
    }

    /**
     * Creates vehicles for each aligned index across the attribute lists.
     *
     * @param names              vehicle names
     * @param vehicleTypes       vehicle types
     * @param capacityKilograms  capacity values in kilograms
     * @param capacityLiters     capacity values in liters
     * @param costPerKilometer   transportation cost per kilometer values
     * @return outcome with created and failed item counts
     */
    @Override
    public BulkCreateOutcome createAll(
            List<Name> names,
            List<VehicleType> vehicleTypes,
            List<VehicleCapacityKilograms> capacityKilograms,
            List<VehicleCapacityLiters> capacityLiters,
            List<TransportationVariableCost> costPerKilometer) {
        int total = names.size();
        assertSameSize(total, vehicleTypes, capacityKilograms, capacityLiters, costPerKilometer);
        List<BulkCreateItemFailure> failures = new ArrayList<>();
        int createdCount = 0;
        for (int index = 0; index < total; index++) {
            try {
                this.createVehicleUseCase.create(
                        names.get(index),
                        vehicleTypes.get(index),
                        capacityKilograms.get(index),
                        capacityLiters.get(index),
                        costPerKilometer.get(index));
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
