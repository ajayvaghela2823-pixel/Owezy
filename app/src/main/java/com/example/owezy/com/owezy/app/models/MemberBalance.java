package com.owezy.app.models;

public class MemberBalance {
    private long memberId;
    private String memberName;
    private String avatarResource;
    private double balance; // Positive = they owe you, Negative = you owe them

    public MemberBalance(long memberId, String memberName, String avatarResource, double balance) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.avatarResource = avatarResource;
        this.balance = balance;
    }

    public long getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getAvatarResource() {
        return avatarResource;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean owesYou() {
        return balance > 0;
    }

    public boolean youOwe() {
        return balance < 0;
    }
}
