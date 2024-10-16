package com.americanexpress.connectchain.prompts;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ValidPromptTemplate {
    private final String template;
    private final Pattern variablePattern = Pattern.compile("\\{([^}]+)}");

    public ValidPromptTemplate(String template) {
        this.template = validateTemplate(template);
    }

    private String validateTemplate(String template) {
        if (template == null || template.trim().isEmpty()) {
            throw new IllegalArgumentException("Template cannot be null or empty");
        }
        return template;
    }

    public String format(Map<String, String> variables) {
        String formattedPrompt = template;
        Matcher matcher = variablePattern.matcher(template);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            String value = variables.get(variableName);

            if (value == null) {
                throw new IllegalArgumentException("Missing value for variable: " + variableName);
            }

            formattedPrompt = formattedPrompt.replace("{" + variableName + "}", sanitizeInput(value));
        }

        return formattedPrompt;
    }

    private String sanitizeInput(String input) {
        // Implement input sanitization logic here
        // For example, remove any potentially harmful characters or patterns
        return input.replaceAll("[<>&'\"]", "");
    }

    public Map<String, Object> extractVariables() {
        Matcher matcher = variablePattern.matcher(template);
        return matcher.results()
                .map(matchResult -> matchResult.group(1))
                .collect(Collectors.toMap(
                        varName -> varName,
                        varName -> null,
                        (v1, v2) -> v1
                ));
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public String toString() {
        return "ValidPromptTemplate{" +
                "template='" + template + '\'' +
                '}';
    }
}