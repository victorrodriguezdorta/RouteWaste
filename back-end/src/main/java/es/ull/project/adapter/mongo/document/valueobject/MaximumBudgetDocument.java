package es.ull.project.adapter.mongo.document.valueobject;

public class MaximumBudgetDocument {
    private double amount;
    private String currency;

    public MaximumBudgetDocument() {
    }

    public MaximumBudgetDocument(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
