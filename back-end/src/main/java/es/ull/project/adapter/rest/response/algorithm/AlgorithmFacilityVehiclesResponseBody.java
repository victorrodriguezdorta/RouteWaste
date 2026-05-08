package es.ull.project.adapter.rest.response.algorithm;

import java.util.List;

import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;

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
    public FacilityResponseBody facility;

    /**
     * Resolved selected vehicles for the facility.
     */
    public List<VehicleResponseBody> selectedVehicles;
}
