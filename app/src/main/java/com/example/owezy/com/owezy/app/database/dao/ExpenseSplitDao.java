package com.owezy.app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.owezy.app.database.entities.ExpenseSplit;

import java.util.List;

@Dao
public interface ExpenseSplitDao {
    @Insert
    void insertExpenseSplits(List<ExpenseSplit> expenseSplits);

    @Query("SELECT * FROM expense_splits WHERE expenseId = :expenseId")
    List<ExpenseSplit> getSplitsForExpense(long expenseId);
}
