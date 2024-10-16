package com.enterprise.connectchain.utils;

import com.enterprise.connectchain.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

public class ProxyConfigurator {
    private static final Logger logger = LoggerFactory.getLogger(ProxyConfigurator.class);
    private static ProxyConfigurator instance;

    private ProxyConfigurator() {
        // Private constructor to prevent instantiation
    }

    public static synchronized ProxyConfigurator getInstance() {
        if (instance == null) {
            instance = new ProxyConfigurator();
        }
        return instance;
    }

    public void setHttpProxy(String host, int port) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", String.valueOf(port));
        logger.info("HTTP proxy set to {}:{}", host, port);
    }

    public void setHttpsProxy(String host, int port) {
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", String.valueOf(port));
        logger.info("HTTPS proxy set to {}:{}", host, port);
    }

    public void clearProxySettings() {
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");
        ProxySelector.setDefault(null);
        logger.info("Proxy settings cleared");
    }

    public void applyProxySettingsFromConfig() {
        Config config = Config.getInstance();
        Optional<String> httpProxyHost = config.get("proxy.http.host");
        Optional<Integer> httpProxyPort = config.get("proxy.http.port");
        Optional<String> httpsProxyHost = config.get("proxy.https.host");
        Optional<Integer> httpsProxyPort = config.get("proxy.https.port");

        if (httpProxyHost.isPresent() && httpProxyPort.isPresent()) {
            setHttpProxy(httpProxyHost.get(), httpProxyPort.get());
        }

        if (httpsProxyHost.isPresent() && httpsProxyPort.isPresent()) {
            setHttpsProxy(httpsProxyHost.get(), httpsProxyPort.get());
        }

        if ((httpProxyHost.isPresent() && httpProxyPort.isPresent()) ||
            (httpsProxyHost.isPresent() && httpsProxyPort.isPresent())) {
            setProxySelector(httpProxyHost.orElse(null), httpProxyPort.orElse(null),
                             httpsProxyHost.orElse(null), httpsProxyPort.orElse(null));
        }
    }

    private void setProxySelector(String httpHost, Integer httpPort, String httpsHost, Integer httpsPort) {
        ProxySelector.setDefault(new ProxySelector() {
            @Override
            public java.util.List<Proxy> select(URI uri) {
                if (uri.getScheme().toLowerCase().startsWith("http")) {
                    if (uri.getScheme().equalsIgnoreCase("https") && httpsHost != null && httpsPort != null) {
                        return Collections.singletonList(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpsHost, httpsPort)));
                    } else if (httpHost != null && httpPort != null) {
                        return Collections.singletonList(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpHost, httpPort)));
                    }
                }
                return Collections.singletonList(Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, java.net.SocketAddress sa, java.io.IOException ioe) {
                logger.error("Proxy connection failed for URI: {}", uri, ioe);
            }
        });
        logger.info("ProxySelector configured with HTTP proxy: {}:{} and HTTPS proxy: {}:{}",
                    httpHost, httpPort, httpsHost, httpsPort);
    }
}