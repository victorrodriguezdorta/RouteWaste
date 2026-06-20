package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class VehicleCapacityKilogramsTests {

    private static final String NEGATIVE_VEHICLE_CAPACITY_MESSAGE = "Vehicle capacity cannot be negative";

    /**
     * Verifies that a valid vehicle capacity in kilograms is created successfully.
     */
    @Test
    void constructorValidValue() {
        VehicleCapacityKilograms capacity = new VehicleCapacityKilograms(1200.0);
        assertEquals(1200.0, capacity.getKilograms());
    }

    /**
     * Verifies that a negative vehicle capacity in kilograms throws an exception.
     */
    @Test
    void constructorNegativeValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VehicleCapacityKilograms(-1.0));
        assertEquals(NEGATIVE_VEHICLE_CAPACITY_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that equal vehicle capacities compare as equal.
     */
    @Test
    void equalsSameValue() {
        VehicleCapacityKilograms firstCapacity = new VehicleCapacityKilograms(1000.0);
        VehicleCapacityKilograms secondCapacity = new VehicleCapacityKilograms(1000.0);
        assertEquals(firstCapacity, secondCapacity);
        assertEquals(firstCapacity.hashCode(), secondCapacity.hashCode());
    }

    /**
     * Verifies that different vehicle capacities compare as different.
     */
    @Test
    void equalsDifferentValue() {
        VehicleCapacityKilograms firstCapacity = new VehicleCapacityKilograms(1000.0);
        VehicleCapacityKilograms secondCapacity = new VehicleCapacityKilograms(1500.0);
        assertNotEquals(firstCapacity, secondCapacity);
    }
}
