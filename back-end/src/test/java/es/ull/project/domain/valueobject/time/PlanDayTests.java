package es.ull.project.domain.valueobject.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PlanDayTests {

    @Test
    void givenValidDay_whenInstantiating_thenSuccess() {
        PlanDay day = new PlanDay(1);
        assertThat(day.getDay()).isEqualTo(1);
    }

    @Test
    void givenValidDay_whenFromInteger_thenSuccess() {
        PlanDay day = PlanDay.fromInteger(1);
        assertThat(day.getDay()).isEqualTo(1);
    }

    @Test
    void givenNullDay_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PlanDay(null));
        assertThat(exception.getMessage()).isEqualTo("Plan day must be a non-negative integer");
    }

    @Test
    void givenNegativeDay_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PlanDay(-1));
        assertThat(exception.getMessage()).isEqualTo("Plan day must be a non-negative integer");
    }

    @Test
    void givenEqualDay_whenEquals_thenTrue() {
        PlanDay day1 = new PlanDay(1);
        PlanDay day2 = new PlanDay(1);
        assertThat(day1).isEqualTo(day2);
        assertThat(day1.hashCode()).isEqualTo(day2.hashCode());
    }

    @Test
    void givenDifferentDay_whenEquals_thenFalse() {
        PlanDay day1 = new PlanDay(1);
        PlanDay day2 = new PlanDay(2);
        assertThat(day1).isNotEqualTo(day2);
    }
}
