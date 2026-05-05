package es.ull.project.adapter.rest.request.algorithm;

/**
 * MaximumBudgetRequestBody
 *
 * Request DTO representing a maximum budget object received from the client.
 */
public class MaximumBudgetRequestBody {

    /** Numeric amount of the maximum budget. */
    public Double amount;

    /** ISO 4217 currency code (e.g. "EUR"). Optional; defaults to EUR. */
    public String currency;
}
