package com.openavt.core.models

/**
 * An OpenAVT metric.
 *
 * @param name Metric name
 * @param type Metric type
 * @param value Metric value
 */
class OAVTMetric(name: String, type: MetricType, value: Number): OAVTSample() {

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
        fun StartTime(value: Int) = OAVTMetric("StartTime", MetricType.Gauge, value)
        /** Number of streams played metric name. */
        fun NumPlays(value: Int) = OAVTMetric("NumPlays", MetricType.Counter, value)
        /** Rebuffer time metric name. */
        fun RebufferTime(value: Int) = OAVTMetric("RebufferTime", MetricType.Gauge, value)
        /** Number of rebufers metric name. */
        fun NumRebuffers(value: Int) = OAVTMetric("NumRebuffers", MetricType.Counter, value)
        /** Playtime since last event. */
        fun PlayTime(value: Int) = OAVTMetric("PlayTime", MetricType.Gauge, value)
        /** Number of streams requested metric name. */
        fun NumRequests(value: Int) = OAVTMetric("NumRequests", MetricType.Counter, value)
        /** Number of streams loaded metric name. */
        fun NumLoads(value: Int) = OAVTMetric("NumLoads", MetricType.Counter, value)
        /** Number of streams ended metric name. */
        fun NumEnds(value: Int) = OAVTMetric("NumEnds", MetricType.Counter, value)
    }
}