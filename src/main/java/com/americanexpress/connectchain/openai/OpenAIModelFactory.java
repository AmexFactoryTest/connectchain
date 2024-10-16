package com.americanexpress.connectchain.openai;

import com.americanexpress.connectchain.Config;
import com.americanexpress.connectchain.utils.ProxyManager;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.Optional;

public class OpenAIModelFactory {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIModelFactory.class);
    private static OpenAIModelFactory instance;
    private final Config config;
    private final ProxyManager proxyManager;

    private OpenAIModelFactory() {
        this.config = Config.getInstance();
        this.proxyManager = ProxyManager.getInstance();
    }

    public static OpenAIModelFactory getInstance() {
        if (instance == null) {
            instance = new OpenAIModelFactory();
        }
        return instance;
    }

    public ChatLanguageModel createModel(String modelName) {
        try {
            String apiKey = (String) config.getConfig("OPENAI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("OpenAI API key is not set in the configuration");
            }

            OpenAiChatModel.Builder modelBuilder = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .temperature((Double) config.getNestedConfig("services", "openai", "temperature", 0.7))
                    .maxTokens((Integer) config.getNestedConfig("services", "openai", "max_tokens", 2048))
                    .topP((Double) config.getNestedConfig("services", "openai", "top_p", 1.0))
                    .frequencyPenalty((Double) config.getNestedConfig("services", "openai", "frequency_penalty", 0.0))
                    .presencePenalty((Double) config.getNestedConfig("services", "openai", "presence_penalty", 0.0));

            // Apply proxy settings if configured
            Optional<Proxy> proxy = proxyManager.getProxy();
            proxy.ifPresent(modelBuilder::proxy);

            return modelBuilder.build();
        } catch (Exception e) {
            logger.error("Error creating OpenAI model: {}", e.getMessage());
            throw new RuntimeException("Failed to create OpenAI model", e);
        }
    }

    public ChatLanguageModel createDefaultModel() {
        String defaultModel = (String) config.getNestedConfig("services", "openai", "default_model", "gpt-3.5-turbo");
        return createModel(defaultModel);
    }
}