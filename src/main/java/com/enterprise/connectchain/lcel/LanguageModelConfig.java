package com.enterprise.connectchain.lcel;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import com.enterprise.connectchain.Config;

import java.time.Duration;

public class LanguageModelConfig {

    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    public static ChatLanguageModel createOpenAiModel() {
        String apiKey = Config.getInstance().get("openai.api_key")
                .orElseThrow(() -> new IllegalStateException("OpenAI API key not found in configuration"));
        String model = Config.getInstance().getOrDefault("openai.model", DEFAULT_MODEL);

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .timeout(DEFAULT_TIMEOUT)
                .maxRetries(3)
                .build();
    }

    public static ChatLanguageModel createAzureOpenAiModel() {
        String endpoint = Config.getInstance().get("azure.endpoint")
                .orElseThrow(() -> new IllegalStateException("Azure OpenAI endpoint not found in configuration"));
        String apiKey = Config.getInstance().get("azure.api_key")
                .orElseThrow(() -> new IllegalStateException("Azure OpenAI API key not found in configuration"));
        String deploymentName = Config.getInstance().get("azure.deployment_name")
                .orElseThrow(() -> new IllegalStateException("Azure OpenAI deployment name not found in configuration"));

        return AzureOpenAiChatModel.builder()
                .endpoint(endpoint)
                .apiKey(apiKey)
                .deploymentName(deploymentName)
                .timeout(DEFAULT_TIMEOUT)
                .maxRetries(3)
                .build();
    }

    public static OpenAiTokenizer createOpenAiTokenizer() {
        String model = Config.getInstance().getOrDefault("openai.model", DEFAULT_MODEL);
        return new OpenAiTokenizer(model);
    }

    public static ChatLanguageModel createModelFromConfig() {
        String provider = Config.getInstance().getOrDefault("llm.provider", "openai");

        return switch (provider.toLowerCase()) {
            case "openai" -> createOpenAiModel();
            case "azure" -> createAzureOpenAiModel();
            default -> throw new IllegalArgumentException("Unsupported LLM provider: " + provider);
        };
    }
}