package es.ull.project.adapter.mongo.document.valueobject;

/**
 * Simple DTO for persisting Location value object in MongoDB.
 */
public class LocationDocument {

    private double latitude;
    private double longitude;
    private String postalAddress;
    private String gisReference;

    public LocationDocument() {
    }

    public LocationDocument(double latitude, double longitude, String postalAddress, String gisReference) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalAddress = postalAddress;
        this.gisReference = gisReference;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getGisReference() {
        return gisReference;
    }

    public void setGisReference(String gisReference) {
        this.gisReference = gisReference;
    }
}
