package com.americanexpress.connectchain.langchain;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LLMChain {
    private static final Logger logger = LoggerFactory.getLogger(LLMChain.class);
    private final LanguageModel llm;
    private final PromptTemplate prompt;

    public LLMChain(LanguageModel llm, PromptTemplate prompt) {
        this.llm = llm;
        this.prompt = prompt;
    }

    public String run(Map<String, String> inputs) {
        try {
            logger.debug("Running LLMChain with inputs: {}", inputs);
            String formattedPrompt = prompt.format(inputs);
            return llm.generate(formattedPrompt);
        } catch (Exception e) {
            logger.error("Error running LLMChain", e);
            throw new RuntimeException("Error running LLMChain", e);
        }
    }

    public CompletableFuture<String> runAsync(Map<String, String> inputs) {
        return CompletableFuture.supplyAsync(() -> run(inputs));
    }
}

interface LanguageModel {
    String generate(String prompt);
}

interface PromptTemplate {
    String format(Map<String, String> inputs);
}