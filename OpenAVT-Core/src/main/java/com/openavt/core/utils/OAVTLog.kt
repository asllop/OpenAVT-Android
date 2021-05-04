package com.openavt.core.utils

import android.util.Log

/**
 * OpenAVT logging.
 */
class OAVTLog {
    /**
     * Log levels.
     */
    enum class LogLevel(val value: Int) {
        /**
         * Log level verbose.
         */
        Verbose(0),
        /**
         * Log level debug.
         */
        Debug(1),
        /**
         * Log level warning.
         */
        Warning(2),
        /**
         * Log level error.
         */
        Error(3),
        /**
         * Log level none.
         */
        None(4)
    }

    companion object {
        private var logLevel = LogLevel.Warning

        private fun log(msg: String, cutLevel: LogLevel) {
            if (this.logLevel <= cutLevel) {
                Log.v("OAVTLog", "" + System.currentTimeMillis() + ": " + msg)
            }
        }

        /**
         * Print a verbose log.
         *
         * @param msg Message.
         */
        fun verbose(msg: String) {
            this.log("[VERBOSE] " + msg, LogLevel.Verbose)
        }

        /**
         * Print a debug log.
         *
         * @param msg Message.
         */
        fun debug(msg: String) {
            this.log("[DEBUG] " + msg, LogLevel.Debug)
        }

        /**
         * Print a warning log.
         *
         * @param msg Message.
         */
        fun warning(msg: String) {
            this.log("[WARNING] " + msg, LogLevel.Warning)
        }

        /**
         * Print a error log.
         *
         * @param msg Message.
         */
        fun error(msg: String) {
            this.log("[ERROR] " + msg, LogLevel.Error)
        }

        /**
         * Set current logging level.
         *
         * @param loglevel Log level.
         */
        fun setLogLevel(logLevel: LogLevel) {
            this.logLevel = logLevel
        }
    }
}