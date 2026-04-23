package es.ull.project.application.usecase.algorithm;

import java.util.List;
import java.util.UUID;

/**
 * AlgorithmExecutionSelection
 *
 * Represents a facility together with the selected vehicles that must be used
 * during the algorithm execution request.
 */
public class AlgorithmExecutionSelection {

    /**
     * Facility identifier included in the execution request.
     */
    private final UUID facilityId;

    /**
     * Vehicle identifiers associated with the facility.
     */
    private final List<UUID> selectedVehicleIds;

    /**
     * Creates a selection with one facility and its vehicles.
     *
     * @param facilityId the selected facility identifier
     * @param selectedVehicleIds the selected vehicle identifiers
     */
    public AlgorithmExecutionSelection(UUID facilityId, List<UUID> selectedVehicleIds) {
        this.facilityId = facilityId;
        this.selectedVehicleIds = List.copyOf(selectedVehicleIds);
    }

    /**
     * Returns the selected facility identifier.
     *
     * @return facility UUID
     */
    public UUID getFacilityId() {
        return this.facilityId;
    }

    /**
     * Returns the selected vehicle identifiers.
     *
     * @return immutable list of vehicle UUIDs
     */
    public List<UUID> getSelectedVehicleIds() {
        return this.selectedVehicleIds;
    }
}
