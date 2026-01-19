package com.owezy.app.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupMembersAdapter extends RecyclerView.Adapter<CreateGroupMembersAdapter.ViewHolder> {

    private List<Member> members;
    private List<String> selectedMemberNames;

    public CreateGroupMembersAdapter(List<Member> members, List<String> selectedMemberNames) {
        this.members = members;
        this.selectedMemberNames = selectedMemberNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.tvMemberName.setText(member.getName());
        
        holder.cbMember.setOnCheckedChangeListener(null);
        holder.cbMember.setChecked(selectedMemberNames.contains(member.getName()));

        holder.cbMember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedMemberNames.contains(member.getName())) {
                    selectedMemberNames.add(member.getName());
                }
            } else {
                selectedMemberNames.remove(member.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<Member> newMembers) {
        this.members = newMembers != null ? newMembers : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbMember;
        TextView tvMemberName;

        ViewHolder(View itemView) {
            super(itemView);
            cbMember = itemView.findViewById(R.id.cb_create_group_member);
            tvMemberName = itemView.findViewById(R.id.tv_create_group_member_name);
        }
    }
}
