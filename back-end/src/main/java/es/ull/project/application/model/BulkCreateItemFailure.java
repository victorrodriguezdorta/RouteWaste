package es.ull.project.application.model;

/**
 * Describes a single failed item within a bulk create operation.
 */
public class BulkCreateItemFailure {

    private final int index;
    private final String message;

    /**
     * Creates a failure descriptor for one bulk item.
     *
     * @param index   zero-based position in the submitted batch
     * @param message human-readable failure reason
     */
    public BulkCreateItemFailure(int index, String message) {
        this.index = index;
        this.message = message;
    }

    /**
     * Returns the zero-based position of the failed item in the batch.
     *
     * @return item index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the human-readable failure reason.
     *
     * @return failure message
     */
    public String getMessage() {
        return message;
    }
}
