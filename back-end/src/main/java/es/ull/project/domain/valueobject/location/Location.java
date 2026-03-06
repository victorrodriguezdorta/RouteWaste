package es.ull.project.domain.valueobject.location;

import es.ull.utils.geolocation.UllGeolocationPoint;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Location
 * 
 * Represents a physical location.
 * Can contain coordinates (latitude, longitude), postal address, and GIS reference.
 * This is a mandatory value object in the domain.
 */
public final class Location {

    /**
     * Postal address constraints.
     */
    private static final int POSTAL_ADDRESS_MAX_LENGTH = 150;
    private static final Pattern POSTAL_ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9\\s,.-]+$");
    private static final String ERROR_ADDRESS_NOT_DEFINED = "Postal address is not defined";
    private static final String ERROR_ADDRESS_EMPTY = "Postal address cannot be empty";
    private static final String ERROR_ADDRESS_MAX_LENGTH = "Postal address must be at most " + POSTAL_ADDRESS_MAX_LENGTH + " characters";
    private static final String ERROR_ADDRESS_FORMAT = "Postal address format is invalid";

    /**
     * GIS reference constraints.
     */
    private static final int GIS_REFERENCE_MAX_LENGTH = 100;
    private static final String ERROR_GIS_NOT_DEFINED = "GIS reference is not defined";
    private static final String ERROR_GIS_EMPTY = "GIS reference cannot be empty";
    private static final String ERROR_GIS_MAX_LENGTH = "GIS reference must be at most " + GIS_REFERENCE_MAX_LENGTH + " characters";

    /**
     * Zero constant for comparisons.
     * Used to avoid magic numbers in the code and improve readability.
     */
    private static final int ZERO = 0;

    /**
     * Geolocation point containing latitude and longitude.
     * It is a required attribute.
     */
    private final UllGeolocationPoint point;

    /**
     * Postal address.
     * It is a required attribute.
     */
    private final String postalAddress;

    /**
     * GIS reference. (Geographic Information System)
     * TODO: maybe change String to a more complex type if needed in the future.
     * It is a required attribute.
     */
    private final String gisReference;

    /**
     * Creates a new Location with coordinates, postal address, and GIS reference.
     * 
     * @param latitude Latitude in decimal degrees.
     * @param longitude Longitude in decimal degrees.
     * @param postalAddress Postal address.
     * @param gisReference GIS reference. (Geographic Information System)
     */
    public Location(double latitude, double longitude, String postalAddress, String gisReference) {
        this.point = new UllGeolocationPoint(longitude, latitude);
        this.validatePostalAddress(postalAddress);
        this.validateGISReference(gisReference);
        this.postalAddress = postalAddress;
        this.gisReference = gisReference;
    }

    /**
     * Validates the postal address.
     *
     * @param postalAddress the postal address to validate
     * @throws IllegalArgumentException if the postal address is invalid
     */
    private void validatePostalAddress(String postalAddress) {
        if (postalAddress == null) {
            throw new IllegalArgumentException(ERROR_ADDRESS_NOT_DEFINED);
        }
        int length = postalAddress.length();
        if (length == ZERO) {
            throw new IllegalArgumentException(ERROR_ADDRESS_EMPTY);
        }
        if (length > POSTAL_ADDRESS_MAX_LENGTH) {
            throw new IllegalArgumentException(ERROR_ADDRESS_MAX_LENGTH);
        }
        if (!POSTAL_ADDRESS_PATTERN.matcher(postalAddress).matches()) {
            throw new IllegalArgumentException(ERROR_ADDRESS_FORMAT);
        }
    }

    /**
     * Validates the GIS reference.
     *
     * @param gisReference the GIS reference to validate
     * @throws IllegalArgumentException if the GIS reference is invalid
     */
    private void validateGISReference(String gisReference) {
        if (gisReference == null) {
            throw new IllegalArgumentException(ERROR_GIS_NOT_DEFINED);
        }
        int length = gisReference.length();
        if (length == ZERO) {
            throw new IllegalArgumentException(ERROR_GIS_EMPTY);
        }
        if (length > GIS_REFERENCE_MAX_LENGTH) {
            throw new IllegalArgumentException(ERROR_GIS_MAX_LENGTH);
        }
    }

    /**
     * Returns the geolocation point.
     *
     * @return the geolocation point
     */
    public UllGeolocationPoint getPoint() {
        return point;
    }

    /**
     * Returns the latitude in decimal degrees.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return point.getLatitude();
    }

    /**
     * Returns the longitude in decimal degrees.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return point.getLongitude();
    }

    /**
     * Returns the postal address.
     *
     * @return the postal address
     */
    public String getPostalAddress() {
        return postalAddress;
    }

    /**
     * Returns the GIS reference.
     *
     * @return the GIS reference
     */
    public String getGISReference() {
        return gisReference;
    }

    /**
     * Compares this location with another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Location other = (Location) otherObject;
        return point.equals(other.point) &&
               postalAddress.equals(other.postalAddress) &&
               gisReference.equals(other.gisReference);
    }

    /**
     * Returns the hash code of this location.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(point, postalAddress, gisReference);
    }

    /**
     * Returns a string representation of this location.
     *
     * @return a formatted string with location details
     */
    @Override
    public String toString() {
        return String.format("Location={latitude=%s, longitude=%s, postalAddress='%s', gisReference='%s'}",
                point.getLatitude(), point.getLongitude(), postalAddress, gisReference);
    }
}