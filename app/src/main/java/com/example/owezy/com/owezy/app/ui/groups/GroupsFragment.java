package com.owezy.app.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.owezy.app.MainActivity;
import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.GroupMember;
import com.owezy.app.database.entities.Member;
import com.owezy.app.models.GroupSummary;
import com.owezy.app.ui.adapters.GroupsAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    private RecyclerView rvGroups;
    private ExtendedFloatingActionButton fabCreateGroup;
    private GroupsAdapter groupsAdapter;
    private AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        // Initialize database
        database = AppDatabase.getDatabase(requireContext());

        // Initialize views
        rvGroups = view.findViewById(R.id.rv_groups);
        fabCreateGroup = view.findViewById(R.id.fab_create_group);

        // Setup RecyclerView
        setupGroupsList();

        // Check and initialize sample data if needed
        initializeSampleDataIfNeeded();

        // Load data
        loadGroups();
        
        // FAB click listener
        fabCreateGroup.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CreateGroupFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void setupGroupsList() {
        groupsAdapter = new GroupsAdapter(new ArrayList<>(), groupSummary -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showGroupDetails(groupSummary.getGroupId());
            }
        });
        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGroups.setAdapter(groupsAdapter);
    }

    private void initializeSampleDataIfNeeded() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int groupCount = database.groupDao().getGroupCount();
            
            if (groupCount == 0) {
                // Create sample members
                Member member1 = new Member("You", "Y");
                Member member2 = new Member("Rohan", "R");
                Member member3 = new Member("Priya", "P");
                Member member4 = new Member("Amit", "A");

                long userId = database.memberDao().insertMember(member1);
                long rohanId = database.memberDao().insertMember(member2);
                long priyaId = database.memberDao().insertMember(member3);
                long amitId = database.memberDao().insertMember(member4);

                // Create sample groups
                Group group1 = new Group("Goa Trip", "✈️", System.currentTimeMillis());
                Group group2 = new Group("Diwali Party", "🎉", System.currentTimeMillis());
                Group group3 = new Group("Birthday Bash", "🎂", System.currentTimeMillis());

                long groupId1 = database.groupDao().insertGroup(group1);
                long groupId2 = database.groupDao().insertGroup(group2);
                long groupId3 = database.groupDao().insertGroup(group3);

                // Add members to groups
                database.groupMemberDao().insertGroupMember(new GroupMember(groupId1, userId));
                database.groupMemberDao().insertGroupMember(new GroupMember(groupId1, rohanId));
                database.groupMemberDao().insertGroupMember(new GroupMember(groupId2, userId));
                database.groupMemberDao().insertGroupMember(new GroupMember(groupId2, priyaId));
                database.groupMemberDao().insertGroupMember(new GroupMember(groupId3, userId));
                database.groupMemberDao().insertGroupMember(new GroupMember(groupId3, amitId));
                
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Sample groups created!", Toast.LENGTH_SHORT).show();
                    loadGroups();
                });
            }
        });
    }

    private void loadGroups() {
        database.groupDao().getAllGroups().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    List<GroupSummary> groupSummaries = new ArrayList<>();
                    for (Group group : groups) {
                        int memberCount = database.groupMemberDao().getMemberCountInGroupSync(group.getId());
                        groupSummaries.add(new GroupSummary(
                            group.getId(),
                            group.getName(),
                            group.getIcon(),
                            memberCount,
                            group.getTotalAmount(),
                            group.getYourBalance()
                        ));
                    }
                    requireActivity().runOnUiThread(() -> {
                        groupsAdapter.updateData(groupSummaries);
                    });
                });
            }
        });
    }
}
