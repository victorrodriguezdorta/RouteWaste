package es.ull.project.domain.valueobject.location;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTests {

    /**
     * Constructor - valid values
     */
    @Test
    void constructor_validValue() {
        Location loc = new Location(10.0, 20.0, "123 Main St, City", "GIS-001");

        assertEquals(10.0, loc.getLatitude());
        assertEquals(20.0, loc.getLongitude());
        assertEquals("123 Main St, City", loc.getPostalAddress());
        assertEquals("GIS-001", loc.getGISReference());
    }

    /**
     * Constructor - latitude out of range
     */
    @Test
    void constructor_invalidLatitude() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(100.0, 20.0, "123 Main St", "GIS-001")
        );
        assertEquals("Latitude must be between -90 and 90", exception.getMessage());
    }

    /**
     * Constructor - longitude out of range
     */
    @Test
    void constructor_invalidLongitude() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 200.0, "123 Main St", "GIS-001")
        );
        assertEquals("Longitude must be between -180 and 180", exception.getMessage());
    }

    /**
     * Constructor - postalAddress null
     */
    @Test
    void constructor_postalAddressNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, null, "GIS-001")
        );
        assertEquals("Postal address is not defined", exception.getMessage());
    }

    /**
     * Constructor - postalAddress empty
     */
    @Test
    void constructor_postalAddressEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, "", "GIS-001")
        );
        assertEquals("Postal address cannot be empty", exception.getMessage());
    }

    /**
     * Constructor - postalAddress too long
     */
    @Test
    void constructor_postalAddressTooLong() {
        String longAddress = "A".repeat(151);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, longAddress, "GIS-001")
        );
        assertEquals("Postal address must be at most 150 characters", exception.getMessage());
    }

    /**
     * Constructor - postalAddress wrong format
     */
    @Test
    void constructor_postalAddressWrongFormat() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, "123#Main St!", "GIS-001")
        );
        assertEquals("Postal address format is invalid", exception.getMessage());
    }

    /**
     * Constructor - GIS reference null
     */
    @Test
    void constructor_gisNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, "123 Main St", null)
        );
        assertEquals("GIS reference is not defined", exception.getMessage());
    }

    /**
     * Constructor - GIS reference empty
     */
    @Test
    void constructor_gisEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, "123 Main St", "")
        );
        assertEquals("GIS reference cannot be empty", exception.getMessage());
    }

    /**
     * Constructor - GIS reference too long
     */
    @Test
    void constructor_gisTooLong() {
        String longGis = "G".repeat(101);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, "123 Main St", longGis)
        );
        assertEquals("GIS reference must be at most 100 characters", exception.getMessage());
    }

    /**
     * Getters
     */
    @Test
    void getters() {
        Location loc = new Location(10.0, 20.0, "123 Main St", "GIS-001");

        assertEquals(10.0, loc.getLatitude());
        assertEquals(20.0, loc.getLongitude());
        assertEquals("123 Main St", loc.getPostalAddress());
        assertEquals("GIS-001", loc.getGISReference());
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        Location l1 = new Location(10.0, 20.0, "123 Main St", "GIS-001");
        Location l2 = new Location(10.0, 20.0, "123 Main St", "GIS-001");
        Location l3 = new Location(11.0, 20.0, "123 Main St", "GIS-001");

        assertEquals(l1, l1);
        assertNotEquals(l1, null);
        assertNotEquals(l1, Integer.valueOf(0));
        assertEquals(l1, l2);
        assertNotEquals(l1, l3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        Location l1 = new Location(10.0, 20.0, "123 Main St", "GIS-001");
        Location l2 = new Location(10.0, 20.0, "123 Main St", "GIS-001");
        Location l3 = new Location(11.0, 20.0, "123 Main St", "GIS-001");

        assertEquals(l1.hashCode(), l2.hashCode());
        assertNotEquals(l1.hashCode(), l3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        Location loc = new Location(10.0, 20.0, "123 Main St", "GIS-001");
        assertEquals(
                "Location={latitude=10.0, longitude=20.0, postalAddress='123 Main St', gisReference='GIS-001'}",
                loc.toString()
        );
    }
}
