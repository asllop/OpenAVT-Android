package com.openavt.core

import com.openavt.core.assets.DummyBackend
import com.openavt.core.assets.DummyTracker
import com.openavt.core.hubs.OAVTHubCore
import com.openavt.core.metrics.OAVTMetricalcCore
import com.openavt.core.models.OAVTAction
import com.openavt.core.utils.OAVTAssert.Companion.assertEquals
import org.junit.Assert.*
import org.junit.Test

class MetricsUnitTest {
    @Test
    fun metrics_workflow() {
        val (instrument, trackerId) = createInstrument()
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        var metric = backend.getLastMetric()!!
        assertEquals(metric.name, "NumRequests")
        assertEquals(metric.value, 1)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "NumLoads")
        assertEquals(metric.value, 1)

        instrument.emit(OAVTAction.BufferBegin, trackerId)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertNull(backend.getLastMetric())

        Thread.sleep(700)

        instrument.emit(OAVTAction.Start, trackerId)
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "NumPlays")
        assertEquals(metric.value, 1)
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "StartTime")
        assertEquals(metric.value.toLong(), 700, 50)

        instrument.emit(OAVTAction.BufferBegin, trackerId)

        // Remove PlayTime metrics
        backend.clearMetrics()

        Thread.sleep(500)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "NumRebuffers")
        assertEquals(metric.value, 1)
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "RebufferTime")
        assertEquals(metric.value.toLong(), 500, 50)

        Thread.sleep(800)

        instrument.emit(OAVTAction.Error, trackerId)
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "PlayTime")
        assertEquals(metric.value.toLong(), 800, 50)

        instrument.emit(OAVTAction.End, trackerId)
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "PlayTime")
        metric = backend.getLastMetric()!!
        assertEquals(metric.name, "NumEnds")
        assertEquals(metric.value, 1)
    }

    private fun createInstrument(): Pair<OAVTInstrument, Int> {
        val instrument = OAVTInstrument(OAVTHubCore(), OAVTMetricalcCore(), DummyBackend())
        val trackerId = instrument.addTracker(DummyTracker())
        instrument.ready()
        return Pair(instrument, trackerId)
    }
}