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
}