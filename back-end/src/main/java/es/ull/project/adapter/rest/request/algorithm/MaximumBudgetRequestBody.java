package es.ull.project.adapter.rest.request.algorithm;

import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;

/**
 * MaximumBudgetRequestBody
 *
 * Request DTO representing a maximum budget object received from the client.
 * Uses domain value objects to enforce type safety.
 */
public class MaximumBudgetRequestBody {

    /**
     * Numeric amount of the maximum budget expressed as a domain value object.
     * The underlying amount is a positive decimal number.
     */
    public MaximumBudget amount;

    /**
     * ISO 4217 currency code (e.g. "EUR"). Optional; defaults to EUR.
     * Represented as the domain Currency value object.
     */
    public Currency currency;
}
