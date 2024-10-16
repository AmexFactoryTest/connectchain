package com.enterprise.connectchain.lcel;

import dev.langchain4j.chain.Chain;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.prompt.PromptTemplate;
import com.enterprise.connectchain.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ChainExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ChainExecutor.class);
    private final Config config;

    public ChainExecutor() {
        this.config = Config.getInstance();
    }

    public String executeQuery(String query, Map<String, String> parameters) {
        try {
            Chain<String, String> chain = createChain();
            String result = chain.execute(query);
            return processResult(result, parameters);
        } catch (Exception e) {
            logger.error("Error executing query: {}", query, e);
            throw new RuntimeException("Failed to execute query", e);
        }
    }

    private Chain<String, String> createChain() {
        ChatLanguageModel model = createLanguageModel();
        PromptTemplate promptTemplate = PromptTemplate.from("{{query}}");

        return Chain.builder()
                .chatLanguageModel(model)
                .promptTemplate(promptTemplate)
                .build();
    }

    private ChatLanguageModel createLanguageModel() {
        String apiKey = config.getOrDefault("openai.api_key", "");
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("OpenAI API key not found in configuration");
        }

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .build();
    }

    private String processResult(String result, Map<String, String> parameters) {
        // Here you can implement any post-processing logic
        // For now, we'll just return the raw result
        return result;
    }
}