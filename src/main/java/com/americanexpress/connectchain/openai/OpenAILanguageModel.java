package com.americanexpress.connectchain.openai;

import com.americanexpress.connectchain.langchain.LanguageModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OpenAILanguageModel implements LanguageModel {
    private static final Logger logger = LoggerFactory.getLogger(OpenAILanguageModel.class);
    private final OkHttpClient client;
    private final String apiKey;
    private final String apiBase;
    private final String engine;
    private final ObjectMapper objectMapper;

    public OpenAILanguageModel(String apiKey, String apiBase, String engine) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.apiKey = apiKey;
        this.apiBase = apiBase;
        this.engine = engine;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String generate(String prompt) {
        logger.debug("Generating text for prompt: {}", prompt);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                String.format("{\"prompt\": \"%s\", \"max_tokens\": 100}", prompt.replace("\"", "\\\""))
        );

        Request request = new Request.Builder()
                .url(apiBase + "/v1/engines/" + engine + "/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String generatedText = jsonNode.path("choices").get(0).path("text").asText();

            logger.debug("Generated text: {}", generatedText);
            return generatedText.trim();
        } catch (IOException e) {
            logger.error("Error generating text from OpenAI API", e);
            throw new RuntimeException("Failed to generate text", e);
        }
    }

    // Additional methods can be added here as needed
}