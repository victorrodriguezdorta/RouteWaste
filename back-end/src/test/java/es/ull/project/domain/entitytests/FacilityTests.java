package es.ull.project.domain.entitytests;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FacilityTests {

    private static final String PROCESSING_AMOUNT_NEGATIVE_MESSAGE = "Processing amount cannot be negative";
    private static final String WASTE_AMOUNT_NEGATIVE_MESSAGE = "Waste amount cannot be negative";

    /**
     * Creates a random location for facility tests.
     *
     * @return valid location for facility tests.
     */
    private static Location randomLocation() {
        return new Location(
                28.4682 + Math.random() * 0.1,
                -16.2546 + Math.random() * 0.1,
                "Facility Address " + ((int) (Math.random() * 1000)),
                "GIS-REF-FAC-" + ((int) (Math.random() * 10000))
        );
    }

    /**
     * Creates a random storage capacity for facility tests.
     *
     * @return valid storage capacity for facility tests.
     */
    private static StorageCapacityKilograms randomStorageCapacity() {
        return new StorageCapacityKilograms(1000.0 + Math.random() * 5000.0);
    }

    /**
     * Creates a random processing capacity for facility tests.
     *
     * @return valid processing capacity for facility tests.
     */
    private static ProcessingCapacityKilogramsPerDay randomProcessingCapacity() {
        return new ProcessingCapacityKilogramsPerDay(500.0 + Math.random() * 2000.0);
    }

    /**
     * Creates a random unloading time for facility tests.
     *
     * @return valid unloading time for facility tests.
     */
    private static UnloadingTime randomUnloadingTime() {
        return new UnloadingTime(30 + (int) (Math.random() * 120));
    }

    /**
     * Creates a random opening fixed cost for facility tests.
     *
     * @return valid opening fixed cost for facility tests.
     */
    private static OpeningFixedCost randomOpeningFixedCost() {
        return new OpeningFixedCost(10000.0 + Math.random() * 50000.0);
    }

    /**
     * Creates a random facility for tests that need a valid aggregate.
     *
     * @return valid facility for tests.
     */
    private static Facility randomFacility() {
        return new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.random()
        );
    }

    /**
     * Creates a random facility name.
     *
     * @return valid facility name for tests.
     */
    private static Name randomName() {
        return new Name("facility-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Verifies the public constructor stores required attributes and initializes computed attributes.
     */
    @Test
    void constructor1Right() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        Name name = randomName();
        Facility facility = new Facility(
                name,
                facilityType,
                location,
                storageCapacity,
                processingCapacity,
                unloadingTime,
                openingFixedCost,
                status
        );
        assertEquals(name, facility.getName());
        assertEquals(facilityType, facility.getFacilityType());
        assertEquals(location, facility.getLocation());
        assertEquals(storageCapacity, facility.getStorageCapacity());
        assertEquals(processingCapacity, facility.getProcessingCapacity());
        assertEquals(unloadingTime, facility.getUnloadingTime());
        assertEquals(openingFixedCost, facility.getOpeningFixedCost());
        assertEquals(status, facility.getStatus());
        assertNotNull(facility.getId());
        assertNotNull(facility.getCurrentFillingLevel());
        assertEquals(0.0, facility.getCurrentFillingLevel().getLitersPerDay());
    }

    /**
     * Verifies that the public constructor rejects an undefined name.
     */
    @Test
    void constructor1NameUndefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(
                        null,
                        FacilityType.random(),
                        randomLocation(),
                        randomStorageCapacity(),
                        randomProcessingCapacity(),
                        randomUnloadingTime(),
                        randomOpeningFixedCost(),
                        FacilityStatus.random())
        );
        assertEquals(Facility.NAME_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the public constructor rejects an undefined facility type.
     */
    @Test
    void constructor1FacilityTypeUndefined() {
        FacilityType facilityType = null;
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity,
                        unloadingTime, openingFixedCost, status)
        );
        assertEquals(Facility.TYPE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the public constructor rejects an undefined location.
     */
    @Test
    void constructor1LocationUndefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = null;
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity,
                        unloadingTime, openingFixedCost, status)
        );
        assertEquals(Facility.LOCATION_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the public constructor rejects an undefined storage capacity.
     */
    @Test
    void constructor1StorageCapacityUndefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = null;
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity,
                        unloadingTime, openingFixedCost, status)
        );
        assertEquals(Facility.STORAGE_CAPACITY_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the public constructor rejects an undefined processing capacity.
     */
    @Test
    void constructor1ProcessingCapacityUndefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = null;
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity,
                        unloadingTime, openingFixedCost, status)
        );
        assertEquals(Facility.PROCESSING_CAPACITY_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the public constructor rejects an undefined unloading time.
     */
    @Test
    void constructor1UnloadingTimeUndefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = null;
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity,
                        unloadingTime, openingFixedCost, status)
        );
        assertEquals(Facility.UNLOADING_TIME_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the public constructor rejects an undefined opening fixed cost.
     */
    @Test
    void constructor1OpeningFixedCostUndefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = null;
        FacilityStatus status = FacilityStatus.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity,
                        unloadingTime, openingFixedCost, status)
        );
        assertEquals(Facility.OPENING_COST_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the public constructor rejects an undefined status.
     */
    @Test
    void constructor1StatusUndefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = null;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity,
                        unloadingTime, openingFixedCost, status)
        );
        assertEquals(Facility.STATUS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the copy constructor keeps identity and core attributes.
     */
    @Test
    void copyConstructorRight() {
        Facility original = new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.PLANNED);
        original.assignWasteDemand(new DailyWasteDemandLitersPerDay(15.0));
        Facility copy = new Facility(original);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getFacilityType(), copy.getFacilityType());
        assertEquals(original.getLocation(), copy.getLocation());
        assertEquals(original.getCurrentFillingLevel(), copy.getCurrentFillingLevel());
    }

    /**
     * Verifies that the restore constructor keeps supplied persisted values.
     */
    @Test
    void restoreConstructorRight() {
        UUID id = UUID.randomUUID();
        Name name = randomName();
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.PLANNED;
        DailyWasteDemandLitersPerDay fillingLevel = new DailyWasteDemandLitersPerDay(25.0);
        Facility facility = new Facility(
                id, name, facilityType, location, storageCapacity, processingCapacity,
                unloadingTime, openingFixedCost, status, fillingLevel);
        assertEquals(id, facility.getId());
        assertEquals(name, facility.getName());
        assertEquals(fillingLevel, facility.getCurrentFillingLevel());
    }

    /**
     * Verifies that restoring a null current filling level defaults it to zero.
     */
    @Test
    void restoreConstructorNullCurrentFillingLevelDefaultsToZero() {
        Facility facility = new Facility(
                UUID.randomUUID(),
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.PLANNED,
                null);
        assertEquals(0.0, facility.getCurrentFillingLevel().getLitersPerDay());
    }

    /**
     * Verifies that updating the name stores the new value.
     */
    @Test
    void updateNameValid() {
        Facility facility = randomFacility();
        Name newName = randomName();
        facility.updateName(newName);
        assertEquals(newName, facility.getName());
    }

    /**
     * Verifies that updating the name rejects an undefined value.
     */
    @Test
    void updateNameUndefined() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateName(null)
        );
        assertEquals(Facility.NAME_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating the facility type stores the new value.
     */
    @Test
    void updateFacilityTypeValid() {
        Facility facility = randomFacility();
        FacilityType newType = FacilityType.random();
        facility.updateFacilityType(newType);
        assertEquals(newType, facility.getFacilityType());
    }

    /**
     * Verifies that updating the facility type rejects an undefined value.
     */
    @Test
    void updateFacilityTypeUndefined() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateFacilityType(null)
        );
        assertEquals(Facility.TYPE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating the location stores the new value.
     */
    @Test
    void updateLocationValid() {
        Facility facility = randomFacility();
        Location newLocation = randomLocation();
        facility.updateLocation(newLocation);
        assertEquals(newLocation, facility.getLocation());
    }

    /**
     * Verifies that updating the location rejects an undefined value.
     */
    @Test
    void updateLocationUndefined() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateLocation(null)
        );
        assertEquals(Facility.LOCATION_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating the storage capacity stores the new value.
     */
    @Test
    void updateStorageCapacityValid() {
        Facility facility = randomFacility();
        StorageCapacityKilograms newCapacity = new StorageCapacityKilograms(2000.0);
        facility.updateStorageCapacity(newCapacity);
        assertEquals(newCapacity, facility.getStorageCapacity());
    }

    /**
     * Verifies that updating the storage capacity rejects an undefined value.
     */
    @Test
    void updateStorageCapacityUndefined() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateStorageCapacity(null)
        );
        assertEquals(Facility.STORAGE_CAPACITY_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating the processing capacity stores the new value.
     */
    @Test
    void updateProcessingCapacityValid() {
        Facility facility = randomFacility();
        ProcessingCapacityKilogramsPerDay newCapacity = new ProcessingCapacityKilogramsPerDay(800.0);
        facility.updateProcessingCapacity(newCapacity);
        assertEquals(newCapacity, facility.getProcessingCapacity());
    }

    /**
     * Verifies that updating the processing capacity rejects an undefined value.
     */
    @Test
    void updateProcessingCapacityUndefined() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateProcessingCapacity(null)
        );
        assertEquals(Facility.PROCESSING_CAPACITY_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating the unloading time stores the new value.
     */
    @Test
    void updateUnloadingTimeValid() {
        Facility facility = randomFacility();
        UnloadingTime newTime = new UnloadingTime(90);
        facility.updateUnloadingTime(newTime);
        assertEquals(newTime, facility.getUnloadingTime());
    }

    /**
     * Verifies that updating the unloading time rejects an undefined value.
     */
    @Test
    void updateUnloadingTimeUndefined() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateUnloadingTime(null)
        );
        assertEquals(Facility.UNLOADING_TIME_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating the opening fixed cost stores the new value.
     */
    @Test
    void updateOpeningFixedCostValid() {
        Facility facility = randomFacility();
        OpeningFixedCost newCost = new OpeningFixedCost(5000.0);
        facility.updateOpeningFixedCost(newCost);
        assertEquals(newCost, facility.getOpeningFixedCost());
    }

    /**
     * Verifies that updating the opening fixed cost rejects an undefined value.
     */
    @Test
    void updateOpeningFixedCostUndefined() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateOpeningFixedCost(null)
        );
        assertEquals(Facility.OPENING_COST_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that processing waste rejects negative amounts.
     */
    @Test
    void processWasteNegativeAmount() {
        Facility facility = randomFacility();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.processWaste(-1.0)
        );
        assertEquals(PROCESSING_AMOUNT_NEGATIVE_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that processing valid waste amounts does not throw.
     */
    @Test
    void processWasteValidAmountDoesNotThrow() {
        Facility facility = randomFacility();
        facility.processWaste(0.0);
        facility.processWaste(100.0);
    }

    /**
     * Verifies that a discarded facility rejects truck unload records.
     */
    @Test
    void recordTruckUnloadDiscardedFacility() {
        Facility facility = new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.DISCARDED);
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> facility.recordTruckUnload(50.0)
        );
        assertEquals(Facility.FACILITY_DISCARDED, exception.getMessage());
    }

    /**
     * Verifies that recording truck unload rejects negative waste amounts.
     */
    @Test
    void recordTruckUnloadNegativeWaste() {
        Facility facility = new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.PLANNED);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.recordTruckUnload(-0.1)
        );
        assertEquals(WASTE_AMOUNT_NEGATIVE_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that recording valid truck unload amounts does not throw.
     */
    @Test
    void recordTruckUnloadValidAmountDoesNotThrow() {
        Facility facility = new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.PLANNED);
        facility.recordTruckUnload(0.0);
        facility.recordTruckUnload(120.0);
    }

    /**
     * Verifies equality behavior for facilities.
     */
    @Test
    void equalsMethod() {
        Facility facility1 = randomFacility();
        Facility facility2 = new Facility(
                randomName(),
                facility1.getFacilityType(),
                facility1.getLocation(),
                facility1.getStorageCapacity(),
                facility1.getProcessingCapacity(),
                facility1.getUnloadingTime(),
                facility1.getOpeningFixedCost(),
                facility1.getStatus()
        );
        Facility facility3 = randomFacility();
        assertTrue(facility1.equals(facility1));
        assertFalse(facility1.equals(null));
        assertFalse(facility1.equals(Integer.valueOf(0)));
        assertNotEquals(facility1, facility2);
        assertNotEquals(facility1.getId(), facility2.getId());
        assertNotEquals(facility1, facility3);
        assertNotEquals(facility1.getId(), facility3.getId());
    }

    /**
     * Verifies hash code behavior for facilities.
     */
    @Test
    void hashCodeMethod() {
        Facility facility1 = randomFacility();
        Facility facility2 = new Facility(
                randomName(),
                facility1.getFacilityType(),
                facility1.getLocation(),
                facility1.getStorageCapacity(),
                facility1.getProcessingCapacity(),
                facility1.getUnloadingTime(),
                facility1.getOpeningFixedCost(),
                facility1.getStatus()
        );
        Facility facility3 = randomFacility();
        assertEquals(facility1.hashCode(), facility1.hashCode());
        assertNotEquals(facility1.hashCode(), facility2.hashCode());
        assertNotEquals(facility1.hashCode(), facility3.hashCode());
    }

    /**
     * Verifies that updating the status stores the new value.
     */
    @Test
    void updateStatusValid() {
        Facility facility = randomFacility();
        FacilityStatus newStatus = FacilityStatus.random();
        facility.updateStatus(newStatus);
        assertEquals(newStatus, facility.getStatus());
    }

    /**
     * Verifies that updating the status rejects an undefined value.
     */
    @Test
    void updateStatusUndefined() {
        Facility facility = randomFacility();
        FacilityStatus newStatus = null;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facility.updateStatus(newStatus)
        );
        assertEquals(Facility.STATUS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that assigning a waste demand updates the current filling level.
     */
    @Test
    void assignWasteDemandValid() {
        Facility facility = new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.PLANNED
        );
        DailyWasteDemandLitersPerDay demand = new DailyWasteDemandLitersPerDay(10.0);
        facility.assignWasteDemand(demand);
        assertEquals(10.0, facility.getCurrentFillingLevel().getLitersPerDay());
    }

    /**
     * Verifies that multiple waste demands accumulate in the current filling level.
     */
    @Test
    void assignWasteDemandMultiple() {
        Facility facility = new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.PLANNED
        );
        DailyWasteDemandLitersPerDay demand1 = new DailyWasteDemandLitersPerDay(20.0);
        DailyWasteDemandLitersPerDay demand2 = new DailyWasteDemandLitersPerDay(30.0);
        facility.assignWasteDemand(demand1);
        facility.assignWasteDemand(demand2);
        assertEquals(50.0, facility.getCurrentFillingLevel().getLitersPerDay());
    }

    /**
     * Verifies that a discarded facility rejects waste demand assignment.
     */
    @Test
    void assignWasteDemandFacilityDiscarded() {
        Facility facility = new Facility(
                randomName(),
                FacilityType.random(),
                randomLocation(),
                randomStorageCapacity(),
                randomProcessingCapacity(),
                randomUnloadingTime(),
                randomOpeningFixedCost(),
                FacilityStatus.DISCARDED
        );
        DailyWasteDemandLitersPerDay demand = new DailyWasteDemandLitersPerDay(10.0);
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> facility.assignWasteDemand(demand)
        );
        assertEquals(Facility.FACILITY_DISCARDED, exception.getMessage());
    }

    /**
     * Verifies the string representation of a facility.
     */
    @Test
    void toStringMethod() {
        Facility facility = randomFacility();
        String expectedValue = String.format(
                "Facility={id=%s, name=%s, type=%s, location=%s, storageCapacity=%s, processingCapacity=%s, "
                        + "unloadingTime=%s, currentFillingLevel=%s, openingCost=%s, status=%s}",
                facility.getId(),
                facility.getName(),
                facility.getFacilityType(),
                facility.getLocation(),
                facility.getStorageCapacity(),
                facility.getProcessingCapacity(),
                facility.getUnloadingTime(),
                facility.getCurrentFillingLevel(),
                facility.getOpeningFixedCost(),
                facility.getStatus()
        );
        assertEquals(expectedValue, facility.toString());
    }
}
