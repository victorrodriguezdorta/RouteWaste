package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.location.Location;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class LocationTests {

    private static final String VALID_POSTAL_ADDRESS = "123 Main St, City";
    private static final String VALID_POSTAL_ADDRESS_SHORT = "123 Main St";
    private static final String VALID_GIS_REFERENCE = "GIS-001";
    private static final String ACCENTED_POSTAL_ADDRESS =
            "Parque García Sanabria, Santa Cruz de Tenerife";
    private static final String LATITUDE_OUT_OF_RANGE_MESSAGE =
            "Latitude must be lesser or equals to 90.0";
    private static final String LONGITUDE_OUT_OF_RANGE_MESSAGE =
            "Longitude must be lesser or equals to 180.0";
    private static final String POSTAL_ADDRESS_NOT_DEFINED_MESSAGE = "Postal address is not defined";
    private static final String POSTAL_ADDRESS_EMPTY_MESSAGE = "Postal address cannot be empty";
    private static final String POSTAL_ADDRESS_TOO_LONG_MESSAGE =
            "Postal address must be at most 150 characters";
    private static final String POSTAL_ADDRESS_INVALID_FORMAT_MESSAGE =
            "Postal address format is invalid";
    private static final String GIS_REFERENCE_NOT_DEFINED_MESSAGE = "GIS reference is not defined";
    private static final String GIS_REFERENCE_EMPTY_MESSAGE = "GIS reference cannot be empty";
    private static final String GIS_REFERENCE_TOO_LONG_MESSAGE =
            "GIS reference must be at most 100 characters";
    private static final String LOCATION_STRING_REPRESENTATION =
            "Location={latitude=10.0, longitude=20.0, postalAddress='123 Main St', gisReference='GIS-001'}";

    /**
     * Constructor - valid values
     */
    @Test
    void constructorValidValue() {
        Location loc = new Location(10.0, 20.0, VALID_POSTAL_ADDRESS, VALID_GIS_REFERENCE);
        assertEquals(10.0, loc.getLatitude());
        assertEquals(20.0, loc.getLongitude());
        assertEquals(VALID_POSTAL_ADDRESS, loc.getPostalAddress());
        assertEquals(VALID_GIS_REFERENCE, loc.getGISReference());
    }

    /**
     * Constructor - latitude out of range
     */
    @Test
    void constructorInvalidLatitude() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(100.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE)
        );
        assertEquals(LATITUDE_OUT_OF_RANGE_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - longitude out of range
     */
    @Test
    void constructorInvalidLongitude() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 200.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE)
        );
        assertEquals(LONGITUDE_OUT_OF_RANGE_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - postalAddress null
     */
    @Test
    void constructorPostalAddressNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, null, VALID_GIS_REFERENCE)
        );
        assertEquals(POSTAL_ADDRESS_NOT_DEFINED_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - postalAddress empty
     */
    @Test
    void constructorPostalAddressEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, "", VALID_GIS_REFERENCE)
        );
        assertEquals(POSTAL_ADDRESS_EMPTY_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - postalAddress too long
     */
    @Test
    void constructorPostalAddressTooLong() {
        String longAddress = "A".repeat(151);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, longAddress, VALID_GIS_REFERENCE)
        );
        assertEquals(POSTAL_ADDRESS_TOO_LONG_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - postalAddress with Spanish accents (allowed)
     */
    @Test
    void constructorPostalAddressWithAccents() {
        Location loc = new Location(
                28.4682,
                -16.2546,
                ACCENTED_POSTAL_ADDRESS,
                VALID_GIS_REFERENCE);
        assertEquals(ACCENTED_POSTAL_ADDRESS, loc.getPostalAddress());
    }

    /**
     * Constructor - postalAddress wrong format
     */
    @Test
    void constructorPostalAddressWrongFormat() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, "123#Main St!", VALID_GIS_REFERENCE)
        );
        assertEquals(POSTAL_ADDRESS_INVALID_FORMAT_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - GIS reference null
     */
    @Test
    void constructorGisNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, null)
        );
        assertEquals(GIS_REFERENCE_NOT_DEFINED_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - GIS reference empty
     */
    @Test
    void constructorGisEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, "")
        );
        assertEquals(GIS_REFERENCE_EMPTY_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - GIS reference too long
     */
    @Test
    void constructorGisTooLong() {
        String longGis = "G".repeat(101);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, longGis)
        );
        assertEquals(GIS_REFERENCE_TOO_LONG_MESSAGE, exception.getMessage());
    }

    /**
     * Getters
     */
    @Test
    void getters() {
        Location loc = new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
        assertEquals(10.0, loc.getLatitude());
        assertEquals(20.0, loc.getLongitude());
        assertEquals(VALID_POSTAL_ADDRESS_SHORT, loc.getPostalAddress());
        assertEquals(VALID_GIS_REFERENCE, loc.getGISReference());
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        Location l1 = new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
        Location l2 = new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
        Location l3 = new Location(11.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
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
        Location l1 = new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
        Location l2 = new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
        Location l3 = new Location(11.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
        assertEquals(l1.hashCode(), l2.hashCode());
        assertNotEquals(l1.hashCode(), l3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        Location loc = new Location(10.0, 20.0, VALID_POSTAL_ADDRESS_SHORT, VALID_GIS_REFERENCE);
        assertEquals(LOCATION_STRING_REPRESENTATION, loc.toString());
    }
}
