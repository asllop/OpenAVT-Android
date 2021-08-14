package com.openavt.core.assets

import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric

class DummyBackend(): OAVTBackendInterface {
    override fun sendEvent(event: OAVTEvent) {}

    override fun sendMetric(metric: OAVTMetric) {}

    override fun instrumentReady(instrument: OAVTInstrument) {}

    override fun endOfService() {}
}