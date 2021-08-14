package com.openavt.core.assets

import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric

class DummyBackend: OAVTBackendInterface {
    var latestEvent: OAVTEvent? = null
    var latestMetric: OAVTMetric? = null

    override fun sendEvent(event: OAVTEvent) {
        this.latestEvent = event
    }

    override fun sendMetric(metric: OAVTMetric) {
        this.latestMetric = metric
    }

    override fun instrumentReady(instrument: OAVTInstrument) {}

    override fun endOfService() {}
}