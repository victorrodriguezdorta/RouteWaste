package es.ull.project.adapter.mongo.document.valueobject;

/**
 * DTO for persisting Distance value object in MongoDB (meters as double).
 */
public class DistanceDocument {
    private double meters;

    public DistanceDocument() {
    }

    public DistanceDocument(double meters) {
        this.meters = meters;
    }

    public double getMeters() {
        return meters;
    }

    public void setMeters(double meters) {
        this.meters = meters;
    }
}
