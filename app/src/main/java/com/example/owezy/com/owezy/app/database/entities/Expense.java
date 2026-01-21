package com.owezy.app.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses", foreignKeys = @ForeignKey(entity = Group.class, parentColumns = "id", childColumns = "groupId", onDelete = ForeignKey.CASCADE), indices = {
        @Index("groupId") })
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long groupId;
    private String description;
    private double amount;
    private String category; // Trip, Dining, Shopping, etc.
    private long date;
    private long paidById; // Member who paid
    private String status; // "settled", "pending"

    public Expense(long groupId, String description, double amount,
            String category, long date, long paidById) {
        this.groupId = groupId;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.paidById = paidById;
        this.status = "pending";
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getPaidById() {
        return paidById;
    }

    public void setPaidById(long paidById) {
        this.paidById = paidById;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
