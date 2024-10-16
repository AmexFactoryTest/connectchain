package com.americanexpress.connectchain;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    private final Map<String, Object> config;

    public Config(String filePath) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            this.config = new ConcurrentHashMap<>(yaml.load(inputStream));
        }
    }

    @SuppressWarnings("unchecked")
    private Object getNestedValue(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = config;
        for (int i = 0; i < keys.length - 1; i++) {
            current = (Map<String, Object>) current.get(keys[i]);
            if (current == null) {
                return null;
            }
        }
        return current.get(keys[keys.length - 1]);
    }

    public String getString(String key) {
        Object value = getNestedValue(key);
        if (value == null) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        return value.toString();
    }

    public String getString(String key, String defaultValue) {
        Object value = getNestedValue(key);
        return value != null ? value.toString() : defaultValue;
    }

    public int getInt(String key) {
        Object value = getNestedValue(key);
        if (value == null) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new IllegalArgumentException("Value for key " + key + " is not a number");
    }

    public int getInt(String key, int defaultValue) {
        Object value = getNestedValue(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new IllegalArgumentException("Value for key " + key + " is not a number");
    }

    public boolean getBoolean(String key) {
        Object value = getNestedValue(key);
        if (value == null) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new IllegalArgumentException("Value for key " + key + " is not a boolean");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = getNestedValue(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new IllegalArgumentException("Value for key " + key + " is not a boolean");
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String key) {
        Object value = getNestedValue(key);
        if (value == null) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        throw new IllegalArgumentException("Value for key " + key + " is not a map");
    }
}