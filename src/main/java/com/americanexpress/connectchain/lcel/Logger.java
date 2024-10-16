package com.americanexpress.connectchain.lcel;

import java.util.function.Supplier;

/**
 * Functional interface for log operations.
 */
@FunctionalInterface
interface LogOperation {
    void log(String message);
}

/**
 * Logger interface defining methods for different log levels.
 */
public interface Logger {
    void debug(Supplier<String> messageSupplier);
    void info(Supplier<String> messageSupplier);
    void warning(Supplier<String> messageSupplier);
    void error(Supplier<String> messageSupplier);
    void setLevel(LogLevel level);
}

/**
 * Enum representing different log levels.
 */
enum LogLevel {
    DEBUG, INFO, WARNING, ERROR
}

/**
 * Implementation of the Logger interface.
 */
class LoggerImpl implements Logger {
    private LogLevel level = LogLevel.INFO;

    private void log(LogLevel messageLevel, Supplier<String> messageSupplier, LogOperation operation) {
        if (messageLevel.ordinal() >= this.level.ordinal()) {
            operation.log(messageSupplier.get());
        }
    }

    @Override
    public void debug(Supplier<String> messageSupplier) {
        log(LogLevel.DEBUG, messageSupplier, message -> System.out.println("DEBUG: " + message));
    }

    @Override
    public void info(Supplier<String> messageSupplier) {
        log(LogLevel.INFO, messageSupplier, message -> System.out.println("INFO: " + message));
    }

    @Override
    public void warning(Supplier<String> messageSupplier) {
        log(LogLevel.WARNING, messageSupplier, message -> System.out.println("WARNING: " + message));
    }

    @Override
    public void error(Supplier<String> messageSupplier) {
        log(LogLevel.ERROR, messageSupplier, message -> System.out.println("ERROR: " + message));
    }

    @Override
    public void setLevel(LogLevel level) {
        this.level = level;
    }
}