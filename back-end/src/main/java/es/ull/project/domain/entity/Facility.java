package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.Objects;
import java.util.UUID;

/**
 * Facility
 *
 * Represents a waste management infrastructure such as:
 * - Truck operating base
 * - Transfer station
 * - Treatment or classification plant
 *
 * It is an aggregate root.
 */
public class Facility {

    public static final String ID_NOT_DEFINED = "Facility id is not defined";
    public static final String TYPE_NOT_DEFINED = "Facility type is not defined";
    public static final String LOCATION_NOT_DEFINED = "Facility location is not defined";
    public static final String STORAGE_CAPACITY_NOT_DEFINED = "Facility storage capacity is not defined";
    public static final String PROCESSING_CAPACITY_NOT_DEFINED = "Facility processing capacity is not defined";
    public static final String UNLOADING_TIME_NOT_DEFINED = "Facility unloading time is not defined";
    public static final String OPENING_COST_NOT_DEFINED = "Opening fixed cost is not defined";
    public static final String STATUS_NOT_DEFINED = "Facility status is not defined";
    public static final String FACILITY_DISCARDED = "Facility is discarded and cannot receive assignments";
    public static final String STORAGE_CAPACITY_EXCEEDED = "Facility storage capacity exceeded";
    public static final String NAME_NOT_DEFINED = "Facility name is not defined";
    private static final String ERROR_PROCESSING_AMOUNT_NEGATIVE = "Processing amount cannot be negative";
    private static final String ERROR_WASTE_AMOUNT_NEGATIVE = "Waste amount cannot be negative";
    private static final int ZERO = 0;

    /**
     * Identifier of the facility.
     * It is a required and immutable attribute.
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * Human-readable facility name.
     * It is a required attribute.
     */
    private Name name;

    /**
     * Type of the facility.
     * It is a required attribute.
     */
    private FacilityType facilityType;

    /**
     * Physical location of the facility.
     * It is a required attribute.
     */
    private Location location;

    /**
     * Storage capacity of the facility in kilograms.
     * It is a required attribute.
     */
    private StorageCapacityKilograms storageCapacity;

    /**
     * Processing capacity of the facility in kilograms per day.
     * It is a required attribute.
     */
    private ProcessingCapacityKilogramsPerDay processingCapacity;

    /**
     * Unloading time for trucks at this facility in minutes.
     * It is a required attribute.
     */
    private UnloadingTime unloadingTime;

    /**
     * Fixed cost to open the facility.
     * It is a required attribute.
     */
    private OpeningFixedCost openingFixedCost;

    /**
     * Current status of the facility.
     * It is a required attribute.
     */
    private FacilityStatus status;

    /**
     * Current filling level of the facility in liters per day.
     * It is a computed attribute.
     */
    private DailyWasteDemandLitersPerDay currentFillingLevel;

    /**
     * Creates a new Facility.
     *
     * @param name                Facility name.
     * @param facilityType        Type of facility.
     * @param location            Facility location.
     * @param storageCapacity     Storage capacity in kilograms.
     * @param processingCapacity  Processing capacity in kilograms per day.
     * @param unloadingTime       Unloading time for trucks in minutes.
     * @param openingFixedCost    Fixed opening cost.
     * @param status              Facility status.
     */
    public Facility(
            Name name,
            FacilityType facilityType,
            Location location,
            StorageCapacityKilograms storageCapacity,
            ProcessingCapacityKilogramsPerDay processingCapacity,
            UnloadingTime unloadingTime,
            OpeningFixedCost openingFixedCost,
            FacilityStatus status) {
        this.validateName(name);
        this.validateFacilityType(facilityType);
        this.validateLocation(location);
        this.validateStorageCapacity(storageCapacity);
        this.validateProcessingCapacity(processingCapacity);
        this.validateUnloadingTime(unloadingTime);
        this.validateOpeningFixedCost(openingFixedCost);
        this.validateStatus(status);
        this.id = UUID.randomUUID();
        this.name = name;
        this.facilityType = facilityType;
        this.location = location;
        this.storageCapacity = storageCapacity;
        this.processingCapacity = processingCapacity;
        this.unloadingTime = unloadingTime;
        this.openingFixedCost = openingFixedCost;
        this.status = status;
        this.currentFillingLevel = new DailyWasteDemandLitersPerDay(0.0);
    }

    /**
     * Copy constructor.
     * Creates a new Facility as a copy of another Facility.
     *
     * @param otherObject the Facility to copy
     */
    public Facility(Facility otherObject) {
        this.id = otherObject.id;
        this.name = otherObject.name;
        this.facilityType = otherObject.facilityType;
        this.location = otherObject.location;
        this.storageCapacity = otherObject.storageCapacity;
        this.processingCapacity = otherObject.processingCapacity;
        this.unloadingTime = otherObject.unloadingTime;
        this.openingFixedCost = otherObject.openingFixedCost;
        this.status = otherObject.status;
        this.currentFillingLevel = otherObject.currentFillingLevel;
    }

