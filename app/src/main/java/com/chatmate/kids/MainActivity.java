    package com.chatmate.kids;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUserInput;
    private Button buttonSend;
    private TextView textViewResponse;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Dil ayarlarını uygula
        LocaleHelper.applyLocale(this);
        
        // Tema ayarlarını uygula - Karanlık mod sorunu için önce tema ayarlarını yükle
        AppThemeManager.applyThemeSettings(this);
        
        // Layout'u yükle
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Status bar rengini mavi yap
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue_700));

        // Ayarlar düğmesi bağlantısı
        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        editTextUserInput = findViewById(R.id.editTextUserInput);
        buttonSend = findViewById(R.id.buttonSend);
        textViewResponse = findViewById(R.id.textViewResponse);
        scrollView = findViewById(R.id.scrollView);
        
        // Kullanıcı girişi sonrası hoş geldin mesajı
        PrefManager prefManager = new PrefManager(this);
        String welcomeMessage = "Hoş geldiniz! Size nasıl yardımcı olabilirim?";
        
        // Dil kontrolü
        if (prefManager.getLanguage().equals("en")) {
            welcomeMessage = "Welcome! How can I help you today?";
        }
        
        textViewResponse.setText("🤖 Edu Chat: " + welcomeMessage);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editTextUserInput.getText().toString().trim();
                if (userInput.isEmpty()) {
                    return;
                }

                String previousText = textViewResponse.getText().toString();
                String userMessage = "👤 You: " + userInput;

                textViewResponse.setText(previousText + "\n\n" + userMessage + "\n⏳ Waiting for response...");

                new Thread(() -> {
                    try {
                        String response = GeminiApiService.sendMessageToGemini(userInput);
                        
                        // Hata mesajı içeriyor mu kontrol et
                        if (response.contains("error")) {
                            runOnUiThread(() -> {
                                String currentText = textViewResponse.getText().toString().replace("⏳ Waiting for response...", "");
                                textViewResponse.setText(currentText + "\n❌ Şu anda yanıt veremiyorum. Lütfen daha sonra tekrar deneyin.");
                                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                                editTextUserInput.setText(""); // Kullanıcı girdisini temizle
                            });
                            return;
                        }
                        
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray candidates = jsonResponse.getJSONArray("candidates");
                            JSONObject firstCandidate = candidates.getJSONObject(0);
                            JSONObject content = firstCandidate.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            String message = parts.getJSONObject(0).getString("text");

                            runOnUiThread(() -> {
                                String currentText = textViewResponse.getText().toString().replace("⏳ Waiting for response...", "");
                                textViewResponse.setText(currentText + "\n🤖 Gemini: " + message);
                                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                                editTextUserInput.setText(""); // Kullanıcı girdisini temizle
                            });
                        } catch (JSONException e) {
                            runOnUiThread(() -> {
                                String currentText = textViewResponse.getText().toString().replace("⏳ Waiting for response...", "");
                                textViewResponse.setText(currentText + "\n❌ Yanıt işlenirken bir sorun oluştu.");
                                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                                editTextUserInput.setText(""); // Kullanıcı girdisini temizle
                            });
                        }
                    } catch (Exception e) {
                        // Tüm hataları yakala
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            String currentText = textViewResponse.getText().toString().replace("⏳ Waiting for response...", "");
                            textViewResponse.setText(currentText + "\n❌ Bir bağlantı hatası oluştu. Lütfen internet bağlantınızı kontrol edin.");
                            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                            editTextUserInput.setText(""); // Kullanıcı girdisini temizle
                        });
                    }
                }).start();
            }
        });
    }

    // Not: Eski menü metodları kaldırıldı, özel ayarlar düğmesi kullanılıyor
}
