package es.ull.project.adapter.rest.response.bulk;

import es.ull.project.domain.valueobject.page.NumberOfElements;
import es.ull.project.domain.valueobject.page.PageFlag;
import java.util.List;

/**
 * Standard response for bulk entity import operations.
 */
public class BulkImportResponseBody {

    /**
     * True when every submitted item was created successfully.
     */
    public PageFlag success;

    /**
     * Number of items in the submitted batch.
     */
    public NumberOfElements totalRequested;

    /**
     * Number of items persisted successfully.
     */
    public NumberOfElements createdCount;

    /**
     * Number of items that failed.
     */
    public NumberOfElements failedCount;

    /**
     * Per-item failure details (empty when {@link #success} is true).
     */
    public List<BulkImportItemFailureBody> failures;
}
