package com.owezy.app.models;

public class GroupSummary {
    private long groupId;
    private String groupName;
    private String icon;
    private int memberCount;
    private double totalAmount;
    private double yourBalance; // Positive = owed to you, Negative = you owe
    private long date;
    private String status; // "Settled up", "You owe", "You are owed"

    public GroupSummary(long groupId, String groupName, String icon, int memberCount,
            double totalAmount, double yourBalance, long date) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.icon = icon;
        this.memberCount = memberCount;
        this.totalAmount = totalAmount;
        this.yourBalance = yourBalance;
        this.date = date;
        this.status = calculateStatus();
    }

    private String calculateStatus() {
        if (Math.abs(yourBalance) < 0.01) {
            return "Settled up";
        } else if (yourBalance > 0) {
            return "You are owed";
        } else {
            return "You owe";
        }
    }

    // Getters
    public long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getIcon() {
        return icon;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getYourBalance() {
        return yourBalance;
    }

    public String getStatus() {
        return status;
    }

    public long getDate() {
        return date;
    }
}
