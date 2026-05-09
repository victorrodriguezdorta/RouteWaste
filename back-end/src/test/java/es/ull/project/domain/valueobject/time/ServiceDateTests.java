package es.ull.project.domain.valueobject.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ServiceDateTests {

    @Test
    void givenValidServiceDate_whenInstantiating_thenSuccess() {
        LocalDate date = LocalDate.of(2026, 5, 7);
        ServiceDate serviceDate = new ServiceDate(date);
        assertThat(serviceDate.getDate()).isEqualTo(date);
    }

    @Test
    void givenNullServiceDate_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ServiceDate(null));
        assertThat(exception.getMessage()).isEqualTo("Service date must not be null");
    }

    @Test
    void givenEqualServiceDate_whenEquals_thenTrue() {
        LocalDate date = LocalDate.of(2026, 5, 7);
        ServiceDate sd1 = new ServiceDate(date);
        ServiceDate sd2 = new ServiceDate(date);
        assertThat(sd1).isEqualTo(sd2);
        assertThat(sd1.hashCode()).isEqualTo(sd2.hashCode());
    }

    @Test
    void givenDifferentServiceDate_whenEquals_thenFalse() {
        ServiceDate sd1 = new ServiceDate(LocalDate.of(2026, 5, 7));
        ServiceDate sd2 = new ServiceDate(LocalDate.of(2026, 5, 8));
        assertThat(sd1).isNotEqualTo(sd2);
    }
}
