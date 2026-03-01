package es.ull.project.adapter.mongo.document.valueobject;

/**
 * DTO to persist `Capacity` value object in MongoDB.
 */
public class CapacityDocument {

    private double value;
    private String quantityUnit;
    private String timeUnit;

    public CapacityDocument() {
    }

    public CapacityDocument(double value, String quantityUnit, String timeUnit) {
        this.value = value;
        this.quantityUnit = quantityUnit;
        this.timeUnit = timeUnit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}
