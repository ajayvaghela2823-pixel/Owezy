package com.owezy.app.ui.expenses;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Expense;
import com.owezy.app.database.entities.ExpenseSplit;
import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.Member;
import com.owezy.app.utils.BalanceCalculator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddExpenseFragment extends Fragment {

    private EditText etAmount, etDescription;
    private Spinner spinnerCategory, spinnerGroup;
    private RecyclerView rvParticipants;
    private ParticipantsAdapter participantsAdapter;
    private RadioGroup rgSplitOptions;
    private TextView tvDate;
    private Button btnSave, btnCancel;
    private AppDatabase database;
    private List<Group> groups;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        database = AppDatabase.getDatabase(requireContext());

        etAmount = view.findViewById(R.id.et_amount);
        etDescription = view.findViewById(R.id.et_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerGroup = view.findViewById(R.id.spinner_group);
        rvParticipants = view.findViewById(R.id.rv_participants);
        rgSplitOptions = view.findViewById(R.id.rg_split_options);
        tvDate = view.findViewById(R.id.tv_date);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        setupParticipantsRecyclerView();
        populateCategoriesSpinner();
        populateGroupsSpinner();

        rgSplitOptions.setOnCheckedChangeListener((group, checkedId) -> {
            participantsAdapter.setCustomSplit(checkedId == R.id.rb_split_custom);
        });

        tvDate.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> saveExpense());

        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void setupParticipantsRecyclerView() {
        participantsAdapter = new ParticipantsAdapter(new ArrayList<>());
        rvParticipants.setLayoutManager(new LinearLayoutManager(getContext()));
        rvParticipants.setAdapter(participantsAdapter);
    }

    private void populateCategoriesSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void populateGroupsSpinner() {
        database.groupDao().getAllGroups().observe(getViewLifecycleOwner(), groups -> {
            if (groups == null || groups.isEmpty()) {
                Toast.makeText(requireContext(), "No groups available. Please create a group first.", Toast.LENGTH_LONG).show();
                this.groups = new ArrayList<>();
                return;
            }
            
            this.groups = groups;
            List<String> groupNames = new ArrayList<>();
            for (Group group : groups) {
                groupNames.add(group.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, groupNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGroup.setAdapter(adapter);
            
            // Auto-select first group and load participants
            if (!groups.isEmpty()) {
                spinnerGroup.setSelection(0);
                updateParticipantsList(groups.get(0).getId());
            }
        });

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (groups != null && !groups.isEmpty() && position < groups.size()) {
                    updateParticipantsList(groups.get(position).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateParticipantsList(long groupId) {
        database.groupMemberDao().getMembersInGroup(groupId).observe(getViewLifecycleOwner(), groupMembers -> {
            if (groupMembers == null || groupMembers.isEmpty()) {
                participantsAdapter.setMembers(new ArrayList<>());
                return;
            }
            List<Long> memberIds = groupMembers.stream().map(gm -> gm.getMemberId()).collect(Collectors.toList());
            database.memberDao().getMembersByIds(memberIds).observe(getViewLifecycleOwner(), members -> {
                if (members != null) {
                    participantsAdapter.setMembers(members);
                }
            });
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);
        }, year, month, day).show();
    }

    private void saveExpense() {
        String amountStr = etAmount.getText().toString();
        String description = etDescription.getText().toString();
        
        // Validation checks
        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (spinnerGroup.getSelectedItem() == null || groups == null || groups.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a group", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String category = spinnerCategory.getSelectedItem().toString();
        String groupName = spinnerGroup.getSelectedItem().toString();
        double amount = Double.parseDouble(amountStr);
        List<Member> selectedMembers = participantsAdapter.getSelectedMembers();

        if (selectedMembers.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one participant", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Group group = database.groupDao().getGroupByName(groupName);
            if (group != null) {
                Expense expense = new Expense(group.getId(), description, amount, category, System.currentTimeMillis(), 1);
                long expenseId = database.expenseDao().insertExpense(expense);

                List<ExpenseSplit> expenseSplits = new ArrayList<>();
                if (rgSplitOptions.getCheckedRadioButtonId() == R.id.rb_split_equally) {
                    double splitAmount = amount / selectedMembers.size();
                    for (Member member : selectedMembers) {
                        expenseSplits.add(new ExpenseSplit(expenseId, member.getId(), splitAmount));
                    }
                } else {
                    Map<Long, Double> customAmounts = participantsAdapter.getCustomAmounts();
                    for (Member member : selectedMembers) {
                        double splitAmount = customAmounts.getOrDefault(member.getId(), 0.0);
                        expenseSplits.add(new ExpenseSplit(expenseId, member.getId(), splitAmount));
                    }
                }
                database.expenseSplitDao().insertExpenseSplits(expenseSplits);

                BalanceCalculator.calculateBalances(group.getId(), database);

                requireActivity().runOnUiThread(() -> getParentFragmentManager().popBackStack());
            }
        });
    }
}
