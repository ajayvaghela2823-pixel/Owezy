package com.owezy.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.models.MemberBalance;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<MemberBalance> friends;

    public FriendsAdapter(List<MemberBalance> friends) {
        this.friends = friends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_balance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemberBalance friend = friends.get(position);
        Context context = holder.itemView.getContext();
        holder.tvFriendName.setText(friend.getMemberName());
        holder.tvFriendBalance.setText(context.getString(R.string.currency_format,
                Math.abs(friend.getBalance())));

        // Set color based on balance
        if (friend.getBalance() > 0) {
            holder.tvFriendBalance.setTextColor(context.getColor(R.color.green_positive));
        } else {
            holder.tvFriendBalance.setTextColor(context.getColor(R.color.red_negative));
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void updateData(List<MemberBalance> newFriends) {
        this.friends = newFriends;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName, tvFriendBalance;

        ViewHolder(View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.tv_friend_name);
            tvFriendBalance = itemView.findViewById(R.id.tv_friend_balance);
        }
    }
}
