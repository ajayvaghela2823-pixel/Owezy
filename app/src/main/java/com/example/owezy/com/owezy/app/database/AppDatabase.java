package com.owezy.app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.owezy.app.database.dao.ExpenseDao;
import com.owezy.app.database.dao.ExpenseSplitDao;
import com.owezy.app.database.dao.GroupDao;
import com.owezy.app.database.dao.GroupMemberDao;
import com.owezy.app.database.dao.MemberDao;
import com.owezy.app.database.dao.SettlementDao;
import com.owezy.app.database.entities.Expense;
import com.owezy.app.database.entities.ExpenseSplit;
import com.owezy.app.database.entities.Group;
import com.owezy.app.database.entities.GroupMember;
import com.owezy.app.database.entities.Member;
import com.owezy.app.database.entities.Settlement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Group.class,
        Member.class,
        Expense.class,
        ExpenseSplit.class,
        GroupMember.class,
        Settlement.class
}, version = 3, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    // DAO accessors
    public abstract GroupDao groupDao();

    public abstract MemberDao memberDao();

    public abstract ExpenseDao expenseDao();

    public abstract ExpenseSplitDao expenseSplitDao();

    public abstract GroupMemberDao groupMemberDao();

    public abstract SettlementDao settlementDao();

    // Singleton instance
    private static volatile AppDatabase INSTANCE;

    // Executor for database operations
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "owezy_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                MemberDao memberDao = INSTANCE.memberDao();
                GroupDao groupDao = INSTANCE.groupDao();
                GroupMemberDao groupMemberDao = INSTANCE.groupMemberDao();

                // Create sample members
                Member member1 = new Member("You", "Y");
                Member member2 = new Member("Rohan", "R");
                Member member3 = new Member("Priya", "P");
                Member member4 = new Member("Amit", "A");

                long userId = memberDao.insertMember(member1);
                long rohanId = memberDao.insertMember(member2);
                long priyaId = memberDao.insertMember(member3);
                long amitId = memberDao.insertMember(member4);

                // Create sample groups
                Group group1 = new Group("Goa Trip", "✈️", System.currentTimeMillis());
                Group group2 = new Group("Diwali Party", "🎉", System.currentTimeMillis());
                Group group3 = new Group("Birthday Bash", "🎂", System.currentTimeMillis());

                long groupId1 = groupDao.insertGroup(group1);
                long groupId2 = groupDao.insertGroup(group2);
                long groupId3 = groupDao.insertGroup(group3);

                // Add members to groups
                groupMemberDao.insertGroupMember(new GroupMember(groupId1, userId));
                groupMemberDao.insertGroupMember(new GroupMember(groupId1, rohanId));
                groupMemberDao.insertGroupMember(new GroupMember(groupId2, userId));
                groupMemberDao.insertGroupMember(new GroupMember(groupId2, priyaId));
                groupMemberDao.insertGroupMember(new GroupMember(groupId3, userId));
                groupMemberDao.insertGroupMember(new GroupMember(groupId3, amitId));
            });
        }
    };
}
