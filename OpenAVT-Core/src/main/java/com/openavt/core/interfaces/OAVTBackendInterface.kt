package com.openavt.core.interfaces

import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric

/**
 * OpenAVT base interface for instrument backend components.
 */
interface OAVTBackendInterface: OAVTComponentInterface {
    /**
     * Send an event.
     *
     * @param event Event received.
     */
    fun sendEvent(event: OAVTEvent)

    /**
     * Send a metric.
     *
     * @param metric Metric received.
     */
    fun sendMetric(metric: OAVTMetric)
}