    /**
     * Restore constructor.
     * Restores a Facility from persistence with all its attributes.
     *
     * @param id                  the facility identifier
     * @param name                the facility name
     * @param facilityType        the type of facility
     * @param location            the facility location
     * @param storageCapacity     the storage capacity in kilograms
     * @param processingCapacity  the processing capacity in kilograms per day
     * @param unloadingTime       the unloading time in minutes
     * @param openingFixedCost    the fixed opening cost
     * @param status              the facility status
     * @param currentFillingLevel the current filling level in liters per day
     */
    public Facility(UUID id,
            Name name,
            FacilityType facilityType,
            Location location,
            StorageCapacityKilograms storageCapacity,
            ProcessingCapacityKilogramsPerDay processingCapacity,
            UnloadingTime unloadingTime,
            OpeningFixedCost openingFixedCost,
            FacilityStatus status,
            DailyWasteDemandLitersPerDay currentFillingLevel) {
        this.validateName(name);
        this.validateFacilityType(facilityType);
        this.validateLocation(location);
        this.validateStorageCapacity(storageCapacity);
        this.validateProcessingCapacity(processingCapacity);
        this.validateUnloadingTime(unloadingTime);
        this.validateOpeningFixedCost(openingFixedCost);
        this.validateStatus(status);
        this.id = id;
        this.name = name;
        this.facilityType = facilityType;
        this.location = location;
        this.storageCapacity = storageCapacity;
        this.processingCapacity = processingCapacity;
        this.unloadingTime = unloadingTime;
        this.openingFixedCost = openingFixedCost;
        this.status = status;
        this.currentFillingLevel = currentFillingLevel != null ? currentFillingLevel : new DailyWasteDemandLitersPerDay(0.0);
    }

    /**
     * Validates that the facility name is not null.
     *
     * @param name the facility name to validate
     * @throws IllegalArgumentException if name is null
     */
    private void validateName(Name name) {
        if (name == null) {
            throw new IllegalArgumentException(NAME_NOT_DEFINED);
        }
    }

    /**
     * Returns the facility name.
     *
     * @return the facility name
     */
    public Name getName() {
        return this.name;
    }

    /**
     * Updates the facility name.
     *
     * @param name the new facility name
     */
    public void updateName(Name name) {
        this.validateName(name);
        this.name = name;
    }

    /**
     * Returns the unique identifier of the facility.
     *
     * @return Facility UUID.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Validates that the facility type is not null.
     *
     * @param facilityType the facility type to validate
     * @throws IllegalArgumentException if facilityType is null
     */
    private void validateFacilityType(FacilityType facilityType) {
        if (facilityType == null) {
            throw new IllegalArgumentException(TYPE_NOT_DEFINED);
        }
    }

