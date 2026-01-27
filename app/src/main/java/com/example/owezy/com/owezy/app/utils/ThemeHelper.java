package com.owezy.app.utils;

import android.content.Context;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Helper class for managing app-wide theme changes
 */
public class ThemeHelper {
    
    /**
     * Apply the saved theme preference
     */
    public static void applyTheme(Context context) {
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(context);
        int themeMode = prefsManager.getThemeMode();
        
        switch (themeMode) {
            case SharedPrefsManager.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case SharedPrefsManager.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case SharedPrefsManager.THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    /**
     * Check if dark mode is currently active
     */
    public static boolean isDarkMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode 
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
    
    /**
     * Get theme mode display name
     */
    public static String getThemeModeName(int themeMode) {
        switch (themeMode) {
            case SharedPrefsManager.THEME_LIGHT:
                return "Light";
            case SharedPrefsManager.THEME_DARK:
                return "Dark";
            case SharedPrefsManager.THEME_SYSTEM:
            default:
                return "System Default";
        }
    }
}
