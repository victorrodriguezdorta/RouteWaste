package es.ull.project.domain.enumerate;

/**
 * ContainerStatus
 *
 * Enumeration representing the monitoring status of a container for a given day.
 */
public enum ContainerStatus {
    CORRECT,
    OVERFLOWED;

    public static ContainerStatus random() {
        ContainerStatus[] values = values();
        return values[(int) (Math.random() * values.length)];
    }

    public static ContainerStatus fromString(String value) {
        if (value == null) {
            return CORRECT;
        }
        try {
            return ContainerStatus.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return CORRECT;
        }
    }
}
