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

  private final double amount;
  private final String currency;

  public MaximumBudget(double amount, String currency) {
    validateAmount(amount);
    validateCurrency(currency);
    this.amount = amount;
    this.currency = currency;
  }

  public double getAmount() {
    return this.amount;
  }

  public String getCurrency() {
    return this.currency;
  }

  private void validateAmount(double amount) {
    if (Double.isNaN(amount) || Double.isInfinite(amount)) {
      throw new IllegalArgumentException(AMOUNT_NOT_VALID);
    }
  }

  private void validateCurrency(String currency) {
    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException(CURRENCY_NOT_DEFINED);
    }
    if (currency.length() != 3) {
      throw new IllegalArgumentException(CURRENCY_NOT_VALID);
    }
  }

  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    MaximumBudget other = (MaximumBudget) otherObject;
    return Double.compare(this.amount, other.amount) == 0 && Objects.equals(this.currency, other.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.amount, this.currency);
  }

  @Override
  public String toString() {
    return String.format("MaximumBudget{amount=%s, currency='%s'}", this.amount, this.currency);
  }
}
