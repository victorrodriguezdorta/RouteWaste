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

  private void validateFacility(Facility facility) {
    if (facility == null) {
      throw new IllegalArgumentException(FACILITY_NOT_DEFINED);
    }
  }

  private void validateContainer(Container container) {
    if (container == null) {
      throw new IllegalArgumentException(CONTAINERS_NOT_DEFINED);
    }
  }

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

  /** Returns the number of containers currently assigned to this cluster. */
  public int getSize() {
    return this.assignedContainers.size();
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(this.facility);
  }

  @Override
  public String toString() {
    return String.format(
        "FacilityCluster{facility=%s, assignedContainers=%d}",
        this.facility.getId(),
        this.assignedContainers.size());
  }
}
