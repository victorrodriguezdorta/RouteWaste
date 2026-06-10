package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;


class FacilityTests {
    
    // ========== HELPER METHODS ==========
    
    private static Location randomLocation() {
        return new Location(
            28.4682 + Math.random() * 0.1,
            -16.2546 + Math.random() * 0.1,
            "Facility Address " + ((int)(Math.random() * 1000)),
            "GIS-REF-FAC-" + ((int)(Math.random() * 10000))
        );
    }
    
    private static StorageCapacityKilograms randomStorageCapacity() {
        return new StorageCapacityKilograms(1000.0 + Math.random() * 5000.0);
    }
    
    private static ProcessingCapacityKilogramsPerDay randomProcessingCapacity() {
        return new ProcessingCapacityKilogramsPerDay(500.0 + Math.random() * 2000.0);
    }
    
    private static UnloadingTime randomUnloadingTime() {
        return new UnloadingTime(30 + (int)(Math.random() * 120));
    }
    
    private static OpeningFixedCost randomOpeningFixedCost() {
        return new OpeningFixedCost(10000.0 + Math.random() * 50000.0);
    }
    
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

    private static Name randomName() {
        return new Name("facility-" + ((int) (Math.random() * 10000)));
    }
    
    // ========== PUBLIC CONSTRUCTORS ==========
    
    @Test
    void constructor_1_right() {
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
        
        // Required attributes:
        assertEquals(name, facility.getName());
        assertEquals(facilityType, facility.getFacilityType());
        assertEquals(location, facility.getLocation());
        assertEquals(storageCapacity, facility.getStorageCapacity());
        assertEquals(processingCapacity, facility.getProcessingCapacity());
        assertEquals(unloadingTime, facility.getUnloadingTime());
        assertEquals(openingFixedCost, facility.getOpeningFixedCost());
        assertEquals(status, facility.getStatus());
        
        // Computed attributes:
        assertNotNull(facility.getId());
        assertNotNull(facility.getCurrentFillingLevel());
        assertEquals(0.0, facility.getCurrentFillingLevel().getLitersPerDay());
    }
    
    @Test
    void constructor_1_name_undefined() {
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

    @Test
    void constructor_1_facilityType_undefined() {
        FacilityType facilityType = null;
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status)
        );
        
