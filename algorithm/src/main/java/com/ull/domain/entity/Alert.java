package com.ull.domain.entity;

/**
 * Represents an alert generated during route planning.
 * 
 * <p>Simple value object that describes events or issues that occur
 * during the greedy routing algorithm execution.
 */
public class Alert {

  public static final String TYPE_NOT_DEFINED = "Alert type is not defined";
  public static final String MESSAGE_NOT_DEFINED = "Alert message is not defined";
  
  /**
   * Type of alert that occurred.
   */
  private final String type;
  
  /**
   * Human-readable message describing the alert.
   */
  private final String message;
  
  /**
   * Additional context data (optional).
   */
  private final Double value;

  /**
   * Create a new Alert.
   * @param type the alert type (e.g., "VEHICLE_FULL", "CONTAINER_OVERFLOWED")
   * @param message the human-readable description
   * @param value optional numeric context (e.g., capacity, load)
   */
  public Alert(String type, String message, Double value) {
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException(TYPE_NOT_DEFINED);
    }
    if (message == null || message.isBlank()) {
      throw new IllegalArgumentException(MESSAGE_NOT_DEFINED);
    }
    this.type = type;
    this.message = message;
    this.value = value;
  }

  /**
   * Create a new Alert without a value.
   * @param type the alert type
   * @param message the human-readable description
   */
  public Alert(String type, String message) {
    this(type, message, null);
  }

  /**
   * Get the alert type.
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Get the alert message.
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get the optional numeric value.
   * @return the value, or null if not set
   */
  public Double getValue() {
    return value;
  }

  /**
   * Returns a readable representation of this alert.
   *
   * @return text containing type, message, and optional value
   */
  @Override
  public String toString() {
    return "Alert{" +
        "type='" + type + '\'' +
        ", message='" + message + '\'' +
        (value != null ? ", value=" + value : "") +
        '}';
  }
}
