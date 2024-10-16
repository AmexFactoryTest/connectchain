package com.americanexpress.connectchain.lcel;

import com.americanexpress.connectchain.Config;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Model {

    private enum ModelType {
        OPENAI,
        AZURE_OPENAI
        // Add other model types as needed
    }

    private static final Config config = Config.getInstance();

    public static ChatLanguageModel createModel(String modelName) {
        ModelType modelType = getModelType(modelName);
        switch (modelType) {
            case OPENAI:
                return createOpenAiModel(modelName);
            case AZURE_OPENAI:
                return createAzureOpenAiModel(modelName);
            default:
                throw new IllegalArgumentException("Unsupported model type: " + modelType);
        }
    }

    public static CompletableFuture<ChatLanguageModel> createModelAsync(String modelName) {
        return CompletableFuture.supplyAsync(() -> createModel(modelName));
    }

    private static ChatLanguageModel createOpenAiModel(String modelName) {
        String apiKey = (String) config.getConfig("OPENAI_API_KEY");
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }

    private static ChatLanguageModel createAzureOpenAiModel(String modelName) {
        String apiKey = (String) config.getConfig("AZURE_OPENAI_API_KEY");
        String endpoint = (String) config.getNestedConfig("services", "azure_openai", "api_base");
        String deploymentName = (String) config.getNestedConfig("services", "azure_openai", "deployment_name");

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .endpoint(endpoint)
                .deploymentName(deploymentName)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }

    private static ModelType getModelType(String modelName) {
        if (modelName.startsWith("gpt-")) {
            return ModelType.OPENAI;
        } else if (modelName.startsWith("azure-")) {
            return ModelType.AZURE_OPENAI;
        } else {
            throw new IllegalArgumentException("Unknown model type for model: " + modelName);
        }
    }

    public static int getContextSize(String modelName) {
        // This is a simplified version. You might want to expand this based on your needs.
        return OpenAiModelName.GPT_3_5_TURBO.contextLength();
    }

    public static OpenAiTokenizer getTokenizer(String modelName) {
        // This assumes all models use the same tokenizer. Adjust if needed.
        return new OpenAiTokenizer(modelName);
    }
}