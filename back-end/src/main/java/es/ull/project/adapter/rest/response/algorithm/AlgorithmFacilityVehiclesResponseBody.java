package es.ull.project.adapter.rest.response.algorithm;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.Vehicle;
import java.util.List;

/**
 * AlgorithmFacilityVehiclesResponseBody
 *
 * Represents one resolved facility together with the resolved selected
 * vehicles returned by the algorithm execution endpoint.
 */
public class AlgorithmFacilityVehiclesResponseBody {

    /**
     * Fully resolved facility.
     */
    public Facility facility;

    /**
     * Resolved selected vehicles for the facility.
     */
    public List<Vehicle> selectedVehicles;
}
