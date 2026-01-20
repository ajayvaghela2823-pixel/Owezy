package com.owezy.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.owezy.app.database.entities.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    long insertExpense(Expense expense);

    @Update
    void updateExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesForGroup(long groupId);

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY date DESC")
    List<Expense> getExpensesForGroupSync(long groupId);

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    LiveData<Expense> getExpenseById(long expenseId);

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    Expense getExpenseByIdSync(long expenseId);

    @Query("SELECT * FROM expenses WHERE groupId = :groupId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesForGroupInDateRange(long groupId, long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE groupId = :groupId")
    double getTotalExpensesForGroup(long groupId);
}
