package com.owezy.app.ui.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.models.ExpenseDetail;

import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private List<ExpenseDetail> expenses;

    public ExpensesAdapter(List<ExpenseDetail> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseDetail expenseDetail = expenses.get(position);
        holder.tvExpenseDescription.setText(expenseDetail.getExpense().getDescription());
        holder.tvExpenseAmount.setText(String.format("₹%.2f", expenseDetail.getExpense().getAmount()));
        holder.tvPaidBy.setText("Paid by " + expenseDetail.getPaidByName());
        holder.ivCategoryIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), getCategoryIcon(expenseDetail.getExpense().getCategory())));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void setExpenses(List<ExpenseDetail> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    private int getCategoryIcon(String category) {
        switch (category) {
            case "Trip":
                return R.drawable.ic_category_trip;
            case "Dining":
                return R.drawable.ic_category_dining;
            case "Shopping":
                return R.drawable.ic_category_shopping;
            case "Gifts":
                return R.drawable.ic_category_gifts;
            default:
                return R.drawable.ic_category_other;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvExpenseDescription, tvPaidBy, tvExpenseAmount;

        ViewHolder(View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvExpenseDescription = itemView.findViewById(R.id.tv_expense_description);
            tvPaidBy = itemView.findViewById(R.id.tv_paid_by);
            tvExpenseAmount = itemView.findViewById(R.id.tv_expense_amount);
        }
    }
}
