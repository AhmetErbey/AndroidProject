package com.chatmate.kids;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {
    public static void applyLocale(Context context) {
        PrefManager prefManager = new PrefManager(context);
        String langCode = prefManager.getLanguage();
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        Resources resources = context.getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
