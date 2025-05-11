package com.chatmate.kids;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class GeminiApiService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public static String sendMessageToGemini(String userInput) {
        try {
            URL url = new URL(API_URL + "?key=" + Constants.GEMINI_API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt = "You are a friendly and smart education chatbot for kids aged 7 to 10. "
                    + "Explain things clearly, step by step, using simple and encouraging language. "
                    + "Always include a friendly greeting like 'Hello!' at the start.";

            String jsonInputString = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"parts\": [\n" +
                    "        {\"text\": \"" + prompt + "\"},\n" +
                    "        {\"text\": \"" + userInput + "\"}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";


            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            // Android log'a cevabı yaz
            Log.d("GeminiAPI", "Cevap: " + response.toString());

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Bir hata oluştu: " + e.getMessage() + "\"}";
        }
    }
}
