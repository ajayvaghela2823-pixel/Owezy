package com.owezy.app.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.Member;

import java.util.List;
import java.util.stream.Collectors;

public class SettleUpFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private long groupId;
    private AppDatabase database;
    private TextView tvGroupName;
    private TextView tvSettleSummary;
    private RecyclerView rvMembers;
    private Button btnSettleAll;
    private SettleMembersAdapter membersAdapter;

    public static SettleUpFragment newInstance(long groupId) {
        SettleUpFragment fragment = new SettleUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_settle_up, container, false);
        
        Toolbar toolbar = view.findViewById(R.id.toolbar_settle);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        
        tvGroupName = view.findViewById(R.id.tv_group_name);
        tvSettleSummary = view.findViewById(R.id.tv_settle_summary);
        rvMembers = view.findViewById(R.id.rv_settle_members);
        btnSettleAll = view.findViewById(R.id.btn_settle_all);
        
        setupMembersRecyclerView();
        loadGroupDetails();
        loadMembers();
        
        btnSettleAll.setOnClickListener(v -> settleAllMembers());
        
        return view;
    }

    private void setupMembersRecyclerView() {
        membersAdapter = new SettleMembersAdapter(this::settleMember);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembers.setAdapter(membersAdapter);
    }

    private void loadGroupDetails() {
        database.groupDao().getGroupById(groupId).observe(getViewLifecycleOwner(), group -> {
            if (group != null) {
                tvGroupName.setText(group.getName());
                String summary = String.format("Total Expenses: ₹%.2f", group.getTotalAmount());
                tvSettleSummary.setText(summary);
            }
        });
    }

    private void loadMembers() {
        database.groupMemberDao().getMembersInGroup(groupId).observe(getViewLifecycleOwner(), groupMembers -> {
            if (groupMembers != null && !groupMembers.isEmpty()) {
                List<Long> memberIds = groupMembers.stream()
                        .map(gm -> gm.getMemberId())
                        .collect(Collectors.toList());
                
                database.memberDao().getMembersByIds(memberIds).observe(getViewLifecycleOwner(), members -> {
                    if (members != null) {
                        membersAdapter.updateMembers(members);
                    }
                });
            }
        });
    }

    private void settleMember(Member member) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Reset member's balance to 0
            member.setBalance(0.0);
            database.memberDao().updateMember(member);
            
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), 
                    member.getName() + " settled successfully!", 
                    Toast.LENGTH_SHORT).show();
                loadMembers(); // Refresh the list
            });
        });
    }

    private void settleAllMembers() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Group group = database.groupDao().getGroupByIdSync(groupId);
            if (group != null) {
                // Get all members in the group and reset their balances
                List<Long> memberIds = database.groupMemberDao().getMembersInGroupSync(groupId)
                        .stream()
                        .map(gm -> gm.getMemberId())
                        .collect(Collectors.toList());
                
                for (Long memberId : memberIds) {
                    Member member = database.memberDao().getMemberByIdSync(memberId);
                    if (member != null) {
                        member.setBalance(0.0);
                        database.memberDao().updateMember(member);
                    }
                }
                
                // Mark group as settled
                group.setSettled(true);
                group.setYourBalance(0.0);
                database.groupDao().updateGroup(group);
                
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "All members settled successfully!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                });
            }
        });
    }
}
