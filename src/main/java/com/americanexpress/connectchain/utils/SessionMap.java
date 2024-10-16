package com.americanexpress.connectchain.utils;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionMap {
    private final Map<String, Session> sessions;
    private final long sessionTimeoutSeconds;
    private final ScheduledExecutorService cleanupExecutor;

    public SessionMap(long sessionTimeoutSeconds) {
        this.sessions = new ConcurrentHashMap<>();
        this.sessionTimeoutSeconds = sessionTimeoutSeconds;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduleCleanup();
    }

    public void put(String key, Object value) {
        sessions.put(key, new Session(value));
    }

    public Object get(String key) {
        Session session = sessions.get(key);
        if (session != null && !session.isExpired()) {
            session.updateLastAccessed();
            return session.getValue();
        }
        return null;
    }

    public void remove(String key) {
        sessions.remove(key);
    }

    public void clear() {
        sessions.clear();
    }

    private void scheduleCleanup() {
        cleanupExecutor.scheduleAtFixedRate(this::cleanup, sessionTimeoutSeconds, sessionTimeoutSeconds, TimeUnit.SECONDS);
    }

    private void cleanup() {
        Instant now = Instant.now();
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private class Session {
        private final Object value;
        private Instant lastAccessed;

        Session(Object value) {
            this.value = value;
            this.lastAccessed = Instant.now();
        }

        Object getValue() {
            return value;
        }

        void updateLastAccessed() {
            this.lastAccessed = Instant.now();
        }

        boolean isExpired() {
            return isExpired(Instant.now());
        }

        boolean isExpired(Instant now) {
            return lastAccessed.plusSeconds(sessionTimeoutSeconds).isBefore(now);
        }
    }
}