    /**
     * Validates that the location is not null.
     *
     * @param location the location to validate
     * @throws IllegalArgumentException if location is null
     */
    private void validateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException(LOCATION_NOT_DEFINED);
        }
    }

    /**
     * Validates that the capacity is not null.
     *
     * @param storageCapacity the storage capacity to validate
     * @throws IllegalArgumentException if storageCapacity is null
     */
    private void validateStorageCapacity(StorageCapacityKilograms storageCapacity) {
        if (storageCapacity == null) {
            throw new IllegalArgumentException(STORAGE_CAPACITY_NOT_DEFINED);
        }
    }

    /**
     * Validates that the processing capacity is not null.
     *
     * @param processingCapacity the processing capacity to validate
     * @throws IllegalArgumentException if processingCapacity is null
     */
    private void validateProcessingCapacity(ProcessingCapacityKilogramsPerDay processingCapacity) {
        if (processingCapacity == null) {
            throw new IllegalArgumentException(PROCESSING_CAPACITY_NOT_DEFINED);
        }
    }

    /**
     * Validates that the unloading time is not null.
     *
     * @param unloadingTime the unloading time to validate
     * @throws IllegalArgumentException if unloadingTime is null
     */
    private void validateUnloadingTime(UnloadingTime unloadingTime) {
        if (unloadingTime == null) {
            throw new IllegalArgumentException(UNLOADING_TIME_NOT_DEFINED);
        }
    }

    /**
     * Validates that the opening fixed cost is not null.
     *
     * @param openingFixedCost the opening fixed cost to validate
     * @throws IllegalArgumentException if openingFixedCost is null
     */
    private void validateOpeningFixedCost(OpeningFixedCost openingFixedCost) {
        if (openingFixedCost == null) {
            throw new IllegalArgumentException(OPENING_COST_NOT_DEFINED);
        }
    }

    /**
     * Validates that the status is not null.
     *
     * @param status the facility status to validate
     * @throws IllegalArgumentException if status is null
     */
    private void validateStatus(FacilityStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(STATUS_NOT_DEFINED);
        }
    }

    /**
     * Assigns waste demand to the facility.
     * Checks capacity and facility status invariants.
     *
     * @param demand Waste demand to assign in liters per day.
     */
    public void assignWasteDemand(DailyWasteDemandLitersPerDay demand) {
        if (this.status.isDiscarded()) {
            throw new IllegalStateException(FACILITY_DISCARDED);
        }
        DailyWasteDemandLitersPerDay newTotal = this.currentFillingLevel.add(demand);
        this.currentFillingLevel = newTotal;
    }

    /**
     * Processes waste from the current filling level.
     * Reduces the current filling level by the processing capacity.
     *
     * @param processingAmount Amount to process in kilograms
     */
    public void processWaste(double processingAmount) {
        if (processingAmount < ZERO) {
            throw new IllegalArgumentException(ERROR_PROCESSING_AMOUNT_NEGATIVE);
        }
    }

    /**
     * Records a truck unload event at this facility.
     * Updates the current filling level based on the truck's waste amount.
     *
     * @param wasteAmountInKilograms Amount of waste unloaded in kilograms
     */
    public void recordTruckUnload(double wasteAmountInKilograms) {
        if (this.status.isDiscarded()) {
            throw new IllegalStateException(FACILITY_DISCARDED);
        }
        if (wasteAmountInKilograms < ZERO) {
            throw new IllegalArgumentException(ERROR_WASTE_AMOUNT_NEGATIVE);
        }
    }

    /**
     * Returns the facility type.
     *
     * @return the type of facility
     */
    public FacilityType getFacilityType() {
        return this.facilityType;
    }

    /**
     * Returns the facility location.
     *
     * @return the location of the facility
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Returns the facility capacity.
     *
     * @return the storage capacity of the facility in kilograms
     */
    public StorageCapacityKilograms getStorageCapacity() {
        return this.storageCapacity;
    }

    /**
     * Returns the facility processing capacity.
     *
     * @return the processing capacity of the facility in kilograms per day
     */
    public ProcessingCapacityKilogramsPerDay getProcessingCapacity() {
        return this.processingCapacity;
    }

    /**
     * Returns the unloading time for trucks.
     *
     * @return the unloading time in minutes
     */
    public UnloadingTime getUnloadingTime() {
        return this.unloadingTime;
    }

    /**
     * Returns the opening fixed cost.
     *
     * @return the fixed cost to open the facility
     */
    public OpeningFixedCost getOpeningFixedCost() {
        return this.openingFixedCost;
    }

    /**
     * Returns the facility status.
     *
     * @return the current status of the facility
     */
    public FacilityStatus getStatus() {
        return this.status;
    }

    /**
     * Returns the current filling level of the facility.
     *
     * @return the current filling level in liters per day
     */
    public DailyWasteDemandLitersPerDay getCurrentFillingLevel() {
        return this.currentFillingLevel;
    }

    /**
     * Updates the facility status.
     *
     * @param status the new facility status
     */
    public void updateStatus(FacilityStatus status) {
        this.validateStatus(status);
        this.status = status;
    }

    /**
     * Updates the facility type.
     *
     * @param facilityType the new facility type
     */
    public void updateFacilityType(FacilityType facilityType) {
        this.validateFacilityType(facilityType);
        this.facilityType = facilityType;
    }

    /**
     * Updates the facility location.
     *
     * @param location the new facility location
     */
    public void updateLocation(Location location) {
        this.validateLocation(location);
        this.location = location;
    }

    /**
     * Updates the facility storage capacity.
     *
     * @param storageCapacity the new storage capacity
     */
    public void updateStorageCapacity(StorageCapacityKilograms storageCapacity) {
        this.validateStorageCapacity(storageCapacity);
        this.storageCapacity = storageCapacity;
    }

    /**
     * Updates the facility processing capacity.
     *
     * @param processingCapacity the new processing capacity
     */
    public void updateProcessingCapacity(ProcessingCapacityKilogramsPerDay processingCapacity) {
        this.validateProcessingCapacity(processingCapacity);
        this.processingCapacity = processingCapacity;
    }

    /**
     * Updates the unloading time for trucks.
     *
     * @param unloadingTime the new unloading time
     */
    public void updateUnloadingTime(UnloadingTime unloadingTime) {
        this.validateUnloadingTime(unloadingTime);
        this.unloadingTime = unloadingTime;
    }

    /**
     * Updates the facility opening fixed cost.
     *
     * @param openingFixedCost the new opening fixed cost
     */
    public void updateOpeningFixedCost(OpeningFixedCost openingFixedCost) {
        this.validateOpeningFixedCost(openingFixedCost);
        this.openingFixedCost = openingFixedCost;
    }

    /**
     * Compares this facility with another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }
        final Facility other = (Facility) otherObject;
        return Objects.equals(this.id, other.id);
    }

    /**
     * Returns the hash code of this facility.
     *
     * @return the hash code based on the facility id
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Returns a string representation of this facility.
     *
     * @return a formatted string with facility details
     */
    @Override
    public String toString() {
        return String.format(
                "Facility={id=%s, name=%s, type=%s, location=%s, storageCapacity=%s, processingCapacity=%s, unloadingTime=%s, currentFillingLevel=%s, openingCost=%s, status=%s}",
                this.id,
                this.name,
                this.facilityType,
                this.location,
                this.storageCapacity,
                this.processingCapacity,
                this.unloadingTime,
                this.currentFillingLevel,
                this.openingFixedCost,
                this.status);
    }
}