package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;

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
    
    private static ContainerCapacityLiters randomCapacityLiters() {
        return new ContainerCapacityLiters(100.0 + Math.random() * 900.0);
    }
    
    private static DailyWasteDemandLitersPerDay randomDailyDemandLitersPerDay() {
        return new DailyWasteDemandLitersPerDay(10.0 + Math.random() * 100.0);
    }
    
    private static Container randomContainer() {
        return new Container(
            randomName(),
            randomLocation(),
            WasteType.random(),
            randomCapacityLiters(),
            randomDailyDemandLitersPerDay(),
            ServiceZone.random()
        );
    }

    private static Name randomName() {
        return new Name("container-" + ((int) (Math.random() * 10000)));
    }
    
    // ========== PUBLIC CONSTRUCTORS ==========
    
    @Test
    void constructor_1_right() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        
        Name name = randomName();
        Container container = new Container(name, location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone);
        
        // Required attributes:
        assertEquals(location, container.getLocation());
        assertEquals(wasteType, container.getWasteType());
        assertEquals(capacityLiters, container.getCapacityLiters());
        assertEquals(dailyDemandLitersPerDay, container.getDailyDemandLitersPerDay());
        
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
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        
        Name name = randomName();
        Container container = new Container(name, location, wasteType, capacityLiters, dailyDemandLitersPerDay);
        
        // Required attributes:
        assertEquals(location, container.getLocation());
        assertEquals(wasteType, container.getWasteType());
        assertEquals(capacityLiters, container.getCapacityLiters());
        assertEquals(dailyDemandLitersPerDay, container.getDailyDemandLitersPerDay());
        
        // Computed attributes:
        assertNotNull(container.getId());
        
        // Optional attributes:
        assertFalse(container.getServiceZone().isPresent());
    }
    
    @Test
    void constructor_1_location_undefined() {
        Location location = null;
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone)
        );
        
        assertEquals(Container.LOCATION_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_wasteType_undefined() {
        Location location = randomLocation();
        WasteType wasteType = null;
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone)
        );
        
        assertEquals(Container.WASTE_TYPE_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_capacityLiters_undefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = null;
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone)
        );
        
        assertEquals(Container.CAPACITY_LITERS_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_1_dailyDemandLitersPerDay_undefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = null;
        ServiceZone serviceZone = ServiceZone.random();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone)
        );
        
        assertEquals(Container.DAILY_DEMAND_LITERS_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_2_location_undefined() {
        Location location = null;
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay)
        );
        
        assertEquals(Container.LOCATION_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_2_wasteType_undefined() {
        Location location = randomLocation();
        WasteType wasteType = null;
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay)
        );
        
        assertEquals(Container.WASTE_TYPE_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_2_capacityLiters_undefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = null;
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay)
        );
        
        assertEquals(Container.CAPACITY_LITERS_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void constructor_2_dailyDemandLitersPerDay_undefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay)
        );
        
        assertEquals(Container.DAILY_DEMAND_LITERS_NOT_DEFINED, exception.getMessage());
    }
    
    // ========== equals() ==========
    
    @Test
    void equalsMethod() {
        Container container1 = randomContainer();
        Container container2 = new Container(
            randomName(),
            container1.getLocation(),
            container1.getWasteType(),
            container1.getCapacityLiters(),
            container1.getDailyDemandLitersPerDay()
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
            randomName(),
            container1.getLocation(),
            container1.getWasteType(),
            container1.getCapacityLiters(),
            container1.getDailyDemandLitersPerDay()
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
    void updateCapacityLiters_valid() {
        Container container = randomContainer();
        ContainerCapacityLiters originalCapacity = container.getCapacityLiters();
        ContainerCapacityLiters newCapacity = randomCapacityLiters();
        
        container.updateCapacityLiters(newCapacity);
        
        assertEquals(newCapacity, container.getCapacityLiters());
        assertNotEquals(originalCapacity, container.getCapacityLiters());
    }
    
    @Test
    void updateCapacityLiters_undefined() {
        Container container = randomContainer();
        ContainerCapacityLiters newCapacity = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> container.updateCapacityLiters(newCapacity)
        );
        
        assertEquals(Container.CAPACITY_LITERS_NOT_DEFINED, exception.getMessage());
    }
    
    @Test
    void updateDailyDemandLitersPerDay_valid() {
        Container container = randomContainer();
        DailyWasteDemandLitersPerDay originalDemand = container.getDailyDemandLitersPerDay();
        DailyWasteDemandLitersPerDay newDemand = randomDailyDemandLitersPerDay();
        
        container.updateDailyDemandLitersPerDay(newDemand);
        
        assertEquals(newDemand, container.getDailyDemandLitersPerDay());
        assertNotEquals(originalDemand, container.getDailyDemandLitersPerDay());
    }
    
    @Test
    void updateDailyDemandLitersPerDay_undefined() {
        Container container = randomContainer();
        DailyWasteDemandLitersPerDay newDemand = null;
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> container.updateDailyDemandLitersPerDay(newDemand)
        );
        
        assertEquals(Container.DAILY_DEMAND_LITERS_NOT_DEFINED, exception.getMessage());
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
            "Container={id=%s, name=%s, location=%s, wasteType=%s, capacityLiters=%s, dailyDemandLitersPerDay=%s, serviceZone=%s}",
            container.getId(),
            container.getName(),
            container.getLocation(),
            container.getWasteType(),
            container.getCapacityLiters(),
            container.getDailyDemandLitersPerDay(),
            container.getServiceZone().isPresent() ? container.getServiceZone().get() : null
        );
        
        assertEquals(expectedValue, container.toString());
    }
    
    @Test
    void toStringMethod_withoutServiceZone() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        Container container = new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay);
        
        String expectedValue = String.format(
            "Container={id=%s, name=%s, location=%s, wasteType=%s, capacityLiters=%s, dailyDemandLitersPerDay=%s, serviceZone=%s}",
            container.getId(),
            container.getName(),
            container.getLocation(),
            container.getWasteType(),
            container.getCapacityLiters(),
            container.getDailyDemandLitersPerDay(),
            null
        );
        
        assertEquals(expectedValue, container.toString());
    }
}
