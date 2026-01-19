package com.owezy.app.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Expense;
import com.owezy.app.models.ExpenseDetail;
import com.owezy.app.ui.expenses.AddExpenseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupDetailsFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private long groupId;

    private AppDatabase database;
    private MembersAdapter membersAdapter;
    private ExpensesAdapter expensesAdapter;

    public static GroupDetailsFragment newInstance(long groupId) {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getLong(ARG_GROUP_ID);
        }
        database = AppDatabase.getDatabase(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_details, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        RecyclerView rvMembers = view.findViewById(R.id.rv_members);
        membersAdapter = new MembersAdapter(new ArrayList<>());
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembers.setAdapter(membersAdapter);

        RecyclerView rvExpenses = view.findViewById(R.id.rv_expenses);
        expensesAdapter = new ExpensesAdapter(new ArrayList<>());
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(expensesAdapter);

        FloatingActionButton fabAddExpense = view.findViewById(R.id.fab_add_expense);
        fabAddExpense.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddExpenseFragment())
                    .addToBackStack(null)
                    .commit();
        });

        FloatingActionButton fabSettleUp = view.findViewById(R.id.fab_settle_up);
        fabSettleUp.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, SettleUpFragment.newInstance(groupId))
                    .addToBackStack(null)
                    .commit();
        });

        loadGroupDetails();
        loadMembers();
        loadExpenses();

        return view;
    }

    private void loadGroupDetails() {
        database.groupDao().getGroupById(groupId).observe(getViewLifecycleOwner(), group -> {
            if (group != null) {
                Toolbar toolbar = getView().findViewById(R.id.toolbar);
                toolbar.setTitle(group.getName());
            }
        });
    }

    private void loadMembers() {
        database.groupMemberDao().getMembersInGroup(groupId).observe(getViewLifecycleOwner(), groupMembers -> {
            if (groupMembers != null) {
                database.memberDao().getMembersByIds(groupMembers.stream().map(gm -> gm.getMemberId()).collect(Collectors.toList()))
                        .observe(getViewLifecycleOwner(), members -> {
                            if (members != null) {
                                membersAdapter.setMembers(members);
                            }
                        });
            }
        });
    }

    private void loadExpenses() {
        database.expenseDao().getExpensesForGroup(groupId).observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    List<ExpenseDetail> expenseDetails = new ArrayList<>();
                    for (Expense expense : expenses) {
                        String paidByName = database.memberDao().getMemberNameById(expense.getPaidById());
                        expenseDetails.add(new ExpenseDetail(expense, paidByName));
                    }
                    requireActivity().runOnUiThread(() -> {
                        expensesAdapter.setExpenses(expenseDetails);
                    });
                });
            }
        });
    }
}
