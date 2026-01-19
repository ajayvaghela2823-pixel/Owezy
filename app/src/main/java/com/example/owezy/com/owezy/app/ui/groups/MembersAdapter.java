package com.owezy.app.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.entities.Member;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private List<Member> members;

    public MembersAdapter(List<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.tvMemberName.setText(member.getName());
        // TODO: Set avatar image
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setMembers(List<Member> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMemberAvatar;
        TextView tvMemberName;

        ViewHolder(View itemView) {
            super(itemView);
            ivMemberAvatar = itemView.findViewById(R.id.iv_member_avatar);
            tvMemberName = itemView.findViewById(R.id.tv_member_name);
        }
    }
}
