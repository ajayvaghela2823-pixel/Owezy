package com.owezy.app.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class SettleMembersAdapter extends RecyclerView.Adapter<SettleMembersAdapter.ViewHolder> {

    public interface OnSettleClickListener {
        void onSettleClick(Member member);
    }

    private List<Member> members;
    private OnSettleClickListener listener;

    public SettleMembersAdapter(OnSettleClickListener listener) {
        this.members = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settle_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.tvMemberName.setText(member.getName());
        
        double balance = member.getBalance();
        
        if (balance > 0) {
            holder.tvBalance.setText(String.format("Owes you: ₹%.2f", balance));
            holder.tvBalance.setTextColor(holder.itemView.getContext().getColor(com.owezy.app.R.color.green_positive));
            holder.btnSettle.setVisibility(View.VISIBLE);
        } else if (balance < 0) {
            holder.tvBalance.setText(String.format("You owe: ₹%.2f", Math.abs(balance)));
            holder.tvBalance.setTextColor(holder.itemView.getContext().getColor(com.owezy.app.R.color.red_negative));
            holder.btnSettle.setVisibility(View.VISIBLE);
        } else {
            holder.tvBalance.setText("Settled up");
            holder.tvBalance.setTextColor(holder.itemView.getContext().getColor(com.owezy.app.R.color.text_secondary));
            holder.btnSettle.setVisibility(View.GONE);
        }

        holder.btnSettle.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSettleClick(member);
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
        TextView tvMemberName;
        TextView tvBalance;
        Button btnSettle;

        ViewHolder(View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tv_settle_member_name);
            tvBalance = itemView.findViewById(R.id.tv_settle_member_balance);
            btnSettle = itemView.findViewById(R.id.btn_settle_member);
        }
    }
}
