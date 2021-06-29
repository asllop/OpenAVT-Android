package com.openavt.core.interfaces

import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric

/**
 * OpenAVT base interface for instrument metricalc components.
 */
interface OAVTMetricalcInterface: OAVTComponentInterface {
    /**
     * Process metrics.
     *
     * @param event Event received.
     * @param tracker Tracker that generated the event.
     * @return Array of metrics.
     */
    fun processMetric(event: OAVTEvent, tracker: OAVTTrackerInterface): Array<OAVTMetric>
}