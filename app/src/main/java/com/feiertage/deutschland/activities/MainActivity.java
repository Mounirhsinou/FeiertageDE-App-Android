package com.feiertage.deutschland.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.fragments.CalendarFragment;
import com.feiertage.deutschland.fragments.HomeFragment;
import com.feiertage.deutschland.fragments.SettingsFragment;
import com.feiertage.deutschland.fragments.StatesFragment;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * MainActivity – bottom-navigation host with 4 tabs:
 * Home | Calendar | States | Settings
 */
public class MainActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener {

    private static final String KEY_NAV_ITEM = "nav_item";

    private BottomNavigationView bottomNav;
    private int currentNavItemId = R.id.nav_home;

    // Fragment instances kept alive to preserve scroll position
    private HomeFragment     homeFragment;
    private CalendarFragment calendarFragment;
    private StatesFragment   statesFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super
        PreferenceManager prefs = PreferenceManager.getInstance(this);
        ThemeHelper.applyTheme(prefs.isDarkMode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(this);

        if (savedInstanceState != null) {
            currentNavItemId = savedInstanceState.getInt(KEY_NAV_ITEM, R.id.nav_home);
        }

        // Re-attach or create fragments
        if (savedInstanceState == null) {
            initFragments();
            showFragment(R.id.nav_home);
        } else {
            // Retrieve existing fragment instances from back stack
            homeFragment     = (HomeFragment)     getSupportFragmentManager().findFragmentByTag("home");
            calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag("calendar");
            statesFragment   = (StatesFragment)   getSupportFragmentManager().findFragmentByTag("states");
            settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("settings");
            showFragment(currentNavItemId);
        }

        bottomNav.setSelectedItemId(currentNavItemId);
    }

    private void initFragments() {
        homeFragment     = new HomeFragment();
        calendarFragment = new CalendarFragment();
        statesFragment   = new StatesFragment();
        settingsFragment = new SettingsFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment,     "home")
                .add(R.id.fragment_container, calendarFragment, "calendar")
                .add(R.id.fragment_container, statesFragment,   "states")
                .add(R.id.fragment_container, settingsFragment, "settings")
                .hide(calendarFragment)
                .hide(statesFragment)
                .hide(settingsFragment)
                .commit();
    }

    private void showFragment(int navItemId) {
        Fragment target = fragmentForId(navItemId);
        if (target == null) return;

        // Hide all, show target
        getSupportFragmentManager().beginTransaction()
                .hide(homeFragment)
                .hide(calendarFragment)
                .hide(statesFragment)
                .hide(settingsFragment)
                .show(target)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();

        currentNavItemId = navItemId;
    }

    private Fragment fragmentForId(int id) {
        if (id == R.id.nav_home)     return homeFragment;
        if (id == R.id.nav_calendar) return calendarFragment;
        if (id == R.id.nav_states)   return statesFragment;
        if (id == R.id.nav_settings) return settingsFragment;
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == currentNavItemId) return true; // Already selected
        showFragment(id);
        return true;
    }

    /** Called by fragments to navigate programmatically (e.g. "View All" → Calendar tab). */
    public void navigateTo(int navItemId) {
        bottomNav.setSelectedItemId(navItemId);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_NAV_ITEM, currentNavItemId);
    }
}
