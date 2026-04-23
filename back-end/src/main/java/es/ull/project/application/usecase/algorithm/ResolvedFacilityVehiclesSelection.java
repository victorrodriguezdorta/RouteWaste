package es.ull.project.application.usecase.algorithm;

import java.util.List;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.Vehicle;

/**
 * ResolvedFacilityVehiclesSelection
 *
 * Represents one facility selection enriched with the full facility data and
 * the full vehicle data associated with it.
 */
public class ResolvedFacilityVehiclesSelection {

    /**
     * Resolved facility entity.
     */
    private final Facility facility;

    /**
     * Resolved vehicle entities.
     */
    private final List<Vehicle> selectedVehicles;

    /**
     * Creates a resolved selection.
     *
     * @param facility resolved facility
     * @param selectedVehicles resolved vehicles
     */
    public ResolvedFacilityVehiclesSelection(Facility facility, List<Vehicle> selectedVehicles) {
        this.facility = facility;
        this.selectedVehicles = List.copyOf(selectedVehicles);
    }

    /**
     * Returns the resolved facility.
     *
     * @return facility entity
     */
    public Facility getFacility() {
        return this.facility;
    }

    /**
     * Returns the resolved vehicles.
     *
     * @return immutable list of vehicles
     */
    public List<Vehicle> getSelectedVehicles() {
        return this.selectedVehicles;
    }
}
