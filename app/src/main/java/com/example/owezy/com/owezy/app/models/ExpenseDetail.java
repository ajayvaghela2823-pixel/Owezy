package com.owezy.app.models;

import com.owezy.app.database.entities.Expense;

public class ExpenseDetail {
    private final Expense expense;
    private final String paidByName;

    public ExpenseDetail(Expense expense, String paidByName) {
        this.expense = expense;
        this.paidByName = paidByName;
    }

    public Expense getExpense() {
        return expense;
    }

    public String getPaidByName() {
        return paidByName;
    }
}
