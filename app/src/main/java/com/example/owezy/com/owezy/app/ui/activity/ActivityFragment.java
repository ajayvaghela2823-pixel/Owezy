package com.owezy.app.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Expense;
import com.owezy.app.database.entities.Group;
import com.owezy.app.models.ExpenseDetail;
import com.owezy.app.ui.expenses.ExpenseDetailsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivityFragment extends Fragment {

    private RecyclerView rvActivities;
    private TextView tvEmptyMessage;
    private ExpenseActivityAdapter activityAdapter;
    private AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        
        database = AppDatabase.getDatabase(requireContext());
        
        rvActivities = view.findViewById(R.id.rv_activities);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);
        
        setupRecyclerView();
        loadActivities();
        
        return view;
    }

    private void setupRecyclerView() {
        activityAdapter = new ExpenseActivityAdapter(expenseDetail -> {
            // Open expense details
            ExpenseDetailsFragment detailsFragment = ExpenseDetailsFragment.newInstance(expenseDetail.getExpense().getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActivities.setAdapter(activityAdapter);
    }

    private void loadActivities() {
        // Load all expenses from all groups
        database.groupDao().getAllGroups().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null && !groups.isEmpty()) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    List<ExpenseDetail> allExpenses = new ArrayList<>();
                    
                    for (Group group : groups) {
                        List<Expense> groupExpenses = database.expenseDao().getExpensesForGroupSync(group.getId());
                        
                        for (Expense expense : groupExpenses) {
                            String paidByName = database.memberDao().getMemberNameById(expense.getPaidById());
                            allExpenses.add(new ExpenseDetail(expense, paidByName));
                        }
                    }
                    
                    // Sort by date, most recent first
                    Collections.sort(allExpenses, new Comparator<ExpenseDetail>() {
                        @Override
                        public int compare(ExpenseDetail e1, ExpenseDetail e2) {
                            return Long.compare(e2.getExpense().getDate(), e1.getExpense().getDate());
                        }
                    });
                    
                    requireActivity().runOnUiThread(() -> {
                        if (allExpenses.isEmpty()) {
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                            rvActivities.setVisibility(View.GONE);
                        } else {
                            tvEmptyMessage.setVisibility(View.GONE);
                            rvActivities.setVisibility(View.VISIBLE);
                            activityAdapter.updateExpenses(allExpenses);
                        }
                    });
                });
            } else {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                rvActivities.setVisibility(View.GONE);
            }
        });
    }
}
