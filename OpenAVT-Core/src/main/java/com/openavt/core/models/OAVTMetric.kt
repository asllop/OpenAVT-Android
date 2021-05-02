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
        fun START_TIME(value: Int) = OAVTMetric("startTime", MetricType.Gauge, value)
        /** Number of streams played metric name. */
        fun NUM_PLAYS(value: Int) = OAVTMetric("numPlays", MetricType.Counter, value)
        /** Rebuffer time metric name. */
        fun REBUFFER_TIME(value: Int) = OAVTMetric("rebufferTime", MetricType.Gauge, value)
        /** Number of rebufers metric name. */
        fun NUM_REBUFFERS(value: Int) = OAVTMetric("numRebuffers", MetricType.Counter, value)
        /** Playtime since last event. */
        fun PLAY_TIME(value: Int) = OAVTMetric("playTime", MetricType.Gauge, value)
        /** Number of streams requested metric name. */
        fun NUM_REQUESTS(value: Int) = OAVTMetric("numRequests", MetricType.Counter, value)
        /** Number of streams loaded metric name. */
        fun NUM_LOADS(value: Int) = OAVTMetric("numLoads", MetricType.Counter, value)
        /** Number of streams ended metric name. */
        fun NUM_ENDS(value: Int) = OAVTMetric("numEnds", MetricType.Counter, value)
    }
}