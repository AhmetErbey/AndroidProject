package com.chatmate.kids;
import com.airbnb.lottie.LottieAnimationView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView toRegisterTextView;
    private SwitchCompat themeSwitch;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String theme = prefs.getString("theme", "light");

        themeSwitch.setChecked(theme.equals("dark"));
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("theme", isChecked ? "dark" : "light");
            editor.apply();
            recreate(); // Uygulama zaten açılışta theme'i uyguluyor
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LottieAnimationView anim = findViewById(R.id.lottieLoginAnim);
        anim.playAnimation(); // Animasyonu manuel başlat
        // Firebase Authentication örneğini al
        mAuth = FirebaseAuth.getInstance();

        // XML öğelerini tanımla
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        toRegisterTextView = findViewById(R.id.toRegister);
        themeSwitch = findViewById(R.id.themeSwitch); // SwitchCompat XML’de tanımlı olmalı

        // Switch başlangıç durumunu yükle
        themeSwitch.setChecked(theme.equals("dark"));

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Lütfen email ve şifre giriniz", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Giriş başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
