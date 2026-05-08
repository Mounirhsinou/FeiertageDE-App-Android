package com.feiertage.deutschland.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * PreferenceManager – wraps SharedPreferences for all user settings.
 * Keys are grouped by domain for clarity.
 */
public class PreferenceManager {

    private static final String PREF_NAME = "feiertage_prefs";

    // ── Keys ──────────────────────────────────────────────────────────────────
    private static final String KEY_SELECTED_STATE      = "selected_state";
    private static final String KEY_SELECTED_YEAR       = "selected_year";
    private static final String KEY_DARK_MODE           = "dark_mode";
    private static final String KEY_NOTIFICATIONS       = "notifications_enabled";
    private static final String KEY_NOTIF_DAYS_BEFORE   = "notif_days_before";
    private static final String KEY_FAVORITES           = "favorites";
    private static final String KEY_FIRST_LAUNCH        = "first_launch";
    private static final String KEY_LAST_NOTIF_SCHEDULE = "last_notif_schedule";

    // ── Defaults ──────────────────────────────────────────────────────────────
    public static final String  DEFAULT_STATE       = "ALL";
    public static final boolean DEFAULT_DARK_MODE   = false;
    public static final boolean DEFAULT_NOTIF       = true;
    public static final int     DEFAULT_NOTIF_DAYS  = 1;

    // Singleton
    private static PreferenceManager instance;
    private final SharedPreferences prefs;

    private PreferenceManager(Context context) {
        prefs = context.getApplicationContext()
                       .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    // ─── State ───────────────────────────────────────────────────────────────

    public String getSelectedState() {
        return prefs.getString(KEY_SELECTED_STATE, DEFAULT_STATE);
    }

    public void setSelectedState(String stateCode) {
        prefs.edit().putString(KEY_SELECTED_STATE, stateCode).apply();
    }

    // ─── Year ────────────────────────────────────────────────────────────────

    public int getSelectedYear() {
        return prefs.getInt(KEY_SELECTED_YEAR, DateUtils.getCurrentYear());
    }

    public void setSelectedYear(int year) {
        prefs.edit().putInt(KEY_SELECTED_YEAR, year).apply();
    }

    // ─── Dark Mode ───────────────────────────────────────────────────────────

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE);
    }

    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    // ─── Notifications ───────────────────────────────────────────────────────

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, DEFAULT_NOTIF);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public int getNotifDaysBefore() {
        return prefs.getInt(KEY_NOTIF_DAYS_BEFORE, DEFAULT_NOTIF_DAYS);
    }

    public void setNotifDaysBefore(int days) {
        prefs.edit().putInt(KEY_NOTIF_DAYS_BEFORE, days).apply();
    }

    // ─── Favorites ───────────────────────────────────────────────────────────

    /** Returns the set of favorite holiday IDs. */
    public Set<String> getFavorites() {
        return new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }

    public boolean isFavorite(String holidayId) {
        return getFavorites().contains(holidayId);
    }

    public void addFavorite(String holidayId) {
        Set<String> favs = getFavorites();
        favs.add(holidayId);
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }

    public void removeFavorite(String holidayId) {
        Set<String> favs = getFavorites();
        favs.remove(holidayId);
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }

    public void toggleFavorite(String holidayId) {
        if (isFavorite(holidayId)) removeFavorite(holidayId);
        else addFavorite(holidayId);
    }

    // ─── First Launch ────────────────────────────────────────────────────────

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    // ─── Notification Schedule timestamp ────────────────────────────────────

    public long getLastNotifSchedule() {
        return prefs.getLong(KEY_LAST_NOTIF_SCHEDULE, 0);
    }

    public void setLastNotifSchedule(long timestamp) {
        prefs.edit().putLong(KEY_LAST_NOTIF_SCHEDULE, timestamp).apply();
    }
}
