package es.ull.project.domain.valueobject.demand;

import java.util.concurrent.TimeUnit;
import java.util.Objects;

/**
 * WasteDemand
 *
 * Representa la demanda esperada de residuos por unidad de tiempo (por ejemplo, toneladas por día).
 * Es un value object inmutable que encapsula el valor, la unidad de cantidad y la unidad de tiempo.
 */
public final class WasteDemand {

    // Mensajes de error
    private static final String ERROR_DEMAND_NEGATIVE = "Waste demand cannot be negative";
    private static final String ERROR_UNITS_NULL = "Units cannot be null";
    private static final String ERROR_OTHER_DEMAND_NULL = "Other WasteDemand cannot be null";
    private static final String ERROR_CAPACITY_NULL = "Capacity cannot be null";
    private static final String ERROR_UNITS_MUST_MATCH = "Units must be the same";
    private static final String ERROR_UNITS_MUST_MATCH_CAPACITY = "Units must be the same to compare WasteDemand and Capacity";

    /**
     * Valor de la demanda de residuos por unidad de tiempo.
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
     * Crea una nueva WasteDemand (demanda de residuos por tiempo).
     *
     * @param value        Valor de demanda (debe ser ≥ 0)
     * @param quantityUnit Unidad de cantidad
     * @param timeUnit     Unidad de tiempo
     */
    public WasteDemand(double value, QuantityUnit quantityUnit, TimeUnit timeUnit) {
        if (value < 0) {
            throw new IllegalArgumentException(ERROR_DEMAND_NEGATIVE);
        }
        if (quantityUnit == null || timeUnit == null) {
            throw new IllegalArgumentException(ERROR_UNITS_NULL);
        }
        this.value = value;
        this.quantityUnit = quantityUnit;
        this.timeUnit = timeUnit;
    }

    /**
     * Crea una nueva WasteDemand con valores por defecto (toneladas/día).
     *
     * @param value Valor de demanda
     */
    public WasteDemand(double value) {
        this(value, new QuantityUnit("tons"), TimeUnit.DAYS); 
    }

    /**
     * Devuelve el valor de la demanda.
     *
     * @return Valor de demanda
     */
    public double getValue() {
        return value;
    }

    /**
     * Devuelve la unidad de cantidad.
     *
     * @return QuantityUnit
     */
    public QuantityUnit getQuantityUnit() {
        return quantityUnit;
    }

    /**
     * Devuelve la unidad de tiempo.
     *
     * @return TimeUnit
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Devuelve una nueva WasteDemand con el valor actualizado.
     *
     * @param newValue Nuevo valor
     * @return Nueva WasteDemand
     */
    public WasteDemand setValue(double newValue) {
        return new WasteDemand(newValue, this.quantityUnit, this.timeUnit);
    }

    /**
     * Devuelve una nueva WasteDemand con la unidad de cantidad actualizada.
     *
     * @param newQuantityUnit Nueva unidad de cantidad
     * @return Nueva WasteDemand
     */
    public WasteDemand setQuantityUnit(QuantityUnit newQuantityUnit) {
        return new WasteDemand(this.value, newQuantityUnit, this.timeUnit);
    }

    /**
     * Devuelve una nueva WasteDemand con la unidad de tiempo actualizada.
     *
     * @param newTimeUnit Nueva unidad de tiempo
     * @return Nueva WasteDemand
     */
    public WasteDemand setTimeUnit(TimeUnit newTimeUnit) {
        return new WasteDemand(this.value, this.quantityUnit, newTimeUnit);
    }

    /**
     * Suma dos WasteDemand si tienen las mismas unidades.
     *
     * @param other Otra WasteDemand
     * @return Nueva WasteDemand con la suma
     * @throws IllegalArgumentException si las unidades no coinciden o el otro objeto es nulo
     */
    public WasteDemand add(WasteDemand other) {
        validateSameUnit(other);
        return new WasteDemand(this.value + other.value, quantityUnit, timeUnit);
    }

    /**
     * Compara si esta demanda es mayor que otra.
     *
     * @param other Otra WasteDemand
     * @return true si es mayor, false en caso contrario
     * @throws IllegalArgumentException si las unidades no coinciden o el otro objeto es nulo
     */
    public boolean greaterThan(WasteDemand other) {
        validateSameUnit(other);
        return this.value > other.value;
    }

    /**
     * Compara si esta demanda es mayor que una capacidad.
     *
     * @param capacity Capacity a comparar
     * @return true si es mayor, false en caso contrario
     * @throws IllegalArgumentException si las unidades no coinciden o el objeto es nulo
     */
    public boolean greaterThan(Capacity capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(ERROR_CAPACITY_NULL);
        }
        if (!this.quantityUnit.equals(capacity.getQuantityUnit()) ||
            !this.timeUnit.equals(capacity.getTimeUnit())) {
            throw new IllegalArgumentException(ERROR_UNITS_MUST_MATCH_CAPACITY);
        }
        return this.value > capacity.getValue();
    }

    /**
     * Valida que las unidades sean iguales entre dos WasteDemand.
     *
     * @param other Otra WasteDemand
     * @throws IllegalArgumentException si las unidades no coinciden o el otro objeto es nulo
     */
    private void validateSameUnit(WasteDemand other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_DEMAND_NULL);
        }
        if (!this.quantityUnit.equals(other.quantityUnit) ||
            !this.timeUnit.equals(other.timeUnit)) {
            throw new IllegalArgumentException(ERROR_UNITS_MUST_MATCH);
        }
    }

    /**
     * Compara la igualdad de dos WasteDemand.
     *
     * @param o Otro objeto
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        WasteDemand that = (WasteDemand) other;
        return Double.compare(that.value, value) == 0 &&
                quantityUnit.equals(that.quantityUnit) &&
                timeUnit.equals(that.timeUnit);
    }

    /**
     * Devuelve el hashCode de la WasteDemand.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, quantityUnit, timeUnit);
    }

    /**
     * Devuelve la representación en String de la WasteDemand.
     *
     * @return String
     */
    @Override
    public String toString() {
        return "WasteDemand=" + value + " " + quantityUnit + "/" + timeUnit;
    }
}