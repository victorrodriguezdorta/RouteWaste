package es.ull.project.domain.entity;

import es.ull.project.domain.valueobject.alert.StopAlertMessage;
import es.ull.project.domain.valueobject.alert.StopAlertType;
import es.ull.project.domain.valueobject.alert.StopAlertValue;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an alert generated during stop processing.
 */
public class StopAlert {

  /**
   * Type of alert.
   * It is a required attribute.
   */
  private final StopAlertType type;
  
  /**
   * Human-readable message describing the alert.
   * It is a required attribute.
   */
  private final StopAlertMessage message;
  
  /**
   * Optional numeric value for context.
   * It is an optional attribute.
   */
  private final StopAlertValue value;

  /**
   * Restore constructor.
   * Creates a stop alert with an optional numeric context value.
   *
   * @param type the alert type
   * @param message the alert message
   * @param value optional numeric value
   */
  public StopAlert(StopAlertType type, StopAlertMessage message, StopAlertValue value) {
    this.type = type;
    this.message = message;
    this.value = value;
  }

  /**
   * Copy constructor.
   *
   * @param otherObject the stop alert to copy
   */
  public StopAlert(StopAlert otherObject) {
    this.type = otherObject.type;
    this.message = otherObject.message;
    this.value = otherObject.value;
  }

  /**
   * Creates a stop alert without numeric context.
   *
   * @param type the alert type
   * @param message the alert message
   */
  public StopAlert(StopAlertType type, StopAlertMessage message) {
    this(type, message, null);
  }

  /**
   * Creates a stop alert from scalar values.
   *
   * @param type the alert type
   * @param message the alert message
   * @param value optional numeric value
   * @return created stop alert
   */
  public static StopAlert fromValues(String type, String message, Double value) {
    return new StopAlert(
        new StopAlertType(type),
        new StopAlertMessage(message),
        value != null ? new StopAlertValue(value) : null);
  }

  /**
   * Creates a stop alert from scalar values without numeric context.
   *
   * @param type the alert type
   * @param message the alert message
   * @return created stop alert
   */
  public static StopAlert fromValues(String type, String message) {
    return fromValues(type, message, null);
  }

  /**
   * Returns the alert type.
   *
   * @return alert type
   */
  public StopAlertType getType() {
    return this.type;
  }

  /**
   * Returns the alert message.
   *
   * @return alert message
   */
  public StopAlertMessage getMessage() {
    return this.message;
  }

  /**
   * Returns the optional alert value.
   *
   * @return alert value
   */
  public Optional<StopAlertValue> getValue() {
    return Optional.ofNullable(this.value);
  }

  /**
   * Compares stop alerts by their field values.
   *
   * @param otherObject object to compare
   * @return true when both alerts have the same values
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    StopAlert other = (StopAlert) otherObject;
    return Objects.equals(this.type, other.type)
        && Objects.equals(this.message, other.message)
        && Objects.equals(this.value, other.value);
  }

  /**
   * Returns a hash code based on all field values.
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.type, this.message, this.value);
  }

  /**
   * Returns a string representation of this stop alert.
   *
   * @return formatted stop alert text
   */
  @Override
  public String toString() {
    return "StopAlert{" +
        "type='" + type + '\'' +
        ", message='" + message + '\'' +
        (value != null ? ", value=" + value : "") +
        '}';
  }
}
