package es.ull.project.domain.valueobject.capacity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VehicleCapacityKilogramsTests {

    @Test
    void givenValidVehicleCapacityKilograms_whenInstantiating_thenSuccess() {
        VehicleCapacityKilograms capacity = new VehicleCapacityKilograms(1200.0);
        assertThat(capacity.getKilograms()).isEqualTo(1200.0);
    }

    @Test
    void givenNegativeVehicleCapacityKilograms_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new VehicleCapacityKilograms(-1.0));
        assertThat(exception.getMessage()).isEqualTo("Vehicle capacity cannot be negative");
    }

    @Test
    void givenEqualVehicleCapacityKilograms_whenEquals_thenTrue() {
        VehicleCapacityKilograms firstCapacity = new VehicleCapacityKilograms(1000.0);
        VehicleCapacityKilograms secondCapacity = new VehicleCapacityKilograms(1000.0);
        assertThat(firstCapacity).isEqualTo(secondCapacity);
        assertThat(firstCapacity.hashCode()).isEqualTo(secondCapacity.hashCode());
    }

    @Test
    void givenDifferentVehicleCapacityKilograms_whenEquals_thenFalse() {
        VehicleCapacityKilograms firstCapacity = new VehicleCapacityKilograms(1000.0);
        VehicleCapacityKilograms secondCapacity = new VehicleCapacityKilograms(1500.0);
        assertThat(firstCapacity).isNotEqualTo(secondCapacity);
    }
}
