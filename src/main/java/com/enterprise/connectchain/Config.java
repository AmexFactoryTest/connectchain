import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.util.List;

class YamlConfig {
    private String version;
    private List<ChainConfig> chains;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ChainConfig> getChains() {
        return chains;
    }

    public void setChains(List<ChainConfig> chains) {
        this.chains = chains;
    }

    public static class ChainConfig {
        private String name;
        private String rpcUrl;
        private String chainId;
        private String nativeCurrency;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRpcUrl() {
            return rpcUrl;
        }

        public void setRpcUrl(String rpcUrl) {
            this.rpcUrl = rpcUrl;
        }

        public String getChainId() {
            return chainId;
        }

        public void setChainId(String chainId) {
            this.chainId = chainId;
        }

        public String getNativeCurrency() {
            return nativeCurrency;
        }

        public void setNativeCurrency(String nativeCurrency) {
            this.nativeCurrency = nativeCurrency;
        }
    }
}

package com.enterprise.connectchain;

package com.enterprise.connectchain;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class Config {
    private static Config instance;
    private YamlConfig yamlConfig;

    private Config() {
        // Private constructor to prevent instantiation
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void loadConfig(String path) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Config file not found: " + path);
            }
            Yaml yaml = new Yaml(new Constructor(YamlConfig.class));
            yamlConfig = yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config from " + path, e);
        }
    }

    public String getVersion() {
        if (yamlConfig == null) {
            throw new IllegalStateException("Configuration not loaded. Call loadConfig() first.");
        }
        return yamlConfig.getVersion();
    }

    public List<YamlConfig.ChainConfig> getChains() {
        if (yamlConfig == null) {
            throw new IllegalStateException("Configuration not loaded. Call loadConfig() first.");
        }
        return yamlConfig.getChains();
    }

    public Optional<YamlConfig.ChainConfig> getChainConfig(String name) {
        return getChains().stream()
                .filter(chain -> chain.getName().equals(name))
                .findFirst();
    }

    public boolean hasChain(String name) {
        return getChainConfig(name).isPresent();
    }

    public void clear() {
        yamlConfig = null;
    }
}