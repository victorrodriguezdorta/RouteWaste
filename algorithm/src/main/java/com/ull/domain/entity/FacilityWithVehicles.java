package com.ull.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Groups a facility with the vehicles assigned to operate from it.
 *
 * This entity is part of the algorithm input model and represents the
 * pairing received in the "facilitiesWithVehicles" section of the JSON.
 */
public class FacilityWithVehicles {

  public static final String FACILITY_NOT_DEFINED = "Facility is not defined";
  public static final String VEHICLES_NOT_DEFINED = "Vehicle list is not defined";

  private final Facility facility;
  private final List<Vehicle> vehicles;

  /**
   * Creates a facility-vehicles grouping.
   *
   * @param facility the facility
   * @param vehicles the list of vehicles assigned to that facility
   */
  public FacilityWithVehicles(Facility facility, List<Vehicle> vehicles) {
    validateFacility(facility);
    validateVehicles(vehicles);
    this.facility = facility;
    this.vehicles = new ArrayList<>(vehicles);
  }

  private void validateFacility(Facility facility) {
    if (facility == null) {
      throw new IllegalArgumentException(FACILITY_NOT_DEFINED);
    }
  }

  private void validateVehicles(List<Vehicle> vehicles) {
    if (vehicles == null) {
      throw new IllegalArgumentException(VEHICLES_NOT_DEFINED);
    }
  }

  public Facility getFacility() {
    return this.facility;
  }

  /**
   * Returns an unmodifiable view of the vehicles list.
   *
   * @return the list of vehicles
   */
  public List<Vehicle> getVehicles() {
    return Collections.unmodifiableList(this.vehicles);
  }

  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    FacilityWithVehicles other = (FacilityWithVehicles) otherObject;
    return Objects.equals(this.facility, other.facility);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.facility);
  }

  @Override
  public String toString() {
    return String.format(
        "FacilityWithVehicles{facility=%s, vehicles=%s}",
        this.facility,
        this.vehicles);
  }
}
