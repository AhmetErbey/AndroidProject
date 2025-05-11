package com.chatmate.kids;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Giriş yapan kullanıcıyı al
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "kullanıcı";

        // Mesajı göster
        TextView tv = new TextView(this);
        tv.setText("Hoş geldin, " + name + "!");
        tv.setTextSize(22);
        tv.setPadding(64, 200, 64, 64);

        setContentView(tv);
    }
}
