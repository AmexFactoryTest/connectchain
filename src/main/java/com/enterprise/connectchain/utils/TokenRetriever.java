package com.enterprise.connectchain.utils;

import com.enterprise.connectchain.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TokenRetriever {
    private static final Logger logger = LoggerFactory.getLogger(TokenRetriever.class);
    private static final ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ReentrantLock> tokenLocks = new ConcurrentHashMap<>();
    private static final Config config = Config.getInstance();

    public static String getToken(String tokenKey) {
        return tokenCache.computeIfAbsent(tokenKey, TokenRetriever::fetchToken);
    }

    public static void refreshToken(String tokenKey) {
        ReentrantLock lock = tokenLocks.computeIfAbsent(tokenKey, k -> new ReentrantLock());
        lock.lock();
        try {
            String newToken = fetchToken(tokenKey);
            tokenCache.put(tokenKey, newToken);
            logger.info("Token refreshed for key: {}", tokenKey);
        } finally {
            lock.unlock();
        }
    }

    public static void clearCache() {
        tokenCache.clear();
        logger.info("Token cache cleared");
    }

    private static String fetchToken(String tokenKey) {
        try {
            String token = config.get(tokenKey)
                    .orElseThrow(() -> new IllegalArgumentException("Token not found for key: " + tokenKey));

            // In a real-world scenario, you might want to decrypt the token here if it's stored encrypted
            return token;
        } catch (Exception e) {
            logger.error("Error fetching token for key: {}", tokenKey, e);
            throw new RuntimeException("Failed to fetch token", e);
        }
    }

    // Utility method to generate a secure random token (for demonstration purposes)
    public static String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}