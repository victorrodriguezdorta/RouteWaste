package es.ull.project.adapter.mongo.document.valueobject;

/**
 * DTO for persisting ServiceTime value object in MongoDB (minutes as double).
 */
public class ServiceTimeDocument {
    private double minutes;

    public ServiceTimeDocument() {
    }

    public ServiceTimeDocument(double minutes) {
        this.minutes = minutes;
    }

    public double getMinutes() {
        return minutes;
    }

    public void setMinutes(double minutes) {
        this.minutes = minutes;
    }
}
