package com.openavt.graphite.backends

import com.openavt.core.OAVTInstrument
import com.openavt.core.buffers.OAVTBuffer
import com.openavt.core.buffers.OAVTReservoirBuffer
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.utils.OAVTLog
import java.util.*

open class OAVTBackendGraphite(buffer: OAVTBuffer = OAVTReservoirBuffer(500), time: Long = 30, host: String, port: Int = 2003): OAVTBackendInterface {
    private val buffer = buffer
    private val time = time
    private val host = host
    private val port = port
    private var timer: Timer? = null

    init {
        OAVTLog.verbose("Init Graphite Backend")
    }

    override fun sendEvent(event: OAVTEvent) {
    }

    override fun sendMetric(metric: OAVTMetric) {
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
    }

    override fun endOfService() {
    }
}