package es.ull.project.application.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of a bulk create operation over multiple entities.
 */
public class BulkCreateOutcome {

    private final int totalRequested;
    private final int createdCount;
    private final List<BulkCreateItemFailure> failures;

    /**
     * Creates a bulk create outcome summary.
     *
     * @param totalRequested number of items submitted
     * @param createdCount   number of items persisted successfully
     * @param failures       per-item failures (may be empty)
     */
    public BulkCreateOutcome(int totalRequested, int createdCount, List<BulkCreateItemFailure> failures) {
        this.totalRequested = totalRequested;
        this.createdCount = createdCount;
        this.failures = failures == null ? List.of() : List.copyOf(failures);
    }

    /**
     * Returns the number of items submitted in the bulk request.
     *
     * @return total items requested
     */
    public int getTotalRequested() {
        return totalRequested;
    }

    /**
     * Returns the number of items persisted successfully.
     *
     * @return created item count
     */
    public int getCreatedCount() {
        return createdCount;
    }

    /**
     * Returns a defensive copy of per-item failures.
     *
     * @return failure details for items that could not be created
     */
    public List<BulkCreateItemFailure> getFailures() {
        return new ArrayList<>(failures);
    }

    /**
     * Returns the number of items that failed to be created.
     *
     * @return failed item count
     */
    public int getFailedCount() {
        return failures.size();
    }

    /**
     * Indicates whether every submitted item was created successfully.
     *
     * @return {@code true} when there are no failures
     */
    public boolean isSuccess() {
        return failures.isEmpty();
    }
}
