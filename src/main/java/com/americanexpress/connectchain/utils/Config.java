package com.americanexpress.connectchain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private Map<String, Object> data;

    public static Config fromEnv() throws IOException {
        String configPath = System.getenv("CONFIG_PATH");
        if (configPath == null) {
            logger.error("CONFIG_PATH environment variable not set");
            throw new IllegalStateException("CONFIG_PATH environment variable not set");
        }
        return new Config(configPath);
    }

    public Config(String filepath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            this.data = mapper.readValue(new File(filepath), Map.class);
        } catch (IOException e) {
            logger.error("Error reading configuration file: {}", filepath, e);
            throw e;
        }
    }

    public Object get(String key) {
        return data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }

    // Additional methods can be added here as needed
}