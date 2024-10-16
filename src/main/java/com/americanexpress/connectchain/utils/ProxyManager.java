package com.americanexpress.connectchain.utils;

import com.americanexpress.connectchain.Config;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.atomic.AtomicReference;

public class ProxyManager {
    private final Config config;
    private final AtomicReference<Proxy> currentProxy;
    private final AtomicReference<String> proxyUsername;
    private final AtomicReference<String> proxyPassword;

    public ProxyManager(Config config) {
        this.config = config;
        this.currentProxy = new AtomicReference<>(null);
        this.proxyUsername = new AtomicReference<>(null);
        this.proxyPassword = new AtomicReference<>(null);
        configureProxy();
    }

    private void configureProxy() {
        String proxyHost = config.getString("proxy.host", null);
        int proxyPort = config.getInt("proxy.port", -1);
        String proxyType = config.getString("proxy.type", "HTTP").toUpperCase();

        if (proxyHost != null && proxyPort > 0) {
            Proxy.Type type = proxyType.equals("SOCKS") ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
            Proxy proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
            currentProxy.set(proxy);

            proxyUsername.set(config.getString("proxy.username", null));
            proxyPassword.set(config.getString("proxy.password", null));
        }
    }

    public OkHttpClient.Builder applyProxy(OkHttpClient.Builder builder) {
        Proxy proxy = currentProxy.get();
        if (proxy != null) {
            builder.proxy(proxy);

            String username = proxyUsername.get();
            String password = proxyPassword.get();
            if (username != null && password != null) {
                Authenticator proxyAuthenticator = (route, response) -> {
                    String credential = Credentials.basic(username, password);
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                };
                builder.proxyAuthenticator(proxyAuthenticator);
            }
        }
        return builder;
    }

    public void enableProxy() {
        configureProxy();
    }

    public void disableProxy() {
        currentProxy.set(null);
        proxyUsername.set(null);
        proxyPassword.set(null);
    }

    public Proxy getCurrentProxy() {
        return currentProxy.get();
    }

    public boolean isProxyEnabled() {
        return currentProxy.get() != null;
    }
}