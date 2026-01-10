package es.ull.project.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.demand.WasteDemand;

class ContainerTests {
    
    // ========== HELPER METHODS ==========
    
    private static Location randomLocation() {
        return new Location(
            28.4682 + Math.random() * 0.1, // Latitude around Tenerife
            -16.2546 + Math.random() * 0.1, // Longitude around Tenerife
            "Test Address " + ((int)(Math.random() * 1000)),
            "GIS-REF-" + ((int)(Math.random() * 10000))
        );
    }
    
    private static WasteDemand randomWasteDemand() {
        return new WasteDemand(10.0 + Math.random() * 100.0);
    }
    
    private static Container randomContainer() {
        return new Container(
            randomLocation(),
            WasteType.random(),
            randomWasteDemand(),
            ServiceZone.random()
        );
    }
    
    // ========== PUBLIC CONSTRUCTORS ==========
    
    @Test
    void constructor_1_right() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        WasteDemand wasteDemand = randomWasteDemand();
        ServiceZone serviceZone = ServiceZone.random();
        
        Container container = new Container(location, wasteType, wasteDemand, serviceZone);
        
        // Required attributes:
        assertEquals(location, container.getLocation());
        assertEquals(wasteType, container.getWasteType());
        assertEquals(wasteDemand, container.getWasteDemand());
        
        // Computed attributes:
        assertNotNull(container.getId());
        
