package com.chatmate.kids;
import com.airbnb.lottie.LottieAnimationView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView toRegisterTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
        toRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(view -> loginUser());
    }

    // İnternet bağlantısını kontrol etmek için metod
    private boolean isNetworkConnected() {
        android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Lütfen email ve şifre giriniz", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // İnternet bağlantısını kontrol et
        if (!isNetworkConnected()) {
            Toast.makeText(LoginActivity.this, "Lütfen internet bağlantınızı kontrol edin", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Giriş işlemi başladığında yükleniyor mesajı göster
        Toast.makeText(LoginActivity.this, "Giriş yapılıyor...", Toast.LENGTH_SHORT).show();
        
        // Timeout süresini artır
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                        
                        // Kullanıcı giriş durumunu kaydet
                        PrefManager prefManager = new PrefManager(LoginActivity.this);
                        prefManager.setBoolean("is_logged_in", true);
                        
                        // Karanlık mod sorunu için Intent'e tema bilgisini ekle
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Hata mesajını daha detaylı göster
                        String errorMessage = "Giriş başarısız";
                        if (task.getException() != null) {
                            String exceptionMessage = task.getException().getMessage();
                            if (exceptionMessage != null && exceptionMessage.contains("network")) {
                                errorMessage = "Ağ bağlantısı zaman aşımına uğradı. Lütfen internet bağlantınızı kontrol edin.";
                            } else {
                                errorMessage += ": " + exceptionMessage;
                            }
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Özel hata durumlarını yönet
                    String errorMessage = "Giriş başarısız";
                    if (e.getMessage() != null && e.getMessage().contains("network")) {
                        errorMessage = "Ağ bağlantısı zaman aşımına uğradı. Lütfen internet bağlantınızı kontrol edin.";
                    } else {
                        errorMessage += ": " + e.getMessage();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }
}
