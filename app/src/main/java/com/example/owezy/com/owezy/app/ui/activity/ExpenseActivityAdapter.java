package com.owezy.app.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.models.ExpenseDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseActivityAdapter extends RecyclerView.Adapter<ExpenseActivityAdapter.ViewHolder> {

    public interface OnExpenseClickListener {
        void onExpenseClick(ExpenseDetail expenseDetail);
    }

    private List<ExpenseDetail> expenses;
    private SimpleDateFormat dateFormat;
    private OnExpenseClickListener listener;

    public ExpenseActivityAdapter(OnExpenseClickListener listener) {
        this.expenses = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseDetail expenseDetail = expenses.get(position);
        
        holder.tvDescription.setText(expenseDetail.getExpense().getDescription());
        holder.tvAmount.setText(String.format("₹%.2f", expenseDetail.getExpense().getAmount()));
        holder.tvPaidBy.setText("Paid by " + expenseDetail.getPaidByName());
        holder.tvCategory.setText(expenseDetail.getExpense().getCategory());
        
        String formattedDate = dateFormat.format(new Date(expenseDetail.getExpense().getDate()));
        holder.tvDate.setText(formattedDate);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExpenseClick(expenseDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void updateExpenses(List<ExpenseDetail> newExpenses) {
        this.expenses = newExpenses != null ? newExpenses : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription;
        TextView tvAmount;
        TextView tvPaidBy;
        TextView tvCategory;
        TextView tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_activity_description);
            tvAmount = itemView.findViewById(R.id.tv_activity_amount);
            tvPaidBy = itemView.findViewById(R.id.tv_activity_paid_by);
            tvCategory = itemView.findViewById(R.id.tv_activity_category);
            tvDate = itemView.findViewById(R.id.tv_activity_date);
        }
    }
}
