package es.ull.project.domain.enumerate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the ServiceZone enumeration.
 */
@DisplayName("ServiceZone Tests")
class ServiceZoneTests {

    @Nested
    @DisplayName("fromString method tests")
    class FromStringTests {

        @Test
        @DisplayName("Should return NEIGHBORHOOD for valid string")
        void shouldReturnNeighborhoodForValidString() {
            assertEquals(ServiceZone.NEIGHBORHOOD, ServiceZone.fromString("NEIGHBORHOOD"));
            assertEquals(ServiceZone.NEIGHBORHOOD, ServiceZone.fromString("neighborhood"));
            assertEquals(ServiceZone.NEIGHBORHOOD, ServiceZone.fromString("  NEIGHBORHOOD  "));
        }

        @Test
        @DisplayName("Should return DISTRICT for valid string")
        void shouldReturnDistrictForValidString() {
            assertEquals(ServiceZone.DISTRICT, ServiceZone.fromString("DISTRICT"));
            assertEquals(ServiceZone.DISTRICT, ServiceZone.fromString("district"));
            assertEquals(ServiceZone.DISTRICT, ServiceZone.fromString("  DISTRICT  "));
        }

        @Test
        @DisplayName("Should return GEOGRAPHICAL_AREA for valid string")
        void shouldReturnGeographicalAreaForValidString() {
            assertEquals(ServiceZone.GEOGRAPHICAL_AREA, ServiceZone.fromString("GEOGRAPHICAL_AREA"));
            assertEquals(ServiceZone.GEOGRAPHICAL_AREA, ServiceZone.fromString("geographical_area"));
            assertEquals(ServiceZone.GEOGRAPHICAL_AREA, ServiceZone.fromString("  GEOGRAPHICAL_AREA  "));
        }

        @Test
        @DisplayName("Should throw exception for null input")
        void shouldThrowExceptionForNullInput() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.fromString(null)
            );
            assertEquals("Service zone is not defined", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for invalid input")
        void shouldThrowExceptionForInvalidInput() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.fromString("INVALID_ZONE")
            );
            assertTrue(exception.getMessage().contains("Service zone is invalid"));
        }
    }

    @Nested
    @DisplayName("indexOf method tests")
    class IndexOfTests {

        @Test
        @DisplayName("Should return correct ordinal for NEIGHBORHOOD")
        void shouldReturnCorrectOrdinalForNeighborhood() {
            assertEquals(0, ServiceZone.indexOf("NEIGHBORHOOD"));
        }

        @Test
        @DisplayName("Should return correct ordinal for DISTRICT")
        void shouldReturnCorrectOrdinalForDistrict() {
            assertEquals(1, ServiceZone.indexOf("DISTRICT"));
        }

        @Test
        @DisplayName("Should return correct ordinal for GEOGRAPHICAL_AREA")
        void shouldReturnCorrectOrdinalForGeographicalArea() {
            assertEquals(2, ServiceZone.indexOf("GEOGRAPHICAL_AREA"));
        }
    }

    @Nested
    @DisplayName("isValid method tests")
    class IsValidTests {

        @Test
        @DisplayName("Should return true for valid service zones")
        void shouldReturnTrueForValidServiceZones() {
            assertTrue(ServiceZone.isValid("NEIGHBORHOOD"));
            assertTrue(ServiceZone.isValid("DISTRICT"));
            assertTrue(ServiceZone.isValid("GEOGRAPHICAL_AREA"));
            assertTrue(ServiceZone.isValid("neighborhood"));
            assertTrue(ServiceZone.isValid("  district  "));
        }

        @Test
        @DisplayName("Should return false for null input")
        void shouldReturnFalseForNullInput() {
            assertFalse(ServiceZone.isValid(null));
        }

        @Test
        @DisplayName("Should return false for invalid input")
        void shouldReturnFalseForInvalidInput() {
            assertFalse(ServiceZone.isValid("INVALID"));
            assertFalse(ServiceZone.isValid(""));
            assertFalse(ServiceZone.isValid("   "));
        }
    }

    @Nested
    @DisplayName("random method tests")
    class RandomTests {

        @Test
        @DisplayName("Should return a valid ServiceZone")
        void shouldReturnValidServiceZone() {
            ServiceZone randomZone = ServiceZone.random();
            assertNotNull(randomZone);
            assertTrue(
                randomZone == ServiceZone.NEIGHBORHOOD ||
                randomZone == ServiceZone.DISTRICT ||
                randomZone == ServiceZone.GEOGRAPHICAL_AREA
            );
        }
    }

    @Nested
    @DisplayName("Enum values tests")
    class EnumValuesTests {

        @Test
        @DisplayName("Should have exactly three values")
        void shouldHaveExactlyThreeValues() {
            assertEquals(3, ServiceZone.values().length);
        }

        @Test
        @DisplayName("Should have correct enum names")
        void shouldHaveCorrectEnumNames() {
            assertEquals("NEIGHBORHOOD", ServiceZone.NEIGHBORHOOD.name());
            assertEquals("DISTRICT", ServiceZone.DISTRICT.name());
            assertEquals("GEOGRAPHICAL_AREA", ServiceZone.GEOGRAPHICAL_AREA.name());
        }
    }
}
