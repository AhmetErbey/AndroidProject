package com.chatmate.kids;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private TextView textCurrentLanguage;
    private TextView textCurrentColorTheme;
    private LinearLayout layoutLanguage;
    private LinearLayout layoutColorTheme;
    private LinearLayout layoutLogout;
    private LinearLayout layoutAbout;
    
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        ThemeHelper.applyThemeColor(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Status bar rengini mavi yap
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue_700));

        // Geri tuşuna tıklama işlevini ekle
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            onBackPressed();
        });

        // ActionBar'a gerek yok, özel toolbar kullanıyoruz
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        prefManager = new PrefManager(this);

        // View bileşenlerini bul
        switchDarkMode = findViewById(R.id.switchDarkMode);
        textCurrentLanguage = findViewById(R.id.textCurrentLanguage);
        textCurrentColorTheme = findViewById(R.id.textCurrentColorTheme);
        layoutLanguage = findViewById(R.id.layoutLanguage);
        layoutColorTheme = findViewById(R.id.layoutColorTheme);
        layoutLogout = findViewById(R.id.layoutLogout);
        layoutAbout = findViewById(R.id.layoutAbout);

        // Mevcut dil ve tema ayarlarını göster
        setupCurrentSettings();
        
        // Karanlık mod ayarı
        setupDarkModeSwitch();
        
        // Dil değiştirme
        setupLanguageSelection();
        
        // Renk teması değiştirme
        setupColorThemeSelection();
        
        // Çıkış yapma
        setupLogout();
        
        // Hakkında
        setupAbout();
    }
    
    private void setupCurrentSettings() {
        // Dil ayarını göster
        String currentLang = prefManager.getLanguage();
        textCurrentLanguage.setText(currentLang.equals("tr") ? getString(R.string.turkish) : getString(R.string.english));
        
        // Renk teması ayarını göster
        int savedColorIndex = prefManager.getInt("theme_color_index", 0);
        String[] colorOptions = getResources().getStringArray(R.array.color_theme_options);
        if (savedColorIndex < colorOptions.length) {
            textCurrentColorTheme.setText(colorOptions[savedColorIndex]);
        }
    }
    
    private void setupDarkModeSwitch() {
        // Mevcut tema modunu al
        int currentThemeMode = AppThemeManager.getCurrentThemeMode(this);
        
        // Karanlık mod durumunu belirle
        boolean isDarkMode = currentThemeMode == AppThemeManager.MODE_DARK || 
                (currentThemeMode == AppThemeManager.MODE_AUTO && AppThemeManager.isSystemInDarkMode(this));
        
        // Switch'i ayarla
        switchDarkMode.setChecked(isDarkMode);
        
        // Karanlık mod değişikliği için listener
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Tema modunu belirle
            int newThemeMode;
            if (currentThemeMode == AppThemeManager.MODE_AUTO) {
                // Sistem temasını takip ediyorsa, kullanıcıya sor
                showThemeModeDialog();
                return;
            } else {
                // Açık/Karanlık mod arasında geçiş yap
                newThemeMode = isChecked ? AppThemeManager.MODE_DARK : AppThemeManager.MODE_LIGHT;
            }
            
            // Tema modunu uygula
            AppThemeManager.setThemeMode(this, newThemeMode, true);
        });
    }
    
    private void setupLanguageSelection() {
        // Mevcut dili göster
        String currentLang = prefManager.getLanguage();
        textCurrentLanguage.setText(currentLang.equals("tr") ? getString(R.string.turkish) : getString(R.string.english));
        
        layoutLanguage.setOnClickListener(v -> {
            // Dil seçimi için dialog göster
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.select_language));
            
            final String[] languages = {"English", "Türkçe"};
            final String[] langCodes = {"en", "tr"};
            
            String currentLanguage = prefManager.getLanguage();
            int checkedItem = currentLanguage.equals("tr") ? 1 : 0;
            
            builder.setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                String selectedLang = langCodes[which];
                if (!selectedLang.equals(prefManager.getLanguage())) {
                    prefManager.setLanguage(selectedLang);
                    setLocale(selectedLang);
                    
                    // Tüm aktiviteleri yeniden başlat
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                dialog.dismiss();
            });
            
            builder.show();
        });
    }
    
    private void setupColorThemeSelection() {
        layoutColorTheme.setOnClickListener(v -> {
            // Renk teması seçimi için dialog göster
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.select_color_theme));
            
            final String[] colorOptions = getResources().getStringArray(R.array.color_theme_options);
            int savedColorIndex = prefManager.getInt("theme_color_index", 0);
            
            builder.setSingleChoiceItems(colorOptions, savedColorIndex, (dialog, which) -> {
                if (which != savedColorIndex) {
                    prefManager.setInt("theme_color_index", which);
                    
                    // Renk teması değişikliğini uygula
                    Toast.makeText(SettingsActivity.this, 
                            getString(R.string.color_theme_label) + ": " + colorOptions[which], 
                            Toast.LENGTH_SHORT).show();
                    
                    // Ana ekrana dön ve temayı uygula
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                dialog.dismiss();
            });
            
            builder.show();
        });
    }
    
    private void setupLogout() {
        layoutLogout.setOnClickListener(v -> {
            // Çıkış yapma onayı için dialog göster
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logout))
                    .setMessage("Çıkış yapmak istediğinize emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        // Kullanıcı oturumunu kapat
                        prefManager.setBoolean("is_logged_in", false);
                        
                        // Login ekranına yönlendir
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
        });
    }
    
    private void setupAbout() {
        layoutAbout.setOnClickListener(v -> {
            // Hakkında bilgisi için dialog göster
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.about))
                    .setMessage("Edu Chat v1.0\n\nBu uygulama çocuklar için eğitici bir sohbet uygulamasıdır.")
                    .setPositiveButton("Tamam", null)
                    .show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
    
    /**
     * Tema modu seçimi için dialog gösterir
     */
    private void showThemeModeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.theme_mode));
        builder.setMessage(getString(R.string.theme_mode_description));
        
        final String[] themeOptions = {
                getString(R.string.theme_system),
                getString(R.string.theme_light),
                getString(R.string.theme_dark)
        };
        
        int currentThemeMode = AppThemeManager.getCurrentThemeMode(this);
        
        builder.setSingleChoiceItems(themeOptions, currentThemeMode, (dialog, which) -> {
            // Tema modunu uygula
            AppThemeManager.setThemeMode(this, which, true);
            dialog.dismiss();
        });
        
        builder.setNegativeButton("İptal", null);
        builder.show();
    }
}
