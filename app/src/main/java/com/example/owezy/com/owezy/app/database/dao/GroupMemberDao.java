package com.owezy.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.owezy.app.database.entities.GroupMember;

import java.util.List;

@Dao
public interface GroupMemberDao {
    @Insert
    void insertGroupMember(GroupMember groupMember);

    @Insert
    void insertGroupMembers(List<GroupMember> groupMembers);

    @Update
    void updateGroupMember(GroupMember groupMember);

    @Delete
    void deleteGroupMember(GroupMember groupMember);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    LiveData<List<GroupMember>> getMembersInGroup(long groupId);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    List<GroupMember> getMembersInGroupSync(long groupId);

    @Query("SELECT COUNT(*) FROM group_members WHERE groupId = :groupId")
    int getMemberCountInGroupSync(long groupId);

    @Query("SELECT * FROM group_members WHERE memberId = :memberId")
    LiveData<List<GroupMember>> getGroupsForMember(long memberId);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND memberId = :memberId")
    GroupMember getGroupMemberSync(long groupId, long memberId);

    @Query("UPDATE group_members SET currentBalance = :balance WHERE groupId = :groupId AND memberId = :memberId")
    void updateBalance(long groupId, long memberId, double balance);

    @Query("DELETE FROM group_members WHERE groupId = :groupId")
    void deleteAllMembersFromGroup(long groupId);
}
