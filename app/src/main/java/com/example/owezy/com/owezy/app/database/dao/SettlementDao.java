package com.owezy.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.owezy.app.database.entities.Settlement;

import java.util.List;

@Dao
public interface SettlementDao {
    @Insert
    long insertSettlement(Settlement settlement);

    @Delete
    void deleteSettlement(Settlement settlement);

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY settlementDate DESC")
    LiveData<List<Settlement>> getSettlementsForGroup(long groupId);

    @Query("SELECT * FROM settlements WHERE groupId = :groupId")
    List<Settlement> getSettlementsForGroupSync(long groupId);

    @Query("SELECT * FROM settlements WHERE fromMemberId = :memberId OR toMemberId = :memberId")
    LiveData<List<Settlement>> getSettlementsForMember(long memberId);
}
