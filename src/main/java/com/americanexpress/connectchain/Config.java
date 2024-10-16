package com.americanexpress.connectchain;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static Config instance;
    private Map<String, Object> config;

    private Config() {
        config = new HashMap<>();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void loadConfig(String yamlFilePath) throws IOException {
        loadFromEnvironment();
        loadFromYamlFile(yamlFilePath);
    }

    private void loadFromEnvironment() {
        // Load configuration from environment variables
        // Add environment variables to config map
        config.put("OPENAI_API_KEY", System.getenv("OPENAI_API_KEY"));
        config.put("AZURE_OPENAI_API_KEY", System.getenv("AZURE_OPENAI_API_KEY"));
        // Add more environment variables as needed
    }

    private void loadFromYamlFile(String yamlFilePath) throws IOException {
        try (InputStream input = new FileInputStream(yamlFilePath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlConfig = yaml.load(input);
            config.putAll(yamlConfig);
        }
    }

    public Object getConfig(String key) {
        return config.get(key);
    }

    public Map<String, Object> getAllConfig() {
        return new HashMap<>(config);
    }

    // Helper method to get a nested configuration value
    public Object getNestedConfig(String... keys) {
        Object current = config;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return current;
    }
}
```

This Java implementation of the Config class provides the following functionality:

1. Singleton pattern to ensure only one instance of Config exists.
2. Methods to load configuration from both environment variables and a YAML file.
3. A method to retrieve configuration values by key.
4. A method to get all configuration as a Map.
5. A helper method to retrieve nested configuration values.

To use this class, you would typically do something like this:

```java
Config config = Config.getInstance();
try {
    config.loadConfig("path/to/your/config.yaml");
} catch (IOException e) {
    // Handle the exception
}

// Get a configuration value
String apiKey = (String) config.getConfig("OPENAI_API_KEY");

// Get a nested configuration value
Object nestedValue = config.getNestedConfig("services", "openai", "model");
```

Note that you'll need to add the SnakeYAML dependency to your project's pom.xml file:

```xml
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>1.29</version> <!-- Use the latest version available -->
</dependency>