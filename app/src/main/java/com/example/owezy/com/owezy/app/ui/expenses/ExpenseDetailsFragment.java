package com.owezy.app.ui.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Expense;
import com.owezy.app.database.entities.ExpenseSplit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseDetailsFragment extends Fragment {

    private static final String ARG_EXPENSE_ID = "expense_id";
    private long expenseId;
    private AppDatabase database;

    private TextView tvDescription;
    private TextView tvAmount;
    private TextView tvCategory;
    private TextView tvPaidBy;
    private TextView tvDate;
    private RecyclerView rvParticipants;
    private ExpenseSplitAdapter splitAdapter;

    public static ExpenseDetailsFragment newInstance(long expenseId) {
        ExpenseDetailsFragment fragment = new ExpenseDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EXPENSE_ID, expenseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expenseId = getArguments().getLong(ARG_EXPENSE_ID);
        }
        database = AppDatabase.getDatabase(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_details, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_expense_details);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        tvDescription = view.findViewById(R.id.tv_expense_detail_description);
        tvAmount = view.findViewById(R.id.tv_expense_detail_amount);
        tvCategory = view.findViewById(R.id.tv_expense_detail_category);
        tvPaidBy = view.findViewById(R.id.tv_expense_detail_paid_by);
        tvDate = view.findViewById(R.id.tv_expense_detail_date);
        rvParticipants = view.findViewById(R.id.rv_expense_participants);

        setupParticipantsRecyclerView();
        loadExpenseDetails();

        return view;
    }

    private void setupParticipantsRecyclerView() {
        splitAdapter = new ExpenseSplitAdapter(new ArrayList<>());
        rvParticipants.setLayoutManager(new LinearLayoutManager(getContext()));
        rvParticipants.setAdapter(splitAdapter);
    }

    private void loadExpenseDetails() {
        database.expenseDao().getExpenseById(expenseId).observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                displayExpenseInfo(expense);
                loadSplits(expense.getId());
            }
        });
    }

    private void displayExpenseInfo(Expense expense) {
        tvDescription.setText(expense.getDescription());
        tvAmount.setText(String.format("₹%.2f", expense.getAmount()));
        tvCategory.setText(expense.getCategory());

        AppDatabase.databaseWriteExecutor.execute(() -> {
            String paidByName = database.memberDao().getMemberNameById(expense.getPaidById());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(expense.getDate()));

            requireActivity().runOnUiThread(() -> {
                tvPaidBy.setText("Paid by " + paidByName);
                tvDate.setText(formattedDate);
            });
        });
    }

    private void loadSplits(long expenseId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ExpenseSplit> splits = database.expenseSplitDao().getSplitsForExpense(expenseId);
            List<MemberSplitInfo> memberSplits = new ArrayList<>();

            for (ExpenseSplit split : splits) {
                String memberName = database.memberDao().getMemberNameById(split.getMemberId());
                memberSplits.add(new MemberSplitInfo(memberName, split.getAmount()));
            }

            requireActivity().runOnUiThread(() -> {
                splitAdapter.updateSplits(memberSplits);
            });
        });
    }

    // Inner class to hold member split information
    public static class MemberSplitInfo {
        private String memberName;
        private double amount;

        public MemberSplitInfo(String memberName, double amount) {
            this.memberName = memberName;
            this.amount = amount;
        }

        public String getMemberName() {
            return memberName;
        }

        public double getAmount() {
            return amount;
        }
    }
}
