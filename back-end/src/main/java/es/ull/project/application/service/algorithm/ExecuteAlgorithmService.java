package es.ull.project.application.service.algorithm;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.application.usecase.algorithm.AlgorithmExecutionSelection;
import es.ull.project.application.usecase.algorithm.ExecuteAlgorithmUseCase;
import es.ull.project.application.usecase.algorithm.ResolvedFacilityVehiclesSelection;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.Vehicle;

/**
 * Service implementation for algorithm execution.
 * This service currently resolves all identifiers received from the frontend
 * and returns the full persisted data without running the final algorithm yet.
 */
public class ExecuteAlgorithmService implements ExecuteAlgorithmUseCase {

    private final FacilityRepository facilityRepository;
    private final VehicleRepository vehicleRepository;
    private final ContainerRepository containerRepository;

    /**
     * Creates a new execute algorithm service.
     *
     * @param facilityRepository repository used to resolve facilities
     * @param vehicleRepository repository used to resolve vehicles
     * @param containerRepository repository used to resolve containers
     */
    public ExecuteAlgorithmService(
            FacilityRepository facilityRepository,
            VehicleRepository vehicleRepository,
            ContainerRepository containerRepository) {
        this.facilityRepository = facilityRepository;
        this.vehicleRepository = vehicleRepository;
        this.containerRepository = containerRepository;
    }

    /**
     * Resolves all identifiers from the request and builds the processed result.
     *
     * @param facilitiesWithVehicles selected facilities and vehicle identifiers
     * @param selectedContainerIds selected container identifiers
     * @param numberOfDays number of planning days
     * @param averagePickupTimeMinutes average pickup time in minutes
     * @return processed result with resolved entities
     */
    @Override
    public AlgorithmExecutionResult execute(
            List<AlgorithmExecutionSelection> facilitiesWithVehicles,
            List<UUID> selectedContainerIds,
            int numberOfDays,
            int averagePickupTimeMinutes) {
        this.validateRequest(facilitiesWithVehicles, selectedContainerIds, numberOfDays, averagePickupTimeMinutes);

        List<ResolvedFacilityVehiclesSelection> resolvedFacilitiesWithVehicles =
                facilitiesWithVehicles.stream()
                        .map(this::resolveFacilityVehiclesSelection)
                        .toList();

        List<Container> resolvedContainers = selectedContainerIds.stream()
                .map(this::resolveContainer)
                .toList();

        return new AlgorithmExecutionResult(
                resolvedFacilitiesWithVehicles,
                resolvedContainers,
                numberOfDays,
                averagePickupTimeMinutes);
    }

    /**
     * Validates the execution request before resolving identifiers.
     *
     * @param facilitiesWithVehicles selected facilities and vehicles
     * @param selectedContainerIds selected containers
     * @param numberOfDays number of planning days
     * @param averagePickupTimeMinutes average pickup time in minutes
     */
    private void validateRequest(
            List<AlgorithmExecutionSelection> facilitiesWithVehicles,
            List<UUID> selectedContainerIds,
            int numberOfDays,
            int averagePickupTimeMinutes) {
        if (facilitiesWithVehicles == null || facilitiesWithVehicles.isEmpty()) {
            throw new IllegalArgumentException("At least one facility with vehicles must be selected");
        }
        if (selectedContainerIds == null || selectedContainerIds.isEmpty()) {
            throw new IllegalArgumentException("At least one container must be selected");
        }
        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("The number of days must be greater than zero");
        }
        if (averagePickupTimeMinutes <= 0) {
            throw new IllegalArgumentException("The average pickup time must be greater than zero");
        }
    }

    /**
     * Resolves one facility selection with all associated vehicle data.
     *
     * @param selection selection to resolve
     * @return resolved facility and vehicles
     */
    private ResolvedFacilityVehiclesSelection resolveFacilityVehiclesSelection(
            AlgorithmExecutionSelection selection) {
        if (selection.getSelectedVehicleIds().isEmpty()) {
            throw new IllegalArgumentException("Each selected facility must include at least one vehicle");
        }

        Facility facility = this.resolveFacility(selection.getFacilityId());
        List<Vehicle> vehicles = selection.getSelectedVehicleIds().stream()
                .map(this::resolveVehicle)
                .toList();

        return new ResolvedFacilityVehiclesSelection(facility, vehicles);
    }

    /**
     * Resolves one facility by its identifier.
     *
     * @param facilityId facility identifier
     * @return resolved facility
     */
    private Facility resolveFacility(UUID facilityId) {
        return this.facilityRepository.findById(facilityId)
                .orElseThrow(() -> new NoSuchElementException("Facility not found: " + facilityId));
    }

    /**
     * Resolves one vehicle by its identifier.
     *
     * @param vehicleId vehicle identifier
     * @return resolved vehicle
     */
    private Vehicle resolveVehicle(UUID vehicleId) {
        return this.vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NoSuchElementException("Vehicle not found: " + vehicleId));
    }

    /**
     * Resolves one container by its identifier.
     *
     * @param containerId container identifier
     * @return resolved container
     */
    private Container resolveContainer(UUID containerId) {
        return this.containerRepository.findById(containerId)
                .orElseThrow(() -> new NoSuchElementException("Container not found: " + containerId));
    }
}
