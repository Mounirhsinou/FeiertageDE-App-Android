package com.feiertage.deutschland.activities;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * ThemeHelper – small utility for applying light/dark mode consistently.
 */
public class ThemeHelper {

    private ThemeHelper() {}

    /**
     * Applies dark or light mode app-wide.
     * Call this before setContentView or before any activity starts.
     */
    public static void applyTheme(boolean darkMode) {
        AppCompatDelegate.setDefaultNightMode(
                darkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
