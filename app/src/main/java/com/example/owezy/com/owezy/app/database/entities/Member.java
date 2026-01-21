package com.owezy.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "members")
public class Member {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String avatarResource; // Drawable resource name or initials
    private double balance;

    public Member(String name, String avatarResource) {
        this.name = name;
        this.avatarResource = avatarResource;
        this.balance = 0.0;
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

    public String getAvatarResource() {
        return avatarResource;
    }

    public void setAvatarResource(String avatarResource) {
        this.avatarResource = avatarResource;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
