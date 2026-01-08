package es.ull.project.domain.valueobject.demand;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Capacity
 *
 * Representa una cantidad por unidad de tiempo (por ejemplo, toneladas por día).
 * Es un value object inmutable que encapsula el valor, la unidad de cantidad y la unidad de tiempo.
 */
public final class Capacity {

    // Mensajes de error
    private static final String ERROR_VALUE_NEGATIVE = "Capacity value must be greater than or equal to 0";
    private static final String ERROR_QUANTITY_UNIT_NOT_DEFINED = "Quantity unit is not defined";
    private static final String ERROR_TIME_UNIT_NOT_DEFINED = "Time unit is not defined";
    private static final String ERROR_OTHER_CAPACITY_NULL = "Other Capacity cannot be null";
    private static final String ERROR_UNITS_MUST_MATCH = "Units must be the same";

    /**
     * Valor de cantidad por unidad de tiempo.
     */
    private final double value;

    /**
     * Unidad de cantidad (por ejemplo, toneladas).
     */
    private final QuantityUnit quantityUnit;

    /**
     * Unidad de tiempo (por ejemplo, día).
     */
    private final TimeUnit timeUnit;

    /**
     * Crea una nueva Capacity (cantidad por tiempo).
     *
     * @param value        Valor de cantidad (debe ser ≥ 0)
     * @param quantityUnit Unidad de cantidad
     * @param timeUnit     Unidad de tiempo
     */
    public Capacity(double value, QuantityUnit quantityUnit, TimeUnit timeUnit) {
        this.validateValue(value);

        if (quantityUnit == null) {
            throw new IllegalArgumentException(ERROR_QUANTITY_UNIT_NOT_DEFINED);
        }
        if (timeUnit == null) {
            throw new IllegalArgumentException(ERROR_TIME_UNIT_NOT_DEFINED);
        }

        this.value = value;
        this.quantityUnit = quantityUnit;
        this.timeUnit = timeUnit;
    }

    /**
     * Valida que el valor sea mayor o igual que cero.
     *
     * @param value Valor a validar
     */
    private void validateValue(double value) {
        if (value < 0) {
            throw new IllegalArgumentException(ERROR_VALUE_NEGATIVE);
        }
    }

    /**
     * Devuelve el valor de cantidad.
     *
     * @return Valor de cantidad
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Devuelve la unidad de cantidad.
     *
     * @return QuantityUnit
     */
    public QuantityUnit getQuantityUnit() {
        return this.quantityUnit;
    }

    /**
     * Devuelve la unidad de tiempo.
     *
     * @return TimeUnit
     */
    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    /**
     * Devuelve una nueva Capacity con el valor actualizado.
     *
     * @param newValue Nuevo valor
     * @return Nueva Capacity
     */
    public Capacity setValue(double newValue) {
        return new Capacity(newValue, this.quantityUnit, this.timeUnit);
    }

    /**
     * Devuelve una nueva Capacity con la unidad de cantidad actualizada.
     *
     * @param newQuantityUnit Nueva unidad de cantidad
     * @return Nueva Capacity
     */
    public Capacity setQuantityUnit(QuantityUnit newQuantityUnit) {
        return new Capacity(this.value, newQuantityUnit, this.timeUnit);
    }

    /**
     * Devuelve una nueva Capacity con la unidad de tiempo actualizada.
     *
     * @param newTimeUnit Nueva unidad de tiempo
     * @return Nueva Capacity
     */
    public Capacity setTimeUnit(TimeUnit newTimeUnit) {
        return new Capacity(this.value, this.quantityUnit, newTimeUnit);
    }

    /**
     * Compara si esta capacidad es mayor que otra.
     *
     * @param other Otra Capacity
     * @return true si es mayor, false en caso contrario
     * @throws IllegalArgumentException si las unidades no coinciden o el otro objeto es nulo
     */
    public boolean greaterThan(Capacity other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        if (!this.quantityUnit.equals(other.quantityUnit) ||
            !this.timeUnit.equals(other.timeUnit)) {
            throw new IllegalArgumentException(ERROR_UNITS_MUST_MATCH);
        }
        return this.value > other.value;
    }

    /**
     * Compara la igualdad de dos Capacity.
     *
     * @param otherObject Otro objeto
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Capacity other = (Capacity) otherObject;
        return Double.compare(this.value, other.value) == 0 &&
                this.quantityUnit.equals(other.quantityUnit) &&
                this.timeUnit.equals(other.timeUnit);
    }

    /**
     * Devuelve el hashCode de la Capacity.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, quantityUnit, timeUnit);
    }

    /**
     * Devuelve la representación en String de la Capacity.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format(
                "Capacity={value=%s, unit=%s/%s}",
                this.value,
                this.quantityUnit.getValue(),
                this.timeUnit.name().toLowerCase()
        );
    }
}