        // Optional attributes:
        assertTrue(container.getServiceZone().isPresent());
        assertEquals(serviceZone, container.getServiceZone().get());
    }
    
    @Test
    void constructor_2_right() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        WasteDemand wasteDemand = randomWasteDemand();
        
        Container container = new Container(location, wasteType, wasteDemand);
        
        // Required attributes:
        assertEquals(location, container.getLocation());
        assertEquals(wasteType, container.getWasteType());
        assertEquals(wasteDemand, container.getWasteDemand());
        
        // Computed attributes:
        assertNotNull(container.getId());
        
        // Optional attributes:
        assertFalse(container.getServiceZone().isPresent());
    }
    
    @Test
    void constructor_1_location_undefined() {
        Location location = null;
        WasteType wasteType = WasteType.random();
        WasteDemand wasteDemand = randomWasteDemand();
        ServiceZone serviceZone = ServiceZone.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(location, wasteType, wasteDemand, serviceZone)
        );
        
        assertEquals(Container.LOCATION_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_wasteType_undefined() {
        Location location = randomLocation();
        WasteType wasteType = null;
        WasteDemand wasteDemand = randomWasteDemand();
        ServiceZone serviceZone = ServiceZone.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(location, wasteType, wasteDemand, serviceZone)
        );
        
        assertEquals(Container.WASTE_TYPE_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_wasteDemand_undefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        WasteDemand wasteDemand = null;
        ServiceZone serviceZone = ServiceZone.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(location, wasteType, wasteDemand, serviceZone)
        );
        
        assertEquals(Container.WASTE_DEMAND_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_2_location_undefined() {
        Location location = null;
        WasteType wasteType = WasteType.random();
        WasteDemand wasteDemand = randomWasteDemand();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(location, wasteType, wasteDemand)
        );
        
        assertEquals(Container.LOCATION_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_2_wasteType_undefined() {
        Location location = randomLocation();
        WasteType wasteType = null;
        WasteDemand wasteDemand = randomWasteDemand();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(location, wasteType, wasteDemand)
        );
        
        assertEquals(Container.WASTE_TYPE_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_2_wasteDemand_undefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        WasteDemand wasteDemand = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(location, wasteType, wasteDemand)
        );
        
        assertEquals(Container.WASTE_DEMAND_NOT_DEFINED, exception.getMessage());
    }
    
    // ========== equals() ==========
    
    @Test
    void equalsMethod() {
        Container container1 = randomContainer();
        Container container2 = new Container(
            container1.getLocation(),
            container1.getWasteType(),
            container1.getWasteDemand()
        );
        Container container3 = randomContainer();
        
        assertTrue(container1.equals(container1));
        assertFalse(container1.equals(null));
        assertFalse(container1.equals( new Object()));
        assertNotEquals(container1, container2);
        assertNotEquals(container1.getId(), container2.getId());
        assertNotEquals(container1, container3);
        assertNotEquals(container1.getId(), container3.getId());
    }
    
    // ========== hashCode() ==========
    
    @Test
    void hashCodeMethod() {
        Container container1 = randomContainer();
        Container container2 = new Container(
            container1.getLocation(),
            container1.getWasteType(),
            container1.getWasteDemand()
        );
        Container container3 = randomContainer();
        
        assertEquals(container1.hashCode(), container1.hashCode());
        assertNotEquals(container1.hashCode(), container2.hashCode());
        assertNotEquals(container1.hashCode(), container3.hashCode());
    }
    
    // ========== STATE MODIFIERS ==========
    
    @Test
    void updateLocation_valid() {
        Container container = randomContainer();
        Location originalLocation = container.getLocation();
        Location newLocation = randomLocation();
        
        container.updateLocation(newLocation);
        
        assertEquals(newLocation, container.getLocation());
        assertNotEquals(originalLocation, container.getLocation());
    }
    
    @Test
    void updateLocation_undefined() {
        Container container = randomContainer();
        Location newLocation = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> container.updateLocation(newLocation)
        );
        
        assertEquals(Container.LOCATION_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void updateWasteType_valid() {
        Container container = randomContainer();
        WasteType newWasteType = WasteType.random();
        
        container.updateWasteType(newWasteType);
        
        assertEquals(newWasteType, container.getWasteType());
    }
    
    @Test
    void updateWasteType_undefined() {
        Container container = randomContainer();
        WasteType newWasteType = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> container.updateWasteType(newWasteType)
        );
        
        assertEquals(Container.WASTE_TYPE_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void updateWasteDemand_valid() {
        Container container = randomContainer();
        WasteDemand originalWasteDemand = container.getWasteDemand();
        WasteDemand newWasteDemand = randomWasteDemand();
        
        container.updateWasteDemand(newWasteDemand);
        
        assertEquals(newWasteDemand, container.getWasteDemand());
        assertNotEquals(originalWasteDemand, container.getWasteDemand());
    }
    
    @Test
    void updateWasteDemand_undefined() {
        Container container = randomContainer();
        WasteDemand newWasteDemand = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> container.updateWasteDemand(newWasteDemand)
        );
        
        assertEquals(Container.WASTE_DEMAND_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void updateServiceZone_valid() {
        Container container = randomContainer();
        ServiceZone newServiceZone = ServiceZone.random();
        
        container.updateServiceZone(newServiceZone);
        
        assertTrue(container.getServiceZone().isPresent());
        assertEquals(newServiceZone, container.getServiceZone().get());
    }
    
    @Test
    void updateServiceZone_null() {
        Container container = randomContainer();
        
        container.updateServiceZone(null);
        
        assertFalse(container.getServiceZone().isPresent());
    }
    
    // ========== toString() ==========
    
    @Test
    void toStringMethod() {
        Container container = randomContainer();
        
        String expectedValue = String.format(
            "Container={id=%s, location=%s, wasteType=%s, wasteDemand=%s, serviceZone=%s}",
            container.getId(),
            container.getLocation(),
            container.getWasteType(),
            container.getWasteDemand(),
            container.getServiceZone().isPresent() ? container.getServiceZone().get() : null
        );
        
        assertEquals(expectedValue, container.toString());
    }
    
    @Test
    void toStringMethod_withoutServiceZone() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        WasteDemand wasteDemand = randomWasteDemand();
        Container container = new Container(location, wasteType, wasteDemand);
        
        String expectedValue = String.format(
            "Container={id=%s, location=%s, wasteType=%s, wasteDemand=%s, serviceZone=%s}",
            container.getId(),
            container.getLocation(),
            container.getWasteType(),
            container.getWasteDemand(),
            null
        );
        
        assertEquals(expectedValue, container.toString());
    }
}
