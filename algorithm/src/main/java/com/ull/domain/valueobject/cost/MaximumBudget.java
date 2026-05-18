package com.ull.domain.valueobject.cost;

import java.util.Objects;

/**
 * MaximumBudget value object used by the algorithm module.
 */
public final class MaximumBudget {

  public static final String AMOUNT_NOT_DEFINED = "Maximum budget amount is not defined";
  public static final String AMOUNT_NOT_VALID = "Maximum budget amount is not valid";
  public static final String CURRENCY_NOT_DEFINED = "Maximum budget currency is not defined";
  public static final String CURRENCY_NOT_VALID = "Maximum budget currency is not valid";
  private static final int ISO_CURRENCY_CODE_LENGTH = 3;
  private static final int COMPARE_EQUALS = 0;

  private final double amount;
  private final String currency;

  /**
   * Creates a maximum budget with amount and currency.
   *
   * @param amount the budget amount
   * @param currency the ISO currency code
   */
  public MaximumBudget(double amount, String currency) {
    validateAmount(amount);
    validateCurrency(currency);
    this.amount = amount;
    this.currency = currency;
  }

  /**
   * Returns the budget amount.
   *
   * @return the maximum budget amount
   */
  public double getAmount() {
    return this.amount;
  }

  /**
   * Returns the budget currency.
   *
   * @return the ISO currency code
   */
  public String getCurrency() {
    return this.currency;
  }

  /**
   * Validates that the budget amount is finite.
   *
   * @param amount the amount to validate
   */
  private void validateAmount(double amount) {
    if (Double.isNaN(amount) || Double.isInfinite(amount)) {
      throw new IllegalArgumentException(AMOUNT_NOT_VALID);
    }
  }

  /**
   * Validates that the budget currency is a three-letter code.
   *
   * @param currency the currency to validate
   */
  private void validateCurrency(String currency) {
    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException(CURRENCY_NOT_DEFINED);
    }
    if (currency.length() != ISO_CURRENCY_CODE_LENGTH) {
      throw new IllegalArgumentException(CURRENCY_NOT_VALID);
    }
  }

  /**
   * Compares this budget with another object by amount and currency.
   *
   * @param otherObject object to compare
   * @return true when both budgets have the same amount and currency
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    MaximumBudget other = (MaximumBudget) otherObject;
    return Double.compare(this.amount, other.amount) == COMPARE_EQUALS && Objects.equals(this.currency, other.currency);
  }

  /**
   * Returns a hash code based on amount and currency.
   *
   * @return hash code for this budget
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.amount, this.currency);
  }

  /**
   * Returns a readable representation of this budget.
   *
   * @return text containing amount and currency
   */
  @Override
  public String toString() {
    return String.format("MaximumBudget{amount=%s, currency='%s'}", this.amount, this.currency);
  }
}
