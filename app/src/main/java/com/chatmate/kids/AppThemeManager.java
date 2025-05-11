package com.chatmate.kids;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

/**
 * Gelişmiş tema yönetimi sağlayan sınıf.
 * Karanlık mod, sistem temasını takip etme ve animasyonlu tema geçişleri destekler.
 */
public class AppThemeManager {

    // Tema modu sabitleri
    public static final int MODE_AUTO = 0;    // Sistem temasını takip et
    public static final int MODE_LIGHT = 1;   // Her zaman açık tema
    public static final int MODE_DARK = 2;    // Her zaman karanlık tema
    
    // Tema geçiş animasyonu süresi (ms)
    private static final int THEME_TRANSITION_DURATION = 500;
    
    /**
     * Uygulama başlangıcında çağrılarak tema ayarlarını uygular.
     * @param context Uygulama context'i
     */
    public static void applyThemeSettings(Context context) {
        PrefManager prefManager = new PrefManager(context);
        
        // Tema modunu al
        int themeMode = prefManager.getInt("theme_mode", MODE_AUTO);
        
        // Tema modunu uygula
        applyThemeMode(themeMode);
        
        // Renk temasını uygula
        ThemeHelper.applyThemeColor(context);
    }
    
    /**
     * Tema modunu değiştirir ve kaydeder.
     * @param context Context
     * @param themeMode Tema modu (MODE_AUTO, MODE_LIGHT, MODE_DARK)
     * @param animate Geçiş animasyonu kullanılsın mı?
     */
    public static void setThemeMode(Context context, int themeMode, boolean animate) {
        // Tema modunu kaydet
        PrefManager prefManager = new PrefManager(context);
        prefManager.setInt("theme_mode", themeMode);
        
        // Karanlık mod durumunu da güncelle (geriye dönük uyumluluk için)
        boolean isDarkMode = themeMode == MODE_DARK || 
                (themeMode == MODE_AUTO && isSystemInDarkMode(context));
        prefManager.setBoolean("dark_mode", isDarkMode);
        
        // Tema modunu uygula
        applyThemeMode(themeMode);
        
        // Aktiviteyi yeniden başlat (animasyonlu veya animasyonsuz)
        if (context instanceof Activity) {
            if (animate) {
                restartActivityWithAnimation((Activity) context);
            } else {
                ((Activity) context).recreate();
            }
        }
    }
    
    /**
     * Belirtilen tema modunu uygular.
     * @param themeMode Tema modu
     */
    private static void applyThemeMode(int themeMode) {
        int nightMode;
        switch (themeMode) {
            case MODE_LIGHT:
                // Telefon karanlık modda olsa bile her zaman açık mod
                nightMode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case MODE_DARK:
                // Telefon açık modda olsa bile her zaman karanlık mod
                nightMode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case MODE_AUTO:
            default:
                // Sistem temasını takip et
                nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }
        
        // Tema modunu uygula
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
    
    /**
     * Sistem temasının karanlık modda olup olmadığını kontrol eder.
     * @param context Context
     * @return Sistem karanlık modda ise true, değilse false
     */
    public static boolean isSystemInDarkMode(Context context) {
        int uiMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return uiMode == Configuration.UI_MODE_NIGHT_YES;
    }
    
    /**
     * Mevcut tema modunu döndürür.
     * @param context Context
     * @return Tema modu (MODE_AUTO, MODE_LIGHT, MODE_DARK)
     */
    public static int getCurrentThemeMode(Context context) {
        PrefManager prefManager = new PrefManager(context);
        return prefManager.getInt("theme_mode", MODE_AUTO);
    }
    
    /**
     * Mevcut tema modunun adını döndürür.
     * @param context Context
     * @return Tema modunun adı ("Sistem", "Açık", "Karanlık")
     */
    public static String getCurrentThemeModeName(Context context) {
        int themeMode = getCurrentThemeMode(context);
        switch (themeMode) {
            case MODE_LIGHT:
                return context.getString(R.string.theme_light);
            case MODE_DARK:
                return context.getString(R.string.theme_dark);
            case MODE_AUTO:
            default:
                return context.getString(R.string.theme_system);
        }
    }
    
    /**
     * Aktiviteyi animasyonlu bir şekilde yeniden başlatır.
     * @param activity Yeniden başlatılacak aktivite
     */
    private static void restartActivityWithAnimation(final Activity activity) {
        // Geçiş animasyonu için görünürlüğü yavaşça azalt
        View rootView = activity.getWindow().getDecorView();
        rootView.animate().alpha(0f).setDuration(THEME_TRANSITION_DURATION / 2).start();
        
        new Handler().postDelayed(() -> {
            // Aktiviteyi yeniden başlat
            Intent intent = activity.getIntent();
            activity.finish();
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.startActivity(intent);
        }, THEME_TRANSITION_DURATION);
    }
}
