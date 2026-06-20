package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.time.ServiceDate;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ServiceDateTests {

    private static final String NULL_SERVICE_DATE_MESSAGE = "Service date must not be null";

    /**
     * Verifies that a valid service date is stored correctly.
     */
    @Test
    void givenValidServiceDateWhenInstantiatingThenSuccess() {
        LocalDate date = LocalDate.of(2026, 5, 7);
        ServiceDate serviceDate = new ServiceDate(date);
        assertThat(serviceDate.getDate()).isEqualTo(date);
    }

    /**
     * Verifies that null service dates are rejected.
     */
    @Test
    void givenNullServiceDateWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ServiceDate(null));
        assertThat(exception.getMessage()).isEqualTo(NULL_SERVICE_DATE_MESSAGE);
    }

    /**
     * Verifies that service dates with equal values are equal and share the same hash code.
     */
    @Test
    void givenEqualServiceDateWhenEqualsThenTrue() {
        LocalDate date = LocalDate.of(2026, 5, 7);
        ServiceDate sd1 = new ServiceDate(date);
        ServiceDate sd2 = new ServiceDate(date);
        assertThat(sd1).isEqualTo(sd2);
        assertThat(sd1.hashCode()).isEqualTo(sd2.hashCode());
    }

    /**
     * Verifies that service dates with different values are not equal.
     */
    @Test
    void givenDifferentServiceDateWhenEqualsThenFalse() {
        ServiceDate sd1 = new ServiceDate(LocalDate.of(2026, 5, 7));
        ServiceDate sd2 = new ServiceDate(LocalDate.of(2026, 5, 8));
        assertThat(sd1).isNotEqualTo(sd2);
    }
}
