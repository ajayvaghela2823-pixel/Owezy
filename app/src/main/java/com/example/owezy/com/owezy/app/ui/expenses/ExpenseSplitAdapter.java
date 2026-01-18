package com.owezy.app.ui.expenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;

import java.util.ArrayList;
import java.util.List;

public class ExpenseSplitAdapter extends RecyclerView.Adapter<ExpenseSplitAdapter.ViewHolder> {

    private List<ExpenseDetailsFragment.MemberSplitInfo> splits;

    public ExpenseSplitAdapter(List<ExpenseDetailsFragment.MemberSplitInfo> splits) {
        this.splits = splits != null ? splits : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_split, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseDetailsFragment.MemberSplitInfo split = splits.get(position);
        holder.tvMemberName.setText(split.getMemberName());
        holder.tvSplitAmount.setText(String.format("₹%.2f", split.getAmount()));
    }

    @Override
    public int getItemCount() {
        return splits.size();
    }

    public void updateSplits(List<ExpenseDetailsFragment.MemberSplitInfo> newSplits) {
        this.splits = newSplits != null ? newSplits : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName;
        TextView tvSplitAmount;

        ViewHolder(View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tv_split_member_name);
            tvSplitAmount = itemView.findViewById(R.id.tv_split_amount);
        }
    }
}
