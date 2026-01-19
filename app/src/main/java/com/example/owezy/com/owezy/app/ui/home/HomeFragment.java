package com.owezy.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.MainActivity;
import com.owezy.app.R;
import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.Member;
import com.owezy.app.models.GroupSummary;
import com.owezy.app.models.MemberBalance;
import com.owezy.app.ui.adapters.FriendsAdapter;
import com.owezy.app.ui.adapters.PendingBillsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private TextView tvYouOweAmount, tvOwedToYouAmount;
    private RecyclerView rvPendingBills, rvFriends;
    private PendingBillsAdapter pendingBillsAdapter;
    private FriendsAdapter friendsAdapter;
    private AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

     
        database = AppDatabase.getDatabase(requireContext());

       
        tvYouOweAmount = view.findViewById(R.id.tv_you_owe_amount);
        tvOwedToYouAmount = view.findViewById(R.id.tv_owed_to_you_amount);
        rvPendingBills = view.findViewById(R.id.rv_pending_bills);
        rvFriends = view.findViewById(R.id.rv_friends);

        
        setupPendingBills();
        setupFriends();

       
        observeDatabase();

        return view;
    }

    private void setupPendingBills() {
        pendingBillsAdapter = new PendingBillsAdapter(new ArrayList<>(), groupSummary -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showGroupDetails(groupSummary.getGroupId());
            }
        });
        rvPendingBills.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingBills.setAdapter(pendingBillsAdapter);
    }

    private void setupFriends() {
        friendsAdapter = new FriendsAdapter(new ArrayList<>());
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvFriends.setAdapter(friendsAdapter);
    }

    private void observeDatabase() {
        database.memberDao().getMemberById(1L).observe(getViewLifecycleOwner(), you -> {
            if (you != null) {
                if (you.getBalance() < 0) {
                    tvYouOweAmount.setText(getString(R.string.currency_format, Math.abs(you.getBalance())));
                    tvOwedToYouAmount.setText(getString(R.string.currency_format, 0.0));
                } else {
                    tvOwedToYouAmount.setText(getString(R.string.currency_format, you.getBalance()));
                    tvYouOweAmount.setText(getString(R.string.currency_format, 0.0));
                }
            }
        });

        database.groupDao().getAllGroups().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    List<GroupSummary> pendingBills = new ArrayList<>();
                    for (Group group : groups) {
                        if (!group.isSettled()) {
                            int memberCount = database.groupMemberDao().getMemberCountInGroupSync(group.getId());
                            pendingBills.add(new GroupSummary(group.getId(), group.getName(), group.getIcon(), memberCount, group.getTotalAmount(), group.getYourBalance()));
                        }
                    }
                    requireActivity().runOnUiThread(() -> {
                        pendingBillsAdapter.updateData(pendingBills);
                    });
                });
            }
        });

        database.memberDao().getAllMembers().observe(getViewLifecycleOwner(), members -> {
            if (members != null) {
                List<MemberBalance> friends = members.stream()
                        .filter(member -> member.getId() != 1L) // Exclude "You"
                        .map(member -> new MemberBalance(member.getId(), member.getName(), member.getAvatarResource(), member.getBalance()))
                        .collect(Collectors.toList());
                friendsAdapter.updateData(friends);
            }
        });
    }
}
