package com.group7.lib.utilities.Logger;

import java.time.LocalTime;

public class Logger {

    public static Logger shared = new Logger("Shared");

    private final String moduleName;

    public Logger(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Logs a message with a specific log level
     *
     * @param message The message to log
     * @param logLevel The log level of the message (INFO, WARNING, ERROR,
     * DEBUG), default is INFO
     */
    public void log(String message, LogLevel logLevel) {
        // output format: [LocalTime] [ModuleName] [LogLevel] [Message]
        System.out.println("[" + LocalTime.now() + "] [" + this.moduleName + "] [" + logLevel + "] " + message);
    }

}
