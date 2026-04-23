package es.ull.project.adapter.rest.mapper;

import java.util.List;

import es.ull.project.adapter.rest.response.algorithm.AlgorithmExecutionResponseBody;
import es.ull.project.adapter.rest.response.algorithm.AlgorithmFacilityVehiclesResponseBody;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.application.usecase.algorithm.ResolvedFacilityVehiclesSelection;

/**
 * Mapper class to convert algorithm execution results into REST response DTOs.
 */
public class AlgorithmExecutionResponseMapper {

    private static final String UTILITY_CLASS_ERROR_MESSAGE = "This is a utility class and cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AlgorithmExecutionResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR_MESSAGE);
    }

    /**
     * Converts an application result into a REST response body.
     *
     * @param result processed algorithm result
     * @return response body ready to be serialized as JSON
     */
    public static AlgorithmExecutionResponseBody toResponseBody(AlgorithmExecutionResult result) {
        AlgorithmExecutionResponseBody responseBody = new AlgorithmExecutionResponseBody();
        responseBody.facilitiesWithVehicles = result.getFacilitiesWithVehicles().stream()
                .map(AlgorithmExecutionResponseMapper::toFacilityVehiclesResponseBody)
                .toList();
        responseBody.selectedContainers = result.getSelectedContainers().stream()
                .map(ContainerResponseMapper::toResponseBody)
                .toList();
        responseBody.numberOfDays = result.getNumberOfDays();
        responseBody.averagePickupTimeMinutes = result.getAveragePickupTimeMinutes();
        return responseBody;
    }

    /**
     * Converts a resolved facility selection into a response body.
     *
     * @param selection resolved facility selection
     * @return mapped response body
     */
    private static AlgorithmFacilityVehiclesResponseBody toFacilityVehiclesResponseBody(
            ResolvedFacilityVehiclesSelection selection) {
        AlgorithmFacilityVehiclesResponseBody responseBody = new AlgorithmFacilityVehiclesResponseBody();
        responseBody.facility = FacilityResponseMapper.toResponseBody(selection.getFacility());
        responseBody.selectedVehicles = selection.getSelectedVehicles().stream()
                .map(VehicleResponseMapper::toResponseBody)
                .toList();
        return responseBody;
    }
}
