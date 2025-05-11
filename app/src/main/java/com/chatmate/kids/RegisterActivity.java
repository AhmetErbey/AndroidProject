package com.chatmate.kids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private TextView toLoginTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // FirebaseAuth örneği alınır
        mAuth = FirebaseAuth.getInstance();

        // XML öğelerini tanımla
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        toLoginTextView = findViewById(R.id.toLogin);

        // Kayıt butonunun tıklama olayını ayarla
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // "Giriş Yap" metnine tıklayınca LoginActivity'e geç
        toLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        // Kullanıcının girdiği email ve şifreyi al
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Lütfen email ve şifre giriniz", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase ile yeni kullanıcı kaydı oluştur
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // Kullanıcı kaydedildi, şimdi displayName ayarla
                        FirebaseUser user = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName("Ahmet") // Şimdilik sabit, sonra EditText ile alacağız
                                .build();

                        if (user != null) {
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}
