package com.chatmate.kids;

import android.app.Activity;
import android.content.Context;

public class ThemeHelper {
    public static void applyThemeColor(Context context) {
        PrefManager prefManager = new PrefManager(context);
        int index = prefManager.getInt("theme_color_index", 0);

        int themeId;
        switch (index) {
            case 1:  // Green
                themeId = R.style.Theme_ChatBotApp_Green;
                break;
            case 2:  // Orange
                themeId = R.style.Theme_ChatBotApp_Orange;
                break;
            default: // Blue (default)
                themeId = R.style.Theme_ChatBotApp_Blue;
                break;
        }

        // Cast only if context is an instance of Activity
        if (context instanceof Activity) {
            ((Activity) context).setTheme(themeId);
        }
    }
}
