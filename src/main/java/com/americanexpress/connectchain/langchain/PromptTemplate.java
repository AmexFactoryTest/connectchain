package com.americanexpress.connectchain.langchain;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PromptTemplate class for handling templated prompts in the ConnectChain framework.
 */
public class PromptTemplate {
    private final String template;
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    /**
     * Constructs a new PromptTemplate with the given template string.
     *
     * @param template The template string containing placeholders in the format {placeholder}
     */
    public PromptTemplate(String template) {
        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("Template cannot be null or empty");
        }
        this.template = template;
    }

    /**
     * Formats the template by replacing placeholders with provided input values.
     *
     * @param inputs A map of placeholder names to their corresponding values
     * @return The formatted string with placeholders replaced by input values
     * @throws IllegalArgumentException if a required input is missing
     */
    public String format(Map<String, String> inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("Inputs map cannot be null");
        }

        StringBuilder result = new StringBuilder(template);
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String value = inputs.get(placeholder);
            if (value == null) {
                throw new IllegalArgumentException("Missing input for placeholder: " + placeholder);
            }
            int start = result.indexOf("{" + placeholder + "}");
            int end = start + placeholder.length() + 2;
            result.replace(start, end, value);
        }

        return result.toString();
    }

    /**
     * Returns the raw template string.
     *
     * @return The original template string
     */
    public String getTemplate() {
        return template;
    }
}