package com.openavt.newrelic.backends

import com.newrelic.agent.android.NewRelic
import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.utils.OAVTLog

/**
 * OpenAVT backend for New Relic.
 */
open class OAVTBackendNewrelic: OAVTBackendInterface {

    override fun sendEvent(event: OAVTEvent) {
        var attr = event.getDictionary().toMutableMap()
        attr["actionName"] = buildActionName(event)
        if (!NewRelic.recordCustomEvent(buildEventType(event), attr)) {
            OAVTLog.error("OAVTBackendNewrelic: Could not record custom event.")
        }
    }

    override fun sendMetric(metric: OAVTMetric) {
        OAVTLog.verbose("OAVTBackendNewrelic: recorded metric")
        NewRelic.recordMetric(buildMetricName(metric), buildMetricCategory(metric), metric.value.toDouble())
    }

    override fun instrumentReady(instrument: OAVTInstrument) {}

    override fun endOfService() {}

    /**
     * Build a event type.
     *
     * Overwrite this method in a subclass to provide a custom event type.
     *
     * @param event An OAVTEvent instance.
     * @return Event type.
     */
    open fun buildEventType(event: OAVTEvent): String {
        return "OAVT"
    }

    /**
     * Build a event action name.
     *
     * Overwrite this method in a subclass to provide a custom action name.
     *
     * @param event An OAVTEvent instance.
     * @return Action name.
     */
    open fun buildActionName(event: OAVTEvent): String {
        return event.action.actionName
    }

    /**
     * Build a metric name.
     *
     * Overwrite this method in a subclass to provide a custom metric name.
     *
     * @param metric An OAVTMetric instance.
     * @return Metric name.
     */
    open fun buildMetricName(metric: OAVTMetric): String {
        return metric.name
    }

    /**
     * Build a metric category.
     *
     * Overwrite this method in a subclass to provide a custom metric category.
     *
     * @param metric An OAVTMetric instance.
     * @return Metric category.
     */
    open fun buildMetricCategory(metric: OAVTMetric): String {
        return "OAVT"
    }
}