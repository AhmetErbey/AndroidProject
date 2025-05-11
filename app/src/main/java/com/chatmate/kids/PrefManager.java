package com.chatmate.kids;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREF_NAME = "AppSettings";
    private static final String KEY_DARK_MODE = "isDarkMode";
    private static final String KEY_LANGUAGE = "language";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Theme settings
    public void setDarkMode(boolean isDark) {
        editor.putBoolean(KEY_DARK_MODE, isDark);
        editor.apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false); // Default: Light mode
    }

    // Language settings
    public void setLanguage(String langCode) {
        editor.putString(KEY_LANGUAGE, langCode);
        editor.apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "en"); // Default: English
    }

    // 💡 NEW: General-purpose integer setter/getter (used for color theme index etc.)
    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }
    
    // Boolean değerler için genel amaçlı setter/getter
    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
