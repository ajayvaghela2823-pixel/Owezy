package com.owezy.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.owezy.app.database.entities.Member;

import java.util.List;

@Dao
public interface MemberDao {
    @Insert
    long insertMember(Member member);

    @Update
    void updateMember(Member member);

    @Delete
    void deleteMember(Member member);

    @Query("SELECT * FROM members ORDER BY name ASC")
    LiveData<List<Member>> getAllMembers();

    @Query("SELECT * FROM members WHERE id = :memberId")
    LiveData<Member> getMemberById(long memberId);

    @Query("SELECT name FROM members WHERE id = :memberId")
    String getMemberNameById(long memberId);

    @Query("SELECT * FROM members WHERE id IN (:memberIds)")
    LiveData<List<Member>> getMembersByIds(List<Long> memberIds);

    @Query("SELECT * FROM members WHERE id = :memberId")
    Member getMemberByIdSync(long memberId);

    @Query("SELECT * FROM members WHERE name = :name LIMIT 1")
    Member getMemberByName(String name);
}
