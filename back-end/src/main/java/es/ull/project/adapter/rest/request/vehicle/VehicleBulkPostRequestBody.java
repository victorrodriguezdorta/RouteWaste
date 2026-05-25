package es.ull.project.adapter.rest.request.vehicle;

import java.util.List;

/**
 * Request body for bulk vehicle creation.
 * Accepts either a JSON array of vehicles or an object with a {@code vehicles} field.
 */
public class VehicleBulkPostRequestBody {

    /**
     * Vehicles to create, in submission order.
     */
    public List<VehiclePostRequestBody> vehicles;
}
