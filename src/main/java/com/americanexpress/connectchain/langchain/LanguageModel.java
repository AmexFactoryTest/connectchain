package com.americanexpress.connectchain.langchain;

/**
 * The LanguageModel interface represents a generic language model capable of generating text based on a given prompt.
 * This interface serves as a foundation for various language model implementations in the ConnectChain framework.
 */
public interface LanguageModel {

    /**
     * Generates text based on the provided prompt.
     *
     * @param prompt The input prompt to guide text generation.
     * @return The generated text as a String.
     */
    String generate(String prompt);
}