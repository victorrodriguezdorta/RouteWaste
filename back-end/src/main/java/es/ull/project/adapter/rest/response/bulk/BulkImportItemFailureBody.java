package es.ull.project.adapter.rest.response.bulk;

import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.domain.valueobject.page.NumberOfElements;
import java.util.List;

/**
 * Describes one failed item in a bulk import response.
 */
public class BulkImportItemFailureBody {

    /**
     * Zero-based index of the failed item in the submitted batch.
     */
    public NumberOfElements index;

    /**
     * Validation or business errors for this item.
     */
    public List<FieldError> errors;
}
