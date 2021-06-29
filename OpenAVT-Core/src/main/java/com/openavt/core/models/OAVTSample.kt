package com.openavt.core.models

/**
 * An OpenAVT Sample.
 */
open class OAVTSample {

    /**
     * Get sample timestamp.
     */
    var timestamp: Long = System.currentTimeMillis()

    /**
     * Convert object to string.
     *
     * @return Object representation.
     */
    override fun toString(): String {
        return timestamp.toString()
    }
}