package com.owezy.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.models.GroupSummary;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private List<GroupSummary> groups;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GroupSummary groupSummary);
    }

    public GroupsAdapter(List<GroupSummary> groups, OnItemClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupSummary group = groups.get(position);
        Context context = holder.itemView.getContext();
        holder.tvGroupName.setText(group.getGroupName());
        holder.tvGroupTotal.setText(context.getString(R.string.currency_format, group.getTotalAmount()));
        holder.tvGroupStatus.setText(group.getStatus());

        // Build member info text
        String memberInfo;
        if (group.getYourBalance() > 0) {
            memberInfo = context.getString(R.string.status_you_are_owed) + " " + context.getString(R.string.currency_format, group.getYourBalance());
            holder.tvGroupStatus.setBackgroundResource(R.drawable.bg_pill_green);
        } else if (group.getYourBalance() < 0) {
            memberInfo = context.getString(R.string.status_you_owe) + " " + context.getString(R.string.currency_format, Math.abs(group.getYourBalance()));
            holder.tvGroupStatus.setBackgroundResource(R.drawable.bg_pill_red);
        } else {
            memberInfo = context.getString(R.string.status_settled);
            holder.tvGroupStatus.setBackgroundResource(R.drawable.bg_card_white);
        }
        holder.tvMemberInfo.setText(memberInfo);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(group);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateData(List<GroupSummary> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvMemberInfo, tvGroupStatus, tvGroupTotal;

        ViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvMemberInfo = itemView.findViewById(R.id.tv_member_info);
            tvGroupStatus = itemView.findViewById(R.id.tv_group_status);
            tvGroupTotal = itemView.findViewById(R.id.tv_group_total);
        }
    }
}
