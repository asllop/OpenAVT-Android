package com.openavt.core.models

/**
 * An OpenAVT metric.
 *
 * @param name Metric name
 * @param type Metric type
 * @param value Metric value
 */
open class OAVTMetric(name: String, type: MetricType, value: Number): OAVTSample() {

    /**
     * Get metric name.
     */
    val name: String = name
    /**
     * Get metric type.
     */
    val type: MetricType = type
    /**
     * Get metric value.
     */
    val value: Number = value

    /**
     * Metric types.
     */
    enum class MetricType {
        /** Type counter. Sum all the values. */
        Counter,
        /** Type gauge. Use the last value. **/
        Gauge
    }

    /**
     * Convert object to string.
     *
     * @return Object representation.
     */
    override fun toString(): String {
        return "Name: " + name + " , Type: " + type + " , Value: " + value
    }

    companion object {
        /** Start time metric name. */
        val START_TIME = "startTime"
        /** Number of streams played metric name. */
        val NUM_PLAYS = "numPlays"
        /** Rebuffer time metric name. */
        val REBUFFER_TIME = "rebufferTime"
        /** Number of rebufers metric name. */
        val NUM_REBUFFERS = "numRebuffers"
        /** Playtime since last event. */
        val PLAY_TIME = "playTime"
        /** Number of streams requested metric name. */
        val NUM_REQUESTS = "numRequests"
        /** Number of streams loaded metric name. */
        val NUM_LOADS = "numLoads"
        /** Number of streams ended metric name. */
        val NUM_ENDS = "numEnds"
    }
}