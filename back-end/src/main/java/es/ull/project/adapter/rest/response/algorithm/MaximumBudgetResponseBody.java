package es.ull.project.adapter.rest.response.algorithm;

/**
 * MaximumBudgetResponseBody
 *
 * Response DTO representing the maximum budget object returned to the client
 * and sent to the algorithm runner.
 */
public class MaximumBudgetResponseBody {

    /** Numeric amount of the maximum budget. */
    public Double amount;

    /** ISO 4217 currency code (e.g. "EUR"). */
    public String currency;
}
