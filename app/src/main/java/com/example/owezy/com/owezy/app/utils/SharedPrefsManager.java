package com.owezy.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Centralized SharedPreferences manager for app settings and preferences
 */
public class SharedPrefsManager {
    
    private static final String PREFS_NAME = "OwezyPrefs";
    
    // Preference keys
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_ANALYTICS_ENABLED = "analytics_enabled";
    
    // Theme mode constants
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;
    
    private static SharedPrefsManager instance;
    private final SharedPreferences prefs;
    
    private SharedPrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context);
        }
        return instance;
    }
    
    // Theme preferences
    public void setThemeMode(int themeMode) {
        prefs.edit().putInt(KEY_THEME_MODE, themeMode).apply();
    }
    
    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, THEME_SYSTEM);
    }
    
    // Currency preferences
    public void setCurrency(String currency) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply();
    }
    
    public String getCurrency() {
        return prefs.getString(KEY_CURRENCY, "₹");
    }
    
    // First launch flag
    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }
    
    public void setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }
    
    // Notifications
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }
    
    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }
    
    // Analytics
    public void setAnalyticsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ANALYTICS_ENABLED, enabled).apply();
    }
    
    public boolean isAnalyticsEnabled() {
        return prefs.getBoolean(KEY_ANALYTICS_ENABLED, true);
    }
    
    /**
     * Clear all preferences (useful for logout or reset)
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
