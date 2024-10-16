package com.americanexpress.connectchain.utils;

import com.americanexpress.connectchain.Config;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.http.HttpClient;
import java.util.Optional;

public class ProxyManager {
    private static ProxyManager instance;
    private Proxy proxy;
    private String proxyHost;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;

    private ProxyManager() {
        loadProxySettings();
    }

    public static ProxyManager getInstance() {
        if (instance == null) {
            instance = new ProxyManager();
        }
        return instance;
    }

    private void loadProxySettings() {
        Config config = Config.getInstance();
        proxyHost = (String) config.getNestedConfig("proxy", "host");
        proxyPort = (Integer) config.getNestedConfig("proxy", "port");
        proxyUsername = (String) config.getNestedConfig("proxy", "username");
        proxyPassword = (String) config.getNestedConfig("proxy", "password");

        if (proxyHost != null && proxyPort != 0) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }
    }

    public Optional<Proxy> getProxy() {
        return Optional.ofNullable(proxy);
    }

    public HttpClient.Builder applyProxy(HttpClient.Builder builder) {
        if (proxy != null) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)));
            if (proxyUsername != null && proxyPassword != null) {
                builder.authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                    }
                });
            }
        }
        return builder;
    }

    public void configureSystemProperties() {
        if (proxy != null) {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", String.valueOf(proxyPort));

            if (proxyUsername != null && proxyPassword != null) {
                System.setProperty("http.proxyUser", proxyUsername);
                System.setProperty("http.proxyPassword", proxyPassword);
                System.setProperty("https.proxyUser", proxyUsername);
                System.setProperty("https.proxyPassword", proxyPassword);
            }
        }
    }

    // This method can be used to apply proxy settings to langchain4j model calls
    public void configureLangChain4j() {
        configureSystemProperties();
        // Additional configuration for langchain4j might be needed here
        // depending on how it handles proxy settings
    }
}
```

This Java implementation of the ProxyManager provides the following functionality:

1. It's a singleton class to ensure consistent proxy settings across the application.
2. It loads proxy settings from the Config class we created earlier.
3. It provides methods to get the proxy settings and apply them to HttpClient.Builder instances.
4. It includes a method to configure system properties for proxy settings, which can be used by libraries that rely on system properties for proxy configuration.
5. It includes a placeholder method `configureLangChain4j()` which sets system properties and can be extended to include any specific configuration needed for langchain4j.

To use this ProxyManager in other parts of the application, you would typically do something like this:

```java
HttpClient.Builder builder = HttpClient.newBuilder();
ProxyManager.getInstance().applyProxy(builder);
HttpClient client = builder.build();

// For langchain4j
ProxyManager.getInstance().configureLangChain4j();
// Then create and use your langchain4j models