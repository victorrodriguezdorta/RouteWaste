package es.ull.project.adapter.mongo.document.valueobject;

/**
 * DTO for persisting OpeningFixedCost value object in MongoDB.
 */
public class OpeningFixedCostDocument {

    private double amount;
    private String currency;

    public OpeningFixedCostDocument() {
    }

    public OpeningFixedCostDocument(double amount, String currency) {
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
