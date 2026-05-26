package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.response.bulk.BulkImportItemFailureBody;
import es.ull.project.adapter.rest.response.bulk.BulkImportResponseBody;
import es.ull.project.application.model.BulkCreateItemFailure;
import es.ull.project.application.model.BulkCreateOutcome;
import es.ull.project.domain.valueobject.page.NumberOfElements;
import es.ull.project.domain.valueobject.page.PageFlag;
import java.util.List;

/**
 * Maps bulk create outcomes to REST response bodies.
 */
public final class BulkImportResponseMapper {

    private static final String GENERAL_FIELD = "general";
    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private BulkImportResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION_MESSAGE);
    }

    /**
     * Converts a bulk create outcome to a REST response body.
     *
     * @param outcome application-layer bulk create result
     * @return REST response body
     */
    public static BulkImportResponseBody toResponseBody(BulkCreateOutcome outcome) {
        BulkImportResponseBody response = new BulkImportResponseBody();
        response.success = new PageFlag(outcome.isSuccess());
        response.totalRequested = new NumberOfElements(outcome.getTotalRequested());
        response.createdCount = new NumberOfElements(outcome.getCreatedCount());
        response.failedCount = new NumberOfElements(outcome.getFailedCount());
        response.failures = outcome.getFailures().stream()
                .map(BulkImportResponseMapper::toItemFailureBody)
                .toList();
        return response;
    }

    /**
     * Maps a single bulk create failure to its REST representation.
     *
     * @param failure application-layer failure for one item
     * @return REST failure body for that item
     */
    private static BulkImportItemFailureBody toItemFailureBody(BulkCreateItemFailure failure) {
        BulkImportItemFailureBody body = new BulkImportItemFailureBody();
        body.index = new NumberOfElements(failure.getIndex());
        body.errors = List.of(new FieldError(GENERAL_FIELD, failure.getMessage()));
        return body;
    }
}
