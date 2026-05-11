package es.ull.project.domain.entity;

/**
 * Represents an alert generated during stop processing.
 * 
 * <p>Simple data class for alerts that Gson will serialize/deserialize automatically.
 */
public class StopAlert {
  
  /**
   * Type of alert (e.g., "VEHICLE_FULL", "CONTAINER_OVERFLOWED").
   */
  public String type;
  
  /**
   * Human-readable message describing the alert.
   */
  public String message;
  
  /**
   * Optional numeric value for context.
   */
  public Double value;

  /**
   * Default constructor for Gson deserialization.
   */
  public StopAlert() {
  }

  /**
   * Constructor with all fields.
   * @param type the alert type
   * @param message the alert message
   * @param value optional numeric value
   */
  public StopAlert(String type, String message, Double value) {
    this.type = type;
    this.message = message;
    this.value = value;
  }

  /**
   * Constructor without value.
   * @param type the alert type
   * @param message the alert message
   */
  public StopAlert(String type, String message) {
    this(type, message, null);
  }

  @Override
  public String toString() {
    return "StopAlert{" +
        "type='" + type + '\'' +
        ", message='" + message + '\'' +
        (value != null ? ", value=" + value : "") +
        '}';
  }
}
