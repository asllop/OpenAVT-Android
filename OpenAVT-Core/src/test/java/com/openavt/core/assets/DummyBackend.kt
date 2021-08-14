package com.openavt.core.assets

import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric

class DummyBackend: OAVTBackendInterface {
    var latestEvent: OAVTEvent? = null
    var latestMetrics: MutableList<OAVTMetric> = mutableListOf()

    override fun sendEvent(event: OAVTEvent) {
        this.latestEvent = event
    }

    override fun sendMetric(metric: OAVTMetric) {
        this.latestMetrics.add(metric)
    }

    override fun instrumentReady(instrument: OAVTInstrument) {}

    override fun endOfService() {}

    fun getLastEvent(): OAVTEvent? {
        val ev = latestEvent
        latestEvent = null
        return ev
    }

    fun getLastMetric(): OAVTMetric? {
        if (latestMetrics.size > 0) {
            return latestMetrics.removeLast()
        }
        else {
            return null
        }
    }
}