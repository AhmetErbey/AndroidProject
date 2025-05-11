package com.chatmate.kids;
import com.airbnb.lottie.LottieAnimationView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Splash ekranını göstermek için XML layout'u ayarla
        setContentView(R.layout.activity_splash);
        LottieAnimationView lottieLoader = findViewById(R.id.lottieLoader);
        lottieLoader.playAnimation(); // zaten autoPlay varsa şart değil
        // 2 saniye sonra Login ekranına geç
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 2000);
    }
}
