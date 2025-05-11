package com.chatmate.kids;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUserInput = findViewById(R.id.editTextUserInput);
        buttonSend = findViewById(R.id.buttonSend);
        textViewResponse = findViewById(R.id.textViewResponse);
        scrollView = findViewById(R.id.scrollView); // 🔹 ScrollView erişimi

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
                    String response = GeminiApiService.sendMessageToGemini(userInput);
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
                            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN)); // 🔹 Otomatik aşağı kaydır
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            String currentText = textViewResponse.getText().toString().replace("⏳ Waiting for response...", "");
                            textViewResponse.setText(currentText + "\n❌ An error occurred while parsing the response.");
                            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                        });
                    }
                }).start();
            }
        });
    }
}