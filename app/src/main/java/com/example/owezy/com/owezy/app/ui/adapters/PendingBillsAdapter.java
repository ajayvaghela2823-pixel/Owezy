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

public class PendingBillsAdapter extends RecyclerView.Adapter<PendingBillsAdapter.ViewHolder> {

    private List<GroupSummary> groupSummaries;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GroupSummary groupSummary);
    }

    public PendingBillsAdapter(List<GroupSummary> groupSummaries, OnItemClickListener listener) {
        this.groupSummaries = groupSummaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupSummary summary = groupSummaries.get(position);
        Context context = holder.itemView.getContext();
        holder.tvGroupName.setText(summary.getGroupName());
        holder.tvBillAmount.setText(context.getString(R.string.currency_format,
                Math.abs(summary.getTotalAmount())));
        holder.tvBillStatus.setText(summary.getStatus());

        
        if (summary.getYourBalance() > 0) {
            holder.tvBillStatus.setBackgroundResource(R.drawable.bg_pill_green);
            holder.tvBillStatus.setTextColor(context.getColor(R.color.white));
        } else if (summary.getYourBalance() < 0) {
            holder.tvBillStatus.setBackgroundResource(R.drawable.bg_pill_red);
            holder.tvBillStatus.setTextColor(context.getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(summary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupSummaries.size();
    }

    public void updateData(List<GroupSummary> newSummaries) {
        this.groupSummaries = newSummaries;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvBillAmount, tvBillStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvBillAmount = itemView.findViewById(R.id.tv_bill_amount);
            tvBillStatus = itemView.findViewById(R.id.tv_bill_status);
        }
    }
}
