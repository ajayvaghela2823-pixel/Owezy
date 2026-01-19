package com.owezy.app.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.owezy.app.database.entities.GroupMember;
import com.owezy.app.database.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupFragment extends Fragment {

    private EditText etGroupName;
    private EditText etNewMemberName;
    private Button btnAddMember;
    private RecyclerView rvMembers;
    private Button btnSave;
    private Button btnCancel;
    private AppDatabase database;
    private CreateGroupMembersAdapter membersAdapter;
    private List<String> selectedMembers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        database = AppDatabase.getDatabase(requireContext());
        selectedMembers = new ArrayList<>();

        Toolbar toolbar = view.findViewById(R.id.toolbar_create_group);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        etGroupName = view.findViewById(R.id.et_group_name);
        etNewMemberName = view.findViewById(R.id.et_new_member_name);
        btnAddMember = view.findViewById(R.id.btn_add_member);
        rvMembers = view.findViewById(R.id.rv_create_group_members);
        btnSave = view.findViewById(R.id.btn_create_group_save);
        btnCancel = view.findViewById(R.id.btn_create_group_cancel);

        setupMembersRecyclerView();
        loadExistingMembers();

        btnAddMember.setOnClickListener(v -> addMember());
        btnSave.setOnClickListener(v -> saveGroup());
        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void setupMembersRecyclerView() {
        membersAdapter = new CreateGroupMembersAdapter(new ArrayList<>(), selectedMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembers.setAdapter(membersAdapter);
    }

    private void loadExistingMembers() {
        database.memberDao().getAllMembers().observe(getViewLifecycleOwner(), members -> {
            if (members != null) {
                membersAdapter.updateMembers(members);
            }
        });
    }

    private void addMember() {
        String memberName = etNewMemberName.getText().toString().trim();
        
        if (memberName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a member name", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            
            Member existingMember = database.memberDao().getMemberByName(memberName);
            
            if (existingMember == null) {
               
                String initials = memberName.length() > 0 ? memberName.substring(0, 1).toUpperCase() : "?";
                Member newMember = new Member(memberName, initials);
                database.memberDao().insertMember(newMember);
                
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Member added!", Toast.LENGTH_SHORT).show();
                    etNewMemberName.setText("");
                    loadExistingMembers();
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Member already exists", Toast.LENGTH_SHORT).show();
                    etNewMemberName.setText("");
                });
            }
        });
    }

    private void saveGroup() {
        String groupName = etGroupName.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a group name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMembers.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one member", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
           
            Group group = new Group(groupName, "👥", System.currentTimeMillis());
            long groupId = database.groupDao().insertGroup(group);

            
            for (String memberName : selectedMembers) {
                Member member = database.memberDao().getMemberByName(memberName);
                if (member != null) {
                    database.groupMemberDao().insertGroupMember(new GroupMember(groupId, member.getId()));
                }
            }

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Group created successfully!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            });
        });
    }
}
