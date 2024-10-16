# Connect Chain
An enterprise-grade, Generative AI framework and utilities for AI-enabled applications. `connectchain` is designed to bridge the gap between enterprise needs and what is available in existing frameworks.

Primary objectives include:
* A login utility for API-based LLM services that integrates with Enterprise Auth Service (EAS). Simplified generation of the JWT token, which is then passed to the modeling service provider.
* Support for configuration-based outbound proxy support at the model level to allow integration with enterprise-level security requirements.
* A set of tools to provide greater control over generated prompts. This is done by adding hooks to the existing langchain packages.

## Contributing

We welcome Your interest in the American Express Open Source Community on GitHub. Any Contributor to
any Open Source Project managed by the American Express Open Source Community must accept and sign
an Agreement indicating agreement to the terms below. Except for the rights granted in this 
Agreement to American Express and to recipients of software distributed by American Express, You
reserve all right, title, and interest, if any, in and to Your Contributions. Please
[fill out the Agreement](https://cla-assistant.io/americanexpress/connectchain).

## License

Any contributions made under this project will be governed by the
[Apache License 2.0](./LICENSE.txt).

## Code of Conduct

This project adheres to the [American Express Community Guidelines](./CODE_OF_CONDUCT.md). By
participating, you are expected to honor these guidelines.

## Installation

To set up the development environment for Connect Chain using Java 17 and Maven, follow these steps:

1. Install Java Development Kit (JDK) 17:
   - Download and install JDK 17 from the official Oracle website or use an OpenJDK distribution.
   - Set the JAVA_HOME environment variable to point to your JDK installation directory.

2. Install Maven:
   - Download Maven from the official Apache Maven website.
   - Extract the archive and add the bin directory to your system's PATH.

3. Clone the Connect Chain repository:
   ```
   git clone https://github.com/americanexpress/connectchain.git
   cd connectchain
   ```

4. Build the project using Maven:
   ```
   mvn clean install
   ```

5. To run tests:
   ```
   mvn test
   ```

6. To add Connect Chain as a dependency in your project, add the following to your `pom.xml`:
   ```xml
   <dependency>
       <groupId>com.americanexpress</groupId>
       <artifactId>connectchain</artifactId>
       <version>1.0.0</version> <!-- Replace with the latest version -->
   </dependency>
   ```

7. Sync your project to download the dependencies.

Now your development environment is set up and ready to use Connect Chain with Java 17 and Maven.

## Configuration

Connect Chain uses YAML configuration files for easy setup and management. Here's an example of how to configure and load the settings in Java:

1. Create a `config.yaml` file in your project's resources directory:

```yaml
llm:
  provider: openai
  model: gpt-3.5-turbo
  temperature: 0.7
  max_tokens: 150

auth:
  type: jwt
  service_url: https://auth.example.com

proxy:
  enabled: true
  host: proxy.example.com
  port: 8080

prompt_hooks:
  - name: content_filter
    type: pre_processing
    class: com.example.hooks.ContentFilterHook
  - name: response_formatter
    type: post_processing
    class: com.example.hooks.ResponseFormatterHook
```

2. Load the configuration in your Java application:

```java
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class ConfigLoader {
    public static Map<String, Object> loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("config.yaml")) {
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }
}

// Usage
Map<String, Object> config = ConfigLoader.loadConfig();
```

3. Access configuration values:

```java
String llmProvider = (String) ((Map<String, Object>) config.get("llm")).get("provider");
boolean proxyEnabled = (boolean) ((Map<String, Object>) config.get("proxy")).get("enabled");
```

This configuration approach allows for easy management of Connect Chain settings and integration with enterprise systems.

## Examples

Here are some examples demonstrating the usage of ConnectChain's core features:

### 1. Initializing ConnectChain

```java
import com.americanexpress.connectchain.ConnectChain;
import com.americanexpress.connectchain.config.ConnectChainConfig;

public class ConnectChainExample {
    public static void main(String[] args) {
        ConnectChainConfig config = new ConnectChainConfig.Builder()
            .withLlmProvider("openai")
            .withModel("gpt-3.5-turbo")
            .withTemperature(0.7)
            .withMaxTokens(150)
            .build();

        ConnectChain connectChain = new ConnectChain(config);
    }
}
```

### 2. Using Enterprise Auth Service

```java
import com.americanexpress.connectchain.auth.EnterpriseAuthService;

public class AuthExample {
    public static void main(String[] args) {
        EnterpriseAuthService authService = new EnterpriseAuthService("https://auth.example.com");
        String jwtToken = authService.generateJwtToken();
        
        // Use the JWT token for API calls
        connectChain.setAuthToken(jwtToken);
    }
}
```

### 3. Configuring Outbound Proxy

```java
import com.americanexpress.connectchain.proxy.ProxyConfig;

public class ProxyExample {
    public static void main(String[] args) {
        ProxyConfig proxyConfig = new ProxyConfig("proxy.example.com", 8080);
        connectChain.setProxyConfig(proxyConfig);
    }
}
```

### 4. Using Prompt Hooks

```java
import com.americanexpress.connectchain.hooks.PromptHook;

public class ContentFilterHook implements PromptHook {
    @Override
    public String process(String input) {
        // Implement content filtering logic
        return filteredInput;
    }
}

public class HookExample {
    public static void main(String[] args) {
        PromptHook contentFilter = new ContentFilterHook();
        connectChain.addPreProcessingHook(contentFilter);
        
        String response = connectChain.generateResponse("Your prompt here");
    }
}
```

For more detailed examples, please refer to the Java files in the `examples` directory.
