package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


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
    
    private static Capacity randomCapacity() {
        return new Capacity(
            100.0 + Math.random() * 500.0,
            new QuantityUnit("tons"),
            TimeUnit.DAY
        );
    }
    
    private static OpeningFixedCost randomOpeningFixedCost() {
        return new OpeningFixedCost(10000.0 + Math.random() * 50000.0);
    }
    
    private static Facility randomFacility() {
        return new Facility(
            FacilityType.random(),
            randomLocation(),
            randomCapacity(),
            randomOpeningFixedCost(),
            FacilityStatus.random()
        );
    }
    
    // ========== PUBLIC CONSTRUCTORS ==========
    
    @Test
    void constructor_1_right() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        Capacity capacity = randomCapacity();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        Facility facility = new Facility(facilityType, location, capacity, openingFixedCost, status);
        
        // Required attributes:
        assertEquals(facilityType, facility.getFacilityType());
        assertEquals(location, facility.getLocation());
        assertEquals(capacity, facility.getCapacity());
        assertEquals(openingFixedCost, facility.getOpeningFixedCost());
        assertEquals(status, facility.getStatus());
        
        // Computed attributes:
        assertNotNull(facility.getId());
        assertNotNull(facility.getAssignedWasteDemand());
        assertEquals(0.0, facility.getAssignedWasteDemand().getValue());
    }
    
    @Test
    void constructor_1_facilityType_undefined() {
        FacilityType facilityType = null;
        Location location = randomLocation();
        Capacity capacity = randomCapacity();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(facilityType, location, capacity, openingFixedCost, status)
        );
        
        assertEquals(Facility.TYPE_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_location_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = null;
        Capacity capacity = randomCapacity();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(facilityType, location, capacity, openingFixedCost, status)
        );
        
        assertEquals(Facility.LOCATION_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_capacity_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        Capacity capacity = null;
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(facilityType, location, capacity, openingFixedCost, status)
        );
        
        assertEquals(Facility.CAPACITY_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_openingFixedCost_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        Capacity capacity = randomCapacity();
        OpeningFixedCost openingFixedCost = null;
        FacilityStatus status = FacilityStatus.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(facilityType, location, capacity, openingFixedCost, status)
        );
        
        assertEquals(Facility.OPENING_COST_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_status_undefined() {
        FacilityType facilityType = FacilityType.random();
        Location location = randomLocation();
        Capacity capacity = randomCapacity();
        OpeningFixedCost openingFixedCost = randomOpeningFixedCost();
        FacilityStatus status = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Facility(facilityType, location, capacity, openingFixedCost, status)
        );
        
        assertEquals(Facility.STATUS_NOT_DEFINED, exception.getMessage());
    }
    
    // ========== equals() ==========
    
    @Test
    void equalsMethod() {
        Facility facility1 = randomFacility();
        Facility facility2 = new Facility(
            facility1.getFacilityType(),
            facility1.getLocation(),
            facility1.getCapacity(),
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
            facility1.getFacilityType(),
            facility1.getLocation(),
            facility1.getCapacity(),
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
        Capacity capacity = new Capacity(100.0, new QuantityUnit("tons"), TimeUnit.DAY);
        Facility facility = new Facility(
            FacilityType.random(),
            randomLocation(),
            capacity,
            randomOpeningFixedCost(),
            FacilityStatus.PLANNED
        );
        
        WasteDemand demand = new WasteDemand(10.0);
        facility.assignWasteDemand(demand);
        
        assertEquals(10.0, facility.getAssignedWasteDemand().getValue());
    }
    
    @Test
    void assignWasteDemand_multiple() {
        Capacity capacity = new Capacity(100.0, new QuantityUnit("tons"), TimeUnit.DAY);
        Facility facility = new Facility(
            FacilityType.random(),
            randomLocation(),
            capacity,
            randomOpeningFixedCost(),
            FacilityStatus.PLANNED
        );
        
        WasteDemand demand1 = new WasteDemand(20.0);
        WasteDemand demand2 = new WasteDemand(30.0);
        
        facility.assignWasteDemand(demand1);
        facility.assignWasteDemand(demand2);
        
        assertEquals(50.0, facility.getAssignedWasteDemand().getValue());
    }
    
    @Test
    void assignWasteDemand_capacityExceeded() {
        Capacity capacity = new Capacity(50.0, new QuantityUnit("tons"), TimeUnit.DAY);
        Facility facility = new Facility(
            FacilityType.random(),
            randomLocation(),
            capacity,
            randomOpeningFixedCost(),
            FacilityStatus.PLANNED
        );
        
        WasteDemand demand = new WasteDemand(60.0);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> facility.assignWasteDemand(demand)
        );
        
        assertEquals(Facility.CAPACITY_EXCEEDED, exception.getMessage());
    }
    
    @Test
    void assignWasteDemand_facilityDiscarded() {
        Facility facility = new Facility(
            FacilityType.random(),
            randomLocation(),
            randomCapacity(),
            randomOpeningFixedCost(),
            FacilityStatus.DISCARDED
        );
        
        WasteDemand demand = new WasteDemand(10.0);
        
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
            "Facility={id=%s, type=%s, location=%s, capacity=%s, assignedDemand=%s, openingCost=%s, status=%s}",
            facility.getId(),
            facility.getFacilityType(),
            facility.getLocation(),
            facility.getCapacity(),
            facility.getAssignedWasteDemand(),
            facility.getOpeningFixedCost(),
            facility.getStatus()
        );
        
        assertEquals(expectedValue, facility.toString());
    }
}
