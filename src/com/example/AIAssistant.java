package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONObject;

public class AIAssistant {
    public static String ask(String question) {

        // It's generally better practice to load API keys from environment variables
        // or a configuration file rather than hardcoding them directly in the source.
        // For this example, we'll keep it as is.
        String apiKey = "your api key";

        String requestBody = """
        {
          "model": "google/gemma-3n-e2b-it:free",
          "messages": [
            {
              "role": "user",
              "content": "Describe the following topic in appropriate lines: %s"
            }
          ]
        }
        """.formatted(question);

        try {
            // Make the API request
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Extract and print response
            JSONObject json = new JSONObject(response.body());
            String content = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return content.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error while communicating with API: " + e.getMessage();
        }
    }
}
