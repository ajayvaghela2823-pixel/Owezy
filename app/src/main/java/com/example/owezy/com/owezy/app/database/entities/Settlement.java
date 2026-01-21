package com.owezy.app.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "settlements", foreignKeys = {
        @ForeignKey(entity = Group.class, parentColumns = "id", childColumns = "groupId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Member.class, parentColumns = "id", childColumns = "fromMemberId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Member.class, parentColumns = "id", childColumns = "toMemberId", onDelete = ForeignKey.CASCADE)
}, indices = { @Index("groupId"), @Index("fromMemberId"), @Index("toMemberId") })
public class Settlement {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long groupId;
    private long fromMemberId; // Who pays
    private long toMemberId; // Who receives
    private double amount;
    private long settlementDate;

    public Settlement(long groupId, long fromMemberId, long toMemberId,
            double amount, long settlementDate) {
        this.groupId = groupId;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
        this.amount = amount;
        this.settlementDate = settlementDate;
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

    public long getFromMemberId() {
        return fromMemberId;
    }

    public void setFromMemberId(long fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    public long getToMemberId() {
        return toMemberId;
    }

    public void setToMemberId(long toMemberId) {
        this.toMemberId = toMemberId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(long settlementDate) {
        this.settlementDate = settlementDate;
    }
}
