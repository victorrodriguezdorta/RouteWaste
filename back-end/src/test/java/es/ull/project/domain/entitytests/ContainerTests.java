package es.ull.project.domain.entitytests;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ContainerTests {

    /**
     * Creates a random location for container tests.
     *
     * @return valid location for container tests.
     */
    private static Location randomLocation() {
        return new Location(
                28.4682 + Math.random() * 0.1,
                -16.2546 + Math.random() * 0.1,
                "Test Address " + ((int) (Math.random() * 1000)),
                "GIS-REF-" + ((int) (Math.random() * 10000))
        );
    }

    /**
     * Creates a random container capacity for tests.
     *
     * @return valid container capacity for tests.
     */
    private static ContainerCapacityLiters randomCapacityLiters() {
        return new ContainerCapacityLiters(100.0 + Math.random() * 900.0);
    }

    /**
     * Creates a random daily waste demand for tests.
     *
     * @return valid daily waste demand for tests.
     */
    private static DailyWasteDemandLitersPerDay randomDailyDemandLitersPerDay() {
        return new DailyWasteDemandLitersPerDay(10.0 + Math.random() * 100.0);
    }

    /**
     * Creates a random container for tests that need a valid aggregate.
     *
     * @return valid container for tests.
     */
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

    /**
     * Creates a random container name.
     *
     * @return valid container name for tests.
     */
    private static Name randomName() {
        return new Name("container-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Verifies that the constructor with service zone stores required, computed, and optional attributes.
     */
    @Test
    void constructor1Right() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        Name name = randomName();
        Container container = new Container(name, location, wasteType, capacityLiters, dailyDemandLitersPerDay,
                serviceZone);
        assertEquals(location, container.getLocation());
        assertEquals(wasteType, container.getWasteType());
        assertEquals(capacityLiters, container.getCapacityLiters());
        assertEquals(dailyDemandLitersPerDay, container.getDailyDemandLitersPerDay());
        assertNotNull(container.getId());
        assertTrue(container.getServiceZone().isPresent());
        assertEquals(serviceZone, container.getServiceZone().get());
    }

    /**
     * Verifies that the constructor without service zone leaves it empty.
     */
    @Test
    void constructor2Right() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        Name name = randomName();
        Container container = new Container(name, location, wasteType, capacityLiters, dailyDemandLitersPerDay);
        assertEquals(location, container.getLocation());
        assertEquals(wasteType, container.getWasteType());
        assertEquals(capacityLiters, container.getCapacityLiters());
        assertEquals(dailyDemandLitersPerDay, container.getDailyDemandLitersPerDay());
        assertNotNull(container.getId());
        assertFalse(container.getServiceZone().isPresent());
    }

    /**
     * Verifies that the constructor with service zone rejects an undefined location.
     */
    @Test
    void constructor1LocationUndefined() {
        Location location = null;
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay,
                        serviceZone)
        );
        assertEquals(Container.LOCATION_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the constructor with service zone rejects an undefined waste type.
     */
    @Test
    void constructor1WasteTypeUndefined() {
        Location location = randomLocation();
        WasteType wasteType = null;
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay,
                        serviceZone)
        );
        assertEquals(Container.WASTE_TYPE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the constructor with service zone rejects undefined capacity liters.
     */
    @Test
    void constructor1CapacityLitersUndefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = null;
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        ServiceZone serviceZone = ServiceZone.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay,
                        serviceZone)
        );
        assertEquals(Container.CAPACITY_LITERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the constructor with service zone rejects undefined daily demand.
     */
    @Test
    void constructor1DailyDemandLitersPerDayUndefined() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = null;
        ServiceZone serviceZone = ServiceZone.random();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay,
                        serviceZone)
        );
        assertEquals(Container.DAILY_DEMAND_LITERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that the constructor without service zone rejects an undefined location.
     */
    @Test
    void constructor2LocationUndefined() {
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

    /**
     * Verifies that the constructor without service zone rejects an undefined waste type.
     */
    @Test
    void constructor2WasteTypeUndefined() {
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

    /**
     * Verifies that the constructor without service zone rejects undefined capacity liters.
     */
    @Test
    void constructor2CapacityLitersUndefined() {
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

    /**
     * Verifies that the constructor without service zone rejects undefined daily demand.
     */
    @Test
    void constructor2DailyDemandLitersPerDayUndefined() {
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

    /**
     * Verifies equality behavior for containers.
     */
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
        assertFalse(container1.equals(new Object()));
        assertNotEquals(container1, container2);
        assertNotEquals(container1.getId(), container2.getId());
        assertNotEquals(container1, container3);
        assertNotEquals(container1.getId(), container3.getId());
    }

    /**
     * Verifies hash code behavior for containers.
     */
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

    /**
     * Verifies that updating the location stores the new value.
     */
    @Test
    void updateLocationValid() {
        Container container = randomContainer();
        Location originalLocation = container.getLocation();
        Location newLocation = randomLocation();
        container.updateLocation(newLocation);
        assertEquals(newLocation, container.getLocation());
        assertNotEquals(originalLocation, container.getLocation());
    }

    /**
     * Verifies that updating the location rejects an undefined value.
     */
    @Test
    void updateLocationUndefined() {
        Container container = randomContainer();
        Location newLocation = null;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> container.updateLocation(newLocation)
        );
        assertEquals(Container.LOCATION_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating the waste type stores the new value.
     */
    @Test
    void updateWasteTypeValid() {
        Container container = randomContainer();
        WasteType newWasteType = WasteType.random();
        container.updateWasteType(newWasteType);
        assertEquals(newWasteType, container.getWasteType());
    }

    /**
     * Verifies that updating the waste type rejects an undefined value.
     */
    @Test
    void updateWasteTypeUndefined() {
        Container container = randomContainer();
        WasteType newWasteType = null;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> container.updateWasteType(newWasteType)
        );
        assertEquals(Container.WASTE_TYPE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating capacity liters stores the new value.
     */
    @Test
    void updateCapacityLitersValid() {
        Container container = randomContainer();
        ContainerCapacityLiters originalCapacity = container.getCapacityLiters();
        ContainerCapacityLiters newCapacity = randomCapacityLiters();
        container.updateCapacityLiters(newCapacity);
        assertEquals(newCapacity, container.getCapacityLiters());
        assertNotEquals(originalCapacity, container.getCapacityLiters());
    }

    /**
     * Verifies that updating capacity liters rejects an undefined value.
     */
    @Test
    void updateCapacityLitersUndefined() {
        Container container = randomContainer();
        ContainerCapacityLiters newCapacity = null;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> container.updateCapacityLiters(newCapacity)
        );
        assertEquals(Container.CAPACITY_LITERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating daily demand stores the new value.
     */
    @Test
    void updateDailyDemandLitersPerDayValid() {
        Container container = randomContainer();
        DailyWasteDemandLitersPerDay originalDemand = container.getDailyDemandLitersPerDay();
        DailyWasteDemandLitersPerDay newDemand = randomDailyDemandLitersPerDay();
        container.updateDailyDemandLitersPerDay(newDemand);
        assertEquals(newDemand, container.getDailyDemandLitersPerDay());
        assertNotEquals(originalDemand, container.getDailyDemandLitersPerDay());
    }

    /**
     * Verifies that updating daily demand rejects an undefined value.
     */
    @Test
    void updateDailyDemandLitersPerDayUndefined() {
        Container container = randomContainer();
        DailyWasteDemandLitersPerDay newDemand = null;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> container.updateDailyDemandLitersPerDay(newDemand)
        );
        assertEquals(Container.DAILY_DEMAND_LITERS_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Verifies that updating service zone stores the new optional value.
     */
    @Test
    void updateServiceZoneValid() {
        Container container = randomContainer();
        ServiceZone newServiceZone = ServiceZone.random();
        container.updateServiceZone(newServiceZone);
        assertTrue(container.getServiceZone().isPresent());
        assertEquals(newServiceZone, container.getServiceZone().get());
    }

    /**
     * Verifies that updating service zone to null clears the optional value.
     */
    @Test
    void updateServiceZoneNull() {
        Container container = randomContainer();
        container.updateServiceZone(null);
        assertFalse(container.getServiceZone().isPresent());
    }

    /**
     * Verifies the string representation of a container with a service zone.
     */
    @Test
    void toStringMethod() {
        Container container = randomContainer();
        String expectedValue = String.format(
                "Container={id=%s, name=%s, location=%s, wasteType=%s, capacityLiters=%s, "
                        + "dailyDemandLitersPerDay=%s, serviceZone=%s}",
                container.getId(),
                container.getName(),
                container.getLocation(),
                container.getWasteType(),
                container.getCapacityLiters(),
                container.getDailyDemandLitersPerDay(),
                container.getServiceZone().orElse(null)
        );
        assertEquals(expectedValue, container.toString());
    }

    /**
     * Verifies the string representation of a container without a service zone.
     */
    @Test
    void toStringMethodWithoutServiceZone() {
        Location location = randomLocation();
        WasteType wasteType = WasteType.random();
        ContainerCapacityLiters capacityLiters = randomCapacityLiters();
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = randomDailyDemandLitersPerDay();
        Container container = new Container(randomName(), location, wasteType, capacityLiters, dailyDemandLitersPerDay);
        String expectedValue = String.format(
                "Container={id=%s, name=%s, location=%s, wasteType=%s, capacityLiters=%s, "
                        + "dailyDemandLitersPerDay=%s, serviceZone=%s}",
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
