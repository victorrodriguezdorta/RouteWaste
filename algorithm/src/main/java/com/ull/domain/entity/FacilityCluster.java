package com.ull.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of assigning containers to a facility during the
 * clustering phase of the algorithm.
 *
 * <p>A cluster groups a facility with the containers that will be collected
 * and delivered to it during the planning horizon.
 */
public class FacilityCluster {

  public static final String FACILITY_NOT_DEFINED = "Facility is not defined";
  public static final String CONTAINERS_NOT_DEFINED = "Container list is not defined";

  private final Facility facility;
  private final List<Container> assignedContainers;

  /**
   * Creates a cluster for the given facility.
   *
   * @param facility the facility that this cluster belongs to
   */
  public FacilityCluster(Facility facility) {
    validateFacility(facility);
    this.facility = facility;
    this.assignedContainers = new ArrayList<>();
  }

  /**
   * Validates that the facility assigned to the cluster exists.
   *
   * @param facility the facility to validate
   */
  private void validateFacility(Facility facility) {
    if (facility == null) {
      throw new IllegalArgumentException(FACILITY_NOT_DEFINED);
    }
  }

  /**
   * Validates that a container can be assigned to this cluster.
   *
   * @param container the container to validate
   */
  private void validateContainer(Container container) {
    if (container == null) {
      throw new IllegalArgumentException(CONTAINERS_NOT_DEFINED);
    }
  }

  /**
   * Returns the facility that owns this cluster.
   *
   * @return the facility associated with the assigned containers
   */
  public Facility getFacility() {
    return this.facility;
  }

  /**
   * Returns an unmodifiable view of the containers assigned to this cluster.
   *
   * @return the assigned containers
   */
  public List<Container> getAssignedContainers() {
    return Collections.unmodifiableList(this.assignedContainers);
  }

  /**
   * Assigns a container to this cluster.
   *
   * @param container the container to add
   */
  public void addContainer(Container container) {
    validateContainer(container);
    this.assignedContainers.add(container);
  }

  /**
   * Returns the number of containers currently assigned to this cluster.
   *
   * @return the assigned container count
   */
  public int getSize() {
    return this.assignedContainers.size();
  }

  /**
   * Compares this cluster with another object by facility.
   *
   * @param otherObject object to compare
   * @return true when both clusters belong to the same facility
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    FacilityCluster other = (FacilityCluster) otherObject;
    return Objects.equals(this.facility, other.facility);
  }

  /**
   * Returns a hash code based on the cluster facility.
   *
   * @return hash code for this cluster
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.facility);
  }

  /**
   * Returns a readable representation of this cluster.
   *
   * @return text containing the facility id and number of containers
   */
  @Override
  public String toString() {
    return String.format(
        "FacilityCluster{facility=%s, assignedContainers=%d}",
        this.facility.getId(),
        this.assignedContainers.size());
  }
}
