package com.owezy.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.owezy.app.database.entities.Group;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert
    long insertGroup(Group group);

    @Update
    void updateGroup(Group group);

    @Delete
    void deleteGroup(Group group);

    @Query("SELECT * FROM groups ORDER BY createdDate DESC")
    LiveData<List<Group>> getAllGroups();

    @Query("SELECT * FROM groups ORDER BY createdDate DESC")
    List<Group> getAllGroupsSync();

    @Query("SELECT * FROM groups WHERE id = :groupId")
    LiveData<Group> getGroupById(long groupId);

    @Query("SELECT * FROM groups WHERE id = :groupId")
    Group getGroupByIdSync(long groupId);

    @Query("SELECT * FROM groups WHERE name = :groupName")
    Group getGroupByName(String groupName);

    @Query("SELECT COUNT(*) FROM groups")
    int getGroupCount();
}
