package com.owezy.app.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Expense;
import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.Member;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName;
    private TextView tvProfileInitials;
    private TextView tvGroupCount;
    private TextView tvExpenseCount;
    private TextView tvTotalAmount;
    private Button btnEditProfile;
    private AppDatabase database;
    private Member currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        database = AppDatabase.getDatabase(requireContext());
        
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileInitials = view.findViewById(R.id.tv_profile_initials);
        tvGroupCount = view.findViewById(R.id.tv_group_count);
        tvExpenseCount = view.findViewById(R.id.tv_expense_count);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        
        loadProfileData();
        loadStatistics();
        
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        
        return view;
    }

    private void loadProfileData() {
        // Load user profile (assuming user ID is 1)
        database.memberDao().getMemberById(1L).observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                currentUser = member;
                tvProfileName.setText(member.getName());
                tvProfileInitials.setText(member.getAvatarResource());
            }
        });
    }

    private void loadStatistics() {
        // Load statistics on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int groupCount = database.groupDao().getGroupCount();
            List<Group> groups = database.groupDao().getAllGroupsSync();
            
            int totalExpenses = 0;
            double total = 0.0;
            
            if (groups != null) {
                for (Group group : groups) {
                    List<Expense> expenses = database.expenseDao().getExpensesForGroupSync(group.getId());
                    if (expenses != null) {
                        totalExpenses += expenses.size();
                        for (Expense expense : expenses) {
                            total += expense.getAmount();
                        }
                    }
                }
            }
            
            int finalExpenses = totalExpenses;
            double finalTotal = total;
            
            // Update UI on main thread
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    tvGroupCount.setText(String.valueOf(groupCount));
                    tvExpenseCount.setText(String.valueOf(finalExpenses));
                    tvTotalAmount.setText(String.format("₹%.2f", finalTotal));
                });
            }
        });
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        
        EditText etName = dialogView.findViewById(R.id.et_edit_name);
        EditText etInitials = dialogView.findViewById(R.id.et_edit_initials);
        
        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etInitials.setText(currentUser.getAvatarResource());
        }
        
        builder.setView(dialogView)
                .setTitle("Edit Profile")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newInitials = etInitials.getText().toString().trim();
                    
                    if (newName.isEmpty()) {
                        Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (newInitials.isEmpty()) {
                        newInitials = newName.substring(0, 1).toUpperCase();
                    }
                    
                    updateProfile(newName, newInitials);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateProfile(String name, String initials) {
        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setAvatarResource(initials);
            
            AppDatabase.databaseWriteExecutor.execute(() -> {
                database.memberDao().updateMember(currentUser);
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                        loadProfileData();
                    });
                }
            });
        }
    }
}
