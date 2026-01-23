package com.owezy.app.database;

import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.GroupMember;
import com.owezy.app.database.entities.Member;
import com.owezy.app.models.GroupSummary;
import com.owezy.app.models.MemberBalance;

import java.util.ArrayList;
import java.util.List;

public class SampleDataGenerator {

    public static void createSampleData(AppDatabase database) {
        // Create sample members
        Member member1 = new Member("You", "Y");
        Member member2 = new Member("Rohan", "R");
        Member member3 = new Member("Priya", "P");
        Member member4 = new Member("Amit", "A");

        long userId = database.memberDao().insertMember(member1);
        long rohanId = database.memberDao().insertMember(member2);
        long priyaId = database.memberDao().insertMember(member3);
        long amitId = database.memberDao().insertMember(member4);

        // Create sample groups
        Group group1 = new Group("Goa Trip", "✈️", System.currentTimeMillis());
        Group group2 = new Group("Diwali Party", "🎉", System.currentTimeMillis());
        Group group3 = new Group("Birthday Bash", "🎂", System.currentTimeMillis());

        long groupId1 = database.groupDao().insertGroup(group1);
        long groupId2 = database.groupDao().insertGroup(group2);
        long groupId3 = database.groupDao().insertGroup(group3);

        // Add members to groups
        database.groupMemberDao().insertGroupMember(new GroupMember(groupId1, userId));
        database.groupMemberDao().insertGroupMember(new GroupMember(groupId1, rohanId));
        database.groupMemberDao().insertGroupMember(new GroupMember(groupId2, userId));
        database.groupMemberDao().insertGroupMember(new GroupMember(groupId2, priyaId));
        database.groupMemberDao().insertGroupMember(new GroupMember(groupId3, userId));
        database.groupMemberDao().insertGroupMember(new GroupMember(groupId3, amitId));
    }

    public static List<GroupSummary> createSampleGroupSummaries() {
        List<GroupSummary> summaries = new ArrayList<>();
        summaries.add(new GroupSummary(1, "Goa Trip", "✈️", 4, 4508.32, 306.54));
        summaries.add(new GroupSummary(2, "Diwali Party", "🎉", 3, 495.00, -495.00));
        summaries.add(new GroupSummary(3, "Birthday Bash", "🎂", 5, 812.50, 53.00));
        return summaries;
    }

    public static List<MemberBalance> createSampleFriends() {
        List<MemberBalance> friends = new ArrayList<>();
        friends.add(new MemberBalance(1, "Priya", "P", 402.75));
        friends.add(new MemberBalance(2, "Rohan", "R", -56.20));
        friends.add(new MemberBalance(3, "Amit", "A", 125.50));
        return friends;
    }
}
