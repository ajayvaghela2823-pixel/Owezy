package com.owezy.app.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense_splits",
        foreignKeys = {
                @ForeignKey(entity = Expense.class, parentColumns = "id", childColumns = "expenseId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class, parentColumns = "id", childColumns = "memberId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("expenseId"),
                @Index("memberId")
        })
public class ExpenseSplit {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long expenseId;
    private long memberId;
    private double amount;

    public ExpenseSplit(long expenseId, long memberId, double amount) {
        this.expenseId = expenseId;
        this.memberId = memberId;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(long expenseId) {
        this.expenseId = expenseId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
