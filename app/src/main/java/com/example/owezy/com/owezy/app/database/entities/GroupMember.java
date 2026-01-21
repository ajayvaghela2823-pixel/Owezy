package com.owezy.app.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_members",
        foreignKeys = {
                @ForeignKey(entity = Group.class, parentColumns = "id", childColumns = "groupId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class, parentColumns = "id", childColumns = "memberId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("groupId"),
                @Index("memberId")
        })
public class GroupMember {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long groupId;
    private long memberId;
    private double currentBalance;

    public GroupMember(long groupId, long memberId) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.currentBalance = 0.0;
    }

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

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }
}
