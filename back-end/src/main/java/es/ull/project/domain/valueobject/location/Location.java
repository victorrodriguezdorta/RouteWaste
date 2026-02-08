package es.ull.project.domain.valueobject.location;

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
     * Latitude and longitude constraints.
     */
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final String ERROR_RANGE_LATITUDE = "Latitude must be between -90 and 90";
    private static final String ERROR_RANGE_LONGITUDE = "Longitude must be between -180 and 180";

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
     * Latitude in decimal degrees.
     * It is a required attribute.
     */
    private final double latitude;

    /**
     * Longitude in decimal degrees.
     * It is a required attribute.
     */
    private final double longitude;

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
        this.validateCoordinates(latitude, longitude);
        this.validatePostalAddress(postalAddress);
        this.validateGISReference(gisReference);
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalAddress = postalAddress;
        this.gisReference = gisReference;
    }

    /**
     * Validates that the coordinates are within valid ranges.
     *
     * @param latitude the latitude to validate
     * @param longitude the longitude to validate
     * @throws IllegalArgumentException if coordinates are out of range
     */
    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException(ERROR_RANGE_LATITUDE);
        }
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException(ERROR_RANGE_LONGITUDE);
        }
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
     * Returns the latitude in decimal degrees.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude in decimal degrees.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
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
        return Double.compare(other.latitude, latitude) == ZERO &&
               Double.compare(other.longitude, longitude) == ZERO &&
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
        return Objects.hash(latitude, longitude, postalAddress, gisReference);
    }

    /**
     * Returns a string representation of this location.
     *
     * @return a formatted string with location details
     */
    @Override
    public String toString() {
        return String.format("Location={latitude=%s, longitude=%s, postalAddress='%s', gisReference='%s'}",
                latitude, longitude, postalAddress, gisReference);
    }
}