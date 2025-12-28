package es.ull.project.domain.valueobject.demand;

import java.util.Objects;

/**
 * Represents the maximum capacity of a facility.
 * Value Object (VO) defined by its value and optional unit.
 */
public final class Capacity {

    private static final String ERROR_CAPACITY_NEGATIVE = "Capacity must be greater than or equal to 0";

    /**
     * Capacity value.
     */
    private final double value;

    /**
     * Optional unit (e.g., tons/day, tons/year).
     */
    private final String unit;

    /**
     * Creates a new Capacity object.
     *
     * @param value Capacity value (must be ≥ 0)
     * @param unit  Optional unit (can be null)
     */
    public Capacity(double value, String unit) {
        this.validateValue(value);
        this.value = value;
        this.unit = unit != null ? unit : "";
    }

    private void validateValue(double value) {
        if (value < 0) {
            throw new IllegalArgumentException(ERROR_CAPACITY_NEGATIVE);
        }
    }

    public double getValue() {
        return this.value;
    }

    public String getUnit() {
        return this.unit;
    }

    /**
     * Returns a new Capacity object with a different value.
     *
     * @param newValue New capacity value
     * @return New Capacity object
     */
    public Capacity setValue(double newValue) {
        return new Capacity(newValue, this.unit);
    }

    /**
     * Returns a new Capacity object with a different unit.
     *
     * @param newUnit New unit
     * @return New Capacity object
     */
    public Capacity setUnit(String newUnit) {
        return new Capacity(this.value, newUnit);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (this.getClass() != otherObject.getClass()) {
            return false;
        }
        Capacity otherCapacity = (Capacity) otherObject;
        return Double.compare(this.value, otherCapacity.value) == 0 &&
               this.unit.equals(otherCapacity.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }

    @Override
    public String toString() {
        return String.format("Capacity={value=%s,unit='%s'}", this.value, this.unit);
    }
}