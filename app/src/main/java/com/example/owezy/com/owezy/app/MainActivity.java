package com.owezy.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.owezy.app.ui.activity.ActivityFragment;
import com.owezy.app.ui.expenses.AddExpenseFragment;
import com.owezy.app.ui.groups.GroupDetailsFragment;
import com.owezy.app.ui.groups.GroupsFragment;
import com.owezy.app.ui.home.HomeFragment;
import com.owezy.app.ui.profile.ProfileFragment;
import com.owezy.app.ui.settings.SettingsFragment;
import com.owezy.app.utils.ThemeHelper;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ExtendedFloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply saved theme preference
        ThemeHelper.applyTheme(this);
        
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.nav_bar_background, getTheme()));
            
            int nightModeFlags = getResources().getConfiguration().uiMode & 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
                getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() | 
                    android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAdd = findViewById(R.id.fab_add);

        // Load default fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false);
        }

        // Bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
                fabAdd.show();
            } else if (itemId == R.id.nav_groups) {
                fragment = new GroupsFragment();
                fabAdd.hide();
            } else if (itemId == R.id.nav_activity) {
                fragment = new ActivityFragment();
                fabAdd.show();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
                fabAdd.hide();
            }

            return loadFragment(fragment, false);
        });

        // FAB click listener
        fabAdd.setOnClickListener(v -> {
            loadFragment(new AddExpenseFragment(), true);
        });
    }

    public boolean loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
            return true;
        }
        return false;
    }

    public void showGroupDetails(long groupId) {
        loadFragment(GroupDetailsFragment.newInstance(groupId), true);
    }
}
