package com.owezy.app.models;

public class ExpenseItem {
    private long expenseId;
    private String category;
    private String description;
    private long date;
    private double amount;
    private String status;
    private boolean involvesYou;

    public ExpenseItem(long expenseId, String category, String description, long date,
            double amount, String status, boolean involvesYou) {
        this.expenseId = expenseId;
        this.category = category;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.status = status;
        this.involvesYou = involvesYou;
    }

    public long getExpenseId() {
        return expenseId;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public long getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public boolean isInvolvesYou() {
        return involvesYou;
    }
}
