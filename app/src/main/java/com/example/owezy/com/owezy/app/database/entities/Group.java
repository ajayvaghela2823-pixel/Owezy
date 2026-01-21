package com.owezy.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "groups")
public class Group {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String icon; // Resource name or emoji
    private long createdDate;
    private double totalAmount;
    private double yourBalance;
    private boolean isSettled;

    public Group(String name, String icon, long createdDate) {
        this.name = name;
        this.icon = icon;
        this.createdDate = createdDate;
        this.totalAmount = 0.0;
        this.yourBalance = 0.0;
        this.isSettled = false;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getYourBalance() {
        return yourBalance;
    }

    public void setYourBalance(double yourBalance) {
        this.yourBalance = yourBalance;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }
}
