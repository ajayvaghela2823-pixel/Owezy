package com.owezy.app.utils;

import com.owezy.app.database.AppDatabase;
import com.owezy.app.database.entities.Expense;
import com.owezy.app.database.entities.ExpenseSplit;
import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.GroupMember;
import com.owezy.app.database.entities.Member;
import com.owezy.app.database.entities.Settlement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceCalculator {

    public static void calculateBalances(long groupId, AppDatabase database) {
        List<GroupMember> groupMembers = database.groupMemberDao().getMembersInGroupSync(groupId);
        List<Expense> expenses = database.expenseDao().getPendingExpensesForGroupSync(groupId);
        double groupTotalAmount = 0;

        Map<Long, Double> memberBalances = new HashMap<>();
        for (GroupMember gm : groupMembers) {
            memberBalances.put(gm.getMemberId(), 0.0);
        }

        for (Expense expense : expenses) {
            groupTotalAmount += expense.getAmount();
            long paidById = expense.getPaidById();
            memberBalances.put(paidById, memberBalances.getOrDefault(paidById, 0.0) + expense.getAmount());

            List<ExpenseSplit> splits = database.expenseSplitDao().getSplitsForExpense(expense.getId());
            
            if (splits == null || splits.isEmpty()) {
                memberBalances.put(paidById, memberBalances.getOrDefault(paidById, 0.0) - expense.getAmount());
            } else {
                for (ExpenseSplit split : splits) {
                    long memberId = split.getMemberId();
                    memberBalances.put(memberId, memberBalances.getOrDefault(memberId, 0.0) - split.getAmount());
                }
            }
        }

        List<Settlement> settlements = database.settlementDao().getSettlementsForGroupSync(groupId);
        for (Settlement settlement : settlements) {
            memberBalances.put(settlement.getFromMemberId(), 
                memberBalances.getOrDefault(settlement.getFromMemberId(), 0.0) + settlement.getAmount());

            memberBalances.put(settlement.getToMemberId(), 
                memberBalances.getOrDefault(settlement.getToMemberId(), 0.0) - settlement.getAmount());
        }

        for (Map.Entry<Long, Double> entry : memberBalances.entrySet()) {
            Member member = database.memberDao().getMemberByIdSync(entry.getKey());
            member.setBalance(entry.getValue());
            database.memberDao().updateMember(member);
        }

        Group group = database.groupDao().getGroupByIdSync(groupId);
        group.setTotalAmount(groupTotalAmount);
        
        group.setSettled(groupTotalAmount <= 0.01);
        
        if (memberBalances.containsKey(1L)) {
            group.setYourBalance(memberBalances.get(1L));
        }
        database.groupDao().updateGroup(group);
    }
}
