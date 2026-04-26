package com.ull.domain.valueobject.location;

import java.util.Objects;

/**
 * Location value object used by the algorithm module.
 *
 * This simplified version keeps only the data needed by the algorithm while
 * preserving the idea of an external value object from the backend model.
 */
public final class Location {

  public static final String LATITUDE_NOT_VALID = "Latitude is not valid";
  public static final String LONGITUDE_NOT_VALID = "Longitude is not valid";
  public static final String POSTAL_ADDRESS_NOT_DEFINED = "Postal address is not defined";
  public static final String GIS_REFERENCE_NOT_DEFINED = "GIS reference is not defined";

  private static final double EARTH_RADIUS_METERS = 6371000.0;

  private final double latitude;
  private final double longitude;
  private final String postalAddress;
  private final String gisReference;

  /**
   * Creates a location with coordinates and descriptive metadata.
   *
   * @param latitude the latitude in decimal degrees
   * @param longitude the longitude in decimal degrees
   * @param postalAddress the postal address
   * @param gisReference the GIS reference
   */
  public Location(double latitude, double longitude, String postalAddress, String gisReference) {
    validateLatitude(latitude);
    validateLongitude(longitude);
    validatePostalAddress(postalAddress);
    validateGisReference(gisReference);
    this.latitude = latitude;
    this.longitude = longitude;
    this.postalAddress = postalAddress;
    this.gisReference = gisReference;
  }

  private void validateLatitude(double latitude) {
    if (latitude < -90.0 || latitude > 90.0) {
      throw new IllegalArgumentException(LATITUDE_NOT_VALID);
    }
  }

  private void validateLongitude(double longitude) {
    if (longitude < -180.0 || longitude > 180.0) {
      throw new IllegalArgumentException(LONGITUDE_NOT_VALID);
    }
  }

  private void validatePostalAddress(String postalAddress) {
    if (postalAddress == null || postalAddress.isBlank()) {
      throw new IllegalArgumentException(POSTAL_ADDRESS_NOT_DEFINED);
    }
  }

  private void validateGisReference(String gisReference) {
    if (gisReference == null || gisReference.isBlank()) {
      throw new IllegalArgumentException(GIS_REFERENCE_NOT_DEFINED);
    }
  }

  public double getLatitude() {
    return this.latitude;
  }

  public double getLongitude() {
    return this.longitude;
  }

  public String getPostalAddress() {
    return this.postalAddress;
  }

  public String getGisReference() {
    return this.gisReference;
  }

  /**
   * Calculates the distance in meters to another location using the
   * Haversine formula.
   *
   * @param otherLocation the target location
   * @return the distance in meters
   */
  public double calculateDistanceTo(Location otherLocation) {
    Objects.requireNonNull(otherLocation, "Other location cannot be null");

    double latitudeDifference = Math.toRadians(otherLocation.latitude - this.latitude);
    double longitudeDifference = Math.toRadians(otherLocation.longitude - this.longitude);
    double originLatitude = Math.toRadians(this.latitude);
    double targetLatitude = Math.toRadians(otherLocation.latitude);

    double a = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
        + Math.cos(originLatitude) * Math.cos(targetLatitude)
            * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS_METERS * c;
  }

  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    Location otherLocation = (Location) otherObject;
    return Double.compare(this.latitude, otherLocation.latitude) == 0
        && Double.compare(this.longitude, otherLocation.longitude) == 0
        && Objects.equals(this.postalAddress, otherLocation.postalAddress)
        && Objects.equals(this.gisReference, otherLocation.gisReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.latitude, this.longitude, this.postalAddress, this.gisReference);
  }

  @Override
  public String toString() {
    return String.format(
        "Location{latitude=%s, longitude=%s, postalAddress='%s', gisReference='%s'}",
        this.latitude,
        this.longitude,
        this.postalAddress,
        this.gisReference);
  }
}