        assertEquals(Facility.TYPE_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_location_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = null;
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status)
        );
        
        assertEquals(Facility.LOCATION_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_storageCapacity_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = null;
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status)
        );
        
        assertEquals(Facility.STORAGE_CAPACITY_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_processingCapacity_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = null;
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status)
        );
        
        assertEquals(Facility.PROCESSING_CAPACITY_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_unloadingTime_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = null;
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status)
        );
        
        assertEquals(Facility.UNLOADING_TIME_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_openingFixedCost_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = null;
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status)
        );
        
        assertEquals(Facility.OPENING_COST_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_status_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        StorageCapacityKilograms storageCapacity = randomStorageCapacity();
        ProcessingCapacityKilogramsPerDay processingCapacity = randomProcessingCapacity();
        UnloadingTime unloadingTime = randomUnloadingTime();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(randomName(), facilityType, location, storageCapacity, processingCapacity, unloadingTime, openingFixedCost, status)
        );
        
        assertEquals(Facility.STATUS_NOT_DEFINED, exception.getMessage());
    }
    
    // ========== COPY & RESTORE CONSTRUCTORS ==========

    @Test
    void copyConstructor_right() {
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

    @Test
    void restoreConstructor_right() {
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

    @Test
    void restoreConstructor_nullCurrentFillingLevel_defaultsToZero() {
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

    // ========== UPDATE METHODS ==========

    @Test
    void updateName_valid() {
        Facility facility = randomFacility();
        Name newName = randomName();

        facility.updateName(newName);

        assertEquals(newName, facility.getName());
    }

    @Test
    void updateName_undefined() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateName(null)
        );

        assertEquals(Facility.NAME_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateFacilityType_valid() {
        Facility facility = randomFacility();
        FacilityType newType = FacilityType.random();

        facility.updateFacilityType(newType);

        assertEquals(newType, facility.getFacilityType());
    }

    @Test
    void updateFacilityType_undefined() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateFacilityType(null)
        );

        assertEquals(Facility.TYPE_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateLocation_valid() {
        Facility facility = randomFacility();
        Location newLocation = randomLocation();

        facility.updateLocation(newLocation);

        assertEquals(newLocation, facility.getLocation());
    }

    @Test
    void updateLocation_undefined() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateLocation(null)
        );

        assertEquals(Facility.LOCATION_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateStorageCapacity_valid() {
        Facility facility = randomFacility();
        StorageCapacityKilograms newCapacity = new StorageCapacityKilograms(2000.0);

        facility.updateStorageCapacity(newCapacity);

        assertEquals(newCapacity, facility.getStorageCapacity());
    }

    @Test
    void updateStorageCapacity_undefined() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateStorageCapacity(null)
        );

        assertEquals(Facility.STORAGE_CAPACITY_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateProcessingCapacity_valid() {
        Facility facility = randomFacility();
        ProcessingCapacityKilogramsPerDay newCapacity = new ProcessingCapacityKilogramsPerDay(800.0);

        facility.updateProcessingCapacity(newCapacity);

        assertEquals(newCapacity, facility.getProcessingCapacity());
    }

    @Test
    void updateProcessingCapacity_undefined() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateProcessingCapacity(null)
        );

        assertEquals(Facility.PROCESSING_CAPACITY_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateUnloadingTime_valid() {
        Facility facility = randomFacility();
        UnloadingTime newTime = new UnloadingTime(90);

        facility.updateUnloadingTime(newTime);

        assertEquals(newTime, facility.getUnloadingTime());
    }

    @Test
    void updateUnloadingTime_undefined() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateUnloadingTime(null)
        );

        assertEquals(Facility.UNLOADING_TIME_NOT_DEFINED, exception.getMessage());
    }

    @Test
    void updateOpeningFixedCost_valid() {
        Facility facility = randomFacility();
        OpeningFixedCost newCost = new OpeningFixedCost(5000.0);

        facility.updateOpeningFixedCost(newCost);

        assertEquals(newCost, facility.getOpeningFixedCost());
    }

    @Test
    void updateOpeningFixedCost_undefined() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateOpeningFixedCost(null)
        );

        assertEquals(Facility.OPENING_COST_NOT_DEFINED, exception.getMessage());
    }

    // ========== processWaste & recordTruckUnload ==========

    @Test
    void processWaste_negativeAmount() {
        Facility facility = randomFacility();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.processWaste(-1.0)
        );

        assertEquals("Processing amount cannot be negative", exception.getMessage());
    }

    @Test
    void processWaste_validAmount_doesNotThrow() {
        Facility facility = randomFacility();

        facility.processWaste(0.0);
        facility.processWaste(100.0);
    }

    @Test
    void recordTruckUnload_discardedFacility() {
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

    @Test
    void recordTruckUnload_negativeWaste() {
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

        assertEquals("Waste amount cannot be negative", exception.getMessage());
    }

    @Test
    void recordTruckUnload_validAmount_doesNotThrow() {
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

    // ========== equals() ==========
    
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
    
    // ========== hashCode() ==========
    
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
    
    // ========== STATE MODIFIERS ==========
    
    @Test
    void updateStatus_valid() {
        Facility facility = randomFacility();
        FacilityStatus newStatus = FacilityStatus.random();
        
        facility.updateStatus(newStatus);
        
        assertEquals(newStatus, facility.getStatus());
    }
    
    @Test
    void updateStatus_undefined() {
        Facility facility = randomFacility();
        FacilityStatus newStatus = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> facility.updateStatus(newStatus)
        );
        
        assertEquals(Facility.STATUS_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void assignWasteDemand_valid() {
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
    
    @Test
    void assignWasteDemand_multiple() {
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
    
    @Test
    void assignWasteDemand_facilityDiscarded() {
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
    
    // ========== toString() ==========
    
    @Test
    void toStringMethod() {
        Facility facility = randomFacility();
        
        String expectedValue = String.format(
            "Facility={id=%s, name=%s, type=%s, location=%s, storageCapacity=%s, processingCapacity=%s, unloadingTime=%s, currentFillingLevel=%s, openingCost=%s, status=%s}",
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
