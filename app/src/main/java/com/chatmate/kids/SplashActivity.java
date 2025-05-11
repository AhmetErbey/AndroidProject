package com.chatmate.kids;
import com.airbnb.lottie.LottieAnimationView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tema ayarlarını uygula
        applyThemeSettings();
        
        super.onCreate(savedInstanceState);

        // Splash ekranını göstermek için XML layout'u ayarla
        setContentView(R.layout.activity_splash);
        LottieAnimationView lottieLoader = findViewById(R.id.lottieLoader);
        lottieLoader.playAnimation(); // zaten autoPlay varsa şart değil
        
        // 2 saniye sonra Login ekranına geç
        new Handler().postDelayed(() -> {
            // Kullanıcının giriş durumunu kontrol et
            PrefManager prefManager = new PrefManager(this);
            boolean isLoggedIn = prefManager.getBoolean("is_logged_in", false);
            
            // Eğer kullanıcı giriş yapmışsa ana ekrana, yapmamışsa giriş ekranına yönlendir
            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
        }, 2000);
    }
    
    /**
     * Tema ayarlarını uygular (karanlık mod ve renk teması)
     */
    private void applyThemeSettings() {
        // Gelişmiş tema yönetim sistemini kullan
        AppThemeManager.applyThemeSettings(this);
    }